package com.ambassador.ambassadorsdk.internal.api.identify;

import android.util.Log;

import com.ambassador.ambassadorsdk.TestUtils;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        ServiceGenerator.class,
        Log.class
})
public class IdentifyApiTest {

    IdentifyApi identifyApi;
    IdentifyClient identifyClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ServiceGenerator.class,
                Log.class
        );

        TestUtils.mockStrings();
        
        IdentifyApi ia = new IdentifyApi(false);
        identifyApi = Mockito.spy(ia);

        identifyClient = Mockito.mock(IdentifyClient.class);
        identifyApi.setIdentifyClient(identifyClient);
    }

    @Test
    public void initTest() {
        // ARRANGE
        Mockito.when(ServiceGenerator.createService(IdentifyClient.class)).thenReturn(identifyClient);

        // ACT
        identifyApi.init();

        // ASSERT
        Mockito.verify(identifyApi, Mockito.times(2)).setIdentifyClient(identifyClient);
    }

    @Test
    public void identifyRequestTest() {
        // ARRANGE
        String sessionId = "sessionId";
        String requestId = "requestId";
        String uid = "uid";
        String auth = "auth";
        IdentifyApi.IdentifyRequestBody requestBody = Mockito.mock(IdentifyApi.IdentifyRequestBody.class);

        // ACT
        identifyApi.identifyRequest(sessionId, requestId, uid, auth, requestBody);

        // ASSERT
        Mockito.verify(identifyClient).identifyRequest(Mockito.eq(sessionId), Mockito.eq(requestId), Mockito.eq(uid), Mockito.eq(auth), Mockito.eq(uid), Mockito.eq(requestBody), Mockito.any(Callback.class));
    }

    @Test
    public void updateNameRequestTest() {
        // ARRANGE
        String sessionId = "sessionId";
        String requestId = "requestId";
        String uid = "uid";
        String auth = "auth";
        IdentifyApi.UpdateNameRequestBody requestBody = Mockito.mock(IdentifyApi.UpdateNameRequestBody.class);
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<IdentifyApi.UpdateNameRequestResponse> callback = (Callback<IdentifyApi.UpdateNameRequestResponse>) invocation.getArguments()[6];
                callback.success(null, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<IdentifyApi.UpdateNameRequestResponse> callback = (Callback<IdentifyApi.UpdateNameRequestResponse>) invocation.getArguments()[6];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 201, "reason", new ArrayList<Header>(), new TypedString("string"));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<IdentifyApi.UpdateNameRequestResponse> callback = (Callback<IdentifyApi.UpdateNameRequestResponse>) invocation.getArguments()[6];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 401, "reason", new ArrayList<Header>(), new TypedString("string"));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).when(identifyClient).updateNameRequest(Mockito.eq(sessionId), Mockito.eq(requestId), Mockito.eq(uid), Mockito.eq(auth), Mockito.eq(uid), Mockito.eq(requestBody), Mockito.any(Callback.class));

        // ACT
        identifyApi.updateNameRequest(sessionId, requestId, uid, auth, requestBody, requestCompletion);
        identifyApi.updateNameRequest(sessionId, requestId, uid, auth, requestBody, requestCompletion);
        identifyApi.updateNameRequest(sessionId, requestId, uid, auth, requestBody, requestCompletion);

        // ASSERT
        Mockito.verify(requestCompletion, Mockito.times(2)).onSuccess(Mockito.anyString());
        Mockito.verify(requestCompletion, Mockito.times(1)).onFailure(Mockito.eq("failure"));
    }

    @Test
    public void createPusherChannelTest() {
        // ARRANGE
        String uid = "uid";
        String auth = "auth";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<IdentifyApi.CreatePusherChannelResponse> callback = (Callback<IdentifyApi.CreatePusherChannelResponse>) invocation.getArguments()[3];
                callback.success(null, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<IdentifyApi.CreatePusherChannelResponse> callback = (Callback<IdentifyApi.CreatePusherChannelResponse>) invocation.getArguments()[3];
                callback.failure(null);
                return null;
            }
        }).when(identifyClient).createPusherChannel(Mockito.eq(uid), Mockito.eq(auth), Mockito.any(Object.class), Mockito.any(Callback.class));

        // ACT
        identifyApi.createPusherChannel(uid, auth, requestCompletion);
        identifyApi.createPusherChannel(uid, auth, requestCompletion);

        // ASSERT
        Mockito.verify(requestCompletion, Mockito.times(1)).onSuccess(Mockito.eq(null));
        Mockito.verify(requestCompletion, Mockito.times(1)).onFailure(Mockito.eq("failure"));
    }

    @Test
    public void externalPusherRequestTest() {

    }

}
