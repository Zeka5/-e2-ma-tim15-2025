package com.example.ma_mobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private TextView tvUserGreeting;
    private TextView tvUserEmail;
    private LinearLayout llUserInfo;

    private String userEmail;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String userEmail) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupUserInterface();
    }

    private void setupUserInterface() {
        // Setup user greeting
        if (!TextUtils.isEmpty(userEmail)) {
            String greeting = getString(R.string.home_user_greeting, userEmail);
            tvUserGreeting.setText(greeting);
            tvUserGreeting.setVisibility(View.VISIBLE);
        }

        // Setup user info display
        if (hasCompleteUserData()) {
            displayUserInfo();
        }
    }

    private boolean hasCompleteUserData() {
        return !TextUtils.isEmpty(userEmail);
    }

    private void displayUserInfo() {
        tvUserEmail.setText("Email: " + userEmail);
        llUserInfo.setVisibility(View.VISIBLE);
    }

    private void initializeViews(View view) {
        tvUserGreeting = view.findViewById(R.id.tv_user_greeting);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        llUserInfo = view.findViewById(R.id.ll_user_info);
    }
}