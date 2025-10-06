package com.example.ma_mobile.websocket;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.ChatMessage;
import com.example.ma_mobile.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;

public class StompManager {
    private static final String TAG = "StompManager";
    private static final String WS_URL = "ws://10.0.2.2:8080/ws";

    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private Gson gson;
    private Map<Long, Disposable> topicSubscriptions;
    private Context context;

    public interface MessageListener {
        void onMessageReceived(ChatMessage message);
        void onError(String error);
    }

    public interface ConnectionListener {
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    public StompManager(Context context) {
        this.context = context;
        gson = new Gson();
        compositeDisposable = new CompositeDisposable();
        topicSubscriptions = new HashMap<>();
    }

    public void connect(ConnectionListener listener) {
        if (stompClient != null && stompClient.isConnected()) {
            listener.onConnected();
            return;
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);

        // Add authentication header
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getBearerToken();
        if (token != null) {
            List<StompHeader> headers = new ArrayList<>();
            headers.add(new StompHeader("Authorization", token));
            stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

            // Connect with headers
            stompClient.connect(headers);
        } else {
            stompClient.connect();
        }

        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "WebSocket opened");
                            listener.onConnected();
                            break;
                        case CLOSED:
                            Log.d(TAG, "WebSocket closed");
                            listener.onDisconnected();
                            break;
                        case ERROR:
                            Log.e(TAG, "WebSocket error", lifecycleEvent.getException());
                            listener.onError("Connection error: " + lifecycleEvent.getException().getMessage());
                            break;
                    }
                });

        compositeDisposable.add(lifecycleDisposable);
        // Connection is already initiated above with headers
    }

    public void subscribeToGuildChat(Long guildId, MessageListener listener) {
        if (stompClient == null || !stompClient.isConnected()) {
            listener.onError("Not connected to WebSocket");
            return;
        }

        String topic = "/topic/guild/" + guildId;

        // Unsubscribe from previous subscription if exists
        if (topicSubscriptions.containsKey(guildId)) {
            topicSubscriptions.get(guildId).dispose();
        }

        Disposable topicDisposable = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        topicMessage -> {
                            Log.d(TAG, "Received message: " + topicMessage.getPayload());
                            try {
                                ChatMessage message = gson.fromJson(topicMessage.getPayload(), ChatMessage.class);
                                listener.onMessageReceived(message);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing message", e);
                                listener.onError("Failed to parse message");
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "Error in topic subscription", throwable);
                            listener.onError("Subscription error: " + throwable.getMessage());
                        }
                );

        topicSubscriptions.put(guildId, topicDisposable);
        compositeDisposable.add(topicDisposable);
    }

    public void sendMessage(Long guildId, Long senderId, String content) {
        if (stompClient == null || !stompClient.isConnected()) {
            Log.e(TAG, "Cannot send message: not connected");
            return;
        }

        JsonObject message = new JsonObject();
        message.addProperty("guildId", guildId);
        message.addProperty("senderId", senderId);
        message.addProperty("content", content);

        stompClient.send("/app/chat.send", gson.toJson(message))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d(TAG, "Message sent successfully"),
                        throwable -> Log.e(TAG, "Error sending message", throwable)
                );
    }

    public void unsubscribeFromGuild(Long guildId) {
        if (topicSubscriptions.containsKey(guildId)) {
            topicSubscriptions.get(guildId).dispose();
            topicSubscriptions.remove(guildId);
        }
    }

    public void disconnect() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = new CompositeDisposable();
        }
        topicSubscriptions.clear();

        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }
    }

    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }
}
