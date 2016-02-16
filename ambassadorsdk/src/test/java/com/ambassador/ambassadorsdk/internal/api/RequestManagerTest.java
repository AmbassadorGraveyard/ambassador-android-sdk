package com.ambassador.ambassadorsdk.internal.api;

import android.util.Log;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.TestUtils;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.PusherChannel;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.api.linkedin.LinkedInApi;
import com.ambassador.ambassadorsdk.internal.injection.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.internal.models.Contact;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.auth.AccessToken;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {
        AmbassadorSingleton.class,
        BulkShareHelper.class,
        ConversionUtility.class,
        PusherChannel.class,
        IdentifyApi.IdentifyRequestBody.class,
        Log.class,
        RequestManager.class,
        ConversionParameters.class,
        BulkShareApi.class,
        ConversionsApi.class,
        IdentifyApi.class,
        LinkedInApi.class,
        AmbassadorConfig.class,
        AsyncTwitterFactory.class,
        TwitterAdapter.class
})
public class RequestManagerTest {

    private RequestManager requestManager;
    private AmbassadorConfig ambassadorConfig;

    private BulkShareApi bulkShareApi;
    private ConversionsApi conversionsApi;
    private IdentifyApi identifyApi;
    private LinkedInApi linkedInApi;

    private String universalId = "***REMOVED***";
    private String universalToken = "SDKToken ***REMOVED***";
    private String userFullName = "Test User";
    private String identifyObject = "identifyObject";
    private String campaignId = "260";
    private String userEmail = "user@test.com";
    private String linkedInToken = "linkedInToken";

    private String sessionId = "sessionId";
    private long requestId = 123L;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class,
                BulkShareHelper.class,
                ConversionUtility.class,
                IdentifyApi.IdentifyRequestBody.class,
                Log.class,
                AsyncTwitterFactory.class,
                TwitterAdapter.class
        );

        TestUtils.mockStrings();

        AmbassadorApplicationComponent mockComponent = Mockito.mock(AmbassadorApplicationComponent.class);
        Mockito.when(AmbassadorSingleton.getInstanceComponent()).thenReturn(mockComponent);
        Mockito.doNothing().when(mockComponent).inject(Mockito.any(RequestManager.class));

        bulkShareApi = PowerMockito.mock(BulkShareApi.class);
        conversionsApi = PowerMockito.mock(ConversionsApi.class);
        identifyApi = PowerMockito.mock(IdentifyApi.class);
        linkedInApi = Mockito.mock(LinkedInApi.class);

        RequestManager rm = new RequestManager(false);
        requestManager = PowerMockito.spy(rm);

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
        List<Contact> contacts = new ArrayList<>();
        String messageToShare = "Check out Ambassador!";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.bulkShareSms(contacts, messageToShare, requestCompletion);

        // ASSERT
        Mockito.verify(bulkShareApi).bulkShareSms(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(BulkShareApi.BulkShareSmsBody.class), Mockito.eq(requestCompletion));
    }

    @Test
    public void bulkShareEmailTest() {
        // ARRANGE
        List<Contact> contacts = new ArrayList<>();
        String messageToShare = "Check out Ambassador!";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.bulkShareEmail(contacts, messageToShare, requestCompletion);

        // ASSERT
        Mockito.verify(bulkShareApi).bulkShareEmail(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(BulkShareApi.BulkShareEmailBody.class), Mockito.eq(requestCompletion));
    }

    @Test
    public void bulkShareTrackTest() {
        // ARRANGE
        List<Contact> contacts = new ArrayList<>();

        // ACT
        requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.SMS);
        requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.EMAIL);
        requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.FACEBOOK);
        requestManager.bulkShareTrack(contacts, BulkShareHelper.SocialServiceTrackType.SMS);
        requestManager.bulkShareTrack(contacts, BulkShareHelper.SocialServiceTrackType.EMAIL);
        requestManager.bulkShareTrack(contacts, BulkShareHelper.SocialServiceTrackType.FACEBOOK);

        // ASSERT
        Mockito.verify(bulkShareApi, Mockito.times(6)).bulkShareTrack(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(BulkShareApi.BulkShareTrackBody[].class));
    }

    @Test
    public void registerConversionRequestTest() {
        // ARRANGE
        ConversionParameters conversionParameters = PowerMockito.mock(ConversionParameters.class);
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
    public void postToTwitterTest() throws Exception {
        // ARRANGE
        String tweetString = "tweetString";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        AsyncTwitter twitter = Mockito.mock(AsyncTwitter.class);
        Mockito.doReturn(twitter).when(requestManager).getTwitter();
        Mockito.doNothing().when(twitter).setOAuthConsumer(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(twitter).setOAuthAccessToken(Mockito.any(AccessToken.class));

        Mockito.doReturn("token").when(ambassadorConfig).getTwitterAccessToken();
        Mockito.doReturn("secret").when(ambassadorConfig).getTwitterAccessTokenSecret();

        RequestManager.AmbTwitterAdapter currentAdapter = requestManager.twitterAdapter;
        final RequestManager.AmbTwitterAdapter twitterAdapter = Mockito.spy(currentAdapter);
        requestManager.twitterAdapter = twitterAdapter;

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                twitterAdapter.completion.onSuccess("success");
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                twitterAdapter.completion.onFailure("failure");
                return null;
            }
        }).when(twitter).updateStatus(Mockito.anyString());

        // ACT & ASSERT
        requestManager.postToTwitter(tweetString, requestCompletion);
        Mockito.verify(requestCompletion).onSuccess("success");


        requestManager.postToTwitter(tweetString, requestCompletion);
        Mockito.verify(requestCompletion).onFailure("failure");
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
