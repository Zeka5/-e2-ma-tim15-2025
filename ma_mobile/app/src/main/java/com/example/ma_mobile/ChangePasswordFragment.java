package com.example.ma_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.repository.AuthRepository;
import com.example.ma_mobile.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout tilCurrentPassword;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnChangePassword;
    private Button btnCancel;
    private ProgressBar progressBar;

    private UserRepository userRepository;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setClickListeners();
    }

    private void initializeViews(View view) {
        tilCurrentPassword = view.findViewById(R.id.til_current_password);
        tilNewPassword = view.findViewById(R.id.til_new_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnCancel = view.findViewById(R.id.btn_cancel);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setClickListeners() {
        btnChangePassword.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_change_password) {
            handleChangePassword();
        } else if (viewId == R.id.btn_cancel) {
            navigateBackToAccount();
        }
    }

    private void handleChangePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (!validateInputs(currentPassword, newPassword, confirmPassword)) {
            return;
        }

        changePassword(currentPassword, newPassword);
    }

    private boolean validateInputs(String currentPassword, String newPassword, String confirmPassword) {
        // Clear previous errors
        tilCurrentPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (currentPassword.isEmpty()) {
            tilCurrentPassword.setError("Current password is required");
            return false;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("New password is required");
            return false;
        }

        if (newPassword.length() < 6) {
            tilNewPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Please confirm your new password");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            return false;
        }

        if (currentPassword.equals(newPassword)) {
            tilNewPassword.setError("New password must be different from current password");
            return false;
        }

        return true;
    }

    private void changePassword(String currentPassword, String newPassword) {
        showLoading(true);

        userRepository.changePassword(currentPassword, newPassword, new UserRepository.PasswordChangeCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        navigateBackToAccount();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(getContext(), "Failed to change password: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void navigateBackToAccount() {
        // Use FragmentManager to go back to the AccountFragment
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnChangePassword.setEnabled(false);
            btnCancel.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnChangePassword.setEnabled(true);
            btnCancel.setEnabled(true);
        }
    }
}