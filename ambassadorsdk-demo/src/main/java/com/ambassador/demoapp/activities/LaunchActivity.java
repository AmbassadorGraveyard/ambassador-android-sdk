package com.ambassador.demoapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ambassador.demoapp.api.pojo.LoginResponse;
import com.ambassador.demoapp.data.User;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.company = new LoginResponse.Company();
        loginResponse.company.universal_token = "f5746f0f2146b569188e2b5d21cb29c6942cf2e0";
        loginResponse.company.sdk_token = "bdd5d626d2f1e2fe8aade0340c73ee1dda9725f0";
        loginResponse.company.avatar_url = "https://ambassador-api-dev.s3.amazonaws.com/uploads/avatars/40846_2015_02_25_21_02_07.jpg";
        loginResponse.company.universal_id = "eff56c65-7b2e-484c-aaf7-3f71f7f418da";
        User.get().load(loginResponse);
        Class<?> activity = User.isStored() ? MainActivity.class : LoginActivity.class;
        activity = CustomizationActivity.class;
        Intent intent = new Intent(this, activity)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishActivity(0);
    }

}
