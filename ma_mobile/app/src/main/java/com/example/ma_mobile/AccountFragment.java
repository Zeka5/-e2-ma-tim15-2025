package com.example.ma_mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ma_mobile.models.User;
import com.example.ma_mobile.models.UserGameStats;
import com.example.ma_mobile.repository.AuthRepository;
import com.example.ma_mobile.repository.UserRepository;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AccountFragment";

    // Profile views
    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvTitle;
    private TextView tvEmail;

    // Stats views
    private TextView tvLevel;
    private TextView tvXp;
    private TextView tvPower;
    private TextView tvCoins;

    // Badge views
    private TextView tvBadgeCount;
    private TextView tvBadgesPlaceholder;

    // Action buttons
    private Button btnLogout;
    private ProgressBar progressBar;

    private UserRepository userRepository;
    private String userEmail;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String userEmail) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString("USER_EMAIL", userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString("USER_EMAIL");
        }
        userRepository = new UserRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setClickListeners();
        loadUserProfile();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_logout) {
            handleLogout();
        }
    }

    private void initializeViews(View view) {
        // Profile views
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvTitle = view.findViewById(R.id.tv_title);
        tvEmail = view.findViewById(R.id.tv_email);

        // Stats views
        tvLevel = view.findViewById(R.id.tv_level);
        tvXp = view.findViewById(R.id.tv_xp);
        tvPower = view.findViewById(R.id.tv_power);
        tvCoins = view.findViewById(R.id.tv_coins);

        // Badge views
        tvBadgeCount = view.findViewById(R.id.tv_badge_count);
        tvBadgesPlaceholder = view.findViewById(R.id.tv_badges_placeholder);

        // Action buttons
        btnLogout = view.findViewById(R.id.btn_logout);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setClickListeners() {
        btnLogout.setOnClickListener(this);
    }

    private void loadUserProfile() {
        showLoading(true);

        userRepository.getCurrentUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        displayUserProfile(user);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Failed to load profile: " + error);
                        Log.e(TAG, "Error loading profile: " + error);
                    });
                }
            }
        });
    }

    private void displayUserProfile(User user) {
        if (user == null) {
            showToast("No user data available");
            return;
        }

        // Display basic user info
        if (user.getUsername() != null) {
            tvUsername.setText(user.getUsername());
        }

        if (user.getEmail() != null) {
            tvEmail.setText(user.getEmail());
        }

        // Set avatar
        if (user.getAvatarId() != null) {
            ivAvatar.setImageResource(user.getAvatarDrawableId());
        }

        // Display game stats if available
        UserGameStats stats = user.getGameStats();
        if (stats != null) {
            displayGameStats(stats);
        } else {
            // Set default values if no stats
            tvLevel.setText("1");
            tvXp.setText("0");
            tvPower.setText("0");
            tvCoins.setText("0");
            tvTitle.setText("Beginner");
        }
    }

    private void displayGameStats(UserGameStats stats) {
        // Display level
        if (stats.getLevel() != null) {
            tvLevel.setText(String.valueOf(stats.getLevel()));
        } else {
            tvLevel.setText("1");
        }

        // Display XP
        if (stats.getExperiencePoints() != null) {
            tvXp.setText(String.valueOf(stats.getExperiencePoints()));
        } else {
            tvXp.setText("0");
        }

        // Display power points
        if (stats.getPowerPoints() != null) {
            tvPower.setText(String.valueOf(stats.getPowerPoints()));
        } else {
            tvPower.setText("0");
        }

        // Display coins
        if (stats.getCoins() != null) {
            tvCoins.setText(String.valueOf(stats.getCoins()));
        } else {
            tvCoins.setText("0");
        }

        // Display title
        if (stats.getTitle() != null && !stats.getTitle().isEmpty()) {
            tvTitle.setText(stats.getTitle());
        } else {
            tvTitle.setText("Adventurer");
        }

        // Display badge count
        if (stats.getBadgeCount() != null && stats.getBadgeCount() > 0) {
            tvBadgeCount.setText(String.valueOf(stats.getBadgeCount()));
            tvBadgesPlaceholder.setVisibility(View.GONE);
        } else {
            tvBadgeCount.setText("0");
            tvBadgesPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogout.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogout.setEnabled(true);
        }
    }

    private void handleLogout() {
        AuthRepository authRepository = new AuthRepository(getContext());
        authRepository.clearSession();

        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

        showToast("Logged out successfully");

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}