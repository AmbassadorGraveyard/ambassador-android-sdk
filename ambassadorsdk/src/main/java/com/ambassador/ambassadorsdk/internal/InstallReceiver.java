package com.ambassador.ambassadorsdk.internal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.ambassador.ambassadorsdk.WelcomeScreenDialog;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.models.WelcomeScreenData;
import com.google.gson.JsonObject;

import javax.inject.Inject;

public final class InstallReceiver extends BroadcastReceiver {

    @Inject protected User user;
    @Inject protected Campaign campaign;
    @Inject protected RequestManager requestManager;
    @Inject protected Utilities Utilities;
    private static final String INTENT_KEY_REFERRER = "referrer";
    private static final String PARAM_REFERRAL_SHORT_CODE = "mbsy_cookie_code";
    private static final String PARAM_WEB_DEVICE_ID = "device_id";
    private static final String PARAM_SPLIT_REGEX = "&";
    private static final String PARAM_SUB_SPLIT_REGEX = "=";

    public InstallReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        AmbSingleton.getInstance().getAmbComponent().inject(this);

        Bundle b = intent.getExtras();
        if (b == null) return;

        final String referrer = b.getString(INTENT_KEY_REFERRER); //"mbsy_cookie_code=jwnZ&device_id=test1234";

        if (referrer == null) return;

        String[] params = referrer.split(PARAM_SPLIT_REGEX);
        String referralShortCode = null;
        String webDeviceId = null;

        for (String param : params) {
            String[] paramSplit = param.split(PARAM_SUB_SPLIT_REGEX);

            if (paramSplit.length <= 1) continue;

            if (paramSplit[0].equalsIgnoreCase(PARAM_REFERRAL_SHORT_CODE)) {
                referralShortCode = paramSplit[1];
            }
            else if (paramSplit[0].equalsIgnoreCase(PARAM_WEB_DEVICE_ID)) {
                webDeviceId = paramSplit[1];
            }
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
                if (!(successResponse instanceof IdentifyApi.GetUserFromShortCodeResponse)) {
                    onFailure(null);
                    return;
                }

                try {
                    IdentifyApi.GetUserFromShortCodeResponse response = (IdentifyApi.GetUserFromShortCodeResponse) successResponse;
                    Activity activity = WelcomeScreenDialog.getActivity();
                    WelcomeScreenDialog welcomeScreenDialog = new WelcomeScreenDialog(activity);
                    WelcomeScreenDialog.BackendData backendData =
                            new WelcomeScreenDialog.BackendData()
                                    .setImageUrl(response.avatar_url)
                                    .setName(response.name);

                    welcomeScreenDialog.load(
                            new WelcomeScreenData()
                                    .withParameters(WelcomeScreenDialog.getParameters())
                                    .withBackendData(backendData)
                                    .parseName()
                    );
                    WelcomeScreenDialog.AvailabilityCallback callback = WelcomeScreenDialog.getAvailabilityCallback();

                    if (callback != null) callback.available(welcomeScreenDialog);

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

    public void registerWith(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.vending.INSTALL_REFERRER");
        context.registerReceiver(this, intentFilter);
    }
}
