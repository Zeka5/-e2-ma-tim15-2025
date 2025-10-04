package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.models.PublicUserProfile;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";
    private static final String ARG_USER_PROFILE = "user_profile";

    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvTitle;
    private TextView tvLevel;
    private TextView tvXp;
    private TextView tvBadgeCount;
    private TextView tvBadgesPlaceholder;
    private ProgressBar progressBar;

    private PublicUserProfile userProfile;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(PublicUserProfile userProfile) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_PROFILE, userProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userProfile = (PublicUserProfile) getArguments().getSerializable(ARG_USER_PROFILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        if (userProfile != null) {
            displayUserProfile(userProfile);
        }
    }

    private void initializeViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvTitle = view.findViewById(R.id.tv_title);
        tvLevel = view.findViewById(R.id.tv_level);
        tvXp = view.findViewById(R.id.tv_xp);
        tvBadgeCount = view.findViewById(R.id.tv_badge_count);
        tvBadgesPlaceholder = view.findViewById(R.id.tv_badges_placeholder);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    public void displayUserProfile(PublicUserProfile profile) {
        this.userProfile = profile;

        if (profile == null) {
            showToast("No user data available");
            return;
        }

        // Set username
        if (profile.getUsername() != null) {
            tvUsername.setText(profile.getUsername());
        }

        // Set title
        if (profile.getTitle() != null && !profile.getTitle().isEmpty()) {
            tvTitle.setText(profile.getTitle());
        } else {
            tvTitle.setText("NOVICE");
        }

        // Set avatar
        if (profile.getAvatarId() != null) {
            ivAvatar.setImageResource(profile.getAvatarDrawableId());
        }

        // Set level
        if (profile.getLevel() != null) {
            tvLevel.setText(String.valueOf(profile.getLevel()));
        } else {
            tvLevel.setText("1");
        }

        // Set XP
        if (profile.getExperiencePoints() != null) {
            tvXp.setText(String.valueOf(profile.getExperiencePoints()));
        } else {
            tvXp.setText("0");
        }

        // Set badge count
        if (profile.getBadgeCount() != null && profile.getBadgeCount() > 0) {
            tvBadgeCount.setText(String.valueOf(profile.getBadgeCount()));
            tvBadgesPlaceholder.setVisibility(View.GONE);
        } else {
            tvBadgeCount.setText("0");
            tvBadgesPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
