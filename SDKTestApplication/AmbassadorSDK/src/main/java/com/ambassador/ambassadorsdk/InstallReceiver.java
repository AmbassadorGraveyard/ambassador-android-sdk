package com.ambassador.ambassadorsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;

/**
 * Created by dylan on 11/18/15.
 */
public class InstallReceiver extends BroadcastReceiver {

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