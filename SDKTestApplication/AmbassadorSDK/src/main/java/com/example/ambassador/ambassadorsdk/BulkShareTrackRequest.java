package com.example.ambassador.ambassadorsdk;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JakeDunahee on 9/16/15.
 */
class BulkShareTrackRequest extends AsyncTask<Void, Void, Void> {
    ArrayList<String> contacts;
    private int statusCode;
    boolean isSMS;

    public BulkShareTrackRequest(ArrayList<ContactObject> contacts, boolean isSMS) {
        this.contacts = BulkShareHelper.verifiedSMSList(contacts);
        this.isSMS = isSMS;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "https://dev-ambassador-api.herokuapp.com/track/share/";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.MBSY_UNIVERSAL_ID);
            connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getAPIKey());

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(BulkShareHelper.contactArray(contacts, isSMS).toString().getBytes());
            wr.flush();
            wr.close();

            statusCode = connection.getResponseCode();
        } catch (IOException ioException) {
            Log.e("IOException", ioException.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // TODO: Check for fail and store locally to retry when signal is available in order to keep share track
//        callIsSuccessful();
        if (Utilities.isSuccessfulResponseCode(statusCode)) {
            Utilities.debugLog("ShareTrack", "Share track SUCCESSFUL!");
        } else {
            Utilities.debugLog("ShareTrack", "Share track FAILURE!");
        }
    }
}
