package com.ambassador.ambassadorsdk.internal.api.conversions;

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
import retrofit.mime.TypedByteArray;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        ServiceGenerator.class,
        Log.class
})
public class ConversionsApiTest {

    ConversionsApi conversionsApi;
    ConversionsClient conversionsClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ServiceGenerator.class,
                Log.class
        );

        TestUtils.mockStrings();

        ConversionsApi ca = new ConversionsApi(false);
        conversionsApi = Mockito.spy(ca);

        conversionsClient = Mockito.mock(ConversionsClient.class);
        conversionsApi.setConversionsClient(conversionsClient);
    }

    @Test
    public void initTest() {
        // ARRANGE
        Mockito.when(ServiceGenerator.createService(ConversionsClient.class)).thenReturn(conversionsClient);

        // ACT
        conversionsApi.init();

        // ASSERT
        Mockito.verify(conversionsApi, Mockito.times(2)).setConversionsClient(conversionsClient);
    }

    @Test
    public void registerConversionRequestTest() {
        // ARRANGE
        String uid = "uid";
        String auth = "auth";
        ConversionsApi.RegisterConversionRequestBody requestBody = Mockito.mock(ConversionsApi.RegisterConversionRequestBody.class);
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[4];
                callback.success(null, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[4];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 201, "reason", new ArrayList<Header>(), new TypedByteArray("text", new byte[]{}));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[4];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 401, "reason", new ArrayList<Header>(), new TypedByteArray("text", new byte[]{}));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).when(conversionsClient).registerConversionRequest(Mockito.eq(uid), Mockito.eq(auth), Mockito.eq(uid), Mockito.eq(requestBody), Mockito.any(Callback.class));

        // ACT
        conversionsApi.registerConversionRequest(uid, auth, requestBody, requestCompletion);
        conversionsApi.registerConversionRequest(uid, auth, requestBody, requestCompletion);
        conversionsApi.registerConversionRequest(uid, auth, requestBody, requestCompletion);

        // ASSERT
        Mockito.verify(requestCompletion, Mockito.times(2)).onSuccess(Mockito.eq("success"));
        Mockito.verify(requestCompletion, Mockito.times(1)).onFailure(Mockito.eq("failure"));
    }

}
