package com.ambassador.ambassadorsdk.internal;

import android.util.Log;

import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInApi;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.StatusesService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        BulkShareHelper.class,
        ConversionUtility.class,
        PusherChannel.class,
        IdentifyApi.IdentifyRequestBody.class,
        TwitterCore.class,
        Log.class
})
public class RequestManagerTest {

    RequestManager requestManager;
    AmbassadorConfig ambassadorConfig;

    BulkShareApi bulkShareApi;
    ConversionsApi conversionsApi;
    IdentifyApi identifyApi;
    LinkedInApi linkedInApi;

    String universalId = "abfd1c89-4379-44e2-8361-ee7b87332e32";
    String universalToken = "SDKToken 9de5757f801ca60916599fa3f3c92131b0e63c6a";
    String userFullName = "Test User";
    String identifyObject = "identifyObject";
    String campaignId = "260";
    String userEmail = "user@test.com";
    String linkedInToken = "linkedInToken";

    String sessionId = "sessionId";
    long requestId = 123L;

    @Mock
    SessionManager<TwitterSession> sessionManager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class,
                BulkShareHelper.class,
                ConversionUtility.class,
                IdentifyApi.IdentifyRequestBody.class,
                TwitterCore.class,
                Log.class
        );

        AmbassadorApplicationComponent mockComponent = Mockito.mock(AmbassadorApplicationComponent.class);
        Mockito.when(AmbassadorSingleton.getInstanceComponent()).thenReturn(mockComponent);
        Mockito.doNothing().when(mockComponent).inject(Mockito.any(RequestManager.class));

        bulkShareApi = Mockito.mock(BulkShareApi.class);
        conversionsApi = Mockito.mock(ConversionsApi.class);
        identifyApi = Mockito.mock(IdentifyApi.class);
        linkedInApi = Mockito.mock(LinkedInApi.class);

        RequestManager rm = new RequestManager(false);
        requestManager = Mockito.spy(rm);

        ambassadorConfig = Mockito.mock(AmbassadorConfig.class);
        Mockito.when(ambassadorConfig.getUniversalID()).thenReturn(universalId);
        Mockito.when(ambassadorConfig.getUniversalKey()).thenReturn(universalToken);
        Mockito.when(ambassadorConfig.getUserFullName()).thenReturn(userFullName);
        Mockito.when(ambassadorConfig.getIdentifyObject()).thenReturn(identifyObject);
        Mockito.when(ambassadorConfig.getCampaignID()).thenReturn(campaignId);
        Mockito.when(ambassadorConfig.getUserEmail()).thenReturn(userEmail);
        Mockito.when(ambassadorConfig.getLinkedInToken()).thenReturn(linkedInToken);

        requestManager.ambassadorConfig = ambassadorConfig;
        requestManager.bulkShareApi = bulkShareApi;
        requestManager.conversionsApi = conversionsApi;
        requestManager.identifyApi = identifyApi;
        requestManager.linkedInApi = linkedInApi;

        PowerMockito.spy(PusherChannel.class);
        PusherChannel.setRequestId(requestId);
        PusherChannel.setSessionId(sessionId);
    }

    @Test
    public void bulkShareSmsTest() {
        // ARRANGE
        List<ContactObject> contactObjects = new ArrayList<>();
        String messageToShare = "Check out Ambassador!";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.bulkShareSms(contactObjects, messageToShare, requestCompletion);

        // ASSERT
        Mockito.verify(bulkShareApi).bulkShareSms(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(BulkShareApi.BulkShareSmsBody.class), Mockito.eq(requestCompletion));
    }

    @Test
    public void bulkShareEmailTest() {
        // ARRANGE
        List<ContactObject> contactObjects = new ArrayList<>();
        String messageToShare = "Check out Ambassador!";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.bulkShareEmail(contactObjects, messageToShare, requestCompletion);

        // ASSERT
        Mockito.verify(bulkShareApi).bulkShareEmail(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(BulkShareApi.BulkShareEmailBody.class), Mockito.eq(requestCompletion));
    }

    @Test
    public void bulkShareTrackTest() {
        // ARRANGE
        List<ContactObject> contactObjects = new ArrayList<>();

        // ACT
        requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.SMS);
        requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.EMAIL);
        requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.FACEBOOK);
        requestManager.bulkShareTrack(contactObjects, BulkShareHelper.SocialServiceTrackType.SMS);
        requestManager.bulkShareTrack(contactObjects, BulkShareHelper.SocialServiceTrackType.EMAIL);
        requestManager.bulkShareTrack(contactObjects, BulkShareHelper.SocialServiceTrackType.FACEBOOK);

        // ASSERT
        Mockito.verify(bulkShareApi, Mockito.times(6)).bulkShareTrack(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(BulkShareApi.BulkShareTrackBody[].class));
    }

    @Test
    public void registerConversionRequestTest() {
        // ARRANGE
        ConversionParameters conversionParameters = Mockito.mock(ConversionParameters.class);
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);
        ConversionsApi.RegisterConversionRequestBody requestBody = Mockito.mock(ConversionsApi.RegisterConversionRequestBody.class);
        BDDMockito.given(ConversionUtility.createConversionRequestBody(conversionParameters, identifyObject)).willReturn(requestBody);

        // ACT
        requestManager.registerConversionRequest(conversionParameters, requestCompletion);

        // ASSERT
        Mockito.verify(conversionsApi).registerConversionRequest(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.eq(requestBody), Mockito.eq(requestCompletion));
    }

    @Test
    public void identifyRequestTest() throws Exception {
        // ACT
        requestManager.identifyRequest();
        String reqId = "" + PusherChannel.getRequestId();

        // ASSERT
        Assert.assertTrue((System.currentTimeMillis() - PusherChannel.getRequestId()) < 1000 * 60 * 60 * 24);
        Mockito.verify(identifyApi).identifyRequest(Mockito.eq(sessionId), Mockito.eq(reqId), Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(IdentifyApi.IdentifyRequestBody.class));
    }

    @Test
    public void updateNameRequest() {
        // ARRANGE
        String email = "test@getambasasdor.com";
        String firstName = "firstName";
        String lastName = "lastName";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.updateNameRequest(email, firstName, lastName, requestCompletion);
        String reqId = "" + PusherChannel.getRequestId();

        // ASSERT
        Assert.assertTrue((System.currentTimeMillis() - PusherChannel.getRequestId()) < 1000 * 60 * 60 * 24);
        Mockito.verify(identifyApi).updateNameRequest(Mockito.eq(sessionId), Mockito.eq(reqId), Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(IdentifyApi.UpdateNameRequestBody.class), Mockito.eq(requestCompletion));
    }

    @Test
    public void createPusherChannelTest() {
        // ARRANGE
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.createPusherChannel(requestCompletion);

        // ASSERT
        Mockito.verify(identifyApi).createPusherChannel(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.eq(requestCompletion));
    }

    @Test
    public void externalPusherRequestTest() {
        // ARRANGE
        String url = "http://getambassador.com";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.externalPusherRequest(url, requestCompletion);

        // ASSERT
        Mockito.verify(identifyApi).externalPusherRequest(Mockito.eq(url), Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.eq(requestCompletion));
    }

    @Test
    public void postToTwitterTest() {
        // ARRANGE
        String tweetString = "tweetString";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);
        TwitterCore twitterCore = Mockito.mock(TwitterCore.class);
        Mockito.when(TwitterCore.getInstance()).thenReturn(twitterCore);
        Mockito.when(twitterCore.getSessionManager()).thenReturn(sessionManager);
        TwitterSession twitterSession = Mockito.mock(TwitterSession.class);
        Mockito.when(sessionManager.getActiveSession()).thenReturn(twitterSession);
        TwitterApiClient twitterApiClient = Mockito.mock(TwitterApiClient.class);
        Mockito.when(twitterCore.getApiClient(twitterSession)).thenReturn(twitterApiClient);
        StatusesService statusesService = Mockito.mock(StatusesService.class);
        Mockito.when(twitterApiClient.getStatusesService()).thenReturn(statusesService);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[8];
                Result result = Mockito.mock(Result.class);
                callback.success(result);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[8];
                TwitterException out = Mockito.mock(TwitterException.class);
                Mockito.when(out.toString()).thenReturn("sad no authentication sadad");
                callback.failure(out);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[8];
                TwitterException out = Mockito.mock(TwitterException.class);
                Mockito.when(out.toString()).thenReturn("asffa");
                callback.failure(out);
                return null;
            }
        }).when(statusesService).update(Mockito.anyString(), Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(Callback.class));

        // ACT & ASSERT
        requestManager.postToTwitter(tweetString, requestCompletion);
        Mockito.verify(requestCompletion).onSuccess("Successfully posted to Twitter");

        // ACT & ASSERT
        requestManager.postToTwitter(tweetString, requestCompletion);
        Mockito.verify(requestCompletion).onFailure("auth");

        requestManager.postToTwitter(tweetString, requestCompletion);
        Mockito.verify(requestCompletion).onFailure("Failure Postring to Twitter");
    }

    @Test
    public void linkedInLoginRequestTest() {
        // ARRANGE
        String code = "12345";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        String urlParams = "params";
        Mockito.when(requestManager.createLinkedInLoginBody(code)).thenReturn(urlParams);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.LinkedInAuthorizedListener listener = (RequestManager.LinkedInAuthorizedListener) invocation.getArguments()[2];
                listener.linkedInAuthorized("accessToken");
                return null;
            }
        }).when(linkedInApi).login(Mockito.eq(urlParams), Mockito.eq(requestCompletion), Mockito.any(RequestManager.LinkedInAuthorizedListener.class));

        // ACT
        requestManager.linkedInLoginRequest(code, requestCompletion);

        // ASSERT
        Mockito.verify(linkedInApi).login(Mockito.eq(urlParams), Mockito.eq(requestCompletion), Mockito.any(RequestManager.LinkedInAuthorizedListener.class));
        Mockito.verify(ambassadorConfig).setLinkedInToken(Mockito.eq("accessToken"));
    }

    @Test
    public void postToLinkedInTest() {
        // ARRANGE
        LinkedInApi.LinkedInPostRequest linkedInPostRequest = Mockito.mock(LinkedInApi.LinkedInPostRequest.class);
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.postToLinkedIn(linkedInPostRequest, requestCompletion);

        // ASSERT
        Mockito.verify(linkedInApi).post(Mockito.eq(linkedInToken), Mockito.eq(linkedInPostRequest), Mockito.eq(requestCompletion));
    }

    @Test
    public void getProfileLinkedInTest() {
        // ARRANGE
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.getProfileLinkedIn(requestCompletion);

        // ASSERT
        Mockito.verify(linkedInApi).getProfile(Mockito.eq(linkedInToken), Mockito.eq(requestCompletion));
    }

}
