package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.User;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

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
}
