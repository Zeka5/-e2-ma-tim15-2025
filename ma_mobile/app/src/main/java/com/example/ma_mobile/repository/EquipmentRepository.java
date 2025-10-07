package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.UserClothing;
import com.example.ma_mobile.models.UserEquipment;
import com.example.ma_mobile.models.UserPotion;
import com.example.ma_mobile.models.UserWeapon;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipmentRepository {
    private static final String TAG = "EquipmentRepository";
    private ApiService apiService;
    private Context context;

    public EquipmentRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface EquipmentCallback {
        void onSuccess(UserEquipment equipment);
        void onError(String error);
    }

    public interface PotionsCallback {
        void onSuccess(List<UserPotion> potions);
        void onError(String error);
    }

    public interface ClothingCallback {
        void onSuccess(List<UserClothing> clothing);
        void onError(String error);
    }

    public interface WeaponsCallback {
        void onSuccess(List<UserWeapon> weapons);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    // Get all equipment
    public void getAllEquipment(EquipmentCallback callback) {
        Call<UserEquipment> call = apiService.getAllEquipment();

        call.enqueue(new Callback<UserEquipment>() {
            @Override
            public void onResponse(Call<UserEquipment> call, Response<UserEquipment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Equipment fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch equipment: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserEquipment> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get potions
    public void getPotions(PotionsCallback callback) {
        Call<List<UserPotion>> call = apiService.getPotions();

        call.enqueue(new Callback<List<UserPotion>>() {
            @Override
            public void onResponse(Call<List<UserPotion>> call, Response<List<UserPotion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Potions fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch potions: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<UserPotion>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get clothing
    public void getClothing(ClothingCallback callback) {
        Call<List<UserClothing>> call = apiService.getClothing();

        call.enqueue(new Callback<List<UserClothing>>() {
            @Override
            public void onResponse(Call<List<UserClothing>> call, Response<List<UserClothing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Clothing fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch clothing: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<UserClothing>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get weapons
    public void getWeapons(WeaponsCallback callback) {
        Call<List<UserWeapon>> call = apiService.getWeapons();

        call.enqueue(new Callback<List<UserWeapon>>() {
            @Override
            public void onResponse(Call<List<UserWeapon>> call, Response<List<UserWeapon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Weapons fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch weapons: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<UserWeapon>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Activate potion
    public void activatePotion(Long potionId, ActionCallback callback) {
        Call<Void> call = apiService.activatePotion(potionId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Potion activated successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to activate potion: " + response.code();
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

    // Deactivate potion
    public void deactivatePotion(Long potionId, ActionCallback callback) {
        Call<Void> call = apiService.deactivatePotion(potionId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Potion deactivated successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to deactivate potion: " + response.code();
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

    // Activate clothing
    public void activateClothing(Long clothingId, ActionCallback callback) {
        Call<Void> call = apiService.activateClothing(clothingId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Clothing activated successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to activate clothing: " + response.code();
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

    // Deactivate clothing
    public void deactivateClothing(Long clothingId, ActionCallback callback) {
        Call<Void> call = apiService.deactivateClothing(clothingId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Clothing deactivated successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = getErrorMessage(response);
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

    // Upgrade weapon
    public void upgradeWeapon(Long weaponId, ActionCallback callback) {
        Call<Void> call = apiService.upgradeWeapon(weaponId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Weapon upgraded successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = getErrorMessage(response);
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

    // Helper method to extract error message from response
    private String getErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                // Try to extract the message from JSON response
                if (errorBody.contains("\"") && errorBody.contains(":")) {
                    // Simple JSON parsing for message field
                    int messageStart = errorBody.indexOf("\"message\"");
                    if (messageStart != -1) {
                        int valueStart = errorBody.indexOf(":", messageStart) + 1;
                        int valueEnd = errorBody.indexOf("\"", valueStart + 2);
                        if (valueEnd != -1) {
                            return errorBody.substring(valueStart + 2, valueEnd);
                        }
                    }
                }
                return errorBody;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error message", e);
        }
        return "Failed with code: " + response.code();
    }
}
