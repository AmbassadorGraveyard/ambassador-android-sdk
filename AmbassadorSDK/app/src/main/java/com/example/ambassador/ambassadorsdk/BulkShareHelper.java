package com.example.ambassador.ambassadorsdk;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by JakeDunahee on 8/11/15.
 */
public class BulkShareHelper {
    public ProgressBar loader;

    //region VERIFIERS -- Verify string objects(phone numbers and email addresses) and return ArrayLists
    public static ArrayList<String> verifiedSMS(ArrayList<ContactObject> contactObjects) {
        ArrayList<String> verifiedNumbers = new ArrayList<>();
        for (ContactObject contact : contactObjects) {
            String strippedNum = contact.phoneNumber.replaceAll("[^0-9]", "");
            if (strippedNum.length() == 11 || strippedNum.length() == 10 || strippedNum.length() == 7
                    || !verifiedNumbers.contains(strippedNum)) {
                verifiedNumbers.add(strippedNum);
                Log.d("phoneNum", strippedNum);
            }
        }

        return verifiedNumbers;
    }

    public static ArrayList<String> verifiedEmail(ArrayList<ContactObject> contactObjects) {
        ArrayList<String> verifiedEmails = new ArrayList<>();
        for (ContactObject contact : contactObjects) {
            if (isValidEmail(contact.emailAddress)) {
                verifiedEmails.add(contact.emailAddress);
                Log.d("Emails", "(" +verifiedEmails.size() + ")" + contact.emailAddress);
            }
        }

        return verifiedEmails;
    }

    private static Boolean isValidEmail(String emailAddress) {
        final Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailRegex.matcher(emailAddress);

        return matcher.find();
    }
    //endregion


    //region SHARED TRACKING
    // Creates a jsonArray of jsonobjects created from validated phone numbers and email addresses
    private static JSONArray contactArray(ArrayList<String> values, Boolean phoneNumbers) {
        String socialName = (phoneNumbers) ? "sms" : "email";
        JSONArray objectsList = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            JSONObject newObject = new JSONObject();
            try {
                newObject.put("short_code", "jB78");
                newObject.put("social_name", socialName);
                if (phoneNumbers) {
                    newObject.put("recipient_email", "");
                    newObject.put("recipient_username", values.get(i));
                } else {
                    newObject.put("recipient_email", values.get(i));
                    newObject.put("recipient_username", "");
                }

                objectsList.put(newObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return objectsList;
    }
    //endregion


    //region BULK SHARING PAYLOADS
    // Ceates a json payload for SMS sharing with all the validated numbers included
    private static JSONObject payloadObjectForSMS(ArrayList<String> numbers) {
        JSONObject object = new JSONObject();
        try {
            object.put("to", numbers);
            object.put("from", "Jake Dunahee");
            object.put("message", "test sms message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    private static JSONObject payloadObjectForEmail(ArrayList<String> emails) {
        JSONObject object = new JSONObject();
        try {
            object.put("to_emails", emails);
            object.put("short_code", "jB78");
            object.put("message", "test email message");
            object.put("subject_line", "test subject line");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public void bulkSMSShare(ArrayList<ContactObject> contacts, Boolean phoneNumbers) {
        if (phoneNumbers) {
            BulkShareSMSRequest smsRequest = new BulkShareSMSRequest();
            smsRequest.contacts = contacts;
            smsRequest.execute();
        } else {
            BulkShareEmailRequest emailRequest = new BulkShareEmailRequest();
            emailRequest.contacts = contacts;
            emailRequest.execute();
        }

    }

    class BulkShareSMSRequest extends AsyncTask<Void, Void, Void> {
        public ArrayList<ContactObject> contacts;
        public int statusCode;
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
                connection.setRequestProperty("Authorization", AmbassadorSingleton.API_KEY);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(BulkShareHelper.payloadObjectForSMS(BulkShareHelper.verifiedSMS(contacts)).toString().getBytes());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                // Get and read response from LinkedIN
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                System.out.println(response);
            } catch (IOException ioException) {
                Log.e("IOException", ioException.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ShareTrackRequest shareTrackRequest = new ShareTrackRequest();
            shareTrackRequest.contacts = contacts;
            shareTrackRequest.isSMS = true;
            shareTrackRequest.execute();
        }
    }

    class BulkShareEmailRequest extends AsyncTask<Void, Void, Void> {
        public ArrayList<ContactObject> contacts;
        public int statusCode;
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
                connection.setRequestProperty("Authorization", AmbassadorSingleton.API_KEY);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(BulkShareHelper.payloadObjectForEmail(BulkShareHelper.verifiedEmail(contacts)).toString().getBytes());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                System.out.println(response);
            } catch (IOException ioException) {
                Log.e("IOException", ioException.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ShareTrackRequest shareTrackRequest = new ShareTrackRequest();
            shareTrackRequest.contacts = contacts;
            shareTrackRequest.isSMS = false;
            shareTrackRequest.execute();
        }
    }

    class ShareTrackRequest extends AsyncTask<Void, Void, Void> {
        public ArrayList<ContactObject> contacts;
        public int statusCode;
        public Boolean isSMS;
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
                connection.setRequestProperty("Authorization", AmbassadorSingleton.API_KEY);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(BulkShareHelper.contactArray(BulkShareHelper.verifiedSMS(contacts), isSMS).toString().getBytes());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                System.out.println(response);
            } catch (IOException ioException) {
                Log.e("IOException", ioException.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (loader != null) {
                loader.setVisibility(View.GONE);
            }
        }
    }
}
