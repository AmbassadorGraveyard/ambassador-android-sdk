package com.ambassador.ambassadorsdk.internal.identify;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class AmbGcmTokenTask extends AmbIdentifyTask {

    @Override
    public void execute(final OnCompleteListener onCompleteListener) throws Exception {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(AmbSingleton.getContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            onCompleteListener.complete();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(AmbSingleton.getContext());
                    String token = gcm.register("440421303402");
                    user.setGcmToken(token);
                    onCompleteListener.complete();
                } catch (Exception e) {
                    onCompleteListener.complete();
                }
            }
        });
    }

}
