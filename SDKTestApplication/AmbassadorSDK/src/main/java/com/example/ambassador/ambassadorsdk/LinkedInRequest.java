package com.example.ambassador.ambassadorsdk;

import android.os.Handler;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by coreyfields on 9/21/15.
 */
public class LinkedInRequest {
    public AsyncResponse mCallback = null;
    private int postStatus;

    public interface AsyncResponse {
        void processLinkedInRequest(int postStatus);
    }

    final Handler mHandler = new Handler();

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            mCallback.processLinkedInRequest(postStatus);
        }
    };

    public void send(final JSONObject object) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                String url  = "https://api.linkedin.com/v1/people/~/shares?format=json";

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Host", "api.linkedin.com");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + AmbassadorSingleton.getInstance().getLinkedInToken());
                    connection.setRequestProperty("x-li-format", "json");

                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(object.toString());
                    wr.flush();
                    wr.close();

                    postStatus = connection.getResponseCode();
                } catch (IOException e) {
                    postStatus = 400;
                    e.printStackTrace();
                }

                mHandler.post(mUpdateResults);
            }
        });

        thread.start();
    }
}