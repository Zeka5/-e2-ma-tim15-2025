package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.FriendRequest;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRepository {
    private static final String TAG = "FriendRepository";
    private ApiService apiService;
    private Context context;

    public FriendRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface FriendRequestCallback {
        void onSuccess(FriendRequest friendRequest);
        void onError(String error);
    }

    public interface FriendRequestListCallback {
        void onSuccess(List<FriendRequest> requests);
        void onError(String error);
    }

    public interface FriendsListCallback {
        void onSuccess(List<User> friends);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    // Send friend request
    public void sendFriendRequest(Long receiverId, FriendRequestCallback callback) {
        Call<FriendRequest> call = apiService.sendFriendRequest(receiverId);

        call.enqueue(new Callback<FriendRequest>() {
            @Override
            public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Friend request sent successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to send friend request: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<FriendRequest> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get pending requests (sent by current user)
    public void getPendingRequests(FriendRequestListCallback callback) {
        Call<List<FriendRequest>> call = apiService.getPendingRequests();

        call.enqueue(new Callback<List<FriendRequest>>() {
            @Override
            public void onResponse(Call<List<FriendRequest>> call, Response<List<FriendRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Pending requests fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch pending requests: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<FriendRequest>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get received requests (incoming friend requests)
    public void getReceivedRequests(FriendRequestListCallback callback) {
        Call<List<FriendRequest>> call = apiService.getReceivedRequests();

        call.enqueue(new Callback<List<FriendRequest>>() {
            @Override
            public void onResponse(Call<List<FriendRequest>> call, Response<List<FriendRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Received requests fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch received requests: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<FriendRequest>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get friends list
    public void getFriends(FriendsListCallback callback) {
        Call<List<User>> call = apiService.getFriends();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Friends list fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch friends: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Accept friend request
    public void acceptFriendRequest(Long requestId, FriendRequestCallback callback) {
        Call<FriendRequest> call = apiService.acceptFriendRequest(requestId);

        call.enqueue(new Callback<FriendRequest>() {
            @Override
            public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Friend request accepted");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to accept request: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<FriendRequest> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Reject friend request
    public void rejectFriendRequest(Long requestId, ActionCallback callback) {
        Call<Void> call = apiService.rejectFriendRequest(requestId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Friend request rejected");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to reject request: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Remove friend
    public void removeFriend(Long friendId, ActionCallback callback) {
        Call<Void> call = apiService.removeFriend(friendId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Friend removed");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to remove friend: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }
}
