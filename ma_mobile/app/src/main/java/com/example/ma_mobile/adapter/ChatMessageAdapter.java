package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_OWN = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private List<ChatMessage> messages = new ArrayList<>();
    private Long currentUserId;

    public ChatMessageAdapter(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_OWN;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_OWN) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_own, parent, false);
            return new OwnMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_other, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof OwnMessageViewHolder) {
            ((OwnMessageViewHolder) holder).bind(message);
        } else if (holder instanceof OtherMessageViewHolder) {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    // ViewHolder for own messages (right side)
    static class OwnMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageContent;
        private TextView tvTimestamp;

        public OwnMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageContent = itemView.findViewById(R.id.tv_message_content);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        public void bind(ChatMessage message) {
            tvMessageContent.setText(message.getContent());
            tvTimestamp.setText(formatTime(message.getCreatedAt()));
        }
    }

    // ViewHolder for other messages (left side)
    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvSenderName;
        private TextView tvMessageContent;
        private TextView tvTimestamp;

        public OtherMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
            tvMessageContent = itemView.findViewById(R.id.tv_message_content);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        public void bind(ChatMessage message) {
            tvSenderName.setText(message.getSenderUsername());
            tvMessageContent.setText(message.getContent());
            tvTimestamp.setText(formatTime(message.getCreatedAt()));

            if (message.getSenderAvatarId() != null) {
                ivAvatar.setImageResource(message.getSenderAvatarDrawableId());
            }
        }
    }

    private static String formatTime(String createdAt) {
        if (createdAt == null) return "";
        try {
            // Parse ISO datetime and extract time
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(createdAt, inputFormatter);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            return "";
        }
    }
}
