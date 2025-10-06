package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("id")
    private Long id;

    @SerializedName("guildId")
    private Long guildId;

    @SerializedName("senderId")
    private Long senderId;

    @SerializedName("senderUsername")
    private String senderUsername;

    @SerializedName("senderAvatarId")
    private Integer senderAvatarId;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    public ChatMessage() {
    }

    public ChatMessage(Long guildId, String content) {
        this.guildId = guildId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public Integer getSenderAvatarId() {
        return senderAvatarId;
    }

    public void setSenderAvatarId(Integer senderAvatarId) {
        this.senderAvatarId = senderAvatarId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getSenderAvatarDrawableId() {
        if (senderAvatarId == null) return com.example.ma_mobile.R.drawable.avatar_1;
        switch (senderAvatarId) {
            case 1: return com.example.ma_mobile.R.drawable.avatar_1;
            case 2: return com.example.ma_mobile.R.drawable.avatar_2;
            case 3: return com.example.ma_mobile.R.drawable.avatar_3;
            case 4: return com.example.ma_mobile.R.drawable.avatar_4;
            case 5: return com.example.ma_mobile.R.drawable.avatar_5;
            default: return com.example.ma_mobile.R.drawable.avatar_1;
        }
    }
}
