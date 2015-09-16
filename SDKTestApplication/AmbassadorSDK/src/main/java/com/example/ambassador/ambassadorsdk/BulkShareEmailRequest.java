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
class BulkShareEmailRequest extends AsyncTask<Void, Void, Void> {
    private ArrayList<ContactObject> contacts;
    private int statusCode;
    String messageToShare;
    EmailRequestCompletion completion;

    interface EmailRequestCompletion {
        void emailShareSucces();
        void emailShareFailure();
    }

    public BulkShareEmailRequest(ArrayList<ContactObject> contacts, String messageToShare, EmailRequestCompletion completion) {
        this.contacts = contacts;
        this.messageToShare = messageToShare;
        this.completion = completion;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "https://dev-ambassador-api.herokuapp.com/share/email/";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.MBSY_UNIVERSAL_ID);
            connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getAPIKey());

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(BulkShareHelper.payloadObjectForEmail(BulkShareHelper.verifiedEmailList(contacts), messageToShare).toString().getBytes());
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
        if (Utilities.isSuccessfulResponseCode(statusCode)) {
            completion.emailShareSucces();
            BulkShareTrackRequest shareTrackRequest = new BulkShareTrackRequest(contacts, false);
            shareTrackRequest.execute();
        } else {
            completion.emailShareFailure();
        }
    }
}
