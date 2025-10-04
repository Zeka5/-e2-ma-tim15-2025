package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class FriendRequest {
    @SerializedName("id")
    private Long id;

    @SerializedName("sender")
    private User sender;

    @SerializedName("receiver")
    private User receiver;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("respondedAt")
    private String respondedAt;

    public FriendRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(String respondedAt) {
        this.respondedAt = respondedAt;
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isAccepted() {
        return "ACCEPTED".equals(status);
    }
}
