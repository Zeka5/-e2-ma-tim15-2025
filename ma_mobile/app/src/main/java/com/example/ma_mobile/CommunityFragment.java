package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.FriendsAdapter;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.repository.FriendRepository;

import java.util.List;

public class CommunityFragment extends Fragment implements FriendsAdapter.OnFriendActionListener {

    private static final String TAG = "CommunityFragment";

    private Button btnSearchUsers;
    private Button btnPendingRequests;
    private Button btnScanQr;
    private Button btnGuilds;
    private TextView tvFriendsCount;
    private RecyclerView rvFriends;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;

    private FriendsAdapter adapter;
    private FriendRepository friendRepository;

    public CommunityFragment() {
        // Required empty public constructor
    }

    public static CommunityFragment newInstance() {
        return new CommunityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendRepository = new FriendRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadFriends();
    }

    private void initializeViews(View view) {
        btnSearchUsers = view.findViewById(R.id.btn_search_users);
        btnPendingRequests = view.findViewById(R.id.btn_pending_requests);
        btnScanQr = view.findViewById(R.id.btn_scan_qr);
        btnGuilds = view.findViewById(R.id.btn_guilds);
        tvFriendsCount = view.findViewById(R.id.tv_friends_count);
        rvFriends = view.findViewById(R.id.rv_friends);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new FriendsAdapter(this);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSearchUsers.setOnClickListener(v -> navigateToSearchUsers());
        btnPendingRequests.setOnClickListener(v -> navigateToPendingRequests());
        btnScanQr.setOnClickListener(v -> navigateToQRScanner());
        btnGuilds.setOnClickListener(v -> navigateToGuilds());
    }

    private void loadFriends() {
        showLoading(true);
        hideEmptyState();

        friendRepository.getFriends(new FriendRepository.FriendsListCallback() {
            @Override
            public void onSuccess(List<User> friends) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (friends.isEmpty()) {
                            showEmptyState();
                        } else {
                            adapter.setFriends(friends);
                            tvFriendsCount.setText(String.valueOf(friends.size()));
                            hideEmptyState();
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

    private void navigateToSearchUsers() {
        if (getActivity() != null) {
            SearchUsersFragment searchFragment = SearchUsersFragment.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, searchFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToPendingRequests() {
        if (getActivity() != null) {
            PendingRequestsFragment pendingRequestsFragment = PendingRequestsFragment.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, pendingRequestsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToQRScanner() {
        if (getActivity() != null) {
            QRScannerFragment qrScannerFragment = QRScannerFragment.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, qrScannerFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToGuilds() {
        if (getActivity() != null) {
            GuildsFragment guildsFragment = GuildsFragment.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, guildsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onViewFriendProfile(User friend) {
        if (getActivity() != null && friend.getId() != null) {
            // Fetch the public profile for this friend
            new com.example.ma_mobile.repository.UserRepository(getContext())
                    .getPublicProfile(friend.getId(), new com.example.ma_mobile.repository.UserRepository.PublicProfileCallback() {
                        @Override
                        public void onSuccess(com.example.ma_mobile.models.PublicUserProfile userProfile) {
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
                                    Log.e(TAG, "Error loading friend profile: " + error);
                                });
                            }
                        }
                    });
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            rvFriends.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvFriends.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        llEmptyState.setVisibility(View.VISIBLE);
        rvFriends.setVisibility(View.GONE);
        tvFriendsCount.setText("0");
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
