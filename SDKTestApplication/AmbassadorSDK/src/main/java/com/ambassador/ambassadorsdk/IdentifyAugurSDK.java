package com.ambassador.ambassadorsdk;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import io.augur.wintermute.Augur;

/**
 * Created by JakeDunahee on 9/1/15.
 */
class IdentifyAugurSDK {
    private String deviceID;
    private String universalID;

    interface AugurCompletion {
        void augurComplete();
    }

    public void getAugur(final AmbassadorConfig ambassadorConfig, final AugurCompletion completion) {
        JSONObject augurConfig = new JSONObject();

        try {
            // required
            augurConfig.put("context", AmbassadorSingleton.get());
            augurConfig.put("apiKey","***REMOVED***");
            // optional
            // config.put("endToEndEncryption", true);
            // config.put("disableConsumerInsights", true);
        } catch (Exception e) {
            Utilities.debugLog("Augur", "JSON.config.error " + augurConfig.toString());
        }

        Augur.getJSON(augurConfig, new Handler.Callback() {
            @Override
            public boolean handleMessage(final Message msg) {
                // json == the full sever response from the Augur API
                String json = msg.getData().getString("json");
                String error = msg.getData().getString("error");

                if (error != null) {
                    Utilities.debugLog("Augur Error", error);
                    return true;
                }

                try {
                    // Alter the device 'type' to be "SmartPhone" or "Tablet" instead of "Android"
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject device = jsonObject.getJSONObject("device");
                    device.put("type", Utilities.deviceType(AmbassadorSingleton.get()));
                    jsonObject.put("device", device);

                    Utilities.debugLog("Augur", "Augur successfully received through SDK call");
                    ambassadorConfig.setIdentifyObject(jsonObject.toString());
                    deviceID = Augur.DID;
                    universalID = Augur.UID;
                    completion.augurComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    //json = e.toString();
                }

                return true;
            }
        });
    }

    String getDeviceID() {
        return deviceID;
    }

    String getUniversalID() {
        return universalID;
    }
}
