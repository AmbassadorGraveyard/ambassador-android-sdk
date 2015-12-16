package com.ambassador.ambassadorsdk.internal;

import android.os.Handler;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInApi;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

/**
 *
 */
public class RequestManager {

    /** */
    @Inject
    AmbassadorConfig ambassadorConfig;

    BulkShareApi bulkShareApi;
    ConversionsApi conversionsApi;
    IdentifyApi identifyApi;
    LinkedInApi linkedInApi;

    /**
     *
     */
    public interface RequestCompletion {
        void onSuccess(Object successResponse);
        void onFailure(Object failureResponse);
    }

    /**
     *
     */
    public RequestManager() {
        AmbassadorSingleton.getComponent().inject(this);

        bulkShareApi = new BulkShareApi(false);
        bulkShareApi.init();

        conversionsApi = new ConversionsApi(false);
        conversionsApi.init();

        identifyApi = new IdentifyApi(false);
        identifyApi.init();

        linkedInApi = new LinkedInApi(false);
        linkedInApi.init();
    }

    /** Instantiates and returns a new BulkShareApi */
    BulkShareApi createBulkShareApi() {
        return new BulkShareApi();
    }

    /** Instantiates and returns a new ConversionsApi */
    ConversionsApi createConversionsApi() {
        return new ConversionsApi();
    }

    /** Instantiates and returns a new IdentifyApi */
    IdentifyApi createIdentifyApi() {
        return new IdentifyApi();
    }

    /** Instantiates and returns a new LinkedInApi */
    LinkedInApi createLinkedInApi() {
        return new LinkedInApi();
    }

    /**
     *
     * @param contacts
     * @param messageToShare
     * @param completion
     */
    public void bulkShareSms(final List<ContactObject> contacts, final String messageToShare, final RequestCompletion completion) {
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();
        List<String> numberList = BulkShareHelper.verifiedSMSList(contacts);
        String name = ambassadorConfig.getUserFullName();
        BulkShareApi.BulkShareSmsBody body = BulkShareHelper.payloadObjectForSMS(numberList, name, messageToShare);

        bulkShareApi.bulkShareSms(uid, auth, body, completion);
    }

    /**
     *
     * @param contacts
     * @param messageToShare
     * @param completion
     */
    public void bulkShareEmail(final List<ContactObject> contacts, final String messageToShare, final RequestCompletion completion) {
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();
        List<String> emailList = BulkShareHelper.verifiedEmailList(contacts);
        BulkShareApi.BulkShareEmailBody body = BulkShareHelper.payloadObjectForEmail(emailList, ambassadorConfig.getReferralShortCode(), ambassadorConfig.getEmailSubjectLine(), messageToShare);

        bulkShareApi.bulkShareEmail(uid, auth, body, completion);
    }

    /**
     *
     * @param shareType
     */
    public void bulkShareTrack(final BulkShareHelper.SocialServiceTrackType shareType) {
        bulkShareTrack(null, shareType);
    }

    /**
     *
     * @param contacts
     * @param shareType
     */
    public void bulkShareTrack(final List<ContactObject> contacts, final BulkShareHelper.SocialServiceTrackType shareType) {
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();
        BulkShareApi.BulkShareTrackBody[] body;
        switch (shareType) {
            case SMS:
                body = BulkShareHelper.contactArray(BulkShareHelper.verifiedSMSList(contacts), shareType, ambassadorConfig.getReferrerShortCode());
                break;
            case EMAIL:
                body = BulkShareHelper.contactArray(BulkShareHelper.verifiedEmailList(contacts), shareType, ambassadorConfig.getReferrerShortCode());
                break;
            default:
                body = BulkShareHelper.contactArray(shareType, ambassadorConfig.getReferrerShortCode());
                break;
        }

        bulkShareApi.bulkShareTrack(uid, auth, body);
    }

    /**
     *
     * @param conversionParameters
     * @param completion
     */
    public void registerConversionRequest(final ConversionParameters conversionParameters, final RequestCompletion completion) {
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();
        ConversionsApi.RegisterConversionRequestBody body = ConversionUtility.createConversionRequestBody(conversionParameters, ambassadorConfig.getIdentifyObject());
        conversionsApi.registerConversionRequest(uid, auth, body, completion);
    }

    /**
     *
     */
    public void identifyRequest() {
        PusherChannel.setRequestId(System.currentTimeMillis());

        String sessionId = PusherChannel.getSessionId();
        String requestId = String.valueOf(PusherChannel.getRequestId());
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();

        String campaignId = ambassadorConfig.getCampaignID();
        String userEmail = ambassadorConfig.getUserEmail();
        String augur = ambassadorConfig.getIdentifyObject();
        IdentifyApi.IdentifyRequestBody body = new IdentifyApi.IdentifyRequestBody(campaignId, userEmail, augur);

        identifyApi.identifyRequest(sessionId, requestId, uid, auth, body);
    }

    /**
     *
     * @param email
     * @param firstName
     * @param lastName
     * @param completion
     */
    public void updateNameRequest(final String email, final String firstName, final String lastName, final RequestCompletion completion) {
        String sessionId = PusherChannel.getSessionId();
        String requestId = String.valueOf(PusherChannel.getRequestId());
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();
        IdentifyApi.UpdateNameRequestBody body = new IdentifyApi.UpdateNameRequestBody(email, firstName, lastName);

        identifyApi.updateNameRequest(sessionId, requestId, uid, auth, body, completion);
    }

    /**
     *
     * @param completion
     */
    void createPusherChannel(final RequestCompletion completion) {
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();

        identifyApi.createPusherChannel(uid, auth, completion);
    }

    /**
     *
     * @param url
     * @param completion
     */
    void externalPusherRequest(final String url, final RequestCompletion completion) {
        String uid = ambassadorConfig.getUniversalID();
        String auth = ambassadorConfig.getUniversalKey();
        identifyApi.externalPusherRequest(url, uid, auth, completion);
    }

    /**
     *
     * @param tweetString
     * @param completion
     */
    public void postToTwitter(final String tweetString, final RequestCompletion completion) {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(twitterSession);
        twitterApiClient.getStatusesService().update(tweetString, null, null, null, null, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                completion.onSuccess("Successfully posted to Twitter");
            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException e) {
                if (e.toString().toLowerCase().contains("no authentication")) {
                    completion.onFailure("auth");
                } else {
                    completion.onFailure("Failure Postring to Twitter");
                }
            }
        });
    }

    /**
     *
     * @param code
     * @param completion
     */
    public void linkedInLoginRequest(final String code, final RequestCompletion completion) {
        String charset = "UTF-8";
        String urlParams = null;

        try {
            urlParams = "grant_type=authorization_code&code=" + URLEncoder.encode(code, charset) +
                    "&redirect_uri=" + URLEncoder.encode(AmbassadorConfig.CALLBACK_URL, charset) +
                    "&client_id=" + URLEncoder.encode(AmbassadorConfig.LINKED_IN_CLIENT_ID, charset) +
                    "&client_secret=" + URLEncoder.encode(AmbassadorConfig.LINKED_IN_CLIENT_SECRET, charset);
        } catch (UnsupportedEncodingException e) {
            Utilities.debugLog("LinkedIn", "LinkedIn Login Request failed due to UnsupportedEncodingException -" + e.getMessage());
        }

        linkedInApi.login(urlParams, completion, new LinkedInAuthorizedListener() {
            @Override
            public void linkedInAuthorized(String accessToken) {
                ambassadorConfig.setLinkedInToken(accessToken);
            }
        });
    }

    /**
     *
     */
    public interface LinkedInAuthorizedListener {
        void linkedInAuthorized(String accessToken);
    }

    /**
     *
     * @param requestBody
     * @param completion
     */
    public void postToLinkedIn(LinkedInApi.LinkedInPostRequest requestBody, final RequestCompletion completion) {
        linkedInApi.post(ambassadorConfig.getLinkedInToken(), requestBody, completion);
    }

    /**
     *
     * @param completion
     */
    public void getProfileLinkedIn(final RequestCompletion completion) {
        linkedInApi.getProfile(ambassadorConfig.getLinkedInToken(), completion);
    }

}
