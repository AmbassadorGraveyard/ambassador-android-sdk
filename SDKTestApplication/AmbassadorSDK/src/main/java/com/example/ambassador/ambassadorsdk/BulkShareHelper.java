package com.example.ambassador.ambassadorsdk;

import android.app.ProgressDialog;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JakeDunahee on 8/11/15.
 */
class BulkShareHelper {
    private String messageToShare;

    interface BulkShareCompletion {
        void bulkShareSuccess();
        void bulkShareFailure();
    }

    // Constuctor
    BulkShareHelper(String messageToShare) {
        this.messageToShare = messageToShare;
    }

    void bulkShare(final ArrayList<ContactObject> contacts, Boolean phoneNumbers, final BulkShareCompletion completion) {
        // Functionality: Request to bulk share emails and sms
        if (phoneNumbers) {
            RequestManager.getInstance().bulkShareSms(contacts, messageToShare, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(String successResponse) {
                    RequestManager.getInstance().bulkShareTrack(contacts, true);
                    completion.bulkShareSuccess();
                }

                @Override
                public void onFailure(String failureResponse) {
                    completion.bulkShareFailure();
                }
            });
        } else {
            RequestManager.getInstance().bulkShareEmail(contacts, messageToShare, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(String successReponse) {
                    RequestManager.getInstance().bulkShareTrack(contacts, false);
                    completion.bulkShareSuccess();
                }

                @Override
                public void onFailure(String failureResponse) {
                    completion.bulkShareFailure();
                }
            });
        }
    }


    // STATIC HELPER FUNCTIONS
    // VERIFIER FUNCTIONS
    static ArrayList<String> verifiedSMSList(ArrayList<ContactObject> contactObjects) {
        ArrayList<String> verifiedNumbers = new ArrayList<>();
        for (ContactObject contact : contactObjects) {
            String strippedNum = contact.phoneNumber.replaceAll("[^0-9]", "");
            if (strippedNum.length() == 11 || strippedNum.length() == 10 || strippedNum.length() == 7 && !verifiedNumbers.contains(strippedNum)) {
                verifiedNumbers.add(strippedNum);
            }
        }

        return verifiedNumbers;
    }

    static ArrayList<String> verifiedEmailList(ArrayList<ContactObject> contactObjects) {
        ArrayList<String> verifiedEmails = new ArrayList<>();
        for (ContactObject contact : contactObjects) {
            if (BulkShareHelper.isValidEmail(contact.emailAddress)) { verifiedEmails.add(contact.emailAddress); }
        }

        return verifiedEmails;
    }

    static boolean isValidEmail(String emailAddress) {
        // Functionality: Boolean that checks for legit email addressing using regex
        Pattern emailRegex = Pattern.compile("^(.+)@(.+)$");
        Matcher matcher = emailRegex.matcher(emailAddress);

        return matcher.matches();
    }
    // END VERIFIER FUNCTIONS


    // JSON OBJECT MAKER
    static JSONArray contactArray(ArrayList<String> values, Boolean phoneNumbers) {
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

    static JSONObject payloadObjectForEmail(ArrayList<String> emails, String message) {
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

    static JSONObject payloadObjectForSMS(ArrayList<String> numbers, String smsMessage) {
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
    // END JSON OBJECT MAKER
    // END STATIC HELPER FUNCTIONS
}
