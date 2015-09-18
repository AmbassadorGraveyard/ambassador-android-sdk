package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by JakeDunahee on 9/1/15.
 */
class Identify implements IIdentify {
    private Context context;
    private String emailAddress;
    private IdentifyPusher pusher;
    IdentifyAugurSDK augur;
    private Timer augurTimer;

    public Identify(Context context, String emailAddress) {
        this.context = context;
        this.emailAddress = emailAddress;
        if (pusher == null) {
            pusher = new IdentifyPusher();
            augur = new IdentifyAugurSDK();
            augurTimer = new Timer();
        }
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
            public void pusherSubscribed() {
                IdentifyRequest request = new IdentifyRequest();
                request.execute();
            }

            @Override
            public void pusherEventTriggered(String data) {
                try {
                    JSONObject pusherObject = new JSONObject(data);
                    if (pusherObject.has("url")) {
                        PusherURLRequest pusherURLRequest = new PusherURLRequest(pusherObject.getString("url"));
                        pusherURLRequest.execute();
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

    private class IdentifyRequest extends AsyncTask<Void, Void, Void> {
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url = AmbassadorSingleton.identifyURL() +
                    AmbassadorSingleton.getInstance().getUniversalID();

            JSONObject identifyObject = new JSONObject();

            try {
                JSONObject augurObject = new JSONObject(AmbassadorSingleton.getInstance().getIdentifyObject());
                identifyObject.put("enroll", true);
                identifyObject.put("campaign_id", AmbassadorSingleton.getInstance().getCampaignID());
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

    class PusherURLRequest extends AsyncTask<Void, Void, Void> {
        String url;
        int statusCode;
        StringBuilder response;

        public PusherURLRequest(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getUniversalKey());

                statusCode = connection.getResponseCode();

                InputStream is = (Utilities.isSuccessfulResponseCode(statusCode)) ? connection.getInputStream() : connection.getErrorStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                response = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                Utilities.debugLog("Pusher External URL", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (Utilities.isSuccessfulResponseCode(statusCode)) {
                Utilities.debugLog("Pusher Save", "Saved pusher object as String = " + response.toString());
                _getAndSavePusherInfo(response.toString());
            }
        }
    }
}
