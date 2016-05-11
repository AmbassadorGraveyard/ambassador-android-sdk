package com.ambassador.ambassadorsdk.internal.api;

import android.util.Log;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.TestUtils;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.envoy.EnvoyApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.identify.AmbassadorIdentification;
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
        EnvoyApi.class,
        PusherManager.Channel.class,
        PusherManager.class,
        PusherListenerAdapter.class
})
public class RequestManagerTest {

    private RequestManager requestManager;

    private Auth auth;
    private User user;
    private Campaign campaign;

    private BulkShareApi bulkShareApi;
    private ConversionsApi conversionsApi;
    private IdentifyApi identifyApi;
    private EnvoyApi envoyApi;

    private BulkShareHelper bulkShareHelper;

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
                PusherManager.Channel.class
        );

        TestUtils.mockStrings();

        PowerMockito.doNothing().when(AmbSingleton.class, "inject", Mockito.any(RequestManager.class));

        bulkShareApi = PowerMockito.mock(BulkShareApi.class);
        conversionsApi = PowerMockito.mock(ConversionsApi.class);
        identifyApi = PowerMockito.mock(IdentifyApi.class);
        envoyApi = PowerMockito.mock(EnvoyApi.class);

        RequestManager rm = new RequestManager(false);
        requestManager = PowerMockito.spy(rm);

        bulkShareHelper = Mockito.spy(BulkShareHelper.class);
        requestManager.bulkShareHelper = bulkShareHelper;

        auth = Mockito.mock(Auth.class);
        user = Mockito.mock(User.class);
        campaign = Mockito.mock(Campaign.class);

        Mockito.when(auth.getUniversalId()).thenReturn(universalId);
        Mockito.when(auth.getUniversalToken()).thenReturn(universalToken);
        Mockito.when(user.getFirstName()).thenReturn(userFirstName);
        Mockito.when(user.getLastName()).thenReturn(userLastName);
        Mockito.when(user.getAugurData()).thenReturn(identifyObject);
        Mockito.when(user.getAmbassadorIdentification()).thenReturn(Mockito.mock(AmbassadorIdentification.class));
        Mockito.when(campaign.getId()).thenReturn(campaignId);
        Mockito.when(user.getUserId()).thenReturn(userEmail);
        Mockito.when(user.getLinkedInAccessToken()).thenReturn(linkedInToken);

        requestManager.auth = auth;
        requestManager.user = user;
        requestManager.campaign = campaign;

        requestManager.bulkShareApi = bulkShareApi;
        requestManager.conversionsApi = conversionsApi;
        requestManager.identifyApi = identifyApi;
        requestManager.envoyApi = envoyApi;

        PowerMockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                try {
                    PusherManager pusherManager = (PusherManager) invocation.getArguments()[0];
                    pusherManager.auth = auth;
                } catch (Exception e) {
                    
                }
                return null;
            }
        }).when(AmbSingleton.class, "inject", Mockito.any(PusherManager.class));
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
        PusherManager pusherManager = Mockito.spy(PusherManager.class);
        PusherManager.Channel channel = Mockito.mock(PusherManager.Channel.class);
        Mockito.when(pusherManager.getChannel()).thenReturn(channel);
        pusherManager.channel = channel;
        channel.requestId = requestId;
        channel.sessionId = sessionId;

        requestManager.identifyRequest(pusherManager, new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {}
            @Override
            public void onFailure(Object failureResponse) {}
        });
        String reqId = "" + pusherManager.channel.requestId;

        // ASSERT
        Mockito.verify(pusherManager).newRequest();
        Mockito.verify(identifyApi).identifyRequest(Mockito.eq(sessionId), Mockito.eq(reqId), Mockito.eq(universalId), Mockito.eq(universalToken), Mockito.any(IdentifyApi.IdentifyRequestBody.class), Mockito.any(RequestManager.RequestCompletion.class));
    }

    @Test
    public void updateNameRequest() {
        // ARRANGE
        PusherManager pusherManager = Mockito.spy(PusherManager.class);
        PusherManager.Channel channel = Mockito.mock(PusherManager.Channel.class);
        pusherManager.channel = channel;
        channel.requestId = requestId;
        channel.sessionId = sessionId;
        String email = "test@getambasasdor.com";
        String firstName = "firstName";
        String lastName = "lastName";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        // ACT
        requestManager.updateNameRequest(pusherManager, email, firstName, lastName, requestCompletion);
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
    public void postToFacebookTest() throws Exception {
        // ARRANGE
        String provider = "facebook";
        String facebookString = "facebookString";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);
        Mockito.doNothing().when(envoyApi).share(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(RequestManager.RequestCompletion.class));

        // ACT
        requestManager.shareWithEnvoy(provider, facebookString, requestCompletion);

        // ASSERT
        Mockito.verify(requestManager).shareWithEnvoy(Mockito.eq(provider), Mockito.eq(facebookString), Mockito.eq(requestCompletion));
    }

    @Test
    public void postToTwitterTest() throws Exception {
        // ARRANGE
        String provider = "twitter";
        String tweetString = "tweetString";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);
        Mockito.doNothing().when(envoyApi).share(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(RequestManager.RequestCompletion.class));

        // ACT
        requestManager.shareWithEnvoy(provider, tweetString, requestCompletion);

        // ASSERT
        Mockito.verify(requestManager).shareWithEnvoy(Mockito.eq(provider), Mockito.eq(tweetString), Mockito.eq(requestCompletion));
    }

    @Test
    public void postToLinkedInTest() {
        // ARRANGE
        String provider = "linkedin";
        String linkedInString = "linkedInString";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);
        Mockito.doNothing().when(envoyApi).share(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(RequestManager.RequestCompletion.class));

        // ACT
        requestManager.shareWithEnvoy(provider, linkedInString, requestCompletion);

        // ASSERT
        Mockito.verify(requestManager).shareWithEnvoy(Mockito.eq(provider), Mockito.eq(linkedInString), Mockito.eq(requestCompletion));
    }

}
