package com.ambassador.ambassadorsdk.internal.api;

import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.PusherChannel;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.api.linkedin.LinkedInApi;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

/**
 * Handles all requests at the highest level. This is what all other internal classes use.
 * Prepares parameters and calls the different Api classes.
 */
public class RequestManager { // TODO: Make final after UI tests figured out

    @Inject protected AmbassadorConfig ambassadorConfig;

    @Inject protected Auth auth;
    @Inject protected User user;
    @Inject protected Campaign campaign;

    protected BulkShareApi bulkShareApi;
    protected ConversionsApi conversionsApi;
    protected IdentifyApi identifyApi;
    protected LinkedInApi linkedInApi;

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
        AmbassadorSingleton.getInstanceComponent().inject(this);
        bulkShareApi = new BulkShareApi(false);
        conversionsApi = new ConversionsApi(false);
        identifyApi = new IdentifyApi(false);
        linkedInApi = new LinkedInApi(false);
        if (doInit) {
            bulkShareApi.init();
            conversionsApi.init();
            identifyApi.init();
            linkedInApi.init();
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
        List<String> numberList = BulkShareHelper.verifiedSMSList(contacts);
        String name = user.getFirstName() + " " + user.getLastName();
        String fromEmail = user.getEmail();
        BulkShareApi.BulkShareSmsBody body = BulkShareHelper.payloadObjectForSMS(numberList, name, messageToShare, fromEmail);

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
        List<String> emailList = BulkShareHelper.verifiedEmailList(contacts);
        String fromEmail = user.getEmail();
        BulkShareApi.BulkShareEmailBody body = BulkShareHelper.payloadObjectForEmail(emailList, campaign.getReferredByShortCode(), campaign.getEmailSubject(), messageToShare, fromEmail);

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
        String fromEmail = user.getEmail();
        BulkShareApi.BulkShareTrackBody[] body;
        switch (shareType) {
            case SMS:
                body = BulkShareHelper.contactArray(BulkShareHelper.verifiedSMSList(contacts), shareType, campaign.getReferredByShortCode(), fromEmail);
                break;
            case EMAIL:
                body = BulkShareHelper.contactArray(BulkShareHelper.verifiedEmailList(contacts), shareType, campaign.getReferredByShortCode(), fromEmail);
                break;
            default:
                body = BulkShareHelper.contactArray(shareType, campaign.getReferredByShortCode(), fromEmail);
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
        ConversionsApi.RegisterConversionRequestBody body = ConversionUtility.createConversionRequestBody(conversionParameters, ambassadorConfig.getIdentifyObject());
        conversionsApi.registerConversionRequest(uid, authKey, body, completion);
    }

    /**
     * Updates the PusherChannel request ID to the current time in
     * milliseconds.
     */
    private void updateRequestId() {
        PusherChannel.setRequestId(System.currentTimeMillis());
    }

    /**
     * Identifies the user on the Ambassador backend using the session info
     * and the identify info returned from augur.
     */
    public void identifyRequest() {
        updateRequestId();

        String sessionId = PusherChannel.getSessionId();
        String requestId = String.valueOf(PusherChannel.getRequestId());
        String uid = auth.getUniversalId();
        String authKey = auth.getUniversalToken();

        String campaignId = campaign.getId();
        String userEmail = user.getEmail();
        String augur = ambassadorConfig.getIdentifyObject();
        IdentifyApi.IdentifyRequestBody body = new IdentifyApi.IdentifyRequestBody(campaignId, userEmail, augur);

        identifyApi.identifyRequest(sessionId, requestId, uid, authKey, body);
    }

    /**
     * Associates an email + name with a session on the Ambassador backend
     * @param email the new email to save in the Ambassador backend
     * @param firstName the new first name to save in the Ambassador backend
     * @param lastName the new last name to save in the Ambassador backend
     * @param completion callback for request completion
     */
    public void updateNameRequest(final String email, final String firstName, final String lastName, final RequestCompletion completion) {
        updateRequestId();

        String sessionId = PusherChannel.getSessionId();
        String requestId = String.valueOf(PusherChannel.getRequestId());
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
    public void updateGcmRegistrationToken(final String registrationToken, final RequestCompletion completion) {
        // set pusher info (probably) and pass through to an api object, including the completion directly, dont call completion callback here
        completion.onSuccess("success");
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

    /**
     * Attempts to post to Twitter on behalf of the user.
     * @param tweetString the mesage to Tweet
     * @param completion callback for request completion
     */
    public void postToTwitter(final String tweetString, final RequestCompletion completion) {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(twitterSession);
        twitterApiClient.getStatusesService().update(tweetString, null, null, null, null, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                completion.onSuccess("Successfully posted to Twitter");
                Utilities.debugLog("amb-request", "SUCCESS: RequestManager.postToTwitter(...)");
            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException e) {
                if (e.toString().toLowerCase().contains("no authentication")) {
                    completion.onFailure("auth");
                } else {
                    completion.onFailure("Failure Posting to Twitter");
                }
                Utilities.debugLog("amb-request", "FAILURE: RequestManager.postToTwitter(...)");
            }
        });
    }

    /**
     * Trades a request code for an access token with the LinkedIn API.
     * @param code the request code that the OAuth gave us
     * @param completion callback for request completion
     */
    public void linkedInLoginRequest(final String code, final RequestCompletion completion) {
        String urlParams = createLinkedInLoginBody(code);
        linkedInApi.login(urlParams, completion, new LinkedInAuthorizedListener() {
            @Override
            public void linkedInAuthorized(String accessToken) {
                auth.setLinkedInToken(accessToken);
            }
        });
    }

    /**
     * Creates String form urlencoded parameters to pass to LinkedIn login.
     * @param code the request code that the OAuth gave us
     * @return a String form of LinkedIn's required form urlencoded params
     */
    @Nullable
    protected String createLinkedInLoginBody(String code) {
        String urlParams = "";
        String charset = "UTF-8";
        String callbackUrl = new StringResource(R.string.linked_in_callback_url).getValue();
        String clientId = new StringResource(R.string.linked_in_client_id).getValue();
        String clientSecret = new StringResource(R.string.linked_in_client_secret).getValue();
        try {
            urlParams = "grant_type=authorization_code&code=" + URLEncoder.encode(code, charset) +
                    "&redirect_uri=" + URLEncoder.encode(callbackUrl, charset) +
                    "&client_id=" + URLEncoder.encode(clientId, charset) +
                    "&client_secret=" + URLEncoder.encode(clientSecret, charset);
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        return urlParams;
    }

    /**
     * Listens for LinkedIn to be authorized and takes an access token back
     */
    public interface LinkedInAuthorizedListener {
        void linkedInAuthorized(String accessToken);
    }

    /**
     * Sends a request to LinkedIn for us to post on behalf of the user.
     * @param requestBody the LinkedInPostRequest describing the post content
     * @param completion callback for request completion
     */
    public void postToLinkedIn(LinkedInApi.LinkedInPostRequest requestBody, final RequestCompletion completion) {
        linkedInApi.post(auth.getLinkedInToken(), requestBody, completion);
    }

    /**
     * Performs a GET request to grab the user's LinkedIn profile info.
     * Doesn't return any information, only calls back with request status.
     * @param completion callback for request completion
     */
    public void getProfileLinkedIn(final RequestCompletion completion) {
        linkedInApi.getProfile(auth.getLinkedInToken(), completion);
    }

}
