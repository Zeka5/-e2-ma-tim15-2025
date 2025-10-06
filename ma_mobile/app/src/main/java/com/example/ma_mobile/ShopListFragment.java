package com.example.ma_mobile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.ClothingTemplateAdapter;
import com.example.ma_mobile.adapter.PotionTemplateAdapter;
import com.example.ma_mobile.models.ClothingTemplate;
import com.example.ma_mobile.models.PotionTemplate;
import com.example.ma_mobile.repository.ShopRepository;

import java.util.List;

public class ShopListFragment extends Fragment
        implements PotionTemplateAdapter.OnPurchaseListener, ClothingTemplateAdapter.OnPurchaseListener {

    private static final String TAG = "ShopListFragment";
    private static final String ARG_TYPE = "type";

    public static final int TYPE_POTIONS = 0;
    public static final int TYPE_CLOTHING = 1;

    private RecyclerView recyclerView;
    private LinearLayout llEmptyState;

    private int type;
    private ShopRepository shopRepository;

    private PotionTemplateAdapter potionAdapter;
    private ClothingTemplateAdapter clothingAdapter;

    public ShopListFragment() {
        // Required empty public constructor
    }

    public static ShopListFragment newInstance(int type) {
        ShopListFragment fragment = new ShopListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }
        shopRepository = new ShopRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_shop_list);
        llEmptyState = view.findViewById(R.id.ll_empty_state);

        setupRecyclerView();
        loadShopItems();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        switch (type) {
            case TYPE_POTIONS:
                potionAdapter = new PotionTemplateAdapter(this);
                recyclerView.setAdapter(potionAdapter);
                break;
            case TYPE_CLOTHING:
                clothingAdapter = new ClothingTemplateAdapter(this);
                recyclerView.setAdapter(clothingAdapter);
                break;
        }
    }

    private void loadShopItems() {
        switch (type) {
            case TYPE_POTIONS:
                loadPotions();
                break;
            case TYPE_CLOTHING:
                loadClothing();
                break;
        }
    }

    private void loadPotions() {
        shopRepository.getAvailablePotions(new ShopRepository.PotionTemplatesCallback() {
            @Override
            public void onSuccess(List<PotionTemplate> potions) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (potions.isEmpty()) {
                            showEmptyState();
                        } else {
                            hideEmptyState();
                            potionAdapter.setPotions(potions);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to load potions: " + error);
                        Log.e(TAG, "Error loading potions: " + error);
                        showEmptyState();
                    });
                }
            }
        });
    }

    private void loadClothing() {
        shopRepository.getAvailableClothing(new ShopRepository.ClothingTemplatesCallback() {
            @Override
            public void onSuccess(List<ClothingTemplate> clothing) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (clothing.isEmpty()) {
                            showEmptyState();
                        } else {
                            hideEmptyState();
                            clothingAdapter.setClothingList(clothing);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to load clothing: " + error);
                        Log.e(TAG, "Error loading clothing: " + error);
                        showEmptyState();
                    });
                }
            }
        });
    }

    @Override
    public void onPurchasePotion(PotionTemplate potion) {
        showPurchaseQuantityDialog(potion);
    }

    private void showPurchaseQuantityDialog(PotionTemplate potion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Purchase " + potion.getName());

        final EditText input = new EditText(getContext());
        input.setHint("Quantity");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText("1");
        builder.setView(input);

        builder.setPositiveButton("Purchase", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            if (!quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity > 0) {
                    purchasePotion(potion, quantity);
                } else {
                    showToast("Quantity must be greater than 0");
                }
            } else {
                showToast("Please enter a quantity");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void purchasePotion(PotionTemplate potion, int quantity) {
        shopRepository.purchasePotion(potion.getId(), quantity, new ShopRepository.PurchaseCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Potion purchased successfully!");
                        // Refresh parent fragment coins
                        if (getParentFragment() instanceof ShopFragment) {
                            ((ShopFragment) getParentFragment()).refreshCoins();
                        }
                        loadPotions(); // Refresh list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Purchase failed: " + error);
                        Log.e(TAG, "Error purchasing potion: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onPurchaseClothing(ClothingTemplate clothing) {
        showPurchaseConfirmationDialog(clothing);
    }

    private void showPurchaseConfirmationDialog(ClothingTemplate clothing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Purchase " + clothing.getName());
        builder.setMessage("Price: " + clothing.getCalculatedPrice() + " coins\n\nConfirm purchase?");

        builder.setPositiveButton("Purchase", (dialog, which) -> purchaseClothing(clothing));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void purchaseClothing(ClothingTemplate clothing) {
        shopRepository.purchaseClothing(clothing.getId(), new ShopRepository.PurchaseCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Clothing purchased successfully!");
                        // Refresh parent fragment coins
                        if (getParentFragment() instanceof ShopFragment) {
                            ((ShopFragment) getParentFragment()).refreshCoins();
                        }
                        loadClothing(); // Refresh list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Purchase failed: " + error);
                        Log.e(TAG, "Error purchasing clothing: " + error);
                    });
                }
            }
        });
    }

    private void showEmptyState() {
        llEmptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        llEmptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
