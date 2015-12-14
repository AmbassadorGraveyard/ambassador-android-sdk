package com.ambassador.ambassadorsdk.internal;

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

    public interface BulkShareCompletion {
        void bulkShareSuccess();
        void bulkShareFailure();
    }

    public enum SocialServiceTrackType {
        SMS("sms"),
        EMAIL("email"),
        TWITTER("twitter"),
        FACEBOOK("facebook"),
        LINKEDIN("linkedin");

        private String stringValue;
        SocialServiceTrackType(String toString) {
            stringValue = toString;
        }

        @Override
        public String toString() {
            return stringValue;
        }
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
                    requestManager.bulkShareTrack(contacts, SocialServiceTrackType.SMS);
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
                    requestManager.bulkShareTrack(contacts, SocialServiceTrackType.EMAIL);
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
            String strippedNum = contact.getPhoneNumber().replaceAll("[^0-9]", "");
            if (strippedNum.length() == 11 || strippedNum.length() == 10 || strippedNum.length() == 7 && !verifiedNumbers.contains(strippedNum)) {
                verifiedNumbers.add(strippedNum);
            }
        }

        return verifiedNumbers;
    }

    static ArrayList<String> verifiedEmailList(List<ContactObject> contactObjects) {
        ArrayList<String> verifiedEmails = new ArrayList<>();
        for (ContactObject contact : contactObjects) {
            if (BulkShareHelper.isValidEmail(contact.getEmailAddress())) { verifiedEmails.add(contact.getEmailAddress()); }
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
    static JSONArray contactArray(List<String> values, SocialServiceTrackType trackType, String shortCode) {
        // Functionality: Creates a jsonArray of jsonObjects created from validated phone numbers and email addresses
        String socialName = trackType.toString();
        JSONArray objectsList = new JSONArray();
        if (values == null) {
            values = new ArrayList<String>();
            values.add("");
        }

        for (int i = 0; i < values.size(); i++) {
            JSONObject newObject = new JSONObject();
            try {
                newObject.put("short_code", shortCode);
                newObject.put("social_name", socialName);

                switch (trackType) {
                    case SMS:
                        newObject.put("recipient_email", "");
                        newObject.put("recipient_username", values.get(i));
                        break;
                    case EMAIL:
                        newObject.put("recipient_email", values.get(i));
                        newObject.put("recipient_username", "");
                        break;
                    default:
                        newObject.put("recipient_email", "");
                        newObject.put("recipient_username", "");
                        break;
                }

                objectsList.put(newObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return objectsList;
    }

    // Overloaded method for optional list parameter
    static JSONArray contactArray(SocialServiceTrackType trackType, String shortCode) {
        return contactArray(null, trackType, shortCode);
    }

    static JSONObject payloadObjectForEmail(List<String> emails, String shortCode, String emailSubject, String message) {
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
        JSONObject object = new JSONObject();
        try {
            object.put("to", new JSONArray(numbers));
            object.put("name", fullName);
            object.put("message", smsMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
    // END JSON OBJECT MAKER
    // END STATIC HELPER FUNCTIONS
}