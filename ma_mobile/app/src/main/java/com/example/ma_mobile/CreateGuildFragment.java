package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.models.Guild;
import com.example.ma_mobile.repository.GuildRepository;

public class CreateGuildFragment extends Fragment {

    private static final String TAG = "CreateGuildFragment";

    private ImageButton btnBack;
    private EditText etGuildName;
    private Button btnCreate;
    private ProgressBar progressBar;

    private GuildRepository guildRepository;

    public CreateGuildFragment() {
        // Required empty public constructor
    }

    public static CreateGuildFragment newInstance() {
        return new CreateGuildFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        guildRepository = new GuildRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_guild, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupListeners();
    }

    private void initializeViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        etGuildName = view.findViewById(R.id.et_guild_name);
        btnCreate = view.findViewById(R.id.btn_create);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnCreate.setOnClickListener(v -> createGuild());
    }

    private void createGuild() {
        String guildName = etGuildName.getText().toString().trim();

        if (guildName.isEmpty()) {
            showToast("Please enter a guild name");
            return;
        }

        if (guildName.length() < 3) {
            showToast("Guild name must be at least 3 characters");
            return;
        }

        showLoading(true);

        guildRepository.createGuild(guildName, new GuildRepository.GuildCallback() {
            @Override
            public void onSuccess(Guild guild) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Guild created successfully!");
                        // Navigate back to guilds fragment
                        getActivity().getSupportFragmentManager().popBackStack();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast(error);
                        Log.e(TAG, "Error creating guild: " + error);
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnCreate.setEnabled(true);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
