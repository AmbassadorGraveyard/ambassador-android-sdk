package com.example.ambassador.ambassadorsdk;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JakeDunahee on 9/15/15.
 */
class BulkShareSMSRequest extends AsyncTask<Void, Void, Void> {
    ArrayList<ContactObject> contacts;
    private int statusCode;
    String messageToShare;

    public BulkShareSMSRequest(ArrayList<ContactObject> contacts, String messageToShare) {
        this.contacts = contacts;
        this.messageToShare = messageToShare;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "https://dev-ambassador-api.herokuapp.com/share/sms/";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.MBSY_UNIVERSAL_ID);
            connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getAPIKey());

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(BulkShareHelper.BulkFormatter.payloadObjectForSMS(BulkShareHelper.BulkFormatter.verifiedSMSList(contacts), messageToShare).toString().getBytes());
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
//            BulkShareHelper.ShareTrackRequest shareTrackRequest = new BulkShareHelper.ShareTrackRequest();
//            shareTrackRequest.contacts = BulkShareHelper.BulkFormatter.verifiedSMSList(contacts);
//            shareTrackRequest.isSMS = true;
//            shareTrackRequest.execute();
        } else {
//            _callIsUnsuccessful();
        }
    }
}
