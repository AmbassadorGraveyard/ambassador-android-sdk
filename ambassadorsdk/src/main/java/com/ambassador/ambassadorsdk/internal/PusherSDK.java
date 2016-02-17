package com.ambassador.ambassadorsdk.internal;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class PusherSDK { // TODO: Make final after UI tests figured out

    protected IdentifyListener identifyListener;

    @Inject protected Auth auth;
    @Inject protected User user;

    @Inject
    protected RequestManager requestManager;

    public PusherSDK() {
        AmbassadorSingleton.getInstanceComponent().inject(this);
    }

    public void createPusher(final PusherSubscribeCallback pusherSubscribeCallback) {
        requestManager.createPusherChannel(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                //following code checks for a possible race condition
                //SCENARIO: client app loads, identify gets called, but before the response
                //comes back (thus before we have a pusher channel) the user clicks presentRAF
                //in this case, the AmbassadorActivity sees that no channel is there and attempts
                //to create one. Now we have two channels set up. So in the onSuccess of createPusher
                //check if one exists. Therefore we only use the channel that was subscribed to first
                if (PusherChannel.getSessionId() == null || PusherChannel.isExpired()) {
                    setupPusher(successResponse, pusherSubscribeCallback);
                }
            }

            @Override
            public void onFailure(Object failureResponse) {
                if (pusherSubscribeCallback != null) {
                    pusherSubscribeCallback.pusherFailed();
                }
                Utilities.debugLog("createPusher", "CREATE PUSHER failed with Response = " + failureResponse);
            }
        });
    }

    void setupPusher(Object successResponse, PusherSubscribeCallback pusherSubscribeCallback) {
        String sessionId = null;
        String channelName = null;
        String expiresAt = null;

        IdentifyApi.CreatePusherChannelResponse resp = null;
        try {
            resp = (IdentifyApi.CreatePusherChannelResponse) successResponse;
        } catch (ClassCastException e) {

        }

        if (resp != null) {
            sessionId = resp.client_session_uid;
            channelName = resp.channel_name;
            expiresAt = resp.expires_at;
        } else {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expiresAtDate = null;
        try {
            expiresAtDate = sdf.parse(expiresAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PusherChannel.setExpiresAt(expiresAtDate);
        if (PusherChannel.isExpired()) {
            createPusher(pusherSubscribeCallback);
            return;
        }
        PusherChannel.setSessionId(sessionId);
        PusherChannel.setChannelName(channelName);

        subscribePusher(pusherSubscribeCallback);
    }

    public void subscribePusher(final PusherSubscribeCallback pusherSubscribeCallback) {
        // HttpAuthorizer is used to append headers and extra parameters to the initial PusherSDK authorization request
        int res = BuildConfig.IS_RELEASE_BUILD ? R.string.pusher_callback_url : R.string.pusher_callback_url_dev;
        String callback = new StringResource(res).getValue();
        HttpAuthorizer authorizer = new HttpAuthorizer(callback);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", auth.getUniversalToken());
        authorizer.setHeaders(headers);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("auth_type", "private");
        queryParams.put("channel", PusherChannel.getChannelName());
        authorizer.setQueryStringParameters(queryParams);

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer);
        options.setEncrypted(true);

        String pusherProd = new StringResource(R.string.pusher_key_prod).getValue();
        String pusherDev = new StringResource(R.string.pusher_key_dev).getValue();
        String key = BuildConfig.IS_RELEASE_BUILD ? pusherProd : pusherDev;
        final Pusher pusher = new Pusher(key, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                Utilities.debugLog("PusherSDK", "State changed from " + connectionStateChange.getPreviousState() + " to " + connectionStateChange.getCurrentState());
                PusherChannel.setConnectionState(pusher.getConnection().getState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Utilities.debugLog("PusherSDK", "There was a problem connecting to PusherSDK" + "Exception = " + s);
            }
        }, ConnectionState.ALL);

        pusher.subscribePrivate(PusherChannel.getChannelName(), new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Utilities.debugLog("PusherSDK", "Failed to subscribe to PusherSDK because " + message + ". The " +
                        "exception was " + e);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                if (pusherSubscribeCallback != null) {
                    pusherSubscribeCallback.pusherSubscribed();
                }
                Utilities.debugLog("PusherSDK", "Successfully subscribed to " + channelName);
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Utilities.debugLog("PusherSDK", "data = " + data);

                try {
                    final JSONObject pusherObject = new JSONObject(data);

                    if (pusherObject.has("url")) {
                        requestManager.externalPusherRequest(pusherObject.getString("url"), new RequestManager.RequestCompletion() {
                            @Override
                            public void onSuccess(Object successResponse) {
                                Utilities.debugLog("PusherSDK External", "Saved pusher object as String = " + successResponse.toString());

                                try {
                                    final JSONObject pusherUrlObject = new JSONObject(successResponse.toString());

                                    //make sure the request id coming back is for the one we sent off
                                    if (pusherUrlObject.getLong("request_id") != PusherChannel.getRequestId()) {
                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                setPusherInfo(successResponse.toString());
                            }

                            @Override
                            public void onFailure(Object failureResponse) {
                                Utilities.debugLog("PusherSDK External", "FAILED to save pusher object with error: " + failureResponse);
                            }
                        });
                    } else {
                        //make sure the request id coming back is for the one we sent off
                        if (pusherObject.getLong("request_id") != PusherChannel.getRequestId()) {
                            return;
                        }
                        if (identifyListener != null) {
                            identifyListener.identified(PusherChannel.getRequestId());
                        }
                        
                        setPusherInfo(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "identify_action");
    }

    void setPusherInfo(String jsonObject) {
        // Functionality: Saves PusherSDK object to SharedPreferences
        JsonObject pusherSave = new JsonObject();

        JsonObject pusherRootObject = new JsonParser().parse(jsonObject).getAsJsonObject();
        JsonObject pusherObject = (JsonObject) new JsonParser().parse(pusherRootObject.get("body").toString());

        JsonElement eleEmail = pusherObject.get("email");
        pusherSave.add("email", eleEmail);

        JsonElement eleFirstName = pusherObject.get("first_name");
        pusherSave.add("firstName", eleFirstName);

        JsonElement eleLastName = pusherObject.get("last_name");
        pusherSave.add("lastName", eleLastName);

        JsonElement elePhone = pusherObject.get("phone");
        pusherSave.add("phoneNumber", elePhone);

        pusherSave.add("urls", pusherObject.get("urls").getAsJsonArray());
        user.setPusherInfo(pusherSave);

        //update full name for SMS sending "from" name
        user.setFirstName(pusherObject.get("first_name").getAsString());
        user.setLastName(pusherObject.get("last_name").getAsString());

        //tell MainActivity to update edittext with url
        Intent intent = new Intent("pusherData");
        LocalBroadcastManager.getInstance(AmbassadorSingleton.getInstanceContext()).sendBroadcast(intent);
    }

    public void setIdentifyListener(IdentifyListener listener) {
        this.identifyListener = listener;
    }

    public interface PusherSubscribeCallback {
        void pusherSubscribed();
        void pusherFailed();
    }

    public interface IdentifyListener {
        void identified(long requestId);
    }

}