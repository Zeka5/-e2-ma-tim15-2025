package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.ma_mobile.adapter.PendingRequestsAdapter;
import com.example.ma_mobile.models.FriendRequest;
import com.example.ma_mobile.repository.FriendRepository;

import java.util.List;

public class PendingRequestsFragment extends Fragment implements PendingRequestsAdapter.OnRequestActionListener {

    private static final String TAG = "PendingRequestsFragment";

    private ImageButton btnBack;
    private TextView tvRequestsCount;
    private RecyclerView rvPendingRequests;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;

    private PendingRequestsAdapter adapter;
    private FriendRepository friendRepository;

    public PendingRequestsFragment() {
        // Required empty public constructor
    }

    public static PendingRequestsFragment newInstance() {
        return new PendingRequestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendRepository = new FriendRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadPendingRequests();
    }

    private void initializeViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        tvRequestsCount = view.findViewById(R.id.tv_requests_count);
        rvPendingRequests = view.findViewById(R.id.rv_pending_requests);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new PendingRequestsAdapter(this);
        rvPendingRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingRequests.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadPendingRequests() {
        showLoading(true);
        hideEmptyState();

        friendRepository.getReceivedRequests(new FriendRepository.FriendRequestListCallback() {
            @Override
            public void onSuccess(List<FriendRequest> requests) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (requests.isEmpty()) {
                            showEmptyState();
                        } else {
                            adapter.setRequests(requests);
                            tvRequestsCount.setText(String.valueOf(requests.size()));
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
                        showToast("Failed to load pending requests: " + error);
                        Log.e(TAG, "Error loading pending requests: " + error);
                        showEmptyState();
                    });
                }
            }
        });
    }

    @Override
    public void onAcceptRequest(FriendRequest request) {
        friendRepository.acceptFriendRequest(request.getId(), new FriendRepository.FriendRequestCallback() {
            @Override
            public void onSuccess(FriendRequest acceptedRequest) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Friend request accepted!");
                        adapter.removeRequest(request);
                        updateRequestCount();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to accept request: " + error);
                        Log.e(TAG, "Error accepting request: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onRejectRequest(FriendRequest request) {
        friendRepository.rejectFriendRequest(request.getId(), new FriendRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Friend request rejected");
                        adapter.removeRequest(request);
                        updateRequestCount();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to reject request: " + error);
                        Log.e(TAG, "Error rejecting request: " + error);
                    });
                }
            }
        });
    }

    private void updateRequestCount() {
        int count = adapter.getItemCount();
        tvRequestsCount.setText(String.valueOf(count));
        if (count == 0) {
            showEmptyState();
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            rvPendingRequests.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvPendingRequests.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        llEmptyState.setVisibility(View.VISIBLE);
        rvPendingRequests.setVisibility(View.GONE);
        tvRequestsCount.setText("0");
    }

    private void hideEmptyState() {
        llEmptyState.setVisibility(View.GONE);
        rvPendingRequests.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
