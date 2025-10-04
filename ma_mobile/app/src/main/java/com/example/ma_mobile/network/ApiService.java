package com.example.ma_mobile.network;

import com.example.ma_mobile.models.FriendRequest;
import com.example.ma_mobile.models.PublicUserProfile;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.models.auth.AuthResponse;
import com.example.ma_mobile.models.auth.LoginRequest;
import com.example.ma_mobile.models.auth.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest registerRequest);

    @GET("users/me")
    Call<User> getCurrentUserProfile();

    // User search endpoints
    @GET("users/search")
    Call<List<PublicUserProfile>> searchUsers(@Query("username") String username);

    @GET("users/profile/{userId}")
    Call<PublicUserProfile> getPublicProfile(@Path("userId") Long userId);

    // Friend request endpoints
    @POST("api/friends/request/{receiverId}")
    Call<FriendRequest> sendFriendRequest(@Path("receiverId") Long receiverId);

    @POST("api/friends/request/{requestId}/accept")
    Call<FriendRequest> acceptFriendRequest(@Path("requestId") Long requestId);

    @DELETE("api/friends/request/{requestId}/reject")
    Call<Void> rejectFriendRequest(@Path("requestId") Long requestId);

    @GET("api/friends/requests/pending")
    Call<List<FriendRequest>> getPendingRequests();

    @GET("api/friends/requests/received")
    Call<List<FriendRequest>> getReceivedRequests();

    @GET("api/friends")
    Call<List<User>> getFriends();

    @DELETE("api/friends/{friendId}")
    Call<Void> removeFriend(@Path("friendId") Long friendId);
}
