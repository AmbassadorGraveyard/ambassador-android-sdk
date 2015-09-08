package com.example.ambassador.ambassadorsdk;

import android.util.Log;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import java.util.HashMap;

/**
 * Created by JakeDunahee on 9/1/15.
 */
class IdentifyPusher {
    interface PusherCompletion {
        void pusherSubscribed();
        void pusherEventTriggered(String data);
    }

    public void createPusher(String augurDeviceID, final PusherCompletion completion) {
        // Functionality: Subcribes to Pusher channel and sets listener for pusher action
        String channelName = "private-snippet-channel@user=" + augurDeviceID;

        // HttpAuthorizer is used to append headers and extra parameters to the initial Pusher authorization request
        HttpAuthorizer authorizer = new HttpAuthorizer("https://dev-ambassador-api.herokuapp.com/auth/subscribe/");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", AmbassadorSingleton.getInstance().getAPIKey());
        authorizer.setHeaders(headers);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("auth_type", "private");
        queryParams.put("channel", channelName);
        authorizer.setQueryStringParameters(queryParams);

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer);
        options.setEncrypted(true);

        Pusher pusher = new Pusher(AmbassadorSingleton.PUSHER_KEY, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                Log.d("Pusher", "State changed from " + connectionStateChange.getPreviousState() + " to " + connectionStateChange.getCurrentState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Log.d("Pusher", "There was a problem connecting to Pusher" + "Exception = " + e);
            }
        }, ConnectionState.ALL);

        pusher.connect();

        pusher.subscribePrivate(channelName, new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.d("Pusher", "Failed to subscribe to Pusher because " + message + ". The " +
                        "exception was " + e);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d("Pusher", "Successfully subscribed to " + channelName);
                completion.pusherSubscribed();
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Log.d("Pusher", "data = " + data);
                completion.pusherEventTriggered(data);
            }
        }, "identify_action");
    }
}
