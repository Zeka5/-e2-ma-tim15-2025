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
import com.example.ma_mobile.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<User> friends;
    private OnFriendActionListener listener;

    public interface OnFriendActionListener {
        void onViewFriendProfile(User friend);
    }

    public FriendsAdapter(OnFriendActionListener listener) {
        this.friends = new ArrayList<>();
        this.listener = listener;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User friend = friends.get(position);
        holder.bind(friend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvEmail;
        private Button btnViewProfile;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_friend_avatar);
            tvUsername = itemView.findViewById(R.id.tv_friend_username);
            tvEmail = itemView.findViewById(R.id.tv_friend_email);
            btnViewProfile = itemView.findViewById(R.id.btn_view_friend_profile);
        }

        public void bind(User friend) {
            tvUsername.setText(friend.getUsername() != null ? friend.getUsername() : "Unknown");
            tvEmail.setText(friend.getEmail() != null ? friend.getEmail() : "");

            // Set avatar
            if (friend.getAvatarId() != null) {
                ivAvatar.setImageResource(friend.getAvatarDrawableId());
            }

            btnViewProfile.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewFriendProfile(friend);
                }
            });
        }
    }
}
