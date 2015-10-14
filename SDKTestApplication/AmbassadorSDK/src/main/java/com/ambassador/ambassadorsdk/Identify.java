package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;


/**
 * Created by JakeDunahee on 9/1/15.
 */
class Identify implements IIdentify {
    private Context context;
    private IdentifyPusher pusher;
    IdentifyAugurSDK augur;

    @Inject
    RequestManager requestManager;

    @Inject
    AmbassadorConfig ambassadorConfig;

    public Identify(Context context) {
        this.context = context;

        AmbassadorSingleton.getComponent().inject(this);

        if (pusher == null) {
            pusher = new IdentifyPusher();
            augur = new IdentifyAugurSDK();
        }
    }

    @Override
    public void getIdentity() {
        augur.getAugur(ambassadorConfig, new IdentifyAugurSDK.AugurCompletion() {
            @Override
            public void augurComplete() {
                setUpPusher(augur.getDeviceID());
            }
        });
    }

    void setUpPusher(String deviceID) {
        pusher.createPusher(deviceID, ambassadorConfig.getUniversalKey(), new IdentifyPusher.PusherCompletion() {
            @Override
            public void pusherEventTriggered(String data) {
                try {
                    JSONObject pusherObject = new JSONObject(data);
                    if (pusherObject.has("url")) {
                        requestManager.externalPusherRequest(pusherObject.getString("url"), new RequestManager.RequestCompletion() {
                            @Override
                            public void onSuccess(Object successResponse) {
                                Utilities.debugLog("Pusher External", "Saved pusher object as String = " + successResponse.toString());
                                getAndsetPusherInfo(successResponse.toString());
                            }

                            @Override
                            public void onFailure(Object failureResponse) {
                                Utilities.debugLog("Pusher External", "FAILED to save pusher object with error: " + failureResponse);
                            }
                        });
                    } else {
                        getAndsetPusherInfo(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getAndsetPusherInfo(String jsonObject) {
        // Functionality: Saves Pusher object to SharedPreferences
        JSONObject pusherSave = new JSONObject();

        try {
            JSONObject pusherObject = new JSONObject(jsonObject);

            pusherSave.put("email", pusherObject.getString("email"));
            pusherSave.put("firstName", pusherObject.getString("first_name"));
            pusherSave.put("lastName", pusherObject.getString("last_name"));
            pusherSave.put("phoneNumber", pusherObject.getString("phone"));
            pusherSave.put("urls", pusherObject.getJSONArray("urls"));

            ambassadorConfig.setPusherInfo(pusherSave.toString());
            _sendIdBroadcast(); // Tells MainActivity to update edittext with url
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _sendIdBroadcast() {
        // Functionality: Posts notification to listener once identity is successfully received
        Intent intent = new Intent("pusherData");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
