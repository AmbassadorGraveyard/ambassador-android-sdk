package com.ambassador.ambassadorsdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 8/11/15.
 */
public class BulkShareHelper {

    @Inject
    RequestManager requestManager;

    interface BulkShareCompletion {
        void bulkShareSuccess();
        void bulkShareFailure();
    }

    public BulkShareHelper() {
        AmbassadorSingleton.getComponent().inject(this);
    }

    public void bulkShare(final String messageToShare, final List<ContactObject> contacts, Boolean phoneNumbers, final BulkShareCompletion completion) {
        // Functionality: Request to bulk share emails and sms
        if (phoneNumbers) {
            requestManager.bulkShareSms(contacts, messageToShare, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    requestManager.bulkShareTrack(contacts, true);
                    completion.bulkShareSuccess();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    completion.bulkShareFailure();
                }
            });
        } else {
            requestManager.bulkShareEmail(contacts, messageToShare, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successReponse) {
                    requestManager.bulkShareTrack(contacts, false);
                    completion.bulkShareSuccess();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    completion.bulkShareFailure();
                }
            });
        }
    }


    // STATIC HELPER FUNCTIONS
    // VERIFIER FUNCTIONS
    static ArrayList<String> verifiedSMSList(List<ContactObject> contactObjects) {
        ArrayList<String> verifiedNumbers = new ArrayList<>();
        for (ContactObject contact : contactObjects) {
            String strippedNum = contact.phoneNumber.replaceAll("[^0-9]", "");
            if (strippedNum.length() == 11 || strippedNum.length() == 10 || strippedNum.length() == 7 && !verifiedNumbers.contains(strippedNum)) {
                verifiedNumbers.add(strippedNum);
            }
        }

        return verifiedNumbers;
    }

    static ArrayList<String> verifiedEmailList(List<ContactObject> contactObjects) {
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
    static JSONArray contactArray(List<String> values, Boolean phoneNumbers, String shortCode) {
        // Functionality: Creates a jsonArray of jsonobjects created from validated phone numbers and email addresses
        String socialName = (phoneNumbers) ? "sms" : "email";
        JSONArray objectsList = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            JSONObject newObject = new JSONObject();
            try {
                newObject.put("short_code", shortCode);
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

    static JSONObject payloadObjectForEmail(List<String> emails, String shortCode, String emailSubject, String message) {
        // Functionality: Creats an email payload object for bulk sharing
        JSONObject object = new JSONObject();
        try {
            object.put("to_emails", new JSONArray(emails));
            object.put("short_code", shortCode);
            object.put("message", message);
            object.put("subject_line", emailSubject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    static JSONObject payloadObjectForSMS(List<String> numbers, String fullName, String smsMessage) {
        // Funcionality: Ceates a json payload for SMS sharing with all the validated numbers included
        JSONObject object = new JSONObject();
        try {
            object.put("to", numbers);
            object.put("from", fullName);
            object.put("message", smsMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
    // END JSON OBJECT MAKER
    // END STATIC HELPER FUNCTIONS
}