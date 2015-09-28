package com.example.ambassador.ambassadorsdk;

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
 * Created by coreyfields on 9/28/15.
 */
public class IdentifyRequest {

    public void send(final String identifier) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int statusCode;

                String url = AmbassadorSingleton.identifyURL() + AmbassadorSingleton.getInstance().getUniversalID();
                JSONObject identifyObject = new JSONObject();

                try {
                    JSONObject augurObject = new JSONObject(AmbassadorSingleton.getInstance().getIdentifyObject());
                    identifyObject.put("enroll", true);
                    identifyObject.put("campaign_id", AmbassadorSingleton.getInstance().getCampaignID());
                    identifyObject.put("email", identifier);
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
            }
        });

        thread.start();
    }
}
