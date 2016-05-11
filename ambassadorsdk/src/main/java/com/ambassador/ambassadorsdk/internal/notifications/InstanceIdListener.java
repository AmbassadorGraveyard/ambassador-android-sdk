package com.ambassador.ambassadorsdk.internal.notifications;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceIDListenerService;

import javax.inject.Inject;

public final class InstanceIdListener extends InstanceIDListenerService {

    @Inject protected RequestManager requestManager;
    @Inject protected User user;

    @Override
    public void onTokenRefresh() {
        try {
            AmbSingleton.inject(this);
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String email = user.getAmbassadorIdentification().getEmail();
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
