package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class GuildInvite implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("guild")
    private Guild guild;

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

    public GuildInvite() {
    }

    public GuildInvite(Long id, Guild guild, User sender, User receiver, String status, String createdAt, String respondedAt) {
        this.id = id;
        this.guild = guild;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
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
}
