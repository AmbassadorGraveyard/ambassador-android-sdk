package com.ambassador.ambassadorsdk.internal.notifications;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmListener extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.v("AMBGCM", "hit");
        super.onMessageReceived(from, data);
    }

}
