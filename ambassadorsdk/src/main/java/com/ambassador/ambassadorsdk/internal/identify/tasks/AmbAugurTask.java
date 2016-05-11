package com.ambassador.ambassadorsdk.internal.identify.tasks;

import android.content.Context;
import android.content.SharedPreferences;
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
        String fingerprint = getSavedFingerprint();
        if (fingerprint != null) {
            handleJson(fingerprint);
            onCompleteListener.complete();
            return;
        }

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
                    onCompleteListener.complete();
                    return true;
                }

                saveFingerprint(json);
                handleJson(json);
                onCompleteListener.complete();
                return true;
            }

        });
    }

    protected void handleJson(String json) {
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
    }

    protected String getSavedFingerprint() {
        SharedPreferences sharedPreferences = AmbSingleton.getContext().getSharedPreferences("fingerprint", Context.MODE_PRIVATE);
        return sharedPreferences.getString("augur", null);
    }

    protected void saveFingerprint(String fingerprint) {
        SharedPreferences sharedPreferences = AmbSingleton.getContext().getSharedPreferences("fingerprint", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("augur", fingerprint).apply();
    }

}
