package com.ambassador.ambassadorsdk.internal;

import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 *
 */
public class BulkShareHelper {

    /** */
    @Inject
    RequestManager requestManager;

    /**
     *
     */
    public interface BulkShareCompletion {
        void bulkShareSuccess();
        void bulkShareFailure();
    }

    /**
     *
     */
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

    /**
     *
     */
    public BulkShareHelper() {
        AmbassadorSingleton.getComponent().inject(this);
    }

    /**
     *
     * @param messageToShare
     * @param contacts
     * @param phoneNumbers
     * @param completion
     */
    public void bulkShare(final String messageToShare, final List<ContactObject> contacts, Boolean phoneNumbers, final BulkShareCompletion completion) {
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
                public void onSuccess(Object successResponse) {
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

    /**
     *
     * @param contactObjects
     * @return
     */
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

    /**
     *
     * @param contactObjects
     * @return
     */
    static ArrayList<String> verifiedEmailList(List<ContactObject> contactObjects) {
        ArrayList<String> verifiedEmails = new ArrayList<>();
        for (ContactObject contact : contactObjects) {
            if (BulkShareHelper.isValidEmail(contact.getEmailAddress())) { verifiedEmails.add(contact.getEmailAddress()); }
        }

        return verifiedEmails;
    }

    /**
     *
     * @param emailAddress
     * @return
     */
    private static boolean isValidEmail(String emailAddress) {
        Pattern emailRegex = Pattern.compile("^(.+)@(.+)$");
        Matcher matcher = emailRegex.matcher(emailAddress);
        return matcher.matches();
    }

    /**
     *
     * @param trackType
     * @param shortCode
     * @return
     */
    static BulkShareApi.BulkShareTrackBody[] contactArray(SocialServiceTrackType trackType, String shortCode) {
        return contactArray(null, trackType, shortCode);
    }

    /**
     *
     * @param values
     * @param trackType
     * @param shortCode
     * @return
     */
    static BulkShareApi.BulkShareTrackBody[] contactArray(List<String> values, SocialServiceTrackType trackType, String shortCode) {
        String short_code = shortCode;
        String social_name = trackType.toString();

        BulkShareApi.BulkShareTrackBody[] objectsList = new BulkShareApi.BulkShareTrackBody[values.size()];
        if (values == null) {
            values = new ArrayList<>();
            values.add("");
        }

        for (int i = 0; i < values.size(); i++) {
            String recipient_email = "";
            String recipient_username = "";
            switch (trackType) {
                case SMS:
                    recipient_username = values.get(i);
                    break;
                case EMAIL:
                    recipient_email = values.get(i);
                    break;
            }

            BulkShareApi.BulkShareTrackBody newObject = new BulkShareApi.BulkShareTrackBody(short_code, social_name, recipient_email, recipient_username);
            objectsList[i] = newObject;
        }

        return objectsList;
    }

    /**
     *
     * @param emailsList
     * @param shortCode
     * @param emailSubject
     * @param message
     * @return
     */
    static BulkShareApi.BulkShareEmailBody payloadObjectForEmail(List<String> emailsList, String shortCode, String emailSubject, String message) {
        String[] emails = emailsList.toArray(new String[emailsList.size()]);
        return new BulkShareApi.BulkShareEmailBody(emailSubject, message, shortCode, emails);
    }

    /**
     *
     * @param numbersList
     * @param fullName
     * @param smsMessage
     * @return
     */
    static BulkShareApi.BulkShareSmsBody payloadObjectForSMS(List<String> numbersList, String fullName, String smsMessage) {
        String[] numbers = numbersList.toArray(new String[numbersList.size()]);
        return new BulkShareApi.BulkShareSmsBody(fullName, smsMessage, numbers);
    }

}