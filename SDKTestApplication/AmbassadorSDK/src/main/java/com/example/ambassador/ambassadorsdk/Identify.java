package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;


/**
 * Created by JakeDunahee on 9/1/15.
 */
class Identify implements IIdentify {
    private Context context;
    private IdentifyPusher pusher;
    private String identifier;
    IdentifyAugurSDK augur;
    private Timer augurTimer;

    @Inject
    IdentifyRequest identifyRequest;

    public Identify(Context context, String identifier) {
        this.context = context;
        this.identifier = identifier;
        if (pusher == null) {
            pusher = new IdentifyPusher();
            augur = new IdentifyAugurSDK();
            augurTimer = new Timer();
        }

        //get injected modules we need
        MyApplication.getComponent().inject(this);
    }

    @Override
    public void getIdentity() {
        augurTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                augur.getAugur(new IdentifyAugurSDK.AugurCompletion() {
                    @Override
                    public void augurComplete() {
                        setUpPusher(augur.deviceID);
                        augurTimer.cancel();
                    }
                });
            }
        }, 0, 5000);
    }

    void setUpPusher(String deviceID) {
        pusher.createPusher(deviceID, new IdentifyPusher.PusherCompletion() {
            @Override
            public void pusherEventTriggered(String data) {
                try {
                    JSONObject pusherObject = new JSONObject(data);
                    if (pusherObject.has("url")) {
                        RequestManager.getInstance().externalPusherRequest(pusherObject.getString("url"), new RequestManager.RequestCompletion() {
                            @Override
                            public void onSuccess(String successResponse) {
                                Utilities.debugLog("Pusher External", "Saved pusher object as String = " + successResponse.toString());
                                _getAndSavePusherInfo(successResponse.toString());
                            }

                            @Override
                            public void onFailure(String failureResponse) {
                                Utilities.debugLog("Pusher External", "FAILED to save pusher object with error: " + failureResponse);
                            }
                        });
                    } else {
                        _getAndSavePusherInfo(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void _getAndSavePusherInfo(String jsonObject) {
        // Functionality: Saves Pusher object to SharedPreferences
        JSONObject pusherSave = new JSONObject();

        try {
            JSONObject pusherObject = new JSONObject(jsonObject);

            pusherSave.put("email", pusherObject.getString("email"));
            pusherSave.put("firstName", pusherObject.getString("first_name"));
            pusherSave.put("lastName", pusherObject.getString("last_name"));
            pusherSave.put("phoneNumber", pusherObject.getString("phone"));
            pusherSave.put("urls", pusherObject.getJSONArray("urls"));

            AmbassadorSingleton.getInstance().savePusherInfo(pusherSave.toString());
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
