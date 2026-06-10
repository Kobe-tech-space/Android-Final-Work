package com.example.finalwork.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "campus_ai_session";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_API_KEY_PREFIX = "api_key_";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void login(String username) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).putString(KEY_USERNAME, username).apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "同学");
    }

    /** 保存当前登录用户的 API Key */
    public void saveApiKey(String apiKey) {
        String username = getUsername();
        if (!username.isEmpty()) {
            prefs.edit().putString(KEY_API_KEY_PREFIX + username, apiKey).apply();
        }
    }

    /** 获取当前登录用户的 API Key */
    public String getApiKey() {
        String username = getUsername();
        return prefs.getString(KEY_API_KEY_PREFIX + username, "");
    }

    /** 检查当前用户是否已配置 API Key */
    public boolean isApiKeyConfigured() {
        String key = getApiKey();
        return key != null && !key.trim().isEmpty();
    }
}
