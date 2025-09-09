package com.example.ma_mobile.models.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("avatarId")
    private Integer avatarId;

    public RegisterRequest(String username, String email, String password, Integer avatarId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatarId = avatarId;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Integer getAvatarId() { return avatarId; }
}
