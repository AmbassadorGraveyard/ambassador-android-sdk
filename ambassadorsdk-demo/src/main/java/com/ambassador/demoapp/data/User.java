package com.ambassador.demoapp.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.api.pojo.LoginResponse;
import com.google.gson.Gson;

public class User {

    protected static User instance;

    protected String universalId;
    protected String universalToken;
    protected String sdkToken;
    protected String avatarUrl;
    protected String name;

    public User() {

    }

    public String getUniversalId() {
        return universalId;
    }

    public String getUniversalToken() {
        return universalToken;
    }

    public String getSdkToken() {
        return sdkToken;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void load(LoginResponse loginResponse) {
        universalId = loginResponse.company.universal_id;
        universalToken = loginResponse.company.universal_token;
        sdkToken = loginResponse.company.sdk_token;
        avatarUrl = loginResponse.company.avatar_url;
        name = loginResponse.company.first_name + " " + loginResponse.company.last_name;
        save();
    }

    protected void save() {
        String serialized = new Gson().toJson(this);
        Demo.get().getSharedPreferences("user", Context.MODE_PRIVATE).edit().putString("user", serialized).apply();
    }

    @Nullable
    protected static User restore() {
        String serialized = Demo.get().getSharedPreferences("user", Context.MODE_PRIVATE).getString("user", null);
        User user = null;
        if (serialized != null) {
            user = new Gson().fromJson(serialized, User.class);
        }

        return user;
    }

    public static boolean isStored() {
        return instance != null && (instance.universalId != null && instance.sdkToken != null);
    }

    @NonNull
    public static User get() {
        if (instance == null) {
            User user = restore();
            if (user != null) {
                instance = user;
                return instance;
            }

            instance = new User();
        }

        return instance;
    } 

}
