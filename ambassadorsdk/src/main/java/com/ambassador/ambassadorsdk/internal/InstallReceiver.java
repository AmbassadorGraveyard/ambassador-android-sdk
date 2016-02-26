package com.ambassador.ambassadorsdk.internal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ambassador.ambassadorsdk.WelcomeScreenDialog;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.models.WelcomeScreenData;
import com.google.gson.JsonObject;

import javax.inject.Inject;

public final class InstallReceiver extends BroadcastReceiver {

    @Inject protected User user;
    @Inject protected Campaign campaign;
    @Inject protected RequestManager requestManager;

    public InstallReceiver() {
        AmbSingleton.inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        final String qstring = b.getString("referrer"); //"mbsy_cookie_code=jwnZ&device_id=test1234";

        if (qstring == null) return;

        String[] param1;
        String[] param2;
        String webDeviceId;
        String referralShortCode;
        try {
            String[] qSplit = qstring.split("&");
            param1 = qSplit[0].split("="); //mbsy_cookie_code=jwnZ
            param2 = qSplit[1].split("="); //device_id=test1234
            referralShortCode = param1[0].equals("mbsy_cookie_code") ? param1[1] : param2[1];
            webDeviceId = param2[0].equals("device_id") ? param2[1] : param1[1];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }

        user.setWebDeviceId(webDeviceId);
        campaign.setReferredByShortCode(referralShortCode);

        Utilities.debugLog("Conversion", "webDeviceId: " + webDeviceId);
        Utilities.debugLog("Conversion", "referralShortCode: " + referralShortCode);

        //if augur came back first, update our device id
        JsonObject identity;
        if (user.getAugurData() != null) {
            identity = user.getAugurData();
           // JsonObject device = identity.getJSONObject("device");
            JsonObject device = identity.get("device").getAsJsonObject();

            //if the webDeviceId has been received on the querystring and it's different than what augur returns, override augur deviceId
            if (webDeviceId != null && !device.get("ID").getAsString().equals(webDeviceId)) {
                device.remove("ID");
                device.addProperty("ID",  user.getWebDeviceId());
                identity.remove("device");
                identity.add("device", device);
                user.setAugurData(identity);
            }
        } else {
            /*
             * Can't rely on Augur definitively.  If no augur data, then use our own.  
             */
            identity = new JsonObject();
            JsonObject consumer = new JsonObject();
            JsonObject device = new JsonObject();
            consumer.addProperty("UID", "");
            device.addProperty("ID", webDeviceId);
            device.addProperty("type", "Android");
            identity.add("consumer", consumer);
            identity.add("device", device);
            user.setAugurData(identity);
        }

        requestManager.getUserFromShortCode(referralShortCode, new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                try {
                    Activity activity = WelcomeScreenDialog.getActivity();
                    WelcomeScreenDialog welcomeScreenDialog = new WelcomeScreenDialog(activity);
                    WelcomeScreenDialog.BackendData backendData = new WelcomeScreenDialog.BackendData().setImageUrl("https://upload.wikimedia.org/wikipedia/commons/7/77/Avatar_cat.png");
                    welcomeScreenDialog.load(WelcomeScreenData.getFromOptions().withParameters(WelcomeScreenDialog.getParameters()).withBackendData(backendData));
                    WelcomeScreenDialog.AvailabilityCallback callback = WelcomeScreenDialog.getAvailabilityCallback();
                    callback.available(welcomeScreenDialog);
                } catch (NullPointerException npe) {
                    Log.e("AmbassadorSDK", npe.toString());
                    // That dev screwed up
                }
            }

            @Override
            public void onFailure(Object failureResponse) {

            }
        });
    }

    public static InstallReceiver getInstance() {
        return new InstallReceiver();
    }

}
