package com.ambassador.ambassadorsdk.internal.api.bulkshare;

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
        ServiceGenerator.class
})
public class BulkShareApiTest {

    BulkShareApi bulkShareApi;
    BulkShareClient bulkShareClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ServiceGenerator.class
        );

        BulkShareApi bsa = new BulkShareApi(false);
        bulkShareApi = Mockito.spy(bsa);

        bulkShareClient = Mockito.mock(BulkShareClient.class);
        bulkShareApi.setBulkShareClient(bulkShareClient);
    }

    @Test
    public void initTest() {
        // ARRANGE
        Mockito.when(ServiceGenerator.createService(BulkShareClient.class)).thenReturn(bulkShareClient);

        // ACT
        bulkShareApi.init();

        // ASSERT
        Mockito.verify(bulkShareApi, Mockito.times(2)).setBulkShareClient(bulkShareClient);
    }

    @Test
    public void bulkShareSmsTest() {
        // ARRANGE
        String uid = "uid";
        String auth = "auth";
        BulkShareApi.BulkShareSmsBody requestBody = Mockito.mock(BulkShareApi.BulkShareSmsBody.class);
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[3];
                callback.success(null, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[3];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 201, "reason", new ArrayList<Header>(), new TypedString("body"));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[3];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 401, "reason", new ArrayList<Header>(), new TypedString("body"));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).when(bulkShareClient).bulkShareSms(Mockito.eq(uid), Mockito.eq(auth), Mockito.eq(requestBody), Mockito.any(Callback.class));

        // ACT
        bulkShareApi.bulkShareSms(uid, auth, requestBody, requestCompletion);
        bulkShareApi.bulkShareSms(uid, auth, requestBody, requestCompletion);
        bulkShareApi.bulkShareSms(uid, auth, requestBody, requestCompletion);

        // ASSERT
        Mockito.verify(requestCompletion, Mockito.times(2)).onSuccess("success");
        Mockito.verify(requestCompletion, Mockito.times(1)).onFailure("failure");
    }

    @Test
    public void bulkShareEmailTest() {
        // ARRANGE
        String uid = "uid";
        String auth = "auth";
        BulkShareApi.BulkShareEmailBody requestBody = Mockito.mock(BulkShareApi.BulkShareEmailBody.class);
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[3];
                callback.success(null, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[3];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 201, "reason", new ArrayList<Header>(), new TypedString("body"));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<String> callback = (Callback<String>) invocation.getArguments()[3];
                RetrofitError error = Mockito.mock(RetrofitError.class);
                Response resp = new Response("url", 401, "reason", new ArrayList<Header>(), new TypedString("body"));
                Mockito.when(error.getResponse()).thenReturn(resp);
                callback.failure(error);
                return null;
            }
        }).when(bulkShareClient).bulkShareEmail(Mockito.eq(uid), Mockito.eq(auth), Mockito.eq(requestBody), Mockito.any(Callback.class));

        // ACT
        bulkShareApi.bulkShareEmail(uid, auth, requestBody, requestCompletion);
        bulkShareApi.bulkShareEmail(uid, auth, requestBody, requestCompletion);
        bulkShareApi.bulkShareEmail(uid, auth, requestBody, requestCompletion);

        // ASSERT
        Mockito.verify(requestCompletion, Mockito.times(2)).onSuccess("success");
        Mockito.verify(requestCompletion, Mockito.times(1)).onFailure("failure");
    }

    @Test
    public void bulkShareTrackTest() {
        // ARRANGE
        String uid = "uid";
        String auth = "auth";
        BulkShareApi.BulkShareTrackBody[] requestBody = new BulkShareApi.BulkShareTrackBody[]{};

        // ACT
        bulkShareApi.bulkShareTrack(uid, auth, requestBody);

        // ASSERT
        Mockito.verify(bulkShareClient).bulkShareTrack(Mockito.eq(uid), Mockito.eq(auth), Mockito.eq(requestBody), Mockito.any(Callback.class));
    }

}
