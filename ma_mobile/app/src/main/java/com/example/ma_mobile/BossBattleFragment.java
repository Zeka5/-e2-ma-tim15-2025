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
import com.example.ma_mobile.models.BattleStatsPreview;
import com.example.ma_mobile.models.Boss;
import com.example.ma_mobile.models.BossBattle;
import com.example.ma_mobile.models.User;
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
    private TextView tvBattleResult, tvXpProgress;
    private ImageView ivBossImage;
    private ProgressBar pbBossHp, progressLoading, pbXpProgress;
    private MaterialButton btnAttack, btnStartBattle;
    private View layoutBattleView, layoutXpProgressView;

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
        tvBattleResult = view.findViewById(R.id.tv_battle_result);
        tvXpProgress = view.findViewById(R.id.tv_xp_progress);
        ivBossImage = view.findViewById(R.id.iv_boss_image);
        pbBossHp = view.findViewById(R.id.pb_boss_hp);
        pbXpProgress = view.findViewById(R.id.pb_xp_progress);
        progressLoading = view.findViewById(R.id.progress_loading);
        btnAttack = view.findViewById(R.id.btn_attack);
        btnStartBattle = view.findViewById(R.id.btn_start_battle);
        layoutBattleView = view.findViewById(R.id.layout_battle);
        layoutXpProgressView = view.findViewById(R.id.layout_xp_progress);
    }

    private void setupClickListeners() {
        btnStartBattle.setOnClickListener(v -> startBattle());
        btnAttack.setOnClickListener(v -> performAttack());
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
        // First, load user profile to get XP and level
        apiService.getCurrentUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    checkBossAvailability(user);
                } else {
                    showError("Failed to load user profile");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showError("Failed to load profile: " + t.getMessage());
            }
        });
    }

    private void checkBossAvailability(User user) {
        apiService.getNextBoss().enqueue(new Callback<Boss>() {
            @Override
            public void onResponse(Call<Boss> call, Response<Boss> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Boss is available - user has reached max XP
                    currentBoss = response.body();
                    displayBossInfo();
                    showStartBattleUI();
                } else if (response.code() == 500) {
                    // Boss not available - show XP progress
                    showXpProgressUI(user);
                } else {
                    // No boss available - show XP progress
                    showXpProgressUI(user);
                }
            }

            @Override
            public void onFailure(Call<Boss> call, Throwable t) {
                // On error, likely boss not available, show XP progress
                showXpProgressUI(user);
            }
        });
    }

    private void startBattle() {
            // After equipment is selected, create the battle
            createBattle();
    }

    private void createBattle() {
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

    private void displayBossInfo() {
        if (currentBoss == null) return;

        tvBossName.setText(currentBoss.getName());
        tvBossLevel.setText("Level " + currentBoss.getLevel() + " Boss");
        tvBossDescription.setText(currentBoss.getDescription());
        tvBossHp.setText(currentBoss.getMaxHp() + "/" + currentBoss.getMaxHp());
        pbBossHp.setMax(currentBoss.getMaxHp());
        pbBossHp.setProgress(currentBoss.getMaxHp());

        // Load and display battle stats preview
        loadBattleStatsPreview();
    }

    private void loadBattleStatsPreview() {
        apiService.getBattleStatsPreview().enqueue(new Callback<BattleStatsPreview>() {
            @Override
            public void onResponse(Call<BattleStatsPreview> call, Response<BattleStatsPreview> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BattleStatsPreview stats = response.body();
                    displayBattleStats(stats);
                }
            }

            @Override
            public void onFailure(Call<BattleStatsPreview> call, Throwable t) {
                // Silently fail, keep default values
            }
        });
    }

    private void displayBattleStats(BattleStatsPreview stats) {
        tvUserPp.setText(String.valueOf(stats.getUserPowerPoints()));
        tvSuccessRate.setText(String.format(Locale.US, "%.0f%%", stats.getSuccessRate() * 100));
        tvAttacksRemaining.setText(String.valueOf(stats.getMaxAttacks()));
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

    private void showXpProgressUI(User user) {
        // Hide battle view, show XP progress view
        layoutBattleView.setVisibility(View.GONE);
        layoutXpProgressView.setVisibility(View.VISIBLE);

        int currentXp = user.getGameStats().getExperiencePoints();
        int currentLevel = user.getGameStats().getLevel();
        int maxXp = calculateMaxXpForLevel(currentLevel);

        // Update XP text
        tvXpProgress.setText(currentXp + "/" + maxXp + " XP");

        // Update progress bar
        pbXpProgress.setMax(maxXp);
        pbXpProgress.setProgress(currentXp);
    }

    private int calculateMaxXpForLevel(int level) {
        if (level == 1) return 200;
        int previousXp = calculateMaxXpForLevel(level - 1);
        int nextXp = previousXp * 2 + previousXp / 2;
        // Round to next hundred
        return (int) Math.ceil(nextXp / 100.0) * 100;
    }

    private void showStartBattleUI() {
        // Show battle view, hide XP progress view
        layoutBattleView.setVisibility(View.VISIBLE);
        layoutXpProgressView.setVisibility(View.GONE);

        btnStartBattle.setVisibility(View.VISIBLE);
        btnAttack.setVisibility(View.GONE);
        tvBattleResult.setVisibility(View.GONE);
    }

    private void showBattleUI() {
        // Show battle view, hide XP progress view
        layoutBattleView.setVisibility(View.VISIBLE);
        layoutXpProgressView.setVisibility(View.GONE);

        btnStartBattle.setVisibility(View.GONE);
        btnAttack.setVisibility(View.VISIBLE);
        btnAttack.setEnabled(true);
        tvBattleResult.setVisibility(View.GONE);
    }

    private void showLoading(boolean show) {
        progressLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
