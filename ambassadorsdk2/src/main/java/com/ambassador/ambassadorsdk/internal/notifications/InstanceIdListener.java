package com.ambassador.ambassadorsdk.internal.notifications;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceIDListenerService;

public final class InstanceIdListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String id = gcm.register("440421303402");
        } catch (Exception e) {
            // wahtever
        }
    }

}
