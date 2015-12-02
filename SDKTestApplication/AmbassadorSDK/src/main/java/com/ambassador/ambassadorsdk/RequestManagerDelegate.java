package com.ambassador.ambassadorsdk;

import org.json.JSONObject;

import java.util.List;

import twitter4j.auth.RequestToken;

public interface RequestManagerDelegate {

    void bulkShareSms(List<ContactObject> a, String b, RequestManager.RequestCompletion c);

    void bulkShareEmail(List<ContactObject> a, String b, RequestManager.RequestCompletion c);

    void bulkShareTrack(List<ContactObject> a, BulkShareHelper.SocialServiceTrackType b);

    void bulkShareTrack(BulkShareHelper.SocialServiceTrackType a);

    void registerConversionRequest(ConversionParameters a, RequestManager.RequestCompletion b);

    void identifyRequest();

    void updateNameRequest(String a, String b, String c, RequestManager.RequestCompletion d);

    void createPusherChannel(RequestManager.RequestCompletion a);

    void externalPusherRequest(String a, RequestManager.RequestCompletion b);

    void twitterLoginRequest(RequestManager.RequestCompletion a);

    void twitterAccessTokenRequest(String a, RequestToken b, RequestManager.RequestCompletion c);

    void postToTwitter(String a, RequestManager.RequestCompletion b);

    void linkedInLoginRequest(String a, RequestManager.RequestCompletion b);

    void postToLinkedIn(JSONObject a, RequestManager.RequestCompletion b);

}
