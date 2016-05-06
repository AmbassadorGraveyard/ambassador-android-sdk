package com.ambassador.ambassadorsdk.internal.identify;

import android.os.Handler;
import android.os.Message;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Secrets;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import io.augur.wintermute.Augur;

public class AmbAugurTask extends AmbIdentifyTask {

    @Override
    public void execute(final OnCompleteListener onCompleteListener) throws Exception {
        JSONObject augurConfig = new JSONObject();
        augurConfig.put("context", AmbSingleton.getContext());
        augurConfig.put("apiKey", Secrets.getAugurKey());
        augurConfig.put("maxRetries", 5);

        Augur.getJSON(augurConfig, new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                String json = msg.getData().getString("json");
                String error = msg.getData().getString("error");

                if (error != null) {
                    Utilities.debugLog("AmbassadorSDK", error);
                    return true;
                }

                JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                JsonObject device = jsonObject.getAsJsonObject("device");

                if (user.getWebDeviceId() != null && !device.get("ID").getAsString().equals(user.getWebDeviceId())) {
                    device.remove("ID");
                    device.addProperty("ID", user.getWebDeviceId());
                }

                device.addProperty("type", AmbAugurTask.this.device.getType());
                jsonObject.remove("device");
                jsonObject.add("device", device);

                user.setAugurData(jsonObject);
                onCompleteListener.complete();
                return true;
            }

        });
    }

}
