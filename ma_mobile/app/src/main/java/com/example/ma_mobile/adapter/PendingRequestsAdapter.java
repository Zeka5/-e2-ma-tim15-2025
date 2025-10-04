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
import com.example.ma_mobile.models.User;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.RequestViewHolder> {

    private List<FriendRequest> requests;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAcceptRequest(FriendRequest request);
        void onRejectRequest(FriendRequest request);
    }

    public PendingRequestsAdapter(OnRequestActionListener listener) {
        this.requests = new ArrayList<>();
        this.listener = listener;
    }

    public void setRequests(List<FriendRequest> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    public void removeRequest(FriendRequest request) {
        int position = requests.indexOf(request);
        if (position != -1) {
            requests.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        FriendRequest request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvEmail;
        private TextView tvRequestTime;
        private Button btnAccept;
        private Button btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_sender_avatar);
            tvUsername = itemView.findViewById(R.id.tv_sender_username);
            tvEmail = itemView.findViewById(R.id.tv_sender_email);
            tvRequestTime = itemView.findViewById(R.id.tv_request_time);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }

        public void bind(FriendRequest request) {
            User sender = request.getSender();

            if (sender != null) {
                tvUsername.setText(sender.getUsername() != null ? sender.getUsername() : "Unknown");
                tvEmail.setText(sender.getEmail() != null ? sender.getEmail() : "");

                // Set avatar
                if (sender.getAvatarId() != null) {
                    ivAvatar.setImageResource(sender.getAvatarDrawableId());
                }
            }

            // Format request time
            if (request.getCreatedAt() != null) {
                tvRequestTime.setText(formatTime(request.getCreatedAt()));
            }

            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptRequest(request);
                }
            });

            btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectRequest(request);
                }
            });
        }

        private String formatTime(String createdAt) {
            // Simple time formatting - you can improve this
            // For now, just return a placeholder
            return "Recently";
        }
    }
}
