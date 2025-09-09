package com.example.ma_mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ma_mobile.repository.AuthRepository;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private TextView tvUserEmail;
    private LinearLayout llUserInfo;
    private Button btnEditProfile;
    private Button btnSettings;
    private Button btnLogout;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupUserInterface();
        setClickListeners();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_edit_profile) {
            handleEditProfile();
        } else if (viewId == R.id.btn_settings) {
            handleSettings();
        } else if (viewId == R.id.btn_logout) {
            handleLogout();
        }
    }



    private void initializeViews(View view) {
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        llUserInfo = view.findViewById(R.id.ll_user_info);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setClickListeners() {
        btnEditProfile.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    private void setupUserInterface() {
        if (hasUserData()) {
            displayUserInfo();
        } else {
            showNoUserData();
        }
    }

    private boolean hasUserData() {
        return !TextUtils.isEmpty(userEmail);
    }

    private void displayUserInfo() {
        llUserInfo.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(userEmail)) {
            tvUserEmail.setText(userEmail);
            tvUserEmail.setVisibility(View.VISIBLE);
        } else {
            tvUserEmail.setVisibility(View.GONE);
        }
    }

    private void showNoUserData() {
        tvUserEmail.setText("No email available");
        llUserInfo.setVisibility(View.VISIBLE);
    }

    private void handleEditProfile() {
        showToast("Edit Profile feature coming soon!");
        // TODO: Navigate to edit profile screen
    }

    private void handleSettings() {
        showToast("Settings feature coming soon!");
        // TODO: Navigate to settings screen
    }

    private void handleLogout() {
        AuthRepository authRepository = new AuthRepository(getContext());
        authRepository.clearSession();

        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //cistimo stack svih prozora
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