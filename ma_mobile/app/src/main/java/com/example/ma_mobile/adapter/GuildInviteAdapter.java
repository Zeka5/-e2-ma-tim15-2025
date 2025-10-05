package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.GuildInvite;

import java.util.ArrayList;
import java.util.List;

public class GuildInviteAdapter extends RecyclerView.Adapter<GuildInviteAdapter.InviteViewHolder> {

    private List<GuildInvite> invites = new ArrayList<>();
    private OnInviteActionListener listener;

    public interface OnInviteActionListener {
        void onAcceptInvite(GuildInvite invite);
        void onRejectInvite(GuildInvite invite);
    }

    public GuildInviteAdapter(OnInviteActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guild_invite, parent, false);
        return new InviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int position) {
        GuildInvite invite = invites.get(position);
        holder.bind(invite);
    }

    @Override
    public int getItemCount() {
        return invites.size();
    }

    public void setInvites(List<GuildInvite> invites) {
        this.invites = invites != null ? invites : new ArrayList<>();
        notifyDataSetChanged();
    }

    class InviteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGuildName;
        private TextView tvSenderName;
        private Button btnAccept;
        private Button btnReject;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuildName = itemView.findViewById(R.id.tv_guild_name);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }

        public void bind(GuildInvite invite) {
            if (invite.getGuild() != null) {
                tvGuildName.setText(invite.getGuild().getName());
            }

            if (invite.getSender() != null) {
                tvSenderName.setText("Invited by: " + invite.getSender().getUsername());
            }

            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptInvite(invite);
                }
            });

            btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectInvite(invite);
                }
            });
        }
    }
}
