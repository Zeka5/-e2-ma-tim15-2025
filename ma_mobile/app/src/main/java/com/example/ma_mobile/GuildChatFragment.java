package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.ChatMessageAdapter;
import com.example.ma_mobile.models.ChatMessage;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;
import com.example.ma_mobile.repository.UserRepository;
import com.example.ma_mobile.websocket.StompManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuildChatFragment extends Fragment {

    private static final String TAG = "GuildChatFragment";
    private static final String ARG_GUILD_ID = "guild_id";
    private static final String ARG_GUILD_NAME = "guild_name";

    private ImageButton btnBack;
    private ImageButton btnSend;
    private TextView tvGuildName;
    private TextView tvConnectionStatus;
    private EditText etMessage;
    private RecyclerView rvMessages;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;

    private Long guildId;
    private String guildName;
    private Long currentUserId;

    private ChatMessageAdapter adapter;
    private StompManager stompManager;
    private ApiService apiService;
    private UserRepository userRepository;

    public GuildChatFragment() {
        // Required empty public constructor
    }

    public static GuildChatFragment newInstance(Long guildId, String guildName) {
        GuildChatFragment fragment = new GuildChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GUILD_ID, guildId);
        args.putString(ARG_GUILD_NAME, guildName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            guildId = getArguments().getLong(ARG_GUILD_ID);
            guildName = getArguments().getString(ARG_GUILD_NAME);
        }
        stompManager = new StompManager(getContext());
        apiService = RetrofitClient.getApiService();
        userRepository = new UserRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guild_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadCurrentUser();
        loadMessages();
        connectWebSocket();
    }

    private void initializeViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnSend = view.findViewById(R.id.btn_send);
        tvGuildName = view.findViewById(R.id.tv_guild_name);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
        etMessage = view.findViewById(R.id.et_message);
        rvMessages = view.findViewById(R.id.rv_messages);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);

        if (guildName != null) {
            tvGuildName.setText(guildName);
        }
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter(currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Start from bottom
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadCurrentUser() {
        userRepository.getCurrentUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserId = user.getId();
                adapter = new ChatMessageAdapter(currentUserId);
                rvMessages.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading current user: " + error);
            }
        });
    }

    private void loadMessages() {
        showLoading(true);

        apiService.getGuildMessages(guildId).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<ChatMessage> messages = response.body();
                            if (messages.isEmpty()) {
                                showEmptyState();
                            } else {
                                hideEmptyState();
                                adapter.setMessages(messages);
                                scrollToBottom();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Failed to load messages");
                        Log.e(TAG, "Error loading messages", t);
                        showEmptyState();
                    });
                }
            }
        });
    }

    private void connectWebSocket() {
        tvConnectionStatus.setText("Connecting...");

        stompManager.connect(new StompManager.ConnectionListener() {
            @Override
            public void onConnected() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvConnectionStatus.setText("Connected");
                        subscribeToGuildChat();
                    });
                }
            }

            @Override
            public void onDisconnected() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvConnectionStatus.setText("Disconnected");
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvConnectionStatus.setText("Connection error");
                        Log.e(TAG, "WebSocket error: " + error);
                    });
                }
            }
        });
    }

    private void subscribeToGuildChat() {
        stompManager.subscribeToGuildChat(guildId, new StompManager.MessageListener() {
            @Override
            public void onMessageReceived(ChatMessage message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        hideEmptyState();
                        adapter.addMessage(message);
                        scrollToBottom();
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Subscription error: " + error);
            }
        });
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();

        if (content.isEmpty()) {
            showToast("Please enter a message");
            return;
        }

        if (!stompManager.isConnected()) {
            showToast("Not connected to chat");
            return;
        }

        if (currentUserId == null) {
            showToast("User not loaded");
            return;
        }

        stompManager.sendMessage(guildId, currentUserId, content);
        etMessage.setText("");
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState() {
        llEmptyState.setVisibility(View.VISIBLE);
        rvMessages.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        llEmptyState.setVisibility(View.GONE);
        rvMessages.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (stompManager != null) {
            stompManager.unsubscribeFromGuild(guildId);
            stompManager.disconnect();
        }
    }
}
