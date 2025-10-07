package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.UserClothingAdapter;
import com.example.ma_mobile.adapter.UserPotionAdapter;
import com.example.ma_mobile.adapter.UserWeaponAdapter;
import com.example.ma_mobile.models.UserClothing;
import com.example.ma_mobile.models.UserPotion;
import com.example.ma_mobile.models.UserWeapon;
import com.example.ma_mobile.repository.EquipmentRepository;

import java.util.List;

public class EquipmentListFragment extends Fragment
        implements UserPotionAdapter.OnPotionActionListener, UserClothingAdapter.OnClothingActionListener,
        UserWeaponAdapter.OnWeaponActionListener {

    private static final String TAG = "EquipmentListFragment";
    private static final String ARG_TYPE = "type";

    public static final int TYPE_POTIONS = 0;
    public static final int TYPE_CLOTHING = 1;
    public static final int TYPE_WEAPONS = 2;

    private RecyclerView recyclerView;
    private LinearLayout llEmptyState;

    private int type;
    private EquipmentRepository equipmentRepository;

    private UserPotionAdapter potionAdapter;
    private UserClothingAdapter clothingAdapter;
    private UserWeaponAdapter weaponAdapter;

    public EquipmentListFragment() {
        // Required empty public constructor
    }

    public static EquipmentListFragment newInstance(int type) {
        EquipmentListFragment fragment = new EquipmentListFragment();
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
        equipmentRepository = new EquipmentRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equipment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_equipment_list);
        llEmptyState = view.findViewById(R.id.ll_empty_state);

        setupRecyclerView();
        loadEquipment();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        switch (type) {
            case TYPE_POTIONS:
                potionAdapter = new UserPotionAdapter(this);
                recyclerView.setAdapter(potionAdapter);
                break;
            case TYPE_CLOTHING:
                clothingAdapter = new UserClothingAdapter(this);
                recyclerView.setAdapter(clothingAdapter);
                break;
            case TYPE_WEAPONS:
                weaponAdapter = new UserWeaponAdapter(this);
                recyclerView.setAdapter(weaponAdapter);
                break;
        }
    }

    public void loadEquipment() {
        switch (type) {
            case TYPE_POTIONS:
                loadPotions();
                break;
            case TYPE_CLOTHING:
                loadClothing();
                break;
            case TYPE_WEAPONS:
                loadWeapons();
                break;
        }
    }

    private void loadPotions() {
        equipmentRepository.getPotions(new EquipmentRepository.PotionsCallback() {
            @Override
            public void onSuccess(List<UserPotion> potions) {
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
        equipmentRepository.getClothing(new EquipmentRepository.ClothingCallback() {
            @Override
            public void onSuccess(List<UserClothing> clothing) {
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

    private void loadWeapons() {
        equipmentRepository.getWeapons(new EquipmentRepository.WeaponsCallback() {
            @Override
            public void onSuccess(List<UserWeapon> weapons) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (weapons.isEmpty()) {
                            showEmptyState();
                        } else {
                            hideEmptyState();
                            weaponAdapter.setWeapons(weapons);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to load weapons: " + error);
                        Log.e(TAG, "Error loading weapons: " + error);
                        showEmptyState();
                    });
                }
            }
        });
    }

    @Override
    public void onActivatePotion(UserPotion potion) {
        equipmentRepository.activatePotion(potion.getId(), new EquipmentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Potion activated!");
                        loadPotions(); // Refresh list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to activate potion: " + error);
                        Log.e(TAG, "Error activating potion: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onDeactivatePotion(UserPotion potion) {
        equipmentRepository.deactivatePotion(potion.getId(), new EquipmentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Potion deactivated!");
                        loadPotions(); // Refresh list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to deactivate potion: " + error);
                        Log.e(TAG, "Error deactivating potion: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onActivateClothing(UserClothing clothing) {
        equipmentRepository.activateClothing(clothing.getId(), new EquipmentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Clothing activated!");
                        loadClothing(); // Refresh list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to activate clothing: " + error);
                        Log.e(TAG, "Error activating clothing: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onDeactivateClothing(UserClothing clothing) {
        equipmentRepository.deactivateClothing(clothing.getId(), new EquipmentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Clothing deactivated!");
                        loadClothing(); // Refresh list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to deactivate clothing: " + error);
                        Log.e(TAG, "Error deactivating clothing: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onUpgradeWeapon(UserWeapon weapon) {
        equipmentRepository.upgradeWeapon(weapon.getId(), new EquipmentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Weapon upgraded successfully!");
                        loadWeapons(); // Refresh list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast(error);
                        Log.e(TAG, "Error upgrading weapon: " + error);
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
