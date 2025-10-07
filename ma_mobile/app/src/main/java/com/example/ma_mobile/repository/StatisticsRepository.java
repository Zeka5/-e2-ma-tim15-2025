package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.UserStatistics;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsRepository {
    private static final String TAG = "StatisticsRepository";
    private final ApiService apiService;
    private Context context;

    public StatisticsRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface StatisticsCallback {
        void onSuccess(UserStatistics statistics);
        void onError(String error);
    }

    public void getUserStatistics(StatisticsCallback callback) {
        Call<UserStatistics> call = apiService.getUserStatistics();

        call.enqueue(new Callback<UserStatistics>() {
            @Override
            public void onResponse(Call<UserStatistics> call, Response<UserStatistics> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Statistics fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch statistics: " + response.message();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserStatistics> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }
}
