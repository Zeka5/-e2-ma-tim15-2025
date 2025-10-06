package com.example.ma_mobile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ma_mobile.models.BattleRewards;
import com.google.android.material.button.MaterialButton;

public class RewardChestDialog extends DialogFragment implements SensorEventListener {

    private static final String ARG_VICTORY = "victory";
    private static final String ARG_COINS = "coins";
    private static final String ARG_EQUIPMENT_TYPE = "equipment_type";
    private static final String ARG_EQUIPMENT_NAME = "equipment_name";

    private static final float SHAKE_THRESHOLD = 15.0f;
    private static final int SHAKE_WAIT_TIME_MS = 250;

    private TextView tvBattleOutcome, tvShakeInstruction, tvCoinsEarned, tvEquipmentEarned;
    private ImageView ivChest;
    private LinearLayout layoutRewards, layoutEquipmentReward;
    private MaterialButton btnClose;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;
    private boolean chestOpened = false;

    private boolean victory;
    private Integer coinsEarned;
    private String equipmentType;
    private String equipmentName;

    private Runnable onDismissListener;

    public static RewardChestDialog newInstance(boolean victory, BattleRewards rewards) {
        RewardChestDialog fragment = new RewardChestDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_VICTORY, victory);
        if (rewards != null) {
            args.putInt(ARG_COINS, rewards.getCoinsEarned());
            args.putString(ARG_EQUIPMENT_TYPE, rewards.getEquipmentType());
            args.putString(ARG_EQUIPMENT_NAME, rewards.getEquipmentName());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            victory = getArguments().getBoolean(ARG_VICTORY);
            coinsEarned = getArguments().getInt(ARG_COINS, 0);
            equipmentType = getArguments().getString(ARG_EQUIPMENT_TYPE);
            equipmentName = getArguments().getString(ARG_EQUIPMENT_NAME);
        }

        // Initialize sensor manager
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_reward_chest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupUI();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        tvBattleOutcome = view.findViewById(R.id.tv_battle_outcome);
        tvShakeInstruction = view.findViewById(R.id.tv_shake_instruction);
        tvCoinsEarned = view.findViewById(R.id.tv_coins_earned);
        tvEquipmentEarned = view.findViewById(R.id.tv_equipment_earned);
        ivChest = view.findViewById(R.id.iv_chest);
        layoutRewards = view.findViewById(R.id.layout_rewards);
        layoutEquipmentReward = view.findViewById(R.id.layout_equipment_reward);
        btnClose = view.findViewById(R.id.btn_close);
    }

    private void setupUI() {
        if (victory) {
            tvBattleOutcome.setText("VICTORY!");
            tvBattleOutcome.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            tvBattleOutcome.setText("PARTIAL VICTORY");
            tvBattleOutcome.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        }

        // Start chest animation
        startChestIdleAnimation();
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> {
            if (onDismissListener != null) {
                onDismissListener.run();
            }
            dismiss();
        });

        // Allow tap to open chest as alternative to shake
        ivChest.setOnClickListener(v -> {
            if (!chestOpened) {
                openChest();
            }
        });
    }

    private void startChestIdleAnimation() {
        // Gentle bounce animation while waiting for shake
        ObjectAnimator bounceY = ObjectAnimator.ofFloat(ivChest, "translationY", 0f, -20f, 0f);
        bounceY.setDuration(1000);
        bounceY.setRepeatCount(ObjectAnimator.INFINITE);
        bounceY.setInterpolator(new AccelerateDecelerateInterpolator());
        bounceY.start();
    }

    private void openChest() {
        if (chestOpened) return;
        chestOpened = true;

        // Stop listening to accelerometer
        if (sensorManager != null && accelerometer != null) {
            sensorManager.unregisterListener(this);
        }

        // Hide shake instruction
        tvShakeInstruction.setVisibility(View.GONE);

        // Animate chest opening
        AnimatorSet openAnimation = new AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivChest, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivChest, "scaleY", 1f, 1.2f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivChest, "rotation", 0f, 360f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivChest, "alpha", 1f, 0.5f, 1f);

        openAnimation.playTogether(scaleX, scaleY, rotation, alpha);
        openAnimation.setDuration(800);
        openAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        openAnimation.start();

        // Show rewards after animation
        ivChest.postDelayed(this::showRewards, 800);
    }

    private void showRewards() {
        // Display rewards
        layoutRewards.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);

        if (coinsEarned != null && coinsEarned > 0) {
            tvCoinsEarned.setText(coinsEarned + " Coins");
        }

        if (equipmentName != null && !equipmentName.isEmpty()) {
            layoutEquipmentReward.setVisibility(View.VISIBLE);
            tvEquipmentEarned.setText(equipmentName);
        }

        // Animate rewards appearing
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layoutRewards, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.start();

        ObjectAnimator slideUp = ObjectAnimator.ofFloat(layoutRewards, "translationY", 100f, 0f);
        slideUp.setDuration(500);
        slideUp.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start listening to accelerometer
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop listening to accelerometer
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (chestOpened) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate acceleration
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            // Check if shake detected
            long currentTime = System.currentTimeMillis();
            if (acceleration > SHAKE_THRESHOLD) {
                if (currentTime - lastShakeTime > SHAKE_WAIT_TIME_MS) {
                    lastShakeTime = currentTime;
                    onShakeDetected();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    private void onShakeDetected() {
        // Shake animation for chest
        ObjectAnimator shakeAnim = ObjectAnimator.ofFloat(
            ivChest, "rotation",
            0f, -15f, 15f, -10f, 10f, -5f, 5f, 0f
        );
        shakeAnim.setDuration(400);
        shakeAnim.start();

        // Open chest after shake animation
        ivChest.postDelayed(this::openChest, 400);
    }

    public void setOnDismissListener(Runnable listener) {
        this.onDismissListener = listener;
    }
}
