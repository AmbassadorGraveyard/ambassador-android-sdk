package com.example.ambassador.ambassadorsdk;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JakeDunahee on 9/29/15.
 */
class RequestManager {
    private static RequestManager mInstance = null;
    static HttpURLConnection connection;

    interface RequestCompletion {
        void onSuccess();
        void onFailure();
    }

    static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager();
        }

        return mInstance;
    }

    void setUpConnection(String methodType, String url) {
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(methodType);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.getInstance().getUniversalID());
            connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getUniversalKey());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.toString());
        }
    }
}
