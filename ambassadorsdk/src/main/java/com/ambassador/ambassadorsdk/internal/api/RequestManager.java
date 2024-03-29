package com.ambassador.ambassadorsdk.internal.api;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.envoy.EnvoyApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;

/**
 * Handles all requests at the highest level. This is what all other internal classes use.
 * Prepares parameters and calls the different Api classes.
 */
public class RequestManager {

    @Inject protected User user;
    @Inject protected Campaign campaign;
    @Inject protected BulkShareHelper bulkShareHelper;

    protected Auth auth;
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
    public RequestManager(Auth auth) {
        this(true, auth);
    }

    /**
     * Constructor with parameter for optionally initializing APIs.
     * @param doInit whether or not to initialize Api objects.
     */
    @Inject
    public RequestManager(boolean doInit, Auth auth) {
        this.auth = auth;
        AmbSingleton.getInstance().getAmbComponent().inject(this);
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
        String authKey = auth.getSdkToken();
        List<String> numberList = bulkShareHelper.verifiedSMSList(contacts);
        String name = user.getAmbassadorIdentification().getFirstName() + " " + user.getAmbassadorIdentification().getLastName();
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
        String authKey = auth.getSdkToken();
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
        String authKey = auth.getSdkToken();
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
        String authKey = auth.getSdkToken();
        Gson gson = new Gson();
        JsonObject getDeviceData = gson.fromJson(user.getDeviceData(), JsonElement.class).getAsJsonObject();
        JsonObject consumer = getDeviceData.getAsJsonObject("consumer");
        JsonObject device = getDeviceData.getAsJsonObject("device");

        String consumerId = consumer.get("UID").getAsString();
        String type = device.get("type").getAsString();
        String deviceId = device.get("ID").getAsString();

        ConversionsApi.RegisterConversionRequestBody body = new ConversionsApi.RegisterConversionRequestBody(
            new ConversionsApi.RegisterConversionRequestBody.FingerprintObject(consumerId, type, deviceId),
            new ConversionsApi.RegisterConversionRequestBody.FieldsObject(conversionParameters, campaign.getReferredByShortCode())
        );

        conversionsApi.registerConversionRequest(uid, authKey, body, completion);
    }

    /**
     * Identifies the user on the Ambassador backend using the session info
     * and the identify info.
     */
    public void identifyRequest(String identifyType, PusherManager pusherManager, RequestCompletion completion) {
        if (pusherManager.getChannel() == null) {
            if (completion != null) completion.onFailure(null);
            return;
        }

        pusherManager.newRequest();

        String sessionId = pusherManager.getSessionId();
        String requestId = String.valueOf(pusherManager.getRequestId());
        String uid = auth.getUniversalId();
        String authKey = auth.getSdkToken();

        String campaignId = campaign.getId();
        String userId = user.getUserId();
        String deviceData = user.getDeviceData() != null ? user.getDeviceData().toString() : null;
        IdentifyApi.IdentifyRequestBody body = new IdentifyApi.IdentifyRequestBody(campaignId, userId, deviceData, user.getAmbassadorIdentification());
        body.identify_type = identifyType;

        identifyApi.identifyRequest(sessionId, requestId, uid, authKey, body, completion);
    }

    /**
     * Identifies a user with default identifyType = "".
     */
    public void identifyRequest(PusherManager pusherManager, RequestCompletion completion) {
        identifyRequest("", pusherManager, completion);
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
        String authKey = auth.getSdkToken();
        IdentifyApi.UpdateNameRequestBody body = new IdentifyApi.UpdateNameRequestBody(email, firstName, lastName);

        identifyApi.updateNameRequest(sessionId, requestId, uid, authKey, body, completion);
    }

    /**
     * Requests data about a short code so we can present a personalized welcome screen.
     * @param shortCode the referred by short code to request data about.
     * @param requestCompletion callback for request completion.
     */
    public void getUserFromShortCode(final String shortCode, final RequestCompletion requestCompletion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getSdkToken();

        identifyApi.getUserFromShortCode(shortCode, uid, authKey, requestCompletion);
    }

    /**
     * Requests campaign from short_code.
     * @param shortCode the referred by short code used to request data.
     */
    public String getCampaignIdFromShortCode(final String shortCode) {
        String uid = auth.getUniversalId();
        String authKey = auth.getSdkToken();

        return identifyApi.getCampaignIdFromShortCode(shortCode, uid, authKey);
    }

    /**
     * Requests data about a company. Returned data is based on authenticated headers.
     * @param requestCompletion callback for request completion.
     */
    public void getCompanyInfo(final RequestCompletion requestCompletion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getSdkToken();
        identifyApi.getCompanyInfo(uid, authKey, requestCompletion);
    }

    /**
     * Requests an Envoy id and secret to use for social OAuth stuff.
     * @param companyUid the ambassador id for the company obtained by getCompanyInfo(...).
     * @param requestCompletion callback for request completion.
     */
    public void getEnvoyKeys(String companyUid, final RequestCompletion requestCompletion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getSdkToken();
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
        String authKey = auth.getSdkToken();

        identifyApi.createPusherChannel(uid, authKey, completion);
    }

    /**
     * Hits a passed in url with authenticated headers
     * @param url the url to hit
     * @param completion callback for request completion
     */
    public void externalPusherRequest(final String url, final RequestCompletion completion) {
        String uid = auth.getUniversalId();
        String authKey = auth.getSdkToken();
        identifyApi.externalPusherRequest(url, uid, authKey, completion);
    }

}
