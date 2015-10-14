package com.ambassador.ambassadorsdk;


import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 9/1/15.
 */
class IdentifyPusher {
    String channelName;

    @Inject
    RequestManager requestManager;

    public IdentifyPusher() {
        AmbassadorSingleton.getComponent().inject(this);
    }

    interface PusherCompletion {
        void pusherEventTriggered(String data);
    }

    public void createPusher(String augurDeviceID, final String universalToken, final PusherCompletion completion) {
        // Functionality: Subscribes to Pusher channel and sets listener for pusher action

        if (augurDeviceID != null) {
            channelName = "private-snippet-channel@user=" + augurDeviceID;
            subscribePusher(universalToken, completion);
            return;
        }

        //if we don't have a deviceID, we need to get a unique pusher channel from our api
        requestManager.createPusherChannel(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                try {
                    JSONObject obj = new JSONObject(successResponse.toString());
                    channelName = obj.getString("channel_name");
                    String expiresAt = obj.getString("expires_at");
                    //String expiresAt = "2011-04-15T20:08:18Z";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date;
                    try {
                        date = sdf.parse(expiresAt);
                        if (date.getTime() < System.currentTimeMillis()) {
                            createPusher(null, universalToken, completion);
                        }
                        else {
                            subscribePusher(universalToken, completion);
                        }
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Object failureResponse) {
                Utilities.debugLog("createPusher", "CREATE PUSHER failed with Response = " + failureResponse);

                //TESTING CODE
/*                channelName = "private-snippet-channel@user=gAAAAABWHreZ0RD2S59JXmfsm1JzOAxlw9dbWlwvcPEM2TWWo49rTFFmLPI2yTmuK8PjQ_GTFpvJX_54LL0-n2cBRd3eM-cJ7YP3Mhhqrswl0JMqfhkgL7lehDq8P6E-3gjn_0FlwdTK-mT5EXCMoa6JjIw_ZLRg7w==";
                String expiresAt = "2015-10-21T20:08:18Z";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date;
                try {
                    date = sdf.parse(expiresAt);
                    if (date.getTime() < System.currentTimeMillis()) {
                        createPusher(null, universalToken, completion);
                    }
                    else {
                        subscribePusher(universalToken, completion);
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }*/

            }
        });
    }

    void subscribePusher(String universalToken, final PusherCompletion completion) {
        // HttpAuthorizer is used to append headers and extra parameters to the initial Pusher authorization request
        HttpAuthorizer authorizer = new HttpAuthorizer(AmbassadorConfig.pusherCallbackURL());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", universalToken);
        authorizer.setHeaders(headers);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("auth_type", "private");
        queryParams.put("channel", channelName);
        authorizer.setQueryStringParameters(queryParams);

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer);
        options.setEncrypted(true);

        String key = AmbassadorConfig.isReleaseBuild ? AmbassadorConfig.PUSHER_KEY_PROD : AmbassadorConfig.PUSHER_KEY_DEV;
        Pusher pusher = new Pusher(key, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                Utilities.debugLog("Pusher", "State changed from " + connectionStateChange.getPreviousState() + " to " + connectionStateChange.getCurrentState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Utilities.debugLog("Pusher", "There was a problem connecting to Pusher" + "Exception = " + e);
            }
        }, ConnectionState.ALL);

        pusher.connect();

        pusher.subscribePrivate(channelName, new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Utilities.debugLog("Pusher", "Failed to subscribe to Pusher because " + message + ". The " +
                        "exception was " + e);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Utilities.debugLog("Pusher", "Successfully subscribed to " + channelName);
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Utilities.debugLog("Pusher", "data = " + data);
                completion.pusherEventTriggered(data);
            }
        }, "identify_action");
    }
}
