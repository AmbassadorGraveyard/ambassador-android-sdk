package com.ambassador.ambassadorsdk.internal.api.linkedin;

import android.util.Log;

import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInApi;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInAuthClient;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.mime.TypedInput;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ServiceGenerator.class,
        LinkedInApi.class,
        Log.class
})
public class LinkedInApiTest {

    LinkedInApi linkedInApi;
    LinkedInClient linkedInClient;
    LinkedInAuthClient linkedInAuthClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ServiceGenerator.class,
                Log.class
        );

        LinkedInApi lia = new LinkedInApi(false);
        linkedInApi = Mockito.spy(lia);

        linkedInClient = Mockito.mock(LinkedInClient.class);
        linkedInApi.setLinkedInClient(linkedInClient);

        linkedInAuthClient = Mockito.mock(LinkedInAuthClient.class);
        linkedInApi.setLinkedInAuthClient(linkedInAuthClient);
    }

    @Test
    public void initTest() {
        // ARRANGE
        Mockito.when(ServiceGenerator.createService(LinkedInClient.class)).thenReturn(linkedInClient);
        Mockito.when(ServiceGenerator.createService(LinkedInAuthClient.class)).thenReturn(linkedInAuthClient);

        // ACT
        linkedInApi.init();

        // ASSERT
        Mockito.verify(linkedInApi, Mockito.times(2)).setLinkedInClient(linkedInClient);
        Mockito.verify(linkedInApi, Mockito.times(2)).setLinkedInAuthClient(linkedInAuthClient);
    }

    @Test
    public void loginTest() throws Exception {
        // ARRANGE
        String urlParams = "urlParams";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);
        RequestManager.LinkedInAuthorizedListener linkedInAuthorizedListener = Mockito.mock(RequestManager.LinkedInAuthorizedListener.class);
        final String accessToken = "accessToken";

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<LinkedInApi.LinkedInLoginResponse> callback = (Callback<LinkedInApi.LinkedInLoginResponse>) invocation.getArguments()[1];
                LinkedInApi.LinkedInLoginResponse out = new LinkedInApi.LinkedInLoginResponse();
                out.access_token = accessToken;
                callback.success(out, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<LinkedInApi.LinkedInLoginResponse> callback = (Callback<LinkedInApi.LinkedInLoginResponse>) invocation.getArguments()[1];
                LinkedInApi.LinkedInLoginResponse out = new LinkedInApi.LinkedInLoginResponse();
                out.access_token = null;
                callback.success(out, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<LinkedInApi.LinkedInLoginResponse> callback = (Callback<LinkedInApi.LinkedInLoginResponse>) invocation.getArguments()[1];
                callback.failure(Mockito.mock(RetrofitError.class));
                return null;
            }
        }).when(linkedInAuthClient).login(Mockito.any(TypedInput.class), Mockito.any(Callback.class));

        // ACT
        linkedInApi.login(urlParams, requestCompletion, linkedInAuthorizedListener);
        linkedInApi.login(urlParams, requestCompletion, linkedInAuthorizedListener);
        linkedInApi.login(urlParams, requestCompletion, linkedInAuthorizedListener);


        // ASSERT
        Mockito.verify(requestCompletion).onSuccess(accessToken);
        Mockito.verify(linkedInAuthorizedListener).linkedInAuthorized(accessToken);
        Mockito.verify(requestCompletion, Mockito.times(2)).onFailure(Mockito.eq("failure"));
    }

    @Test
    public void postTest() {
        // ARRANGE
        String token = "token";
        LinkedInApi.LinkedInPostRequest requestBody = Mockito.mock(LinkedInApi.LinkedInPostRequest.class);
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<Object> callback = (Callback<Object>) invocation.getArguments()[2];
                callback.success(null, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<Object> callback = (Callback<Object>) invocation.getArguments()[2];
                callback.failure(null);
                return null;
            }
        }).when(linkedInClient).post(Mockito.eq("Bearer " + token), Mockito.eq(requestBody), Mockito.any(Callback.class));

        // ACT
        linkedInApi.post(token, requestBody, requestCompletion);
        linkedInApi.post(token, requestBody, requestCompletion);

        // ASSERT
        Mockito.verify(requestCompletion).onFailure(Mockito.eq("failure"));
        Mockito.verify(requestCompletion).onSuccess(Mockito.eq("success"));
    }

    @Test
    public void getProfileTest() {
        // ARRANGE
        String token = "token";
        RequestManager.RequestCompletion requestCompletion = Mockito.mock(RequestManager.RequestCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<LinkedInApi.LinkedInProfileResponse> callback = (Callback<LinkedInApi.LinkedInProfileResponse>) invocation.getArguments()[1];
                LinkedInApi.LinkedInProfileResponse out = new LinkedInApi.LinkedInProfileResponse();
                out.firstName = "firstName";
                out.lastName = "lastName";
                callback.success(out, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<LinkedInApi.LinkedInProfileResponse> callback = (Callback<LinkedInApi.LinkedInProfileResponse>) invocation.getArguments()[1];
                LinkedInApi.LinkedInProfileResponse out = new LinkedInApi.LinkedInProfileResponse();
                out.firstName = "firstName";
                out.lastName = null;
                callback.success(out, null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<LinkedInApi.LinkedInProfileResponse> callback = (Callback<LinkedInApi.LinkedInProfileResponse>) invocation.getArguments()[1];
                callback.failure(null);
                return null;
            }
        }).when(linkedInClient).getProfile(Mockito.eq("Bearer " + token), Mockito.any(Callback.class));

        // ACT
        linkedInApi.getProfile(token, requestCompletion);
        linkedInApi.getProfile(token, requestCompletion);
        linkedInApi.getProfile(token, requestCompletion);

        // ASSERT
        Mockito.verify(requestCompletion, Mockito.times(1)).onSuccess(Mockito.eq("success"));
        Mockito.verify(requestCompletion, Mockito.times(2)).onFailure(Mockito.eq("failure"));
    }

}
