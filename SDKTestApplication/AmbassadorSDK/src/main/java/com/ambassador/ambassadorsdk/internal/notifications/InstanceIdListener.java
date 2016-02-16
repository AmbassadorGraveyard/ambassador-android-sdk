package com.ambassador.ambassadorsdk.internal.notifications;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceIDListenerService;

import javax.inject.Inject;

public final class InstanceIdListener extends InstanceIDListenerService {

    @Inject protected RequestManager requestManager;
    @Inject protected AmbassadorConfig ambassadorConfig;

    @Override
    public void onTokenRefresh() {
        try {
            AmbassadorSingleton.getInstanceComponent().inject(this);
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String email = ambassadorConfig.getUserEmail();
            String id = gcm.register("440421303402");
            requestManager.updateGcmRegistrationToken(email, id, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    // No reaction required
                }

                @Override
                public void onFailure(Object failureResponse) {
                    // No reaction required
                }
            });
        } catch (Exception e) {
            // whatever
        }
    }

}
