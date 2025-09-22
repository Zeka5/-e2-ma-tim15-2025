package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.User;
import com.example.ma_mobile.models.auth.AuthResponse;
import com.example.ma_mobile.models.auth.LoginRequest;
import com.example.ma_mobile.models.auth.RegisterRequest;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;
import com.example.ma_mobile.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private ApiService apiService;
    private SessionManager sessionManager;

    public AuthRepository(Context context) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(context);
    }

    public void login(LoginRequest loginRequest, AuthCallback<AuthResponse> callback) {
        Call<AuthResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    if (authResponse.getUser() != null && authResponse.getToken() != null) {
                        sessionManager.saveUserSession(authResponse.getToken(), authResponse.getUser());
                        callback.onSuccess(authResponse);
                    } else {
                        callback.onError("Login failed - missing user data");
                    }
                } else {
                    Log.e("AuthRepository", "Response not successful or body is null");
                    callback.onError("Login failed. Please check your credentials.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("AuthRepository", "onFailure called: " + t.getMessage());
                Log.e("AuthRepository", "Stack trace: ", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(RegisterRequest registerRequest, AuthCallback<User> callback) {

        Call<User> call = apiService.register(registerRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onError("Registration failed - missing user data");
                    }
                } else {
                    callback.onError("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public User getCurrentUser() {
        return sessionManager.getUser();
    }

    public void clearSession() {
        sessionManager.clearSession();
    }

    public interface AuthCallback<T> {
        void onSuccess(T response);
        void onError(String error);
    }
}
