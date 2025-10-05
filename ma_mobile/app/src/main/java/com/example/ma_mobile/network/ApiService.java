package com.example.ma_mobile.network;

import com.example.ma_mobile.models.Category;
import com.example.ma_mobile.models.CreateCategoryRequest;
import com.example.ma_mobile.models.CreateTaskRequest;
import com.example.ma_mobile.models.Task;
import com.example.ma_mobile.models.UpdateCategoryRequest;
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
}
