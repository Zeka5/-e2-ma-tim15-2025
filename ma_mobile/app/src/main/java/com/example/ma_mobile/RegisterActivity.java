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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;
    private Button btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setClickListeners();
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

        String fullName = getFullNameInput();
        String email = getEmailInput();
        String phone = getPhoneInput();
        String password = getPasswordInput();
        String confirmPassword = getConfirmPasswordInput();

        if (validateRegisterInputs(fullName, email, phone, password, confirmPassword)) {
            showSuccessAndNavigateToLogin();
        }
    }

    private void handleBackToLoginClick() {
        finish(); //zatvori samo ovu aktivnost i vraca se na prvu mogucu otvorenu (login)
    }

    private String getFullNameInput() {
        return etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
    }

    private String getEmailInput() {
        return etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
    }

    private String getPhoneInput() {
        return etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
    }

    private String getPasswordInput() {
        return etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
    }

    private String getConfirmPasswordInput() {
        return etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
    }

    private boolean validateRegisterInputs(String fullName, String email, String phone,
                                           String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else if (fullName.length() < 2) {
            tilFullName.setError("Please enter a valid full name");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!isValidEmail(email)) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number is required");
            isValid = false;
        } else if (!isValidPhone(phone)) {
            tilPhone.setError("Please enter a valid phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
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

    private boolean isValidPhone(String phone) {
        String cleanPhone = phone.replaceAll("[^\\d]", "");
        return cleanPhone.length() >= 8 && cleanPhone.length() <= 12;
    }

    private void showSuccessAndNavigateToLogin() {
        showToast("Registration successful!");

        // Navigate directly to HomeScreen after successful registration
        navigateToHome();
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(RegisterActivity.this, MainActivity.class);

        // Pass user data to HomeActivity
        homeIntent.putExtra("USER_EMAIL", getEmailInput());
//        homeIntent.putExtra("USER_NAME", getFullNameInput());
//        homeIntent.putExtra("USER_PHONE", getPhoneInput());
//        homeIntent.putExtra("FROM_REGISTER", true); // Flag to indicate user came from registration

        startActivity(homeIntent);

        // Clear the back stack - user shouldn't go back to register/login after successful registration
        finishAffinity(); // This clears all activities in the task
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void clearErrors() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private void initializeViews() {
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
    }

    private void setClickListeners() {
        btnRegister.setOnClickListener(this);
        btnBackToLogin.setOnClickListener(this);
    }
}