package com.ambassador.ambassadorsdk.internal;

import android.os.Handler;
import android.os.Message;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import javax.inject.Inject;

import io.augur.wintermute.Augur;

public class IdentifyAugurSDK implements IIdentify {

    @Inject protected Device deviceObj;
    @Inject protected User user;

    public IdentifyAugurSDK() {
        AmbSingleton.getGraph().inject(this);
    }

    @Override
    public void getIdentity() {
        String augurKey = new StringResource(R.string.augur_api_key).getValue();
        JSONObject augurConfig = new JSONObject();

        try {
            // required
            augurConfig.put("context", AmbSingleton.getContext());
            augurConfig.put("apiKey", augurKey);
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
            public boolean handleMessage(Message msg) {
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
                    if (user.getWebDeviceId() != null && !device.getString("ID").equals(user.getWebDeviceId())) {
                        device.remove("ID");
                        device.put("ID", user.getWebDeviceId());
                    }

                    device.put("type", deviceObj.getType());
                    jsonObject.put("device", device);

                    Utilities.debugLog("Augur", "Augur successfully received through SDK call");
                    user.setAugurData((JsonObject) new JsonParser().parse(jsonObject.toString()));
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
