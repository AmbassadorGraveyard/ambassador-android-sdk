package com.ambassador.ambassadorsdk.internal.api;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.envoy.EnvoyApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.models.Contact;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles all requests at the highest level. This is what all other internal classes use.
 * Prepares parameters and calls the different Api classes.
 */
@Singleton
public class RequestManager {

    @Inject protected Auth auth;
    @Inject protected User user;
    @Inject protected Campaign campaign;
    @Inject protected BulkShareHelper bulkShareHelper;

    protected BulkShareApi bulkShareApi;
    protected ConversionsApi conversionsApi;
    protected IdentifyApi identifyApi;
    protected EnvoyApi envoyApi;

    /**
     * Standard callback used throughout the codebase.
     */
    public interface RequestCompletion {
        void onSuccess(Object successResponse);
        void onFailure(Object failureResponse);
    }

    /**
     * Default constructor.
     * Instantiates the RequestManager and automatically initializes the APIs.
     */
    public RequestManager() {
        this(true);
    }

    /**
     * Constructor with parameter for optionally initializing APIs.
     * @param doInit whether or not to initialize Api objects.
     */
    public RequestManager(boolean doInit) {
        AmbSingleton.inject(this);
        bulkShareApi = new BulkShareApi(false);
        conversionsApi = new ConversionsApi(false);
        identifyApi = new IdentifyApi(false);
        envoyApi = new EnvoyApi(false);
        if (doInit) {
            bulkShareApi.init();
            conversionsApi.init();
            identifyApi.init();
            envoyApi.init();
        }
    }

    /**
     * Sends a request to the Ambassador backend which will deliver text messages
     * to a list of contacts with a passed in message.
     * @param contacts the list of ContactObjects to send the SMS to
     * @param messageToShare the message to send in the SMS
     * @param completion callback for request completion
     */
    public void bulkShareSms(final List<Contact> contacts, final String messageToShare, final RequestCompletion completion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        List<String> numberList = bulkShareHelper.verifiedSMSList(contacts);
        String name = user.getFirstName() + " " + user.getLastName();
        String fromEmail = user.getAmbassadorIdentification().getEmail();
        BulkShareApi.BulkShareSmsBody body = bulkShareHelper.payloadObjectForSMS(numberList, name, messageToShare, fromEmail);

        bulkShareApi.bulkShareSms(uid, authKey, body, completion);
    }

    /**
     * Sends a request to the Ambassador backend which will deliver emails
     * to a list of contacts with a passed in message.
     * @param contacts the list of ContactObjects to send the email to
     * @param messageToShare the message to send in the email
     * @param completion callback for request completion
     */
    public void bulkShareEmail(final List<Contact> contacts, final String messageToShare, final RequestCompletion completion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        List<String> emailList = bulkShareHelper.verifiedEmailList(contacts);
        String fromEmail = user.getAmbassadorIdentification().getEmail();
        BulkShareApi.BulkShareEmailBody body = bulkShareHelper.payloadObjectForEmail(emailList, campaign.getShortCode(), campaign.getEmailSubject(), messageToShare, fromEmail);

        bulkShareApi.bulkShareEmail(uid, authKey, body, completion);
    }

    /**
     * Method overload not requiring a list of contacts
     * @param shareType enum that describes the source of the share: Facebook, SMS, etc.
     */
    public void bulkShareTrack(final BulkShareHelper.SocialServiceTrackType shareType) {
        bulkShareTrack(null, shareType);
    }

    /**
     * Tells the Ambassador backend info about the share, like who it
     * was sent to, how it was shared, etc.
     * @param contacts the list of contacts that received the share
     * @param shareType enum that describes the source of the share: Facebook, SMS, etc.
     */
    public void bulkShareTrack(final List<Contact> contacts, final BulkShareHelper.SocialServiceTrackType shareType) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        String fromEmail = user.getAmbassadorIdentification().getEmail();
        BulkShareApi.BulkShareTrackBody[] body;
        switch (shareType) {
            case SMS:
                body = bulkShareHelper.contactArray(bulkShareHelper.verifiedSMSList(contacts), shareType, campaign.getShortCode(), fromEmail);
                break;
            case EMAIL:
                body = bulkShareHelper.contactArray(bulkShareHelper.verifiedEmailList(contacts), shareType, campaign.getShortCode(), fromEmail);
                break;
            default:
                body = bulkShareHelper.contactArray(shareType, campaign.getShortCode(), fromEmail);
                break;
        }

        bulkShareApi.bulkShareTrack(uid, authKey, body);
    }

    /**
     * Registers a conversion on the Ambassador backend.
     * @param conversionParameters the ConversionParameters object storing all info about the conversion
     * @param completion callback for request completion
     */
    public void registerConversionRequest(final ConversionParameters conversionParameters, final RequestCompletion completion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        ConversionsApi.RegisterConversionRequestBody body = ConversionUtility.createConversionRequestBody(conversionParameters, user.getAugurData().toString());
        conversionsApi.registerConversionRequest(uid, authKey, body, completion);
    }

    /**
     * Identifies the user on the Ambassador backend using the session info
     * and the identify info returned from augur.
     */
    public void identifyRequest(PusherManager pusherManager, RequestCompletion completion) {
        if (pusherManager.getChannel() == null) {
            if (completion != null) completion.onFailure(null);
            return;
        }

        pusherManager.newRequest();

        String sessionId = pusherManager.getSessionId();
        String requestId = String.valueOf(pusherManager.getRequestId());
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();

        String campaignId = campaign.getId();
        String userId = user.getUserId();
        String augur = user.getAugurData() != null ? user.getAugurData().toString() : null;
        IdentifyApi.IdentifyRequestBody body = new IdentifyApi.IdentifyRequestBody(campaignId, userId, augur, user.getAmbassadorIdentification());

        identifyApi.identifyRequest(sessionId, requestId, uid, authKey, body, completion);
    }

    /**
     * Associates an email + name with a session on the Ambassador backend
     * @param email the new email to save in the Ambassador backend
     * @param firstName the new first name to save in the Ambassador backend
     * @param lastName the new last name to save in the Ambassador backend
     * @param completion callback for request completion
     */
    public void updateNameRequest(PusherManager pusherManager, final String email, final String firstName, final String lastName, final RequestCompletion completion) {
        if (pusherManager.getChannel() == null) {
            return;
        }

        pusherManager.newRequest();

        String sessionId = pusherManager.getSessionId();
        String requestId = String.valueOf(pusherManager.getRequestId());
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        IdentifyApi.UpdateNameRequestBody body = new IdentifyApi.UpdateNameRequestBody(email, firstName, lastName);

        identifyApi.updateNameRequest(sessionId, requestId, uid, authKey, body, completion);
    }

    /**
     * Associates a GCM registration token with a user on the backend to serve push notifications.
     * @param registrationToken the token generated by GCM for this unique user
     * @param completion callback for request completion
     */
    public void updateGcmRegistrationToken(final String email, final String registrationToken, final RequestCompletion completion) {
        IdentifyApi.UpdateGcmTokenBody body = new IdentifyApi.UpdateGcmTokenBody(email, registrationToken);
        completion.onSuccess("success");
    }

    /**
     * Requests data about a short code so we can present a personalized welcome screen.
     * @param shortCode the referred by short code to request data about.
     * @param requestCompletion callback for request completion.
     */
    public void getUserFromShortCode(final String shortCode, final RequestCompletion requestCompletion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();

        identifyApi.getUserFromShortCode(shortCode, uid, authKey, requestCompletion);
    }

    /**
     * Requests data about a company. Returned data is based on authenticated headers.
     * @param requestCompletion callback for request completion.
     */
    public void getCompanyInfo(final RequestCompletion requestCompletion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        identifyApi.getCompanyInfo(uid, authKey, requestCompletion);
    }

    /**
     * Requests an Envoy id and secret to use for social OAuth stuff.
     * @param companyUid the ambassador id for the company obtained by getCompanyInfo(...).
     * @param requestCompletion callback for request completion.
     */
    public void getEnvoyKeys(String companyUid, final RequestCompletion requestCompletion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        identifyApi.getEnvoyKeys(uid, authKey, companyUid, requestCompletion);
    }

    /**
     * Requests an access token usable to share to a social network authenticated as a user.
     * @param popup the String unique popup code generated during OAuth.
     * @param requestCompletion callback for request completion.
     */
    public void getEnvoyAccessToken(String popup, final RequestCompletion requestCompletion) {
        String clientId = auth.getEnvoyId();
        String clientSecret = auth.getEnvoySecret();
        envoyApi.getAccessToken(clientId, clientSecret, popup, requestCompletion);
    }

    /**
     * Shares a message to a users authenticated social account using Envoy.
     * @param provider the String name of the provider to share to [facebook, twitter, linkedin].
     * @param message the String message to share to the social network.
     * @param requestCompletion callback for request compeltion.
     */
    public void shareWithEnvoy(String provider,  String message, final RequestCompletion requestCompletion) {
        String clientId = auth.getEnvoyId();
        String clientSecret = auth.getEnvoySecret();

        String accessToken;
        switch(provider) {
            case "facebook":
                accessToken = user.getFacebookAccessToken();
                break;
            case "twitter":
                accessToken = user.getTwitterAccessToken();
                break;
            case "linkedin":
                accessToken = user.getLinkedInAccessToken();
                break;
            default:
                requestCompletion.onFailure(null);
                return;
        }

        envoyApi.share(provider, clientId, clientSecret, accessToken, message, requestCompletion);
    }

    /**
     * Asks the Ambassador backend to open a Pusher channel.
     * Stores the channel information when it receives back.
     * @param completion callback for request completion
     */
    public void createPusherChannel(final RequestCompletion completion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();

        identifyApi.createPusherChannel(uid, authKey, completion);
    }

    /**
     * Hits a passed in url with authenticated headers
     * @param url the url to hit
     * @param completion callback for request completion
     */
    public void externalPusherRequest(final String url, final RequestCompletion completion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();
        identifyApi.externalPusherRequest(url, uid, authKey, completion);
    }

}
