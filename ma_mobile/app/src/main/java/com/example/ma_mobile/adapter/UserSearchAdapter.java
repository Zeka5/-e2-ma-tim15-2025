package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.FriendRequest;
import com.example.ma_mobile.models.PublicUserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {

    private List<PublicUserProfile> users;
    private Map<Long, String> friendRequestStatus; // userId -> "PENDING" or "ACCEPTED"
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onViewProfile(PublicUserProfile user);
        void onAddFriend(PublicUserProfile user);
    }

    public UserSearchAdapter(OnUserActionListener listener) {
        this.users = new ArrayList<>();
        this.friendRequestStatus = new HashMap<>();
        this.listener = listener;
    }

    public void setUsers(List<PublicUserProfile> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void setFriendRequests(List<FriendRequest> requests) {
        friendRequestStatus.clear();
        for (FriendRequest request : requests) {
            // Store status for both sender and receiver
            // This handles both outgoing requests (we sent) and incoming requests (we received)
            if (request.getSender() != null && request.getSender().getId() != null) {
                friendRequestStatus.put(request.getSender().getId(), request.getStatus());
            }
            if (request.getReceiver() != null && request.getReceiver().getId() != null) {
                friendRequestStatus.put(request.getReceiver().getId(), request.getStatus());
            }
        }
        notifyDataSetChanged();
    }

    public void updateFriendRequestStatus(Long userId, String status) {
        friendRequestStatus.put(userId, status);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_search, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        PublicUserProfile user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvTitle;
        private TextView tvLevel;
        private Button btnViewProfile;
        private Button btnAddFriend;
        private Button btnPending;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvTitle = itemView.findViewById(R.id.tv_user_title);
            tvLevel = itemView.findViewById(R.id.tv_user_level);
            btnViewProfile = itemView.findViewById(R.id.btn_view_profile);
            btnAddFriend = itemView.findViewById(R.id.btn_add_friend);
            btnPending = itemView.findViewById(R.id.btn_pending);
        }

        public void bind(PublicUserProfile user) {
            // Set user data
            tvUsername.setText(user.getUsername() != null ? user.getUsername() : "Unknown");
            tvTitle.setText(user.getTitle() != null ? user.getTitle() : "Adventurer");
            tvLevel.setText(user.getLevel() != null ? String.valueOf(user.getLevel()) : "1");

            // Set avatar
            if (user.getAvatarId() != null) {
                ivAvatar.setImageResource(user.getAvatarDrawableId());
            }

            // Handle button visibility based on friend request status
            String status = friendRequestStatus.get(user.getId());

            if ("ACCEPTED".equals(status)) {
                // Already friends - only show view button
                btnAddFriend.setVisibility(View.GONE);
                btnPending.setVisibility(View.GONE);
                btnViewProfile.setVisibility(View.VISIBLE);
            } else if ("PENDING".equals(status)) {
                // Request pending - show disabled pending button
                btnAddFriend.setVisibility(View.GONE);
                btnPending.setVisibility(View.VISIBLE);
                btnViewProfile.setVisibility(View.VISIBLE);
            } else {
                // No request - show add friend button
                btnAddFriend.setVisibility(View.VISIBLE);
                btnPending.setVisibility(View.GONE);
                btnViewProfile.setVisibility(View.VISIBLE);
            }

            // Set click listeners
            btnViewProfile.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewProfile(user);
                }
            });

            btnAddFriend.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddFriend(user);
                }
            });
        }
    }
}
