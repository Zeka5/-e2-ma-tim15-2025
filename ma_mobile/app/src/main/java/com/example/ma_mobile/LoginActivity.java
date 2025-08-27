package com.example.ma_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private Button btnGoToRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setClickListeners();
    }

    private void setClickListeners() {
        btnLogin.setOnClickListener(this);
        btnGoToRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_login) {
            handleLoginClick();
        } else if (viewId == R.id.btn_go_to_register) {
            handleRegisterClick();
        }
    }

    private void handleLoginClick() {
        clearErrors();

        String email = getEmailInput();
        String password = getPasswordInput();

        if (validateLoginInputs(email, password)) {
            navigateToHome();
        }
    }

    private void handleRegisterClick() {
        navigateToRegister();
    }

    private String getEmailInput() {
        return etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
    }

    private String getPasswordInput() {
        return etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
    }

    private boolean validateLoginInputs(String email, String password) {
        boolean isValid = true;

        // Email validation
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.login_email_required));
            isValid = false;
        } else if (!isValidEmail(email)) {
            tilEmail.setError(getString(R.string.login_email_invalid));
            isValid = false;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.login_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.login_password_min_length));
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);

        String userEmail = getEmailInput();
        homeIntent.putExtra("USER_EMAIL", userEmail);

        startActivity(homeIntent);

        finish();

        showToast(getString(R.string.login_success_message));
    }

    private void navigateToRegister() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        // ne pozivamo finish() da bi korisnik mogao da se vrati
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initializeViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoToRegister = findViewById(R.id.btn_go_to_register);
    }
}