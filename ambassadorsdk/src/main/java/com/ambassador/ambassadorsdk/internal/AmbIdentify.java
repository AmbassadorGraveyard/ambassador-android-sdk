package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.notifications.GcmHandler;

import javax.inject.Inject;

public class AmbIdentify {

    @Inject protected User user;

    protected String emailAddress;

    public AmbIdentify(String emailAddress) {
        this.emailAddress = emailAddress;
        AmbSingleton.inject(this);
    }

    public void execute(Context context) {
        user.clear();
        user.setEmail(emailAddress);

        new GcmHandler().getRegistrationToken(AmbSingleton.getContext());
        new AugurIdentify().getIdentity(AmbSingleton.getContext());
    }

}
