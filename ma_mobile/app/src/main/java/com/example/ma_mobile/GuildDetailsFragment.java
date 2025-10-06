package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.GuildMemberAdapter;
import com.example.ma_mobile.models.Guild;
import com.example.ma_mobile.models.PublicUserProfile;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.repository.GuildRepository;
import com.example.ma_mobile.repository.UserRepository;

public class GuildDetailsFragment extends Fragment implements GuildMemberAdapter.OnMemberActionListener {

    private static final String TAG = "GuildDetailsFragment";
    private static final String ARG_GUILD_ID = "guild_id";

    private TextView tvGuildName;
    private TextView tvMemberCount;
    private Button btnGuildChat;
    private Button btnInviteFriends;
    private Button btnLeaveDeleteGuild;
    private RecyclerView rvMembers;
    private ProgressBar progressBar;

    private GuildRepository guildRepository;
    private UserRepository userRepository;
    private GuildMemberAdapter memberAdapter;

    private Long guildId;
    private Guild currentGuild;
    private Long currentUserId;

    public GuildDetailsFragment() {
        // Required empty public constructor
    }

    public static GuildDetailsFragment newInstance(Long guildId) {
        GuildDetailsFragment fragment = new GuildDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GUILD_ID, guildId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            guildId = getArguments().getLong(ARG_GUILD_ID);
        }
        guildRepository = new GuildRepository(getContext());
        userRepository = new UserRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guild_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadCurrentUser();
        loadGuildDetails();
    }

    private void initializeViews(View view) {
        tvGuildName = view.findViewById(R.id.tv_guild_name);
        tvMemberCount = view.findViewById(R.id.tv_member_count);
        btnGuildChat = view.findViewById(R.id.btn_guild_chat);
        btnInviteFriends = view.findViewById(R.id.btn_invite_friends);
        btnLeaveDeleteGuild = view.findViewById(R.id.btn_leave_delete_guild);
        rvMembers = view.findViewById(R.id.rv_members);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        memberAdapter = new GuildMemberAdapter(this);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembers.setAdapter(memberAdapter);
    }

    private void setupListeners() {
        btnGuildChat.setOnClickListener(v -> navigateToGuildChat());
        btnInviteFriends.setOnClickListener(v -> navigateToInviteFriends());
        btnLeaveDeleteGuild.setOnClickListener(v -> showLeaveDeleteConfirmation());
    }

    private void loadCurrentUser() {
        userRepository.getCurrentUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserId = user.getId();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading current user: " + error);
            }
        });
    }

    private void loadGuildDetails() {
        showLoading(true);

        guildRepository.getGuildById(guildId, new GuildRepository.GuildCallback() {
            @Override
            public void onSuccess(Guild guild) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        currentGuild = guild;
                        displayGuildDetails(guild);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Failed to load guild: " + error);
                        Log.e(TAG, "Error loading guild: " + error);
                    });
                }
            }
        });
    }

    private void displayGuildDetails(Guild guild) {
        tvGuildName.setText(guild.getName());

        int memberCount = guild.getMemberCount();
        tvMemberCount.setText(memberCount + (memberCount == 1 ? " member" : " members"));

        // Show invite button only if current user is the leader
        if (currentUserId != null && guild.isLeader(currentUserId)) {
            btnInviteFriends.setVisibility(View.VISIBLE);
            btnLeaveDeleteGuild.setText("Delete Guild");
        } else {
            btnInviteFriends.setVisibility(View.GONE);
            btnLeaveDeleteGuild.setText("Leave Guild");
        }

        // Display members
        if (guild.getMembers() != null && guild.getLeader() != null) {
            memberAdapter.setMembers(guild.getMembers(), guild.getLeader().getId());
        }
    }

    private void navigateToGuildChat() {
        if (getActivity() != null && currentGuild != null) {
            GuildChatFragment chatFragment = GuildChatFragment.newInstance(
                    currentGuild.getId(),
                    currentGuild.getName()
            );
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToInviteFriends() {
        if (getActivity() != null && currentGuild != null) {
            InviteFriendsFragment inviteFriendsFragment = InviteFriendsFragment.newInstance(currentGuild.getId());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, inviteFriendsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showLeaveDeleteConfirmation() {
        if (currentGuild == null || currentUserId == null) return;

        boolean isLeader = currentGuild.isLeader(currentUserId);
        String title = isLeader ? "Delete Guild?" : "Leave Guild?";
        String message = isLeader
                ? "Are you sure you want to delete this guild? This action cannot be undone."
                : "Are you sure you want to leave this guild?";

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(isLeader ? "Delete" : "Leave", (dialog, which) -> {
                    if (isLeader) {
                        deleteGuild();
                    } else {
                        leaveGuild();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void leaveGuild() {
        showLoading(true);

        guildRepository.leaveGuild(new GuildRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Left guild successfully");
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
                        showToast("Failed to leave guild: " + error);
                        Log.e(TAG, "Error leaving guild: " + error);
                    });
                }
            }
        });
    }

    private void deleteGuild() {
        showLoading(true);

        guildRepository.deleteGuild(guildId, new GuildRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Guild deleted successfully");
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
                        showToast("Failed to delete guild: " + error);
                        Log.e(TAG, "Error deleting guild: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onViewMemberProfile(User member) {
        if (getActivity() != null && member.getId() != null) {
            userRepository.getPublicProfile(member.getId(), new UserRepository.PublicProfileCallback() {
                @Override
                public void onSuccess(PublicUserProfile userProfile) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            UserProfileFragment profileFragment = UserProfileFragment.newInstance(userProfile);
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, profileFragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showToast("Failed to load profile: " + error);
                            Log.e(TAG, "Error loading member profile: " + error);
                        });
                    }
                }
            });
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
