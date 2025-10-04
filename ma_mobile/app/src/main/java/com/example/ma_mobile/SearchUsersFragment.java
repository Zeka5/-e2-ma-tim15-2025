package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.UserSearchAdapter;
import com.example.ma_mobile.models.FriendRequest;
import com.example.ma_mobile.models.PublicUserProfile;
import com.example.ma_mobile.repository.FriendRepository;
import com.example.ma_mobile.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SearchUsersFragment extends Fragment implements UserSearchAdapter.OnUserActionListener {

    private static final String TAG = "SearchUsersFragment";

    private ImageButton btnBack;
    private TextInputEditText etSearch;
    private Button btnSearch;
    private RecyclerView rvUsers;
    private TextView tvEmptyState;
    private ProgressBar progressBar;

    private UserSearchAdapter adapter;
    private UserRepository userRepository;
    private FriendRepository friendRepository;

    public SearchUsersFragment() {
        // Required empty public constructor
    }

    public static SearchUsersFragment newInstance() {
        return new SearchUsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(getContext());
        friendRepository = new FriendRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadPendingRequests();
        performInitialSearch();
    }

    private void initializeViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        rvUsers = view.findViewById(R.id.rv_users);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new UserSearchAdapter(this);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnSearch.setOnClickListener(v -> performSearch());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performInitialSearch() {
        // Load all users initially with empty search
        searchUsers("");
    }

    private void performSearch() {
        String query = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        searchUsers(query);
    }

    private void searchUsers(String query) {
        showLoading(true);
        hideEmptyState();

        userRepository.searchUsers(query, new UserRepository.SearchUsersCallback() {
            @Override
            public void onSuccess(List<PublicUserProfile> users) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (users.isEmpty()) {
                            showEmptyState();
                        } else {
                            adapter.setUsers(users);
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
                        showToast("Failed to search users: " + error);
                        Log.e(TAG, "Error searching users: " + error);
                        showEmptyState();
                    });
                }
            }
        });
    }

    private void loadPendingRequests() {
        // Load pending friend requests (both sent and received)
        friendRepository.getPendingRequests(new FriendRepository.FriendRequestListCallback() {
            @Override
            public void onSuccess(List<FriendRequest> requests) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adapter.setFriendRequests(requests);
                        // Also load friends list after setting requests
                        loadFriends();
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading pending requests: " + error);
                // Still try to load friends even if pending requests fail
                loadFriends();
            }
        });
    }

    private void loadFriends() {
        friendRepository.getFriends(new FriendRepository.FriendsListCallback() {
            @Override
            public void onSuccess(List<com.example.ma_mobile.models.User> friends) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Mark all friends as ACCEPTED in the adapter
                        for (com.example.ma_mobile.models.User friend : friends) {
                            if (friend.getId() != null) {
                                adapter.updateFriendRequestStatus(friend.getId(), "ACCEPTED");
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading friends: " + error);
            }
        });
    }

    @Override
    public void onViewProfile(PublicUserProfile user) {
        if (getActivity() != null) {
            UserProfileFragment profileFragment = UserProfileFragment.newInstance(user);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, profileFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onAddFriend(PublicUserProfile user) {
        friendRepository.sendFriendRequest(user.getId(), new FriendRepository.FriendRequestCallback() {
            @Override
            public void onSuccess(FriendRequest friendRequest) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Friend request sent to " + user.getUsername());
                        adapter.updateFriendRequestStatus(user.getId(), "PENDING");
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to send friend request: " + error);
                        Log.e(TAG, "Error sending friend request: " + error);
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            rvUsers.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvUsers.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        rvUsers.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        tvEmptyState.setVisibility(View.GONE);
        rvUsers.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
