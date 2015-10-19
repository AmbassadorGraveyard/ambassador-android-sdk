package com.ambassador.ambassadorsdk;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

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
    Context context;
    AmbassadorConfig ambassadorConfig;

    @Inject
    RequestManager requestManager;

    public IdentifyPusher(Context context, AmbassadorConfig ambassadorConfig) {
        this.context = context;
        this.ambassadorConfig = ambassadorConfig;
        AmbassadorSingleton.getComponent().inject(this);
    }

    interface PusherCompletion {
        void pusherEventTriggered(String data);
    }

    public void createPusher() {
        requestManager.createPusherChannel(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                createPusherChannelSuccess(successResponse);
            }

            @Override
            public void onFailure(Object failureResponse) {
                Utilities.debugLog("createPusher", "CREATE PUSHER failed with Response = " + failureResponse);
            }
        });
    }

    void createPusherChannelSuccess(Object successResponse) {
        String sessionId = null;
        String expiresAt = null;
        String channelName = null;
        try {
            JSONObject obj = new JSONObject(successResponse.toString());
            sessionId = obj.getString("client_session_uid");
            channelName = obj.getString("channel_name");
            expiresAt = obj.getString("expires_at");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expiresAtDate = null;
        try {
            expiresAtDate = sdf.parse(expiresAt);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        PusherChannel.setSessionId(sessionId);
        PusherChannel.setChannelName(channelName);
        PusherChannel.setExpiresAt(expiresAtDate);

        if (PusherChannel.isExpired()) {
            createPusher();
            return;
        }

        subscribePusher(ambassadorConfig.getUniversalKey());
    }

    void subscribePusher(String universalToken) {
        // HttpAuthorizer is used to append headers and extra parameters to the initial IdentifyPusher authorization request
        HttpAuthorizer authorizer = new HttpAuthorizer(AmbassadorConfig.pusherCallbackURL());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", universalToken);
        authorizer.setHeaders(headers);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("auth_type", "private");
        queryParams.put("channel", PusherChannel.getchannelName());
        authorizer.setQueryStringParameters(queryParams);

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer);
        options.setEncrypted(true);

        String key = AmbassadorConfig.isReleaseBuild ? AmbassadorConfig.PUSHER_KEY_PROD : AmbassadorConfig.PUSHER_KEY_DEV;
        com.pusher.client.Pusher pusher = new com.pusher.client.Pusher(key, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                Utilities.debugLog("IdentifyPusher", "State changed from " + connectionStateChange.getPreviousState() + " to " + connectionStateChange.getCurrentState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Utilities.debugLog("IdentifyPusher", "There was a problem connecting to IdentifyPusher" + "Exception = " + e);
            }
        }, ConnectionState.ALL);

        pusher.connect();

        pusher.subscribePrivate(PusherChannel.getchannelName(), new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Utilities.debugLog("IdentifyPusher", "Failed to subscribe to IdentifyPusher because " + message + ". The " +
                        "exception was " + e);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Utilities.debugLog("IdentifyPusher", "Successfully subscribed to " + channelName);
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Utilities.debugLog("IdentifyPusher", "data = " + data);

                try {
                    final JSONObject pusherObject = new JSONObject(data);

                    if (pusherObject.has("url")) {
                        requestManager.externalPusherRequest(pusherObject.getString("url"), new RequestManager.RequestCompletion() {
                            @Override
                            public void onSuccess(Object successResponse) {
                                Utilities.debugLog("IdentifyPusher External", "Saved pusher object as String = " + successResponse.toString());

                                try {
                                    final JSONObject pusherUrlObject = new JSONObject(successResponse.toString());
                                    //make sure the request id coming back is for the one we sent off
                                    if (pusherUrlObject.getLong("request_id") != PusherChannel.getRequestId()) return;
                                }
                                catch(JSONException e) {
                                    e.printStackTrace();
                                }
                                setPusherInfo(successResponse.toString());
                            }

                            @Override
                            public void onFailure(Object failureResponse) {
                                Utilities.debugLog("IdentifyPusher External", "FAILED to save pusher object with error: " + failureResponse);
                            }
                        });
                    } else {
                        //make sure the request id coming back is for the one we sent off
                        if (pusherObject.getLong("request_id") != PusherChannel.getRequestId()) return;
                        setPusherInfo(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "identify_action");
    }

    void setPusherInfo(String jsonObject) {
        // Functionality: Saves IdentifyPusher object to SharedPreferences
        JSONObject pusherSave = new JSONObject();

        try {
            JSONObject pusherObject = new JSONObject(jsonObject);

            pusherSave.put("email", pusherObject.getString("email"));
            pusherSave.put("firstName", pusherObject.getString("first_name"));
            pusherSave.put("lastName", pusherObject.getString("last_name"));
            pusherSave.put("phoneNumber", pusherObject.getString("phone"));
            pusherSave.put("urls", pusherObject.getJSONArray("urls"));
            ambassadorConfig.setPusherInfo(pusherSave.toString());

            //tell MainActivity to update edittext with url
            Intent intent = new Intent("pusherData");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
