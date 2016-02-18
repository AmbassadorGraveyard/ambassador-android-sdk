package com.ambassador.ambassadorsdk.internal.api;

import android.util.Log;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.TestUtils;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.api.linkedin.LinkedInApi;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.google.gson.JsonObject;

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
        AmbSingleton.class,
        BulkShareHelper.class,
        ConversionUtility.class,
        IdentifyApi.IdentifyRequestBody.class,
        Log.class,
        RequestManager.class,
        ConversionParameters.class,
        BulkShareApi.class,
        ConversionsApi.class,
        IdentifyApi.class,
        LinkedInApi.class,
        AsyncTwitterFactory.class,
        TwitterAdapter.class,
        LinkedInApi.class,
        PusherManager.Channel.class,
        PusherManager.class,
        PusherListenerAdapter.class
})
public class RequestManagerTest {

    private RequestManager requestManager;

    private Auth auth;

    private BulkShareApi bulkShareApi;
    private ConversionsApi conversionsApi;
    private IdentifyApi identifyApi;
    private LinkedInApi linkedInApi;

    private PusherManager pusherManager;

    private String universalId = "***REMOVED***";
    private String universalToken = "SDKToken ***REMOVED***";
    private String userFirstName = "Test";
    private String userLastName = "User";
    private JsonObject identifyObject = new JsonObject();
    private String campaignId = "260";
    private String userEmail = "user@test.com";
    private String linkedInToken = "linkedInToken";

    private String sessionId = "sessionId";
    private long requestId = 123L;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                BulkShareHelper.class,
                ConversionUtility.class,
                IdentifyApi.IdentifyRequestBody.class,
                Log.class,
                AsyncTwitterFactory.class,
                TwitterAdapter.class,
                PusherManager.Channel.class
        );

        TestUtils.mockStrings();

        PowerMockito.doNothing().when(AmbSingleton.class, "inject", Mockito.any(RequestManager.class));

        bulkShareApi = PowerMockito.mock(BulkShareApi.class);
        conversionsApi = PowerMockito.mock(ConversionsApi.class);
        identifyApi = PowerMockito.mock(IdentifyApi.class);
        linkedInApi = Mockito.mock(LinkedInApi.class);

        RequestManager rm = new RequestManager(false);
        requestManager = PowerMockito.spy(rm);

        auth = Mockito.mock(Auth.class);
        User user = Mockito.mock(User.class);
        Campaign campaign = Mockito.mock(Campaign.class);

        Mockito.when(auth.getUniversalId()).thenReturn(universalId);
        Mockito.when(auth.getUniversalToken()).thenReturn(universalToken);
        Mockito.when(user.getFirstName()).thenReturn(userFirstName);
        Mockito.when(user.getLastName()).thenReturn(userLastName);
        Mockito.when(user.getAugurData()).thenReturn(identifyObject);
        Mockito.when(campaign.getId()).thenReturn(campaignId);
        Mockito.when(user.getEmail()).thenReturn(userEmail);
        Mockito.when(auth.getLinkedInToken()).thenReturn(linkedInToken);

        requestManager.auth = auth;
        requestManager.user = user;
        requestManager.campaign = campaign;

        requestManager.bulkShareApi = bulkShareApi;
        requestManager.conversionsApi = conversionsApi;
        requestManager.identifyApi = identifyApi;
        requestManager.linkedInApi = linkedInApi;

        PowerMockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PusherManager pusherManager = (PusherManager) invocation.getArguments()[0];
                pusherManager.auth = auth;
                return null;
            }
        }).when(AmbSingleton.class, "inject", Mockito.any(PusherManager.class));

        pusherManager = Mockito.spy(new PusherManager());
        PusherManager.Channel channel = Mockito.spy(new PusherManager.Channel());
        pusherManager.channel = channel;
        channel.requestId = requestId;
        channel.sessionId = sessionId;

        requestManager.pusherManager = pusherManager;
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
        BDDMockito.given(ConversionUtility.createConversionRequestBody(conversionParameters, identifyObject.toString())).willReturn(requestBody);

        // ACT
        requestManager.registerConversionRequest(conversionParameters, requestCompletion);

        // ASSERT
        Mockito.verify(conversionsApi).registerConversionRequest(Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.eq(requestBody), Mockito.eq(requestCompletion));
    }

    @Test
    public void identifyRequestTest() throws Exception {
        // ACT
        requestManager.identifyRequest();
        String reqId = "" + pusherManager.channel.requestId;

        // ASSERT
        Mockito.verify(pusherManager).newRequest();
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
        String reqId = "" + pusherManager.channel.requestId;

        // ASSERT
        Mockito.verify(pusherManager).newRequest();
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

        Mockito.doReturn("token").when(auth).getTwitterToken();
        Mockito.doReturn("secret").when(auth).getTwitterSecret();

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
        Mockito.doReturn(urlParams).when(requestManager).createLinkedInLoginBody(Mockito.eq(code));

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
        Mockito.verify(auth).setLinkedInToken(Mockito.eq("accessToken"));
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
