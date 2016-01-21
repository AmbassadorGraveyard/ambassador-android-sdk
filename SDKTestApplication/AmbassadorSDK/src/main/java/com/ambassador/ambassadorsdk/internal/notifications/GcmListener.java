package com.ambassador.ambassadorsdk.internal.notifications;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public final class GcmListener extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Log.v("AMB-GCM", "hit");

        // use data.getString(KEY) to get key values from the data block of the post req
    }

}
