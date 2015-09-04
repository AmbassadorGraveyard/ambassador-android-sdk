package com.example.ambassador.ambassadorsdk;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JakeDunahee on 8/11/15.
 */
class BulkShareHelper {
    private ProgressDialog loader;
    private String messageToShare;

    // Constuctor
    BulkShareHelper(ProgressDialog loader, String messageToShare) {
        this.loader = loader;
        this.messageToShare = messageToShare;
    }

    void bulkShare(ArrayList<ContactObject> contacts, Boolean phoneNumbers) {
        // Functionality: Request to bulk share emails and sms
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

    // REQUEST HELPER METHODS
    private void _setUpConnection(HttpURLConnection connection) {
        try {
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.MBSY_UNIVERSAL_ID);
            connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getAPIKey());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    private void _callIsSuccessful() {
        loader.hide();
        loader.dismiss();
        loader.getOwnerActivity().finish();
        Toast.makeText(loader.getOwnerActivity(), "Message successfully shared!", Toast.LENGTH_SHORT).show();
    }

    private void _callIsUnsuccessful() {
        loader.hide();
        loader.dismiss();
        Toast.makeText(loader.getOwnerActivity(), "Unable to share message. Please try again.", Toast.LENGTH_SHORT).show();
    }
    // END REQUEST HELPER METHODS


    // CLASS: Static class that filters arrayLists and creates JSON objects
    private static class BulkFormatter {
        // VERIFIER FUNCTIONS
        private static ArrayList<String> _verifiedSMSList(ArrayList<ContactObject> contactObjects) {
            ArrayList<String> verifiedNumbers = new ArrayList<>();
            for (ContactObject contact : contactObjects) {
                String strippedNum = contact.phoneNumber.replaceAll("[^0-9]", "");
                if (strippedNum.length() == 11 || strippedNum.length() == 10 || strippedNum.length() == 7
                        || !verifiedNumbers.contains(strippedNum)) {
                    verifiedNumbers.add(strippedNum);
                }
            }

            return verifiedNumbers;
        }

        private static ArrayList<String> _verifiedEmailList(ArrayList<ContactObject> contactObjects) {
            ArrayList<String> verifiedEmails = new ArrayList<>();
            for (ContactObject contact : contactObjects) {
                if (_isValidEmail(contact.emailAddress)) { verifiedEmails.add(contact.emailAddress); }
            }

            return verifiedEmails;
        }

        private static Boolean _isValidEmail(String emailAddress) {
            // Functionality: Boolean that checks for legit email addressing using regex
            final Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = emailRegex.matcher(emailAddress);

            return matcher.find();
        }
        // END VERIFIER FUNCTIONS


        // JSON OBJECT MAKER
        private static JSONArray _contactArray(ArrayList<String> values, Boolean phoneNumbers) {
            // Functionality: Creates a jsonArray of jsonobjects created from validated phone numbers and email addresses
            String socialName = (phoneNumbers) ? "sms" : "email";
            JSONArray objectsList = new JSONArray();
            for (int i = 0; i < values.size(); i++) {
                JSONObject newObject = new JSONObject();
                try {
                    newObject.put("short_code", AmbassadorSingleton.getInstance().getShortCode());
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

        private static JSONObject _payloadObjectForSMS(ArrayList<String> numbers, String smsMessage) {
            // Funcionality: Ceates a json payload for SMS sharing with all the validated numbers included
            JSONObject object = new JSONObject();
            try {
                object.put("to", numbers);
                object.put("from", AmbassadorSingleton.getInstance().getFullName());
                object.put("message", smsMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return object;
        }

        private static JSONObject _payloadObjectForEmail(ArrayList<String> emails, String message) {
            // Functionality: Creats an email payload object for bulk sharing
            JSONObject object = new JSONObject();
            try {
                object.put("to_emails", new JSONArray(emails));
                object.put("short_code", AmbassadorSingleton.getInstance().getShortCode());
                object.put("message", message);
                object.put("subject_line", AmbassadorSingleton.getInstance().getEmailSubjectLine());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return object;
        }
        // END JSON OBJECT MAKER
    }


    private class BulkShareSMSRequest extends AsyncTask<Void, Void, Void> {
        private ArrayList<ContactObject> contacts;
        private int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://dev-ambassador-api.herokuapp.com/share/sms/";

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                _setUpConnection(connection);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(BulkFormatter._payloadObjectForSMS(BulkFormatter._verifiedSMSList(contacts), messageToShare).toString().getBytes());
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
                ShareTrackRequest shareTrackRequest = new ShareTrackRequest();
                shareTrackRequest.contacts = BulkFormatter._verifiedSMSList(contacts);
                shareTrackRequest.isSMS = true;
                shareTrackRequest.execute();
            } else {
                _callIsUnsuccessful();
            }
        }
    }

    private class BulkShareEmailRequest extends AsyncTask<Void, Void, Void> {
        private ArrayList<ContactObject> contacts;
        private int statusCode;
        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://dev-ambassador-api.herokuapp.com/share/email/";

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                _setUpConnection(connection);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(BulkFormatter._payloadObjectForEmail(BulkFormatter._verifiedEmailList(contacts), messageToShare).toString().getBytes());
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
                ShareTrackRequest shareTrackRequest = new ShareTrackRequest();
                shareTrackRequest.contacts = BulkFormatter._verifiedEmailList(contacts);
                shareTrackRequest.isSMS = false;
                shareTrackRequest.execute();
            } else {
                _callIsUnsuccessful();
            }
        }
    }

    private class ShareTrackRequest extends AsyncTask<Void, Void, Void> {
        private ArrayList<String> contacts;
        private int statusCode;
        private Boolean isSMS;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://dev-ambassador-api.herokuapp.com/track/share/";

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                _setUpConnection(connection);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(BulkFormatter._contactArray(contacts, isSMS).toString().getBytes());
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
            _callIsSuccessful();
        }
    }
}
