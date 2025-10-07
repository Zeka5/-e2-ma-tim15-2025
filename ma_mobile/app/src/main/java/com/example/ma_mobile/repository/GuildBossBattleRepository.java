package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.GuildBossBattle;
import com.example.ma_mobile.models.GuildBossMissionProgress;
import com.example.ma_mobile.models.GuildBossMissionSummary;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuildBossBattleRepository {
    private static final String TAG = "GuildBossBattleRepo";
    private ApiService apiService;
    private Context context;

    public GuildBossBattleRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface BattleCallback {
        void onSuccess(GuildBossBattle battle);
        void onError(String error);
    }

    public interface ProgressCallback {
        void onSuccess(GuildBossMissionProgress progress);
        void onError(String error);
    }

    public interface SummaryCallback {
        void onSuccess(GuildBossMissionSummary summary);
        void onError(String error);
    }

    public interface BattleListCallback {
        void onSuccess(List<GuildBossBattle> battles);
        void onError(String error);
    }

    // Start guild boss battle (leader only)
    public void startGuildBossBattle(Long guildId, BattleCallback callback) {
        Log.d(TAG, "startGuildBossBattle - guildId: " + guildId);
        Call<GuildBossBattle> call = apiService.startGuildBossBattle(guildId);
        Log.d(TAG, "Start battle request URL: " + call.request().url());

        call.enqueue(new Callback<GuildBossBattle>() {
            @Override
            public void onResponse(Call<GuildBossBattle> call, Response<GuildBossBattle> response) {
                Log.d(TAG, "Start battle response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guild boss battle started successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    String errorMessage = "Failed to start battle: " + response.code() + " - " + errorBody;
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<GuildBossBattle> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get active guild boss battle
    public void getActiveGuildBossBattle(Long guildId, BattleCallback callback) {
        Log.d(TAG, "getActiveGuildBossBattle - guildId: " + guildId);
        Call<GuildBossBattle> call = apiService.getActiveGuildBossBattle(guildId);
        Log.d(TAG, "Request URL: " + call.request().url());

        call.enqueue(new Callback<GuildBossBattle>() {
            @Override
            public void onResponse(Call<GuildBossBattle> call, Response<GuildBossBattle> response) {
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response message: " + response.message());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "Active guild boss battle fetched successfully");
                        callback.onSuccess(response.body());
                    } else if (response.code() == 204) {
                        // No active battle
                        Log.d(TAG, "No active guild boss battle (204)");
                        callback.onSuccess(null);
                    } else {
                        Log.e(TAG, "Response successful but body is null");
                        callback.onError("Failed to fetch battle");
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    String errorMessage = "Failed to fetch battle: " + response.code() + " - " + errorBody;
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<GuildBossBattle> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get guild boss battle progress
    public void getGuildBossBattleProgress(Long guildId, SummaryCallback callback) {
        Log.d(TAG, "getGuildBossBattleProgress - guildId: " + guildId);
        Call<GuildBossMissionSummary> call = apiService.getGuildBossBattleProgress(guildId);
        Log.d(TAG, "Request URL: " + call.request().url());

        call.enqueue(new Callback<GuildBossMissionSummary>() {
            @Override
            public void onResponse(Call<GuildBossMissionSummary> call, Response<GuildBossMissionSummary> response) {
                Log.d(TAG, "Progress response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guild boss battle progress fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    String errorMessage = "Failed to fetch progress: " + response.code() + " - " + errorBody;
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<GuildBossMissionSummary> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get my progress
    public void getMyProgress(Long guildId, ProgressCallback callback) {
        Call<GuildBossMissionProgress> call = apiService.getMyProgress(guildId);

        call.enqueue(new Callback<GuildBossMissionProgress>() {
            @Override
            public void onResponse(Call<GuildBossMissionProgress> call, Response<GuildBossMissionProgress> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "My progress fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch my progress: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<GuildBossMissionProgress> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get guild boss battle history
    public void getGuildBossBattleHistory(Long guildId, BattleListCallback callback) {
        Call<List<GuildBossBattle>> call = apiService.getGuildBossBattleHistory(guildId);

        call.enqueue(new Callback<List<GuildBossBattle>>() {
            @Override
            public void onResponse(Call<List<GuildBossBattle>> call, Response<List<GuildBossBattle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guild boss battle history fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch history: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<GuildBossBattle>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }
}
