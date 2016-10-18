package com.ambassador.ambassadorsdk.internal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Middleman class to handle sharing and tracking SMS and email shares. Makes the share and track
 * requests and has helper methods to payload data.
 */
public class BulkShareHelper {

    /** Used to execute any actual HTTP requests. */
    @Inject protected RequestManager requestManager;

    /** Used to read useful information from the device and OS. */
    @Inject protected Device device;

    private static final int CHECK_SEND_SMS_PERMISSIONS = 1;

    /**
     * Enum to help with bulk share tracking. Defines the possible share sources and returns a String
     * useful for the request body of a bulk share track.
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
     * Default constructor. Injects dependencies.
     */
    public BulkShareHelper() {
        AmbSingleton.inject(this);
    }

    /**
     * Shares a message using SMS or email.
     * @param messageToShare the raw and final message that should appear in the email or sms.
     * @param contacts the list of Contact objects that should receive the message.
     * @param phoneNumbers boolean to determine whether this is an SMS share or email share.
     * @param completion callback for BulkShare completion.
     */
    public void bulkShare(final String messageToShare, final List<Contact> contacts, Boolean phoneNumbers, final BulkShareCompletion completion) {
        if (!phoneNumbers) {
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

            return;
        }

        //if only 1 contact and device is equipped to send, use native SMS for a better experience
        if (contacts.size() == 1 && AmbSingleton.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Contact contact = contacts.get(0);
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + contact.getPhoneNumber()));
            intent.putExtra("sms_body", messageToShare);
            intent.putExtra("exit_on_sent", true);
            completion.launchSmsIntent(contact.getPhoneNumber(), intent);
        }
        else {
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
        }
    }

    private boolean handleSendSMSPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(AmbSingleton.getContext(), Manifest.permission.SEND_SMS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, CHECK_SEND_SMS_PERMISSIONS);
        }
        return false;
    }

    /**
     * Processes a list of Contacts into a list suitable for SMS sharing.
     * @param contacts the list of contacts to process.
     * @return a List of Contact objects that all have suitable details for SMS sharing.
     */
    public ArrayList<String> verifiedSMSList(List<Contact> contacts) {
        ArrayList<String> verifiedNumbers = new ArrayList<>();
        if (contacts == null) return verifiedNumbers;
        for (Contact contact : contacts) {
            String strippedNum = contact.getPhoneNumber().replaceAll("[^0-9]", "");
            if ((strippedNum.length() == 11 || strippedNum.length() == 10 || strippedNum.length() == 7) && !verifiedNumbers.contains(strippedNum)) {
                verifiedNumbers.add(strippedNum);
            }
        }

        return verifiedNumbers;
    }

    /**
     * Processes a list of Contacts into a list suitable for email sharing.
     * @param contacts the list of contacts to process.
     * @return a List of Contact objects that all have suitable details for email sharing.
     */
    public ArrayList<String> verifiedEmailList(List<Contact> contacts) {
        ArrayList<String> verifiedEmails = new ArrayList<>();
        if (contacts == null) return verifiedEmails;
        for (Contact contact : contacts) {
            if (isValidEmail(contact.getEmailAddress()) && !verifiedEmails.contains(contact.getEmailAddress())) {
                verifiedEmails.add(contact.getEmailAddress());
            }
        }

        return verifiedEmails;
    }
    /**
     * Uses a regular expression to determine if an email address is valid.
     * @param emailAddress the String email address to check.
     * @return boolean determining if the email address is valid.
     */
    public boolean isValidEmail(String emailAddress) {
        Pattern emailRegex = Pattern.compile("^(.+)@(.+)\\.(.+)$");
        Matcher matcher = emailRegex.matcher(emailAddress);
        return matcher.matches();
    }

    /**
     * Sets up an array of BulKShareTrackBody objects which is required to perform a BulkShareTrack
     * request.
     * @param values the list of details on the sharing user. Can be phone numbers, emails, or list can be null.
     * @param trackType the enum SocialServiceTrackType describing how the message was shared.
     * @param shortCode short code of the sharing user.
     * @return an array of BulkShareTrackBodys fully prepared for a BulkShareTrack request.
     */
    public BulkShareApi.BulkShareTrackBody[] contactArray(List<String> values, SocialServiceTrackType trackType, String shortCode, String fromEmail) {
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
                default:
                    break;
            }

            BulkShareApi.BulkShareTrackBody newObject = new BulkShareApi.BulkShareTrackBody(short_code, social_name, recipient_email, recipient_username, fromEmail);
            objectsList[i] = newObject;
        }

        return objectsList;
    }

    /**
     * Method overload for contactArray that does not require a List of values.  This is useful because
     * the values are only relevant for tracking SMS and email.
     * @param trackType the enum SocialServiceTrackType describing how the message was shared.
     * @param shortCode short code of the sharing user.
     * @return array of BulkShareTrackBody from method overload.
     */
    public BulkShareApi.BulkShareTrackBody[] contactArray(SocialServiceTrackType trackType, String shortCode, String fromEmail) {
        return contactArray(null, trackType, shortCode, fromEmail);
    }


    /**
     * Creates request payload for bulk sharing an email message.
     * @param emailsList the list of String email addresses to share to.
     * @param shortCode the short code of the user sharing.
     * @param emailSubject the subject for the email.
     * @param message the message to put in the email.
     * @return a BulkShareEmailBody object required to make a bulkShare request for emails.
     */
    public BulkShareApi.BulkShareEmailBody payloadObjectForEmail(List<String> emailsList, String shortCode, String emailSubject, String message, String fromEmail) {
        String[] emails = emailsList.toArray(new String[emailsList.size()]);
        return new BulkShareApi.BulkShareEmailBody(emailSubject, message, shortCode, fromEmail, emails);
    }

    /**
     * Creates request payload for bulk sharing an SMS message.
     * @param numbersList the list of String phone numbers to share to.
     * @param fullName the full name of the user sharing.
     * @param smsMessage the message to put in the SMS.
     * @return a BulkShareSmsBody object required to make a bulkShare request for SMS.
     */
    public BulkShareApi.BulkShareSmsBody payloadObjectForSMS(List<String> numbersList, String fullName, String smsMessage, String fromEmail) {
        String[] numbers = numbersList.toArray(new String[numbersList.size()]);
        return new BulkShareApi.BulkShareSmsBody(fullName, smsMessage, fromEmail, numbers);
    }


    /**
     * Callback interface for the success/failure status of a bulkShare request. Also has callback
     * for launching SMS intent.
     */
    public interface BulkShareCompletion {
        void bulkShareSuccess();
        void bulkShareFailure();
        void launchSmsIntent(String phoneNumber, Intent intent);
    }

}