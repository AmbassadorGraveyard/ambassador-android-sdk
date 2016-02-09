package com.ambassador.ambassadorsdk.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public final class InstallReceiver extends BroadcastReceiver {

    @Inject
    protected AmbassadorConfig ambassadorConfig;

    @Inject protected User user;
    @Inject protected Campaign campaign;

    public InstallReceiver() {
        AmbassadorSingleton.getInstanceComponent().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        final String qstring = b.getString("referrer"); //"mbsy_cookie_code=jwnZ&device_id=test1234";
        //Toast.makeText(context, qstring, Toast.LENGTH_LONG).show();

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
            //Toast.makeText(context, "parse exception" + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        user.setWebDeviceId(webDeviceId);
        campaign.setReferredByShortCode(referralShortCode);

        Utilities.debugLog("Conversion", "webDeviceId: " + webDeviceId);
        Utilities.debugLog("Conversion", "referralShortCode: " + referralShortCode);
        //Toast.makeText(context, "webDeviceId: " + webDeviceId + " referralShortCode: " + referralShortCode, Toast.LENGTH_LONG).show();

        //if augur came back first, update our device id
        JSONObject identity;
        if (ambassadorConfig.getIdentifyObject() != null) {
            //Toast.makeText(context, "augur", Toast.LENGTH_LONG).show();
            try {
                identity = new JSONObject(ambassadorConfig.getIdentifyObject());
                JSONObject device = identity.getJSONObject("device");

                //if the webDeviceId has been received on the querystring and it's different than what augur returns, override augur deviceId
                if (webDeviceId != null && !device.getString("ID").equals(webDeviceId)) {
                    device.remove("ID");
                    device.put("ID",  user.getWebDeviceId());
                    identity.remove("device");
                    identity.put("device", device);
                    ambassadorConfig.setIdentifyObject(identity.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //USE THE CODE BELOW IF WE DECIDE TO NOT DEPEND ON THE AUGUR RESPONSE COMING BACK
        //CURRENTLY IF NO AUGUR, THEN NO CONVERSIONS
        //in the case of the augur request never returning, create an identify object so conversion can still go through
        /*else {
            try {
                identity = new JSONObject();
                JSONObject consumer = new JSONObject();
                JSONObject device = new JSONObject();
                consumer.put("UID", "");
                device.put("ID", webDeviceId);
                device.put("type", "Android");
                identity.put("consumer", consumer);
                identity.put("device", device);
                ambassadorConfig.setIdentifyObject(identity.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    public static InstallReceiver getInstance() {
        return new InstallReceiver();
    }

}