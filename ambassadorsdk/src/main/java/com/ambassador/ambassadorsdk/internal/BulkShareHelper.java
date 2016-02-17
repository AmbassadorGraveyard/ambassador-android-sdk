package com.ambassador.ambassadorsdk.internal;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.models.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 *
 */
public class BulkShareHelper { // TODO: Make final after UI tests figured out

    /** */
    @Inject protected RequestManager requestManager;

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
        AmbassadorSingleton.getInstanceComponent().inject(this);
    }

    /**
     *
     * @param messageToShare
     * @param contacts
     * @param phoneNumbers
     * @param completion
     */
    public void bulkShare(final String messageToShare, final List<Contact> contacts, Boolean phoneNumbers, final BulkShareCompletion completion) {
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
     * @param contacts
     * @return
     */
    public static ArrayList<String> verifiedSMSList(List<Contact> contacts) {
        ArrayList<String> verifiedNumbers = new ArrayList<>();
        for (Contact contact : contacts) {
            String strippedNum = contact.getPhoneNumber().replaceAll("[^0-9]", "");
            if ((strippedNum.length() == 11 || strippedNum.length() == 10 || strippedNum.length() == 7) && !verifiedNumbers.contains(strippedNum)) {
                verifiedNumbers.add(strippedNum);
            }
        }

        return verifiedNumbers;
    }

    /**
     *
     * @param contacts
     * @return
     */
    public static ArrayList<String> verifiedEmailList(List<Contact> contacts) {
        ArrayList<String> verifiedEmails = new ArrayList<>();
        for (Contact contact : contacts) {
            if (BulkShareHelper.isValidEmail(contact.getEmailAddress()) && !verifiedEmails.contains(contact.getEmailAddress())) {
                verifiedEmails.add(contact.getEmailAddress());
            }
        }

        return verifiedEmails;
    }
    /**
     *
     * @param emailAddress
     * @return
     */
    public static boolean isValidEmail(String emailAddress) {
        Pattern emailRegex = Pattern.compile("^(.+)@(.+)\\.(.+)$");
        Matcher matcher = emailRegex.matcher(emailAddress);
        return matcher.matches();
    }

    /**
     *
     * @param trackType
     * @param shortCode
     * @return
     */
    public static BulkShareApi.BulkShareTrackBody[] contactArray(SocialServiceTrackType trackType, String shortCode, String fromEmail) {
        return contactArray(null, trackType, shortCode, fromEmail);
    }

    /**
     *
     * @param values
     * @param trackType
     * @param shortCode
     * @return
     */
    public static BulkShareApi.BulkShareTrackBody[] contactArray(List<String> values, SocialServiceTrackType trackType, String shortCode, String fromEmail) {
        String short_code = shortCode;
        String social_name = trackType.toString();

        if (values == null) {
            values = new ArrayList<>();
            values.add("");
        }

        BulkShareApi.BulkShareTrackBody[] objectsList = new BulkShareApi.BulkShareTrackBody[values.size()];

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

            BulkShareApi.BulkShareTrackBody newObject = new BulkShareApi.BulkShareTrackBody(short_code, social_name, recipient_email, recipient_username, fromEmail);
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
    public static BulkShareApi.BulkShareEmailBody payloadObjectForEmail(List<String> emailsList, String shortCode, String emailSubject, String message, String fromEmail) {
        String[] emails = emailsList.toArray(new String[emailsList.size()]);
        return new BulkShareApi.BulkShareEmailBody(emailSubject, message, shortCode, fromEmail, emails);
    }

    /**
     *
     * @param numbersList
     * @param fullName
     * @param smsMessage
     * @return
     */
    public static BulkShareApi.BulkShareSmsBody payloadObjectForSMS(List<String> numbersList, String fullName, String smsMessage, String fromEmail) {
        String[] numbers = numbersList.toArray(new String[numbersList.size()]);
        return new BulkShareApi.BulkShareSmsBody(fullName, smsMessage, fromEmail, numbers);
    }

}