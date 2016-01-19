package com.ambassador.ambassadorsdk.internal;

import android.os.Handler;
import android.os.Message;

import com.ambassador.ambassadorsdk.utils.Device;

import org.json.JSONObject;

import javax.inject.Inject;

import io.augur.wintermute.Augur;

/**
 * Created by JakeDunahee on 9/1/15.
 */
public class IdentifyAugurSDK implements IIdentify {

    @Inject
    AmbassadorConfig ambassadorConfig;

    public IdentifyAugurSDK() {
        AmbassadorSingleton.getInstanceComponent().inject(this);
    }

    @Override
    public void getIdentity() {
        JSONObject augurConfig = new JSONObject();

        try {
            // required
            augurConfig.put("context", AmbassadorSingleton.getInstanceContext());
            augurConfig.put("apiKey", AmbassadorConfig.AUGUR_API_KEY);
            // optional
            //augurConfig.put("timeout", 1000); // default: 5000 (5 seconds)
            augurConfig.put("maxRetries", 5); // default: 5
            //augurConfig.put("endToEndEncryption", false); // default: false
            //augurConfig.put("disableConsumerInsights", true); // default: false
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

                    //if the webDeviceId has been received on the querystring and it's different than what augur returns, override augur deviceId
                    if (ambassadorConfig.getWebDeviceId() != null && !device.getString("ID").equals(ambassadorConfig.getWebDeviceId())) {
                        device.remove("ID");
                        device.put("ID", ambassadorConfig.getWebDeviceId());
                    }

                    device.put("type", new Device().getType());
                    jsonObject.put("device", device);

                    Utilities.debugLog("Augur", "Augur successfully received through SDK call");
                    ambassadorConfig.setIdentifyObject(jsonObject.toString());
                    //deviceID = Augur.DID;
                    //universalID = Augur.UID;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
    }
}
