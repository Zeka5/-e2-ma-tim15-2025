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

public class GuildMemberAdapter extends RecyclerView.Adapter<GuildMemberAdapter.MemberViewHolder> {

    private List<User> members = new ArrayList<>();
    private Long leaderId;
    private OnMemberActionListener listener;

    public interface OnMemberActionListener {
        void onViewMemberProfile(User member);
    }

    public GuildMemberAdapter(OnMemberActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guild_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User member = members.get(position);
        holder.bind(member, leaderId);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setMembers(List<User> members, Long leaderId) {
        this.members = members != null ? members : new ArrayList<>();
        this.leaderId = leaderId;
        notifyDataSetChanged();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvRole;
        private Button btnViewProfile;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvRole = itemView.findViewById(R.id.tv_role);
            btnViewProfile = itemView.findViewById(R.id.btn_view_profile);
        }

        public void bind(User member, Long leaderId) {
            tvUsername.setText(member.getUsername());

            if (member.getAvatarId() != null) {
                ivAvatar.setImageResource(member.getAvatarDrawableId());
            }

            // Show leader badge if this member is the leader
            if (leaderId != null && leaderId.equals(member.getId())) {
                tvRole.setText("Leader");
                tvRole.setVisibility(View.VISIBLE);
            } else {
                tvRole.setVisibility(View.GONE);
            }

            btnViewProfile.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewMemberProfile(member);
                }
            });
        }
    }
}
