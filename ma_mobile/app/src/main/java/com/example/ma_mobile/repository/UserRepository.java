package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.ChangePasswordRequest;
import com.example.ma_mobile.models.PublicUserProfile;
import com.example.ma_mobile.models.PurchaseItemRequest;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private ApiService apiService;
    private Context context;

    public UserRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface UserProfileCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface SearchUsersCallback {
        void onSuccess(List<PublicUserProfile> users);
        void onError(String error);
    }

    public interface PublicProfileCallback {
        void onSuccess(PublicUserProfile user);
        void onError(String error);
    }

    public interface PasswordChangeCallback {
        void onSuccess();
        void onError(String error);
    }
    public void getCurrentUserProfile(UserProfileCallback callback) {
        Call<User> call = apiService.getCurrentUserProfile();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Profile fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch profile: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void searchUsers(String username, SearchUsersCallback callback) {
        Call<List<PublicUserProfile>> call = apiService.searchUsers(username);

        call.enqueue(new Callback<List<PublicUserProfile>>() {
            @Override
            public void onResponse(Call<List<PublicUserProfile>> call, Response<List<PublicUserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Users searched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to search users: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<PublicUserProfile>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getPublicProfile(Long userId, PublicProfileCallback callback) {
        Call<PublicUserProfile> call = apiService.getPublicProfile(userId);

        call.enqueue(new Callback<PublicUserProfile>() {
            @Override
            public void onResponse(Call<PublicUserProfile> call, Response<PublicUserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Public profile fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch public profile: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PublicUserProfile> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void changePassword(String currentPassword, String newPassword, PasswordChangeCallback callback) {
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
        Call<Void> call = apiService.changePassword(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Password changed successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed: " + response.message();
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
