package com.example.ma_mobile.models;

import com.example.ma_mobile.R;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("avatarId")
    private Integer avatarId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("gameStats")
    private UserGameStats gameStats;

    @SerializedName("currentGuild")
    private Guild currentGuild;

    @SerializedName("guildId")
    private Long guildId;

    @SerializedName("guildName")
    private String guildName;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public UserGameStats getGameStats() {
        return gameStats;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setGameStats(UserGameStats gameStats) {
        this.gameStats = gameStats;
    }

    public Guild getCurrentGuild() {
        return currentGuild;
    }

    public void setCurrentGuild(Guild currentGuild) {
        this.currentGuild = currentGuild;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public int getAvatarDrawableId() {
        switch (avatarId != null ? avatarId : 1) {
            case 1: return R.drawable.avatar_1;
            case 2: return R.drawable.avatar_2;
            case 3: return R.drawable.avatar_3;
            case 4: return R.drawable.avatar_4;
            case 5: return R.drawable.avatar_5;
            default: return R.drawable.avatar_1;
        }
    }
}
