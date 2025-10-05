package com.example.ma_mobile.network;

import com.example.ma_mobile.models.Category;
import com.example.ma_mobile.models.CreateCategoryRequest;
import com.example.ma_mobile.models.CreateTaskRequest;
import com.example.ma_mobile.models.Task;
import com.example.ma_mobile.models.TaskInstance;
import com.example.ma_mobile.models.UpdateCategoryRequest;
import com.example.ma_mobile.models.FriendRequest;
import com.example.ma_mobile.models.Guild;
import com.example.ma_mobile.models.GuildInvite;
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
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest registerRequest);

    @GET("users/me")
    Call<User> getCurrentUserProfile();

    // Category endpoints
    @GET("categories")
    Call<List<Category>> getAllCategories();

    @GET("categories/{id}")
    Call<Category> getCategoryById(@Path("id") Long id);

    @POST("categories")
    Call<Category> createCategory(@Body CreateCategoryRequest request);

    @PUT("categories/{id}")
    Call<Category> updateCategory(@Path("id") Long id, @Body UpdateCategoryRequest request);

    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") Long id);

    // Task endpoints
    @GET("tasks")
    Call<List<Task>> getAllTasks();

    @GET("tasks/status/{status}")
    Call<List<Task>> getTasksByStatus(@Path("status") String status);

    @GET("tasks/date-range")
    Call<List<Task>> getTasksByDateRange(@Query("start") String start, @Query("end") String end);

    @GET("tasks/{id}")
    Call<Task> getTaskById(@Path("id") Long id);

    @POST("tasks")
    Call<Task> createTask(@Body CreateTaskRequest request);

    @PUT("tasks/{id}")
    Call<Task> updateTask(@Path("id") Long id, @Body CreateTaskRequest request);

    @PUT("tasks/{id}/complete")
    Call<Task> completeTask(@Path("id") Long id);

    @DELETE("tasks/{id}")
    Call<Void> deleteTask(@Path("id") Long id);

    // Task Instance endpoints
    @GET("task-instances/task/{taskId}")
    Call<List<TaskInstance>> getTaskInstances(@Path("taskId") Long taskId);

    @PUT("task-instances/{id}/complete")
    Call<TaskInstance> completeTaskInstance(@Path("id") Long id);

    @DELETE("task-instances/{id}")
    Call<Void> deleteTaskInstance(@Path("id") Long id);
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

    // Guild endpoints
    @POST("api/guilds")
    Call<Guild> createGuild(@Query("name") String name);

    @GET("api/guilds")
    Call<List<Guild>> getAllGuilds();

    @GET("api/guilds/search")
    Call<List<Guild>> searchGuilds(@Query("name") String name);

    @GET("api/guilds/{guildId}")
    Call<Guild> getGuildById(@Path("guildId") Long guildId);

    @DELETE("api/guilds/{guildId}")
    Call<Void> deleteGuild(@Path("guildId") Long guildId);

    @POST("api/guilds/{guildId}/invite/{userId}")
    Call<GuildInvite> inviteToGuild(@Path("guildId") Long guildId, @Path("userId") Long userId);

    @POST("api/guilds/invites/{inviteId}/accept")
    Call<GuildInvite> acceptGuildInvite(@Path("inviteId") Long inviteId);

    @DELETE("api/guilds/invites/{inviteId}/reject")
    Call<Void> rejectGuildInvite(@Path("inviteId") Long inviteId);

    @GET("api/guilds/invites/pending")
    Call<List<GuildInvite>> getPendingGuildInvites();

    @POST("api/guilds/leave")
    Call<Void> leaveGuild();
}
