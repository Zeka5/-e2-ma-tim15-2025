package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.FriendInviteGuildAdapter;
import com.example.ma_mobile.models.Guild;
import com.example.ma_mobile.models.GuildInvite;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.repository.FriendRepository;
import com.example.ma_mobile.repository.GuildRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InviteFriendsFragment extends Fragment implements FriendInviteGuildAdapter.OnInviteClickListener {

    private static final String TAG = "InviteFriendsFragment";
    private static final String ARG_GUILD_ID = "guild_id";

    private ImageButton btnBack;
    private RecyclerView rvFriends;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;

    private FriendRepository friendRepository;
    private GuildRepository guildRepository;
    private FriendInviteGuildAdapter adapter;

    private Long guildId;
    private Guild currentGuild;

    public InviteFriendsFragment() {
        // Required empty public constructor
    }

    public static InviteFriendsFragment newInstance(Long guildId) {
        InviteFriendsFragment fragment = new InviteFriendsFragment();
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
        friendRepository = new FriendRepository(getContext());
        guildRepository = new GuildRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadGuildDetails();
    }

    private void initializeViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        rvFriends = view.findViewById(R.id.rv_friends);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new FriendInviteGuildAdapter(this);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadGuildDetails() {
        showLoading(true);

        guildRepository.getGuildById(guildId, new GuildRepository.GuildCallback() {
            @Override
            public void onSuccess(Guild guild) {
                currentGuild = guild;
                loadFriends();
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

    private void loadFriends() {
        friendRepository.getFriends(new FriendRepository.FriendsListCallback() {
            @Override
            public void onSuccess(List<User> friends) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        List<User> nonMemberFriends = filterNonMembers(friends);
                        if (nonMemberFriends.isEmpty()) {
                            showEmptyState();
                        } else {
                            hideEmptyState();
                            adapter.setFriends(nonMemberFriends);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Failed to load friends: " + error);
                        Log.e(TAG, "Error loading friends: " + error);
                        showEmptyState();
                    });
                }
            }
        });
    }

    private List<User> filterNonMembers(List<User> friends) {
        if (currentGuild == null || currentGuild.getMembers() == null) {
            return friends;
        }

        // Create a set of member IDs for fast lookup
        Set<Long> memberIds = new HashSet<>();
        for (User member : currentGuild.getMembers()) {
            if (member.getId() != null) {
                memberIds.add(member.getId());
            }
        }

        // Filter out friends who are already members
        List<User> nonMembers = new ArrayList<>();
        for (User friend : friends) {
            if (friend.getId() != null && !memberIds.contains(friend.getId())) {
                nonMembers.add(friend);
            }
        }

        return nonMembers;
    }

    @Override
    public void onInviteClick(User friend) {
        if (friend.getId() == null) return;

        guildRepository.inviteToGuild(guildId, friend.getId(), new GuildRepository.GuildInviteCallback() {
            @Override
            public void onSuccess(GuildInvite invite) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Invite sent to " + friend.getUsername());
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast(error);
                        Log.e(TAG, "Error sending invite: " + error);
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState() {
        llEmptyState.setVisibility(View.VISIBLE);
        rvFriends.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        llEmptyState.setVisibility(View.GONE);
        rvFriends.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
