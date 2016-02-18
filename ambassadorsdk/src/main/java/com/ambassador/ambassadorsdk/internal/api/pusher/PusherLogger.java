package com.ambassador.ambassadorsdk.internal.api.pusher;

import android.util.Log;

import com.ambassador.ambassadorsdk.internal.api.PusherManager;

/**
 * Logs all events. Needs to be explicitly added to PusherManager to be called.
 * pusherManager.addPusherListener(new PusherLogger());.
 */
public class PusherLogger implements PusherManager.PusherListener {

    @Override
    public void connected() {
        Log.v("AMB-PUSHER", "connected()");
    }

    @Override
    public void disconnected() {
        Log.v("AMB-PUSHER", "disconnected()");
    }

    @Override
    public void subscribed() {
        Log.v("AMB-PUSHER", "subscribed()");
    }

    @Override
    public void unsubscribed() {
        Log.v("AMB-PUSHER", "unsubscribed()");
    }

    @Override
    public void connectionFailed() {
        Log.v("AMB-PUSHER", "connectionFailed()");
    }

    @Override
    public void subscriptionFailed() {
        Log.v("AMB-PUSHER", "subscriptionFailed()");
    }

    @Override
    public void onEvent(String data) {
        Log.v("AMB-PUSHER", "onEvent(" + data + ")");
    }

}
