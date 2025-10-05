package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.Guild;
import com.example.ma_mobile.models.GuildInvite;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuildRepository {
    private static final String TAG = "GuildRepository";
    private ApiService apiService;
    private Context context;

    public GuildRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface GuildCallback {
        void onSuccess(Guild guild);
        void onError(String error);
    }

    public interface GuildListCallback {
        void onSuccess(List<Guild> guilds);
        void onError(String error);
    }

    public interface GuildInviteCallback {
        void onSuccess(GuildInvite invite);
        void onError(String error);
    }

    public interface GuildInviteListCallback {
        void onSuccess(List<GuildInvite> invites);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    // Create guild
    public void createGuild(String name, GuildCallback callback) {
        Call<Guild> call = apiService.createGuild(name);

        call.enqueue(new Callback<Guild>() {
            @Override
            public void onResponse(Call<Guild> call, Response<Guild> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guild created successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessageByCode(response.code());
                    Log.e(TAG, "Error " + response.code() + ": " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Guild> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get all guilds
    public void getAllGuilds(GuildListCallback callback) {
        Call<List<Guild>> call = apiService.getAllGuilds();

        call.enqueue(new Callback<List<Guild>>() {
            @Override
            public void onResponse(Call<List<Guild>> call, Response<List<Guild>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guilds fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch guilds: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Guild>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Search guilds
    public void searchGuilds(String name, GuildListCallback callback) {
        Call<List<Guild>> call = apiService.searchGuilds(name);

        call.enqueue(new Callback<List<Guild>>() {
            @Override
            public void onResponse(Call<List<Guild>> call, Response<List<Guild>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guilds search completed successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to search guilds: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Guild>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get guild by ID
    public void getGuildById(Long guildId, GuildCallback callback) {
        Call<Guild> call = apiService.getGuildById(guildId);

        call.enqueue(new Callback<Guild>() {
            @Override
            public void onResponse(Call<Guild> call, Response<Guild> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guild fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch guild: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Guild> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Delete guild
    public void deleteGuild(Long guildId, ActionCallback callback) {
        Call<Void> call = apiService.deleteGuild(guildId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Guild deleted successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to delete guild: " + response.code();
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

    // Invite user to guild
    public void inviteToGuild(Long guildId, Long userId, GuildInviteCallback callback) {
        Call<GuildInvite> call = apiService.inviteToGuild(guildId, userId);

        call.enqueue(new Callback<GuildInvite>() {
            @Override
            public void onResponse(Call<GuildInvite> call, Response<GuildInvite> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guild invite sent successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessageByCode(response.code());
                    Log.e(TAG, "Error " + response.code() + ": " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<GuildInvite> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Accept guild invite
    public void acceptGuildInvite(Long inviteId, GuildInviteCallback callback) {
        Call<GuildInvite> call = apiService.acceptGuildInvite(inviteId);

        call.enqueue(new Callback<GuildInvite>() {
            @Override
            public void onResponse(Call<GuildInvite> call, Response<GuildInvite> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Guild invite accepted");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to accept invite: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<GuildInvite> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Reject guild invite
    public void rejectGuildInvite(Long inviteId, ActionCallback callback) {
        Call<Void> call = apiService.rejectGuildInvite(inviteId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Guild invite rejected");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to reject invite: " + response.code();
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

    // Get pending guild invites
    public void getPendingGuildInvites(GuildInviteListCallback callback) {
        Call<List<GuildInvite>> call = apiService.getPendingGuildInvites();

        call.enqueue(new Callback<List<GuildInvite>>() {
            @Override
            public void onResponse(Call<List<GuildInvite>> call, Response<List<GuildInvite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Pending guild invites fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch invites: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<GuildInvite>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Leave guild
    public void leaveGuild(ActionCallback callback) {
        Call<Void> call = apiService.leaveGuild();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Left guild successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to leave guild: " + response.code();
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

    private String getErrorMessageByCode(int code) {
        switch (code) {
            case 400:
                return "Invalid request or already in a guild";
            case 404:
                return "Guild or user not found";
            case 409:
                return "Guild invite already exists";
            default:
                return "Operation failed";
        }
    }
}
