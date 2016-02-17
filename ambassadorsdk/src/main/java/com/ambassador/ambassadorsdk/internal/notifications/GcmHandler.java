package com.ambassador.ambassadorsdk.internal.notifications;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public final class GcmHandler {

    protected Context context;

    public GcmHandler(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    public void getRegistrationToken(@NonNull final RegistrationListener registrationListener) {
        if (checkPlayServices()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                        String token = gcm.register("440421303402");
                        registrationListener.registrationSuccess(token);
                    } catch (IOException e) {
                        registrationListener.registrationFailure(e);
                    }
                }
            }).start();
        } else {
            registrationListener.registrationFailure(new RuntimeException("Google Play Services out of date."));
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public interface RegistrationListener {
        void registrationSuccess(String token);
        void registrationFailure(Throwable e);
    }

}
