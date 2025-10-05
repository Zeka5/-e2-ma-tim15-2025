package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.GuildInviteAdapter;
import com.example.ma_mobile.models.GuildInvite;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.repository.GuildRepository;
import com.example.ma_mobile.repository.UserRepository;

import java.util.List;

public class GuildsFragment extends Fragment implements GuildInviteAdapter.OnInviteActionListener {

    private static final String TAG = "GuildsFragment";

    private ImageButton btnBack;
    private Button btnCreateGuild;
    private TextView tvInvitesCount;
    private RecyclerView rvGuildInvites;
    private LinearLayout llNoGuild;
    private LinearLayout llNoInvites;
    private View guildDetailsContainer;
    private ProgressBar progressBar;

    private GuildRepository guildRepository;
    private UserRepository userRepository;
    private GuildInviteAdapter inviteAdapter;

    public GuildsFragment() {
        // Required empty public constructor
    }

    public static GuildsFragment newInstance() {
        return new GuildsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        guildRepository = new GuildRepository(getContext());
        userRepository = new UserRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guilds, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        checkUserGuildStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh guild status when returning to this fragment
        checkUserGuildStatus();
    }

    private void initializeViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnCreateGuild = view.findViewById(R.id.btn_create_guild);
        tvInvitesCount = view.findViewById(R.id.tv_invites_count);
        rvGuildInvites = view.findViewById(R.id.rv_guild_invites);
        llNoGuild = view.findViewById(R.id.ll_no_guild);
        llNoInvites = view.findViewById(R.id.ll_no_invites);
        guildDetailsContainer = view.findViewById(R.id.guild_details_container);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        inviteAdapter = new GuildInviteAdapter(this);
        rvGuildInvites.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGuildInvites.setAdapter(inviteAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnCreateGuild.setOnClickListener(v -> navigateToCreateGuild());
    }

    private void checkUserGuildStatus() {
        showLoading(true);

        userRepository.getCurrentUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (user.getGuildId() != null) {
                            // User is in a guild, show guild details
                            showGuildDetails(user.getGuildId());
                        } else {
                            // User has no guild, show invites and create option
                            showNoGuildView();
                            loadGuildInvites();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Failed to load guild status: " + error);
                        Log.e(TAG, "Error loading user profile: " + error);
                    });
                }
            }
        });
    }

    private void showNoGuildView() {
        llNoGuild.setVisibility(View.VISIBLE);
        guildDetailsContainer.setVisibility(View.GONE);
    }

    private void showGuildDetails(Long guildId) {
        llNoGuild.setVisibility(View.GONE);
        guildDetailsContainer.setVisibility(View.VISIBLE);

        // Navigate to guild details fragment
        GuildDetailsFragment guildDetailsFragment = GuildDetailsFragment.newInstance(guildId);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.guild_details_container, guildDetailsFragment)
                .commit();
    }

    private void loadGuildInvites() {
        guildRepository.getPendingGuildInvites(new GuildRepository.GuildInviteListCallback() {
            @Override
            public void onSuccess(List<GuildInvite> invites) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (invites.isEmpty()) {
                            showEmptyInvitesState();
                        } else {
                            hideEmptyInvitesState();
                            inviteAdapter.setInvites(invites);
                            tvInvitesCount.setText(String.valueOf(invites.size()));
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to load invites: " + error);
                        Log.e(TAG, "Error loading guild invites: " + error);
                        showEmptyInvitesState();
                    });
                }
            }
        });
    }

    private void navigateToCreateGuild() {
        if (getActivity() != null) {
            CreateGuildFragment createGuildFragment = CreateGuildFragment.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createGuildFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onAcceptInvite(GuildInvite invite) {
        showLoading(true);

        guildRepository.acceptGuildInvite(invite.getId(), new GuildRepository.GuildInviteCallback() {
            @Override
            public void onSuccess(GuildInvite acceptedInvite) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Joined guild successfully!");
                        // Refresh the view to show guild details
                        checkUserGuildStatus();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Failed to accept invite: " + error);
                        Log.e(TAG, "Error accepting invite: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onRejectInvite(GuildInvite invite) {
        guildRepository.rejectGuildInvite(invite.getId(), new GuildRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Invite declined");
                        loadGuildInvites(); // Refresh the list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to decline invite: " + error);
                        Log.e(TAG, "Error declining invite: " + error);
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showEmptyInvitesState() {
        llNoInvites.setVisibility(View.VISIBLE);
        rvGuildInvites.setVisibility(View.GONE);
        tvInvitesCount.setText("0");
    }

    private void hideEmptyInvitesState() {
        llNoInvites.setVisibility(View.GONE);
        rvGuildInvites.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
