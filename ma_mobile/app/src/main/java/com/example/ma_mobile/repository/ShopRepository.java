package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.ClothingTemplate;
import com.example.ma_mobile.models.PotionTemplate;
import com.example.ma_mobile.models.PurchaseItemRequest;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopRepository {
    private static final String TAG = "ShopRepository";
    private ApiService apiService;
    private Context context;

    public ShopRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface PotionTemplatesCallback {
        void onSuccess(List<PotionTemplate> potions);
        void onError(String error);
    }

    public interface ClothingTemplatesCallback {
        void onSuccess(List<ClothingTemplate> clothing);
        void onError(String error);
    }

    public interface PurchaseCallback {
        void onSuccess();
        void onError(String error);
    }

    // Get available potions
    public void getAvailablePotions(PotionTemplatesCallback callback) {
        Call<List<PotionTemplate>> call = apiService.getAvailablePotions();

        call.enqueue(new Callback<List<PotionTemplate>>() {
            @Override
            public void onResponse(Call<List<PotionTemplate>> call, Response<List<PotionTemplate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Available potions fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch available potions: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<PotionTemplate>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Get available clothing
    public void getAvailableClothing(ClothingTemplatesCallback callback) {
        Call<List<ClothingTemplate>> call = apiService.getAvailableClothing();

        call.enqueue(new Callback<List<ClothingTemplate>>() {
            @Override
            public void onResponse(Call<List<ClothingTemplate>> call, Response<List<ClothingTemplate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Available clothing fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch available clothing: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<ClothingTemplate>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    // Purchase potion
    public void purchasePotion(Long templateId, Integer quantity, PurchaseCallback callback) {
        PurchaseItemRequest request = new PurchaseItemRequest(templateId, quantity);
        Call<Void> call = apiService.purchasePotion(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Potion purchased successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = getErrorMessageByCode(response.code());
                    Log.e(TAG, "Error " + response.code() + ": " + errorMessage);
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

    // Purchase clothing
    public void purchaseClothing(Long templateId, PurchaseCallback callback) {
        Call<Void> call = apiService.purchaseClothing(templateId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Clothing purchased successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = getErrorMessageByCode(response.code());
                    Log.e(TAG, "Error " + response.code() + ": " + errorMessage);
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
                return "Already own this item or invalid request";
            case 402:
                return "Insufficient coins";
            case 404:
                return "Item not found";
            default:
                return "Failed to purchase item";
        }
    }
}
