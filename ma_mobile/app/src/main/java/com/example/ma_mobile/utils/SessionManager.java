package com.example.ma_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ma_mobile.models.User;
import com.google.gson.Gson;

public class SessionManager {

    private static final String PREF_NAME = "MyAppSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
    }

    public void saveUserSession(String token, User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }
    public User getUser() {
        String userJson = preferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    public String getBearerToken() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }
    public void updateUser(User user) {
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
