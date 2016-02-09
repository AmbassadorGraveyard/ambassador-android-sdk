package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.AccountService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        RequestManager.class,
        TwitterCore.class
})
public class AuthTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class,
                TwitterCore.class
        );

        context = Mockito.mock(Context.class);
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);

        sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);

        editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.when(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor);
    }

    @Test
    public void saveWithNonNullContextDoesSaveSerialized() {
        // ARRANGE
        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.linkedInToken = "linkedInToken";
        auth.twitterToken = "twitterToken";
        auth.twitterSecret = "twitterSecret";

        // ACT
        auth.save();

        // ASSERT
        Mockito.verify(context).getSharedPreferences(Mockito.eq("auth"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(editor).putString(Mockito.eq("auth"), Mockito.eq("{\"universalId\":\"universalId\",\"universalToken\":\"universalToken\",\"linkedInToken\":\"linkedInToken\",\"twitterToken\":\"twitterToken\",\"twitterSecret\":\"twitterSecret\"}"));
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveWithNullContextDoesNotSave() {
        // ARRANGE
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(null);

        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.linkedInToken = "linkedInToken";
        auth.twitterToken = "twitterToken";
        auth.twitterSecret = "twitterSecret";

        // ACT
        auth.save();

        // ASSERT
        Mockito.verify(context, Mockito.never()).getSharedPreferences(Mockito.eq("auth"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(sharedPreferences, Mockito.never()).edit();
        Mockito.verify(editor, Mockito.never()).putString(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(editor, Mockito.never()).apply();
    }

    @Test
    public void clearDoesClearAllFields() {
        // ARRANGE
        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.linkedInToken = "linkedInToken";
        auth.twitterToken = "twitterToken";
        auth.twitterSecret = "twitterSecret";

        // ACT
        auth.clear();

        // ASSERT
        Assert.assertNull(auth.getUniversalId());
        Assert.assertNull(auth.getUniversalToken());
        Assert.assertNull(auth.getLinkedInToken());
        Assert.assertNull(auth.getTwitterToken());
        Assert.assertNull(auth.getTwitterSecret());
    }

    @Test
    public void settersDoSaveOnInvocatio() {
        // ARRANGE
        Auth auth = Mockito.spy(new Auth());
        Mockito.doNothing().when(auth).save();

        // ACT
        auth.setUniversalId("universalId");
        auth.setUniversalToken("universalToken");
        auth.setLinkedInToken("linkedInToken");
        auth.setTwitterToken("twitterToken");
        auth.setTwitterSecret("twitterSecret");

        // ASSERT
        Mockito.verify(auth, Mockito.times(5)).save();
    }

    @Test
    public void nullifyTwitterIfInvalidNullSessionManagerTest() {
        // ARRANGE
        Auth auth = Mockito.spy(Auth.class);
        TwitterCore twitterCore = Mockito.mock(TwitterCore.class);
        Mockito.when(TwitterCore.getInstance()).thenReturn(twitterCore);
        Mockito.when(twitterCore.getSessionManager()).thenReturn(null);
        Auth.NullifyCompleteListener listener = Mockito.mock(Auth.NullifyCompleteListener.class);

        // ACT
        auth.nullifyTwitterIfInvalid(listener);

        // ASSERT
        Mockito.verify(auth).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void nullifyTwitterInvalidTest() {
        // ARRANGE
        Auth.NullifyCompleteListener listener = Mockito.mock(Auth.NullifyCompleteListener.class);

        String token = "token";
        String secret = "secret";

        Auth auth = Mockito.spy(Auth.class);
        TwitterCore twitterCore = Mockito.mock(TwitterCore.class);
        SessionManager sessionManager = Mockito.mock(SessionManager.class);
        TwitterSession twitterSession = Mockito.mock(TwitterSession.class);
        TwitterAuthToken authToken = Mockito.spy(new TwitterAuthToken("token", "secret"));

        Mockito.when(TwitterCore.getInstance()).thenReturn(twitterCore);
        Mockito.when(twitterCore.getSessionManager()).thenReturn(sessionManager);
        Mockito.when(sessionManager.getActiveSession()).thenReturn(twitterSession);
        Mockito.when(twitterSession.getAuthToken()).thenReturn(authToken);

        Mockito.doNothing().when(sessionManager).clearActiveSession();

        Mockito.doNothing().when(auth).setTwitterToken(Mockito.anyString());
        Mockito.doNothing().when(auth).setTwitterSecret(Mockito.anyString());
        Mockito.when(auth.getTwitterToken()).thenReturn(token);
        Mockito.when(auth.getTwitterSecret()).thenReturn(secret);

        Mockito.doNothing().when(sessionManager).setActiveSession(Mockito.any(TwitterSession.class));

        TwitterApiClient apiClient = Mockito.mock(TwitterApiClient.class);
        AccountService accountService = Mockito.mock(AccountService.class);
        Mockito.when(twitterCore.getApiClient()).thenReturn(apiClient);
        Mockito.when(apiClient.getAccountService()).thenReturn(accountService);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<User> callback = (Callback<User>) invocation.getArguments()[2];
                callback.success(null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<User> callback = (Callback<User>) invocation.getArguments()[2];
                callback.failure(new TwitterException("ex"));
                return null;
            }
        }).when(accountService).verifyCredentials(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(Callback.class));

        // ACT
        auth.nullifyTwitterIfInvalid(listener);

        // ASSERT
        Mockito.verify(auth).setTwitterToken(Mockito.eq(token));
        Mockito.verify(auth).setTwitterSecret(Mockito.eq(secret));
        Mockito.verify(sessionManager).setActiveSession(Mockito.any(TwitterSession.class));
        Mockito.verify(auth).callNullifyComplete(Mockito.eq(listener));

        // ACT
        auth.nullifyTwitterIfInvalid(listener);

        // ASSERT
        Mockito.verify(auth, Mockito.times(2)).setTwitterToken(Mockito.eq(token));
        Mockito.verify(auth, Mockito.times(2)).setTwitterSecret(Mockito.eq(secret));
        Mockito.verify(sessionManager, Mockito.times(2)).setActiveSession(Mockito.any(TwitterSession.class));
        Mockito.verify(sessionManager).clearActiveSession();
        Mockito.verify(auth, Mockito.times(2)).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void nullifyLinkedInIfInvalidNullTest() {
        // ARRANGE
        Auth.NullifyCompleteListener listener = Mockito.mock(Auth.NullifyCompleteListener.class);
        Auth auth = Mockito.spy(Auth.class);
        Mockito.when(auth.getLinkedInToken()).thenReturn(null);

        // ACT
        auth.nullifyLinkedInIfInvalid(listener);

        // ASSERT
        Mockito.verify(auth).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void nullifyLinkedInIfInvalidTest() {
        // ARRANGE
        Auth.NullifyCompleteListener listener = Mockito.mock(Auth.NullifyCompleteListener.class);
        Auth auth = Mockito.spy(Auth.class);
        String token = "token";
        Mockito.when(auth.getLinkedInToken()).thenReturn(token);
        RequestManager requestManager = Mockito.mock(RequestManager.class);
        Mockito.doReturn(requestManager).when(auth).buildRequestManager();

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[0];
                completion.onSuccess(null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[0];
                completion.onFailure(null);
                return null;
            }
        }).when(requestManager).getProfileLinkedIn(Mockito.any(RequestManager.RequestCompletion.class));

        Mockito.doNothing().when(auth).save();

        // ACT
        auth.nullifyLinkedInIfInvalid(listener);
        auth.nullifyLinkedInIfInvalid(listener);

        // ASSERT
        Mockito.verify(auth).setLinkedInToken(null);
        Mockito.verify(auth, Mockito.times(2)).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void callNullifyCompleteNotNullTest() {
        // ARRANGE
        Auth auth = Mockito.spy(Auth.class);
        Auth.NullifyCompleteListener listener = Mockito.mock(Auth.NullifyCompleteListener.class);
        Mockito.doNothing().when(listener).nullifyComplete();

        // ACT
        auth.callNullifyComplete(listener);

        // ASSERT
        Mockito.verify(listener).nullifyComplete();
    }

    @Test
    public void callNullifyCompleteNullTest() {
        // ARRANGE
        Auth auth = Mockito.spy(Auth.class);
        Auth.NullifyCompleteListener listener = null;

        // ACT
        auth.callNullifyComplete(listener);

        // ASSERT
        // If nothing happens (no thrown exception), all is good
    }

}
