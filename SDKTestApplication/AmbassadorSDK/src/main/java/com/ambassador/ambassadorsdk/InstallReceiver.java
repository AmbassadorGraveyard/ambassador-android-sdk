package com.ambassador.ambassadorsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Created by dylan on 11/18/15.
 */
public class InstallReceiver extends BroadcastReceiver {
    @Inject
    AmbassadorConfig ambassadorConfig;

    public InstallReceiver() {
        AmbassadorSingleton.getComponent().inject(this);

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                //TESTING - MODIFY THESE LINES TO RECEIVE PARAMETERS FROM INTENT
                //if (false) {
                String webDeviceId = "web1234";
                String referralShortCode = "mbsy1234";
                ambassadorConfig.setWebDeviceId(webDeviceId);
                ambassadorConfig.setReferralShortCode(referralShortCode);
                //END TESTING

                //TESTING - THIS CODE SHOULD BE MOVED TO THE BOTTOM OF THE RUN BLOCK WHEN INTENT IS READY
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
                //}
                //END TESTING
            }
        }, 0, 1);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        Toast.makeText(context, b.getString("referrer"), Toast.LENGTH_LONG).show();

        final String hit = "http://104.131.52.60:3000/hit/{name}".replace("{name}", b.getString("referrer"));

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                RequestManager rm = new RequestManager();
                HttpURLConnection connection = rm.setUpConnection("GET", hit);
                String resp = rm.getResponse(connection, 200);
                Log.v("RESPONSE", resp);
            }
        };

        new Thread(r).start();
    }

    public static InstallReceiver getInstance() {
        return new InstallReceiver();
    }

}