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

public class FriendInviteGuildAdapter extends RecyclerView.Adapter<FriendInviteGuildAdapter.FriendViewHolder> {

    private List<User> friends = new ArrayList<>();
    private OnInviteClickListener listener;

    public interface OnInviteClickListener {
        void onInviteClick(User friend);
    }

    public FriendInviteGuildAdapter(OnInviteClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_invite_guild, parent, false);
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

    public void setFriends(List<User> friends) {
        this.friends = friends != null ? friends : new ArrayList<>();
        notifyDataSetChanged();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private Button btnInvite;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            btnInvite = itemView.findViewById(R.id.btn_invite);
        }

        public void bind(User friend) {
            tvUsername.setText(friend.getUsername());

            if (friend.getAvatarId() != null) {
                ivAvatar.setImageResource(friend.getAvatarDrawableId());
            }

            btnInvite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInviteClick(friend);
                }
            });
        }
    }
}
