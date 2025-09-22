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

    @SerializedName("avatar_id")
    private Integer avatarId;

    @SerializedName("created_at")
    private String createdAt;

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

    public String getCreatedAt() {
        return createdAt;
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

    public Integer getAvatarId() { return avatarId; }
    public void setAvatarId(Integer avatarId) { this.avatarId = avatarId; }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
