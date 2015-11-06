package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

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
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.HashMap;

import javax.inject.Singleton;

import dagger.Component;
import javassist.bytecode.analysis.Util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by dylan on 11/6/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AmbassadorSingleton.class, PusherSDK.class, PusherChannel.class, LocalBroadcastManager.class, Context.class, AmbassadorConfig.class, Utilities.class})
public class PusherSDKTest {

    PusherSDK pusherSDK;
    RequestManager mockRequestManager;
    AmbassadorConfig mockAmbassadorConfig;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(com.ambassador.ambassadorsdk.PusherSDKTest pusherSDKTest);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerPusherSDKTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);

        PowerMockito.mockStatic(AmbassadorSingleton.class);
        AmbassadorApplicationComponent application = mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(AmbassadorSingleton.getComponent()).thenReturn(application);
        doNothing().when(application).inject(any(PusherSDK.class));
        pusherSDK = Mockito.spy(PusherSDK.class);

        PowerMockito.spy(Utilities.class);

        mockRequestManager = mock(RequestManager.class);
        mockAmbassadorConfig = mock(AmbassadorConfig.class);
        pusherSDK.requestManager = mockRequestManager;
        pusherSDK.ambassadorConfig = mockAmbassadorConfig;
    }

    @Test
    public void createPusherTest() {
        // ARRANGE
        doNothing().when(pusherSDK).setupPusher(any(), any(PusherSDK.PusherSubscribeCallback.class));
        PusherSDK.PusherSubscribeCallback mockPusherSubscribeCallback = mock(PusherSDK.PusherSubscribeCallback.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocationOnMock.getArguments()[0];
                completion.onSuccess("fakeResponse");
                return null;
            }
        }).when(mockRequestManager).createPusherChannel(any(RequestManager.RequestCompletion.class));

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
        verify(pusherSDK, times(3)).setupPusher("fakeResponse", mockPusherSubscribeCallback);

        // ACT
        /** fail both conditions */
        Mockito.when(PusherChannel.getSessionId()).thenReturn("xxx");
        Mockito.when(PusherChannel.isExpired()).thenReturn(false);
        pusherSDK.createPusher(mockPusherSubscribeCallback);

        // ASSERT
        /** make sure we still only have 3 calls */
        verify(pusherSDK, times(3)).setupPusher("fakeResponse", mockPusherSubscribeCallback);
    }

    @Test
    public void setupPusherTest() {
        // ARRANGE
        String successResponse = "{\"client_session_uid\":\"test1\", \"channel_name\":\"test2\", \"expires_at\":\"2015-11-13T19:22:13.701\"}";
        PusherSDK.PusherSubscribeCallback mockPusherSubscribeCallback = mock(PusherSDK.PusherSubscribeCallback.class);
        PowerMockito.mockStatic(PusherChannel.class);
        doNothing().when(pusherSDK).subscribePusher(any(PusherSDK.PusherSubscribeCallback.class));
        doNothing().when(pusherSDK).createPusher(any(PusherSDK.PusherSubscribeCallback.class));

        // ACT
        Mockito.when(PusherChannel.isExpired()).thenReturn(true);
        pusherSDK.setupPusher(successResponse, mockPusherSubscribeCallback);
        Mockito.when(PusherChannel.isExpired()).thenReturn(false);
        pusherSDK.setupPusher(successResponse, mockPusherSubscribeCallback);

        // ASSERT
        verify(pusherSDK, times(1)).createPusher(any(PusherSDK.PusherSubscribeCallback.class));
        verify(pusherSDK, times(1)).subscribePusher(any(PusherSDK.PusherSubscribeCallback.class));
    }

    @Test
    public void subscribePusherTest() throws Exception {
        // ARRANGE
        PusherSDK.PusherSubscribeCallback mockPusherSubscribeCallback = mock(PusherSDK.PusherSubscribeCallback.class);

        PowerMockito.mockStatic(AmbassadorConfig.class);
        Mockito.when(AmbassadorConfig.pusherCallbackURL()).thenReturn("fakeResponse");

        HttpAuthorizer mockHttpAuthorizer = mock(HttpAuthorizer.class);
        PowerMockito.whenNew(HttpAuthorizer.class).withAnyArguments().thenReturn(mockHttpAuthorizer);

        HashMap mockHeaders = mock(HashMap.class);
        when(mockHeaders.put(any(), any())).thenReturn("fake");
        doNothing().when(mockHttpAuthorizer).setHeaders(any(HashMap.class));

        HashMap mockQueryParams = mock(HashMap.class);
        when(mockQueryParams.put(any(), any())).thenReturn("fake");
        doNothing().when(mockHttpAuthorizer).setQueryStringParameters(any(HashMap.class));

        PowerMockito.whenNew(HashMap.class).withNoArguments().thenReturn(mockHeaders);

        PusherOptions mockPusherOptions = mock(PusherOptions.class);
        when(mockPusherOptions.setAuthorizer(any(Authorizer.class))).thenReturn(null);
        when(mockPusherOptions.setEncrypted(anyBoolean())).thenReturn(null);

        PowerMockito.whenNew(PusherOptions.class).withAnyArguments().thenReturn(mockPusherOptions);

        Pusher mockPusher = mock(Pusher.class);
        Connection mockConnection = mock(Connection.class);
        when(mockPusher.getConnection()).thenReturn(mockConnection);
        when(mockConnection.getState()).thenReturn(null);

        PowerMockito.whenNew(Pusher.class).withAnyArguments().thenReturn(mockPusher);

        final ConnectionStateChange mockConnectionStateChange = mock(ConnectionStateChange.class);

        PowerMockito.mockStatic(PusherChannel.class);
        PowerMockito.mockStatic(Utilities.class);

        doAnswer(new Answer() {
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
        }).when(mockPusher).connect(any(ConnectionEventListener.class), any(ConnectionState.class));

        PrivateChannelEventListener mockPrivateChannelEventListener = mock(PrivateChannelEventListener.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PrivateChannelEventListener privateChannelEventListener = (PrivateChannelEventListener) invocation.getArguments()[1];
//                privateChannelEventListener.
                return null;
            }
        }).when(mockPusher).subscribePrivate(anyString(), any(PrivateChannelEventListener.class), anyString());

        // ACT
        pusherSDK.subscribePusher(mockPusherSubscribeCallback);
        pusherSDK.subscribePusher(mockPusherSubscribeCallback);

        // ASSERT
        verify(mockPusher, times(1)).getConnection();
        verify(mockConnection, times(1)).getState();
    }

    @Test
    public void setPusherInfoTest() throws Exception {
        // ARRANGE
        String jsonObject = "fakeJson";
        JSONObject pusherSave = mock(JSONObject.class);
        JSONObject pusherObject = mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withNoArguments().thenReturn(pusherSave);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(pusherObject);
        when(pusherObject.getString(anyString())).thenReturn("fakeString");
        when(pusherObject.getJSONArray(anyString())).thenReturn(new JSONArray());
        when(pusherSave.put(anyString(), anyString())).thenReturn(null);
        when(pusherSave.put(anyString(), any(JSONArray.class))).thenReturn(null);
        doNothing().when(mockAmbassadorConfig).setPusherInfo(anyString());

        PowerMockito.mockStatic(LocalBroadcastManager.class);
        PowerMockito.mockStatic(AmbassadorSingleton.class);
        Context mockContext = mock(Context.class);
        Mockito.when(AmbassadorSingleton.get()).thenReturn(mockContext);
        LocalBroadcastManager mockLocalBroadcastManager = mock(LocalBroadcastManager.class);
        Mockito.when(LocalBroadcastManager.getInstance(mockContext)).thenReturn(mockLocalBroadcastManager);
        when(mockLocalBroadcastManager.sendBroadcast(any(Intent.class))).thenReturn(true);

        // ACT
        pusherSDK.setPusherInfo(jsonObject);

        // ASSERT
        verify(mockAmbassadorConfig).setPusherInfo(anyString());
        verify(mockLocalBroadcastManager).sendBroadcast(any(Intent.class));
    }

}
