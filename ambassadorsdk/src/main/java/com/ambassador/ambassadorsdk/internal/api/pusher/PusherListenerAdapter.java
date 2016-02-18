package com.ambassador.ambassadorsdk.internal.api.pusher;


import com.ambassador.ambassadorsdk.internal.api.PusherManager;

/**
 * Adapter class for the PusherListener interface. Serves to be overriden and passed as the listener
 * to prevent implementing un-needed callbacks.
 */
public abstract class PusherListenerAdapter implements PusherManager.PusherListener {

    @Override
    public void connected() {
        // Adapter, intentionally empty.
    }

    @Override
    public void disconnected() {
        // Adapter, intentionally empty.
    }

    @Override
    public void subscribed() {
        // Adapter, intentionally empty.
    }

    @Override
    public void unsubscribed() {
        // Adapter, intentionally empty.
    }

    @Override
    public void connectionFailed() {
        // Adapter, intentionally empty.
    }

    @Override
    public void subscriptionFailed() {
        // Adapter, intentionally empty.
    }

    @Override
    public void onEvent(String data) {
        // Adapter, intentionally empty.
    }

}