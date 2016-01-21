package com.ambassador.ambassadorsdk.internal.broadcasts;

import android.app.IntentService;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmListener extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
    }

}
