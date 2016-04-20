package com.ambassador.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ambassador.demo.activities.login.LoginActivity;
import com.ambassador.demo.activities.main.MainActivity;
import com.ambassador.demo.data.User;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class<?> activity = User.isStored() ? MainActivity.class : LoginActivity.class;
        Intent intent = new Intent(this, activity)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishActivity(0);
    }

}
