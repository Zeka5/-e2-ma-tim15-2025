package com.example.ma_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ma_mobile.models.User;
import com.example.ma_mobile.models.auth.AuthResponse;
import com.example.ma_mobile.models.auth.RegisterRequest;
import com.example.ma_mobile.repository.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout tilUsername;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;
    private Button btnBackToLogin;

    private ProgressBar progressBar;
    private AuthRepository authRepository;

    private ImageView[] avatarImageViews;
    private int selectedAvatarId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeRepository();
        checkIfAlreadyLoggedIn();

        initializeViews();
        setClickListeners();
    }

    private void initializeRepository() {
        authRepository = new AuthRepository(this);
    }

    private void checkIfAlreadyLoggedIn() {
        if (authRepository.isLoggedIn()) {
            navigateToMainScreen();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_register) {
            handleRegisterClick();
        } else if (viewId == R.id.btn_back_to_login) {
            handleBackToLoginClick();
        }
    }

    private void handleRegisterClick() {
        clearErrors();

        String username = getUsernameInput();
        String email = getEmailInput();
        String password = getPasswordInput();
        String confirmPassword = getConfirmPasswordInput();


        if (validateRegisterInputs(username, email, password, confirmPassword)) {
            Log.d("RegisterActivity", "AVATAR ID:"+selectedAvatarId);
            RegisterRequest registerRequest = new RegisterRequest(username, email, password, selectedAvatarId);
            performRegister(registerRequest);
        } else {
            Log.d("RegisterActivity", "Validation failed");
        }
    }

    private void performRegister(RegisterRequest registerRequest) {
        showLoading(true);

        authRepository.register(registerRequest, new AuthRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User user) {
                showLoading(false);
                showToast(getString(R.string.register_success_message));
                finish();
            }

            @Override
            public void onError(String error) {
                Log.e("RegisterActivity", "Register ERROR: " + error);
                showLoading(false);
                showToast(error != null ? error : "Registration failed");
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
            btnBackToLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            btnBackToLogin.setEnabled(true);
        }
    }

    private void handleBackToLoginClick() {
        finish(); //zatvori samo ovu aktivnost i vraca se na prvu mogucu otvorenu (login)
    }

    private String getUsernameInput() {
        return etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
    }

    private String getEmailInput() {
        return etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
    }

    private String getPasswordInput() {
        return etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
    }

    private String getConfirmPasswordInput() {
        return etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
    }

    private boolean validateRegisterInputs(String username, String email,
                                           String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Full name is required");
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters long");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!isValidEmail(email)) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 3) {
            tilPassword.setError("Password must be at least 3 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Password confirmation is required");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void navigateToMainScreen() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);

        if (authRepository.getCurrentUser() != null) {
            mainIntent.putExtra("USER_EMAIL", authRepository.getCurrentUser().getEmail());
            mainIntent.putExtra("USER_USERNAME", authRepository.getCurrentUser().getUsername());
        }

        startActivity(mainIntent);
        finishAffinity(); // Clear back stack
    }

    private void showToast(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private void initializeViews() {
        tilUsername = findViewById(R.id.til_username);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
        progressBar = findViewById(R.id.progress_bar);
        avatarImageViews = new ImageView[5];
        avatarImageViews[0] = findViewById(R.id.iv_avatar_1);
        avatarImageViews[1] = findViewById(R.id.iv_avatar_2);
        avatarImageViews[2] = findViewById(R.id.iv_avatar_3);
        avatarImageViews[3] = findViewById(R.id.iv_avatar_4);
        avatarImageViews[4] = findViewById(R.id.iv_avatar_5);

        setupAvatarSelection();
    }

    private void setupAvatarSelection() {
        for (int i = 0; i < avatarImageViews.length; i++) {
            ImageView imageView = avatarImageViews[i];

            // Make circular using clipToOutline
            imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_background));
            imageView.setClipToOutline(true);

            // Set initial state
            if (i == 0) {
                selectAvatar(0); // Select first avatar
            }

            final int avatarIndex = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAvatar(avatarIndex);
                }
            });
        }
    }

    private void selectAvatar(int index) {
        for (int i = 0; i < avatarImageViews.length; i++) {
            ImageView imageView = avatarImageViews[i];
            // Set normal circular background
            imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_background));
        }

        // Select the clicked avatar
        ImageView selectedImageView = avatarImageViews[index];
        // Set selected circular background
        selectedImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_background_selected));

        selectedAvatarId = index + 1;
    }

    private void setClickListeners() {
        btnRegister.setOnClickListener(this);
        btnBackToLogin.setOnClickListener(this);
    }
}