package com.example.ma_mobile.models.auth;

import com.example.ma_mobile.models.User;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    @SerializedName("expirationDate")
    private String expirationDate;

    public String getToken() { return token; }
    public User getUser() { return user; }
    public String getExpirationDate() {return expirationDate;}
}
