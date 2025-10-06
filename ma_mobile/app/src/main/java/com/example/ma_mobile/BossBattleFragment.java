package com.example.ma_mobile;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.models.AttackRequest;
import com.example.ma_mobile.models.AttackResponse;
import com.example.ma_mobile.models.Boss;
import com.example.ma_mobile.models.BossBattle;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BossBattleFragment extends Fragment {

    private TextView tvBossName, tvBossLevel, tvBossDescription, tvBossHp;
    private TextView tvUserPp, tvSuccessRate, tvAttacksRemaining;
    private TextView tvActiveEquipment, tvBattleResult;
    private ImageView ivBossImage;
    private ProgressBar pbBossHp, progressLoading;
    private MaterialButton btnAttack, btnStartBattle, btnSelectEquipment;

    private ApiService apiService;
    private BossBattle currentBattle;
    private Boss currentBoss;
    private List<Long> selectedEquipmentIds = new ArrayList<>();

    public BossBattleFragment() {
        // Required empty public constructor
    }

    public static BossBattleFragment newInstance() {
        return new BossBattleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getApiService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_boss_battle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
        loadBossData();
    }

    private void initializeViews(View view) {
        tvBossName = view.findViewById(R.id.tv_boss_name);
        tvBossLevel = view.findViewById(R.id.tv_boss_level);
        tvBossDescription = view.findViewById(R.id.tv_boss_description);
        tvBossHp = view.findViewById(R.id.tv_boss_hp);
        tvUserPp = view.findViewById(R.id.tv_user_pp);
        tvSuccessRate = view.findViewById(R.id.tv_success_rate);
        tvAttacksRemaining = view.findViewById(R.id.tv_attacks_remaining);
        tvActiveEquipment = view.findViewById(R.id.tv_active_equipment);
        tvBattleResult = view.findViewById(R.id.tv_battle_result);
        ivBossImage = view.findViewById(R.id.iv_boss_image);
        pbBossHp = view.findViewById(R.id.pb_boss_hp);
        progressLoading = view.findViewById(R.id.progress_loading);
        btnAttack = view.findViewById(R.id.btn_attack);
        btnStartBattle = view.findViewById(R.id.btn_start_battle);
        btnSelectEquipment = view.findViewById(R.id.btn_select_equipment);
    }

    private void setupClickListeners() {
        btnStartBattle.setOnClickListener(v -> startBattle());
        btnAttack.setOnClickListener(v -> performAttack());
        btnSelectEquipment.setOnClickListener(v -> showEquipmentSelection());
    }

    private void loadBossData() {
        showLoading(true);

        // First, check if there's an active battle
        apiService.getCurrentBattle().enqueue(new Callback<BossBattle>() {
            @Override
            public void onResponse(Call<BossBattle> call, Response<BossBattle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Active battle exists
                    currentBattle = response.body();
                    currentBoss = currentBattle.getBoss();
                    displayBattle();
                    showBattleUI();
                } else {
                    // No active battle, load next boss
                    loadNextBoss();
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<BossBattle> call, Throwable t) {
                showLoading(false);
                showError("Failed to load battle: " + t.getMessage());
            }
        });
    }

    private void loadNextBoss() {
        apiService.getNextBoss().enqueue(new Callback<Boss>() {
            @Override
            public void onResponse(Call<Boss> call, Response<Boss> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBoss = response.body();
                    displayBossInfo();
                    showStartBattleUI();
                } else {
                    showError("No boss available");
                }
            }

            @Override
            public void onFailure(Call<Boss> call, Throwable t) {
                showError("Failed to load boss: " + t.getMessage());
            }
        });
    }

    private void startBattle() {
        showLoading(true);

        apiService.startBattle().enqueue(new Callback<BossBattle>() {
            @Override
            public void onResponse(Call<BossBattle> call, Response<BossBattle> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentBattle = response.body();
                    currentBoss = currentBattle.getBoss();
                    displayBattle();
                    showBattleUI();
                    Toast.makeText(requireContext(), "Battle started!", Toast.LENGTH_SHORT).show();
                } else {
                    showError("Failed to start battle");
                }
            }

            @Override
            public void onFailure(Call<BossBattle> call, Throwable t) {
                showLoading(false);
                showError("Failed to start battle: " + t.getMessage());
            }
        });
    }

    private void performAttack() {
        if (currentBattle == null) {
            showError("No active battle");
            return;
        }

        btnAttack.setEnabled(false);
        showLoading(true);

        AttackRequest request = new AttackRequest(
            currentBattle.getId(),
            selectedEquipmentIds
        );

        apiService.attack(request).enqueue(new Callback<AttackResponse>() {
            @Override
            public void onResponse(Call<AttackResponse> call, Response<AttackResponse> response) {
                showLoading(false);
                btnAttack.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    handleAttackResponse(response.body());
                } else {
                    showError("Attack failed");
                }
            }

            @Override
            public void onFailure(Call<AttackResponse> call, Throwable t) {
                showLoading(false);
                btnAttack.setEnabled(true);
                showError("Attack failed: " + t.getMessage());
            }
        });
    }

    private void handleAttackResponse(AttackResponse response) {
        // Update UI with attack result
        if (response.getHit()) {
            showAttackAnimation(true);
            Toast.makeText(requireContext(),
                "Hit! Dealt " + response.getDamageDealt() + " damage!",
                Toast.LENGTH_SHORT).show();
        } else {
            showAttackAnimation(false);
            Toast.makeText(requireContext(),
                "Miss! Attack failed!",
                Toast.LENGTH_SHORT).show();
        }

        // Update boss HP
        updateBossHp(response.getBossCurrentHp());
        tvAttacksRemaining.setText(String.valueOf(response.getAttacksRemaining()));

        // Check if battle is complete
        if (response.getBattleComplete()) {
            handleBattleComplete(response);
        }
    }

    private void handleBattleComplete(AttackResponse response) {
        btnAttack.setEnabled(false);

        if ("WON".equals(response.getBattleResult())) {
            tvBattleResult.setText("VICTORY! Boss Defeated!");
            tvBattleResult.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            showRewardDialog(response, true);
        } else {
            tvBattleResult.setText("DEFEAT! Boss survived!");
            tvBattleResult.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            if (response.getRewards() != null && response.getRewards().getCoinsEarned() > 0) {
                showRewardDialog(response, false);
            } else {
                // No rewards, reload boss
                tvBattleResult.append("\nNo rewards earned. Try again!");
                btnStartBattle.setVisibility(View.VISIBLE);
                btnAttack.setVisibility(View.GONE);
            }
        }
        tvBattleResult.setVisibility(View.VISIBLE);
    }

    private void showRewardDialog(AttackResponse response, boolean victory) {
        RewardChestDialog dialog = RewardChestDialog.newInstance(
            victory,
            response.getRewards()
        );
        dialog.setOnDismissListener(() -> {
            // After claiming rewards, reload the next boss
            loadBossData();
        });
        dialog.show(getChildFragmentManager(), "RewardChestDialog");
    }

    private void showEquipmentSelection() {
        EquipmentSelectionDialog dialog = EquipmentSelectionDialog.newInstance(selectedEquipmentIds);
        dialog.setOnEquipmentSelectedListener(equipmentIds -> {
            selectedEquipmentIds = equipmentIds;
            updateEquipmentDisplay();
        });
        dialog.show(getChildFragmentManager(), "EquipmentSelectionDialog");
    }

    private void updateEquipmentDisplay() {
        if (selectedEquipmentIds.isEmpty()) {
            tvActiveEquipment.setText("No equipment selected");
        } else {
            tvActiveEquipment.setText(selectedEquipmentIds.size() + " equipment item(s) selected");
        }
    }

    private void displayBossInfo() {
        if (currentBoss == null) return;

        tvBossName.setText(currentBoss.getName());
        tvBossLevel.setText("Level " + currentBoss.getLevel() + " Boss");
        tvBossDescription.setText(currentBoss.getDescription());
        tvBossHp.setText(currentBoss.getMaxHp() + "/" + currentBoss.getMaxHp());
        pbBossHp.setMax(currentBoss.getMaxHp());
        pbBossHp.setProgress(currentBoss.getMaxHp());
    }

    private void displayBattle() {
        if (currentBattle == null || currentBattle.getBoss() == null) return;

        displayBossInfo();

        // Update current HP
        updateBossHp(currentBattle.getCurrentHp());

        // Update user stats
        tvUserPp.setText(String.valueOf(currentBattle.getUserPpAtBattle()));
        tvSuccessRate.setText(String.format(Locale.US, "%.0f%%",
            currentBattle.getSuccessRateAtBattle() * 100));
        tvAttacksRemaining.setText(String.valueOf(5 - currentBattle.getAttacksUsed()));
    }

    private void updateBossHp(int currentHp) {
        if (currentBoss == null) return;

        tvBossHp.setText(currentHp + "/" + currentBoss.getMaxHp());
        pbBossHp.setProgress(currentHp);

        // Animate HP bar
        ObjectAnimator animator = ObjectAnimator.ofInt(pbBossHp, "progress", pbBossHp.getProgress(), currentHp);
        animator.setDuration(500);
        animator.start();
    }

    private void showAttackAnimation(boolean hit) {
        // Shake boss image
        ObjectAnimator animator = ObjectAnimator.ofFloat(ivBossImage, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f);
        animator.setDuration(500);
        animator.start();

        if (hit) {
            // Scale animation for hit
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivBossImage, "scaleX", 1f, 0.9f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivBossImage, "scaleY", 1f, 0.9f, 1f);
            scaleX.setDuration(300);
            scaleY.setDuration(300);
            scaleX.start();
            scaleY.start();
        }
    }

    private void showStartBattleUI() {
        btnStartBattle.setVisibility(View.VISIBLE);
        btnAttack.setVisibility(View.GONE);
        btnSelectEquipment.setEnabled(true);
        tvBattleResult.setVisibility(View.GONE);
    }

    private void showBattleUI() {
        btnStartBattle.setVisibility(View.GONE);
        btnAttack.setVisibility(View.VISIBLE);
        btnAttack.setEnabled(true);
        btnSelectEquipment.setEnabled(false);
        tvBattleResult.setVisibility(View.GONE);
    }

    private void showLoading(boolean show) {
        progressLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
