package com.ambassador.ambassadorsdk.internal.notifications;

import android.content.Context;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import javax.inject.Inject;

public final class GcmHandler {

    @Inject protected User user;
    @Inject protected RequestManager requestManager;

    public GcmHandler() {
        AmbSingleton.inject(this);
    }

    public void getRegistrationToken(final Context context) {
        if (checkPlayServices(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                        String token = gcm.register("440421303402");
                        user.setGcmToken(token);
                        Log.v("AMB_GCM", token);
                        requestManager.updateGcmRegistrationToken(user.getEmail(), user.getGcmToken(), null);
                    } catch (IOException e) {
                        Log.v("AmbassadorSDK", e.toString());
                    }
                }
            }).start();
        }
    }

    protected boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

}
