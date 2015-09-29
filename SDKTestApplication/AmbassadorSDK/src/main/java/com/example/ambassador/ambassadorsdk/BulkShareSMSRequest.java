package com.example.ambassador.ambassadorsdk;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JakeDunahee on 9/15/15.
 */
class BulkShareSMSRequest extends AsyncTask<Void, Void, Void> {
    ArrayList<ContactObject> contacts;
    private int statusCode;
    String messageToShare;
    SMSRequestCompletion completion;

    interface SMSRequestCompletion {
        void smsShareSuccess();
        void smsShareFailed();
    }

    public BulkShareSMSRequest(ArrayList<ContactObject> contacts, String messageToShare, SMSRequestCompletion completion) {
        this.contacts = contacts;
        this.messageToShare = messageToShare;
        this.completion = completion;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = AmbassadorSingleton.bulkSMSShareURL();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.getInstance().getUniversalID());
            connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getUniversalKey());

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(BulkShareHelper.payloadObjectForSMS(BulkShareHelper.verifiedSMSList(contacts), messageToShare).toString().getBytes());
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
            completion.smsShareSuccess();
            BulkShareTrackRequest shareTrackRequest = new BulkShareTrackRequest(contacts, true);
            shareTrackRequest.execute();
        } else {
            completion.smsShareFailed();
        }
    }
}
