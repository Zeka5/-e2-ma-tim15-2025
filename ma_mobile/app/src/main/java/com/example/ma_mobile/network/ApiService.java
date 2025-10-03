package com.example.ma_mobile.network;

import com.example.ma_mobile.models.User;
import com.example.ma_mobile.models.auth.AuthResponse;
import com.example.ma_mobile.models.auth.LoginRequest;
import com.example.ma_mobile.models.auth.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest registerRequest);

    @GET("users/me")
    Call<User> getCurrentUserProfile();
}
