package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ambassador.ambassadorsdk.TestUtils;
import com.ambassador.ambassadorsdk.internal.injection.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.Connection;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        PusherSDK.class,
        PusherChannel.class,
        LocalBroadcastManager.class,
        Context.class,
        AmbassadorConfig.class,
        Utilities.class,
        RequestManager.class
})
public class PusherSDKTest {

    private PusherSDK pusherSDK;
    private RequestManager mockRequestManager;
    private AmbassadorConfig mockAmbassadorConfig;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(AmbassadorSingleton.class);
        TestUtils.mockStrings();

        AmbassadorApplicationComponent application = Mockito.mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceComponent()).thenReturn(application);
        Mockito.doNothing().when(application).inject(Mockito.any(PusherSDK.class));
        pusherSDK = Mockito.spy(PusherSDK.class);

        PowerMockito.spy(Utilities.class);

        mockRequestManager = PowerMockito.mock(RequestManager.class);
        mockAmbassadorConfig = Mockito.mock(AmbassadorConfig.class);
        pusherSDK.requestManager = mockRequestManager;
        pusherSDK.ambassadorConfig = mockAmbassadorConfig;
    }

    @Test
    public void createPusherSuccessTest() {
        // ARRANGE
        Mockito.doNothing().when(pusherSDK).setupPusher(Mockito.any(), Mockito.any(PusherSDK.PusherSubscribeCallback.class));
        PusherSDK.PusherSubscribeCallback mockPusherSubscribeCallback = Mockito.mock(PusherSDK.PusherSubscribeCallback.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocationOnMock.getArguments()[0];
                completion.onSuccess("fakeResponse");
                return null;
            }
        }).when(mockRequestManager).createPusherChannel(Mockito.any(RequestManager.RequestCompletion.class));

        PowerMockito.mockStatic(PusherChannel.class);

        // ACT
        /** pass both conditions */
        Mockito.when(PusherChannel.getSessionId()).thenReturn(null);
        Mockito.when(PusherChannel.isExpired()).thenReturn(true);
        pusherSDK.createPusher(mockPusherSubscribeCallback);

        /** pass first condition only */
        Mockito.when(PusherChannel.getSessionId()).thenReturn(null);
        Mockito.when(PusherChannel.isExpired()).thenReturn(false);
        pusherSDK.createPusher(mockPusherSubscribeCallback);

        /** pass second condition only */
        Mockito.when(PusherChannel.getSessionId()).thenReturn("xxx");
        Mockito.when(PusherChannel.isExpired()).thenReturn(true);
        pusherSDK.createPusher(mockPusherSubscribeCallback);

        // ASSERT
        /** make sure we got 3 calls */
        Mockito.verify(pusherSDK, Mockito.times(3)).setupPusher("fakeResponse", mockPusherSubscribeCallback);

        // ACT
        /** fail both conditions */
        Mockito.when(PusherChannel.getSessionId()).thenReturn("xxx");
        Mockito.when(PusherChannel.isExpired()).thenReturn(false);
        pusherSDK.createPusher(mockPusherSubscribeCallback);

        // ASSERT
        /** make sure we still only have 3 calls */
        Mockito.verify(pusherSDK, Mockito.times(3)).setupPusher("fakeResponse", mockPusherSubscribeCallback);
    }

    public void setupPusherTest() {

    }

    @Test
    public void subscribePusherTest() throws Exception {
        // ARRANGE
        PusherSDK.PusherSubscribeCallback mockPusherSubscribeCallback = Mockito.mock(PusherSDK.PusherSubscribeCallback.class);

        PowerMockito.mockStatic(AmbassadorConfig.class);
        Mockito.when(AmbassadorConfig.pusherCallbackURL()).thenReturn("fakeResponse");

        HttpAuthorizer mockHttpAuthorizer = Mockito.mock(HttpAuthorizer.class);
        PowerMockito.whenNew(HttpAuthorizer.class).withAnyArguments().thenReturn(mockHttpAuthorizer);

        HashMap mockHeaders = Mockito.mock(HashMap.class);
        Mockito.when(mockHeaders.put(Mockito.any(), Mockito.any())).thenReturn("fake");
        Mockito.doNothing().when(mockHttpAuthorizer).setHeaders(Mockito.any(HashMap.class));

        HashMap mockQueryParams = Mockito.mock(HashMap.class);
        Mockito.when(mockQueryParams.put(Mockito.any(), Mockito.any())).thenReturn("fake");
        Mockito.doNothing().when(mockHttpAuthorizer).setQueryStringParameters(Mockito.any(HashMap.class));

        PowerMockito.whenNew(HashMap.class).withNoArguments().thenReturn(mockHeaders);

        PusherOptions mockPusherOptions = Mockito.mock(PusherOptions.class);
        Mockito.when(mockPusherOptions.setAuthorizer(Mockito.any(Authorizer.class))).thenReturn(null);
        Mockito.when(mockPusherOptions.setEncrypted(Mockito.anyBoolean())).thenReturn(null);

        PowerMockito.whenNew(PusherOptions.class).withAnyArguments().thenReturn(mockPusherOptions);

        Pusher mockPusher = Mockito.mock(Pusher.class);
        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockPusher.getConnection()).thenReturn(mockConnection);
        Mockito.when(mockConnection.getState()).thenReturn(null);

        PowerMockito.whenNew(Pusher.class).withAnyArguments().thenReturn(mockPusher);

        final ConnectionStateChange mockConnectionStateChange = Mockito.mock(ConnectionStateChange.class);

        PowerMockito.mockStatic(PusherChannel.class);
        PowerMockito.mockStatic(Utilities.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ConnectionEventListener connectionEventListener = (ConnectionEventListener) invocationOnMock.getArguments()[0];
                connectionEventListener.onConnectionStateChange(mockConnectionStateChange);
                return null;
            }
        })
        .doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ConnectionEventListener connectionEventListener = (ConnectionEventListener) invocationOnMock.getArguments()[0];
                connectionEventListener.onError("fakeString", "fakeString", new Exception());
                return null;
            }
        }).when(mockPusher).connect(Mockito.any(ConnectionEventListener.class), Mockito.any(ConnectionState.class));

        PrivateChannelEventListener mockPrivateChannelEventListener = Mockito.mock(PrivateChannelEventListener.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PrivateChannelEventListener privateChannelEventListener = (PrivateChannelEventListener) invocation.getArguments()[1];
                return null;
            }
        }).when(mockPusher).subscribePrivate(Mockito.anyString(), Mockito.any(PrivateChannelEventListener.class), Mockito.anyString());

        // ACT
        pusherSDK.subscribePusher(mockPusherSubscribeCallback);
        pusherSDK.subscribePusher(mockPusherSubscribeCallback);

        // ASSERT
        Mockito.verify(mockPusher, Mockito.times(1)).getConnection();
        Mockito.verify(mockConnection, Mockito.times(1)).getState();
    }

    @Test
    public void setPusherInfoTest() throws Exception {
        // ARRANGE
        String jsonObject = "fakeJson";
        JSONObject pusherSave = Mockito.mock(JSONObject.class);
        JSONObject pusherObject = Mockito.mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withNoArguments().thenReturn(pusherSave);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(pusherObject);
        Mockito.when(pusherObject.getString(Mockito.anyString())).thenReturn("fakeString");
        Mockito.when(pusherObject.getJSONArray(Mockito.anyString())).thenReturn(new JSONArray());
        Mockito.when(pusherSave.put(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(pusherSave.put(Mockito.anyString(), Mockito.any(JSONArray.class))).thenReturn(null);
        Mockito.doNothing().when(mockAmbassadorConfig).setPusherInfo(Mockito.anyString());

        PowerMockito.mockStatic(LocalBroadcastManager.class);
        PowerMockito.mockStatic(AmbassadorSingleton.class);
        Context mockContext = Mockito.mock(Context.class);
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(mockContext);
        LocalBroadcastManager mockLocalBroadcastManager = Mockito.mock(LocalBroadcastManager.class);
        Mockito.when(LocalBroadcastManager.getInstance(mockContext)).thenReturn(mockLocalBroadcastManager);
        Mockito.when(mockLocalBroadcastManager.sendBroadcast(Mockito.any(Intent.class))).thenReturn(true);

        // ACT
        pusherSDK.setPusherInfo(jsonObject);

        // ASSERT
        Mockito.verify(mockAmbassadorConfig).setPusherInfo(Mockito.anyString());
        Mockito.verify(mockLocalBroadcastManager).sendBroadcast(Mockito.any(Intent.class));
    }

}
