package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;

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

import twitter4j.AsyncTwitter;
import twitter4j.auth.AccessToken;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        RequestManager.class,
})
public class AuthTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class
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
    public void nullifyTwitterIfInvalidNullTokensTest() {
        // ARRANGE
        Auth auth = Mockito.spy(new Auth());
        Mockito.doReturn(null).when(auth).getTwitterToken();
        Mockito.doReturn(null).when(auth).getTwitterSecret();

        Auth.NullifyCompleteListener listener = Mockito.mock(Auth.NullifyCompleteListener.class);

        // ACT
        auth.nullifyTwitterIfInvalid(listener);

        // ASSERT
        Mockito.verify(auth).setTwitterToken(null);
        Mockito.verify(auth).setTwitterSecret(null);
        Mockito.verify(auth).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void nullifyTwitterIfInvalidNonNullTokensTest() {
        // ARRANGE
        Auth auth = Mockito.spy(new Auth());
        AsyncTwitter twitter = Mockito.mock(AsyncTwitter.class);
        Mockito.doReturn(twitter).when(auth).getTwitter();

        Mockito.doReturn("token").when(auth).getTwitterToken();
        Mockito.doReturn("secret").when(auth).getTwitterSecret();

        Mockito.doNothing().when(twitter).setOAuthConsumer(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(twitter).setOAuthAccessToken(Mockito.any(AccessToken.class));

        Auth.NullifyCompleteListener listener = Mockito.mock(Auth.NullifyCompleteListener.class);

        final Auth.AmbTwitterAdapter adapter = Mockito.spy(auth.ambTwitterAdapter);
        auth.ambTwitterAdapter = adapter;

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                adapter.gotUserTimeline(null);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                adapter.onException(null, null);
                return null;
            }
        }).when(twitter).getUserTimeline();

        // ACT
        auth.nullifyTwitterIfInvalid(listener);

        // ASSERT
//        Mockito.verify(ambassadorConfig).setTwitterAccessToken(null);
//        Mockito.verify(ambassadorConfig).setTwitterAccessTokenSecret(null);
//        Mockito.verify(ambassadorConfig).callNullifyComplete(Mockito.eq(listener));

        // ACT
        auth.nullifyTwitterIfInvalid(listener);

        // ASSERT
//        Mockito.verify(ambassadorConfig, Mockito.times(1)).setTwitterAccessToken(null);
//        Mockito.verify(ambassadorConfig, Mockito.times(1)).setTwitterAccessTokenSecret(null);
//        Mockito.verify(ambassadorConfig, Mockito.times(2)).callNullifyComplete(Mockito.eq(listener));
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
