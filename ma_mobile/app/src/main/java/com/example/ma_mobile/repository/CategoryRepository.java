package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.Category;
import com.example.ma_mobile.models.CreateCategoryRequest;
import com.example.ma_mobile.models.UpdateCategoryRequest;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private ApiService apiService;
    private Context context;

    public CategoryRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface CategoryListCallback {
        void onSuccess(List<Category> categories);
        void onError(String error);
    }

    public interface CategoryCallback {
        void onSuccess(Category category);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public void getAllCategories(CategoryListCallback callback) {
        Call<List<Category>> call = apiService.getAllCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Categories fetched successfully: " + response.body().size());
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch categories: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getCategoryById(Long id, CategoryCallback callback) {
        Call<Category> call = apiService.getCategoryById(id);

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Category fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch category: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void createCategory(CreateCategoryRequest request, CategoryCallback callback) {
        Call<Category> call = apiService.createCategory(request);

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Category created successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to create category: " + response.code();
                    if (response.code() == 400) {
                        errorMessage = "Category with this color already exists";
                    }
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void updateCategory(Long id, UpdateCategoryRequest request, CategoryCallback callback) {
        Call<Category> call = apiService.updateCategory(id, request);

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Category updated successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to update category: " + response.code();
                    if (response.code() == 400) {
                        errorMessage = "Category with this color already exists";
                    }
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void deleteCategory(Long id, DeleteCallback callback) {
        Call<Void> call = apiService.deleteCategory(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Category deleted successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to delete category: " + response.code();
                    if (response.code() == 400) {
                        errorMessage = "Cannot delete category with active tasks";
                    }
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
