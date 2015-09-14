package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by JakeDunahee on 9/1/15.
 */
class Identify implements IIdentify {
    private Context context;
    private String emailAddress;
    private IdentifyPusher pusher;
    private IdentifyAugurSDK augur;

    public Identify(Context context, String emailAddress) {
        this.context = context;
        this.emailAddress = emailAddress;
        if (pusher == null) {
            pusher = new IdentifyPusher();
            augur = new IdentifyAugurSDK();
        }
    }

    @Override
    public void getIdentity() {
        augur.getAugur(new IdentifyAugurSDK.AugurCompletion() {
            @Override
            public void augurComplete() {
                _setUpPusher(augur.deviceID);
            }
        });
    }

    private void _setUpPusher(String deviceID) {
        pusher.createPusher(deviceID, new IdentifyPusher.PusherCompletion() {
            @Override
            public void pusherSubscribed() {
                IdentifyRequest request = new IdentifyRequest();
                request.execute();
            }

            @Override
            public void pusherEventTriggered(String data) {
                _getAndSavePusherInfo(data);
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

    private class IdentifyRequest extends AsyncTask<Void, Void, Void> {
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url = AmbassadorSingleton.identifyURL() +
                    AmbassadorSingleton.getInstance().getUniversalID();

            JSONObject identifyObject = new JSONObject();

            try {
                JSONObject augurObject = new JSONObject(AmbassadorSingleton.getInstance().getIdentifyObject());
                identifyObject.put("email", emailAddress);
                identifyObject.put("source", "android_sdk_pilot");
                identifyObject.put("mbsy_source", "");
                identifyObject.put("mbsy_cookie_code", "");
                identifyObject.put("fp", augurObject);
                Utilities.debugLog("Augur", "Identify Object = " + identifyObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getUniversalKey());

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(identifyObject.toString());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                InputStream is = (Utilities.isSuccessfulResponseCode(statusCode)) ? connection.getInputStream() : connection.getErrorStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                Utilities.debugLog("Pusher", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
