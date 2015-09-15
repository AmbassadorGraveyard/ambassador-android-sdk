package com.example.ambassador.ambassadorsdk;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import io.augur.wintermute.Augur;

/**
 * Created by JakeDunahee on 9/1/15.
 */
class IdentifyAugurSDK {
    public String deviceID;

    interface AugurCompletion {
        void augurComplete();
    }

    public void getAugur(final AugurCompletion completion) {
        Augur.getJSON(MyApplication.getAppContext(), "***REMOVED***", new Handler.Callback() {
            @Override
            public boolean handleMessage(final Message msg) {
                // json == the full sever response from the Augur API
                String json = msg.getData().getString("json");
                try {
                    // Alter the device 'type' to be "SmartPhone" or "Tablet" instead of "Android"
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject device = jsonObject.getJSONObject("device");
                    device.put("type", Utilities.deviceType(MyApplication.getAppContext()));
                    jsonObject.put("device", device);

                    Utilities.debugLog("Augur", "Augur successfully received through SDK call");
                    AmbassadorSingleton.getInstance().setIdentifyObject(jsonObject.toString());
                    deviceID = Augur.DID;
                    completion.augurComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }
}
