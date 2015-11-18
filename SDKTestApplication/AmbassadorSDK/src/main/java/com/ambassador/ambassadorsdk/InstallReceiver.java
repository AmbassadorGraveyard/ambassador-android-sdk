package com.ambassador.ambassadorsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(hit).openConnection();
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IOException", e.toString());
                }
            }
        };

        new Thread(r).start();
    }

    public static InstallReceiver getInstance() {
        return new InstallReceiver();
    }

}