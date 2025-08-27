package com.example.ma_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvUserGreeting;
    private TextView tvUserEmail;
    private TextView tvUserPhone;
    private LinearLayout llUserInfo;
    private Button btnMainScreen;
    private Button btnAccountScreen;

    private String userEmail;
    private String userName;
    private String userPhone;
    private boolean fromRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setSupportActionBar(toolbar);
        extractUserDataFromIntent();
        setupUserInterface();
        setClickListeners();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

    }
    private void extractUserDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            userEmail = intent.getStringExtra("USER_EMAIL");
        }
    }

    private void setupUserInterface() {
        if (!TextUtils.isEmpty(userName)) {
            String greeting = getString(R.string.home_user_greeting, userName);
            tvUserGreeting.setText(greeting);
            tvUserGreeting.setVisibility(View.VISIBLE);
        }
    }

    private void setClickListeners() {
        btnMainScreen.setOnClickListener(this);
        btnAccountScreen.setOnClickListener(this);
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvUserGreeting = findViewById(R.id.tv_user_greeting);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        llUserInfo = findViewById(R.id.ll_user_info);
        btnMainScreen = findViewById(R.id.btn_main_screen);
        btnAccountScreen = findViewById(R.id.btn_account_screen);
    }
}