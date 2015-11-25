package com.ambassador.ambassadorsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

/**
 * Created by dylan on 11/18/15.
 */
public class InstallReceiver extends BroadcastReceiver {
    @Inject
    AmbassadorConfig ambassadorConfig;

    public InstallReceiver() {
        AmbassadorSingleton.getComponent().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        final String qstring = b.getString("referrer"); //"mbsy_cookie_code%3DjwnZ%26device_id%3Dtest1234";
        Toast.makeText(context, qstring, Toast.LENGTH_LONG).show();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                String[] param1, param2;
                String webDeviceId, referralShortCode;
                try {
                    String[] qSplit = qstring.split("%26");
                    param1 = qSplit[0].split("%3D"); //mbsy_cookie_code%3DjwnZ
                    param2 = qSplit[1].split("%3D"); //device_id%3Dtest1234
                    referralShortCode = param1[0].equals("mbsy_cookie_code") ? param1[1] : param2[1];
                    webDeviceId = param2[0].equals("device_id") ? param2[1] : param1[1];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }

                ambassadorConfig.setWebDeviceId(webDeviceId);
                ambassadorConfig.setReferralShortCode(referralShortCode);

                //if augur came back first, update our device id
                JSONObject identity;
                if (ambassadorConfig.getIdentifyObject() != null) {
                    try {
                        identity = new JSONObject(ambassadorConfig.getIdentifyObject());
                        JSONObject device = identity.getJSONObject("device");

                        //if the webDeviceId has been received on the querystring and it's different than what augur returns, override augur deviceId
                        if (webDeviceId != null && !device.getString("ID").equals(webDeviceId)) {
                            device.remove("ID");
                            device.put("ID", ambassadorConfig.getWebDeviceId());
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
        };

        new Thread(r).start();
    }

    public static InstallReceiver getInstance() {
        return new InstallReceiver();
    }

}