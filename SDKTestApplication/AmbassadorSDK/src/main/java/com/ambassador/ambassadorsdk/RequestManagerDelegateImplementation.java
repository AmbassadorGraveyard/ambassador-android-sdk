package com.ambassador.ambassadorsdk;

import org.json.JSONObject;

import java.util.List;

import twitter4j.auth.RequestToken;

/**
 * Created by dylan on 12/2/15.
 */
public class RequestManagerDelegateImplementation implements RequestManagerDelegate {

    @Override
    public void bulkShareSms(List<ContactObject> a, String b, RequestManager.RequestCompletion c) {

    }

    @Override
    public void bulkShareEmail(List<ContactObject> a, String b, RequestManager.RequestCompletion c) {

    }

    @Override
    public void bulkShareTrack(List<ContactObject> a, BulkShareHelper.SocialServiceTrackType b) {

    }

    @Override
    public void bulkShareTrack(BulkShareHelper.SocialServiceTrackType a) {

    }

    @Override
    public void registerConversionRequest(ConversionParameters a, RequestManager.RequestCompletion b) {

    }

    @Override
    public void identifyRequest() {

    }

    @Override
    public void updateNameRequest(String a, String b, String c, RequestManager.RequestCompletion d) {

    }

    @Override
    public void createPusherChannel(RequestManager.RequestCompletion a) {

    }

    @Override
    public void externalPusherRequest(String a, RequestManager.RequestCompletion b) {

    }

    @Override
    public void twitterLoginRequest(RequestManager.RequestCompletion a) {

    }

    @Override
    public void twitterAccessTokenRequest(String a, RequestToken b, RequestManager.RequestCompletion c) {

    }

    @Override
    public void postToTwitter(String a, RequestManager.RequestCompletion b) {

    }

    @Override
    public void linkedInLoginRequest(String a, RequestManager.RequestCompletion b) {

    }

    @Override
    public void postToLinkedIn(JSONObject a, RequestManager.RequestCompletion b) {

    }

}
