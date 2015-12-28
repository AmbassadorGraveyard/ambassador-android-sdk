package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.SharedPreferences;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.MalformedURLException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        TwitterCore.class,
        RequestManager.class
})
public class AmbassadorConfigTest {
    
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorConfig.class,
                AmbassadorSingleton.class,
                TwitterCore.class
        );

        Context context = mock(Context.class);

        sharedPrefs = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);

        when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        doNothing().when(editor).apply();
    }

    @Test
    public void staticUrlGetTests() {
        // ACT
        String pusherCallbackURL = AmbassadorConfig.pusherCallbackURL();

        // ASSERT
        assertTrue(isURLReturnGood(pusherCallbackURL));
    }

    /** helper for staticUrlGetTests() */
    private boolean matchesURL(String url) {
        try {
            URL tmp = new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /** helper for staticUrlGetTests() */
    private boolean isReleaseServer(String url) {
        try {
            URL tmp = new URL(url);
            return tmp.getHost().equals("api.getambassador.com");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /** helper for staticUrlGetTests() */
    private boolean isURLReturnGood(String url) {
        return (matchesURL(url) && isReleaseServer(url) == AmbassadorConfig.isReleaseBuild);
    }

    /**
     * Setter Tests
     */

    @Test
    public void setLinkedInTokenTest() throws Exception {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setLinkedInToken(testValue);

        // ASSERT
        verify(editor).putString("linkedInToken", testValue);
        verify(editor).apply();
    }

    @Test
    public void setTwitterAccessTokenTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setTwitterAccessToken(testValue);

        // ASSERT
        verify(editor).putString("twitterToken", testValue);
        verify(editor).apply();
    }

    @Test
    public void setTwitterAccessTokenSecretTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setTwitterAccessTokenSecret(testValue);

        // ASSERT
        verify(editor).putString("twitterTokenSecret", testValue);
        verify(editor).apply();
    }

    @Test
    public void setIdentifyObjectTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setIdentifyObject(testValue);

        // ASSERT
        verify(editor).putString("identifyObject", testValue);
        verify(editor).apply();
    }

    @Test
    public void setCampaignIDTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setCampaignID(testValue);

        // ASSERT
        verify(editor).putString("campaignID", testValue);
        verify(editor).apply();
    }

    @Test
    public void setPusherInfoTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setPusherInfo(testValue);

        // ASSERT
        verify(editor).putString("pusherObject", testValue);
        verify(editor).apply();
    }

    @Test
    public void setURLTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setURL(testValue);

        // ASSERT
        verify(editor).putString("url", testValue);
        verify(editor).apply();
    }

    @Test
    public void setUniversalTokenTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setUniversalToken(testValue);

        // ASSERT
        verify(editor).putString("universalToken", testValue);
        verify(editor).apply();
    }

    @Test
    public void setUniversalIDTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setUniversalID(testValue);

        // ASSERT
        verify(editor).putString("universalID", testValue);
        verify(editor).apply();
    }

    @Test
    public void setShortCodeTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setReferralShortCode(testValue);
        ambassadorConfig.setReferrerShortCode(testValue);

        // ASSERT
        verify(editor).putString("referralShortCode", testValue);
        verify(editor).putString("referrerShortCode", testValue);
        verify(editor, times(2)).apply();
    }

    @Test
    public void setUserFullNameTest() {
        // ARRANGE
        String testValue1 = "test1";
        String testValue2 = "test2";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setUserFullName(testValue1, testValue2);

        // ASSERT
        verify(editor).putString("fullName", testValue1 + " " + testValue2);
        verify(editor).apply();
    }

    @Test
    public void setEmailSubjectTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setEmailSubject(testValue);

        // ASSERT
        verify(editor).putString("subjectLine", testValue);
        verify(editor).apply();
    }

    @Test
    public void setUserEmailTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setUserEmail(testValue);

        // ASSERT
        verify(editor).putString("userEmail", testValue);
        verify(editor).apply();
    }

    @Test
    public void setRafParametersTest() throws Exception {
        // ARRANGE
        String testValue1 = "test1", testValue2 = "test2", testValue3 = "test3", testValue4 = "test4";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setRafParameters(testValue1, testValue2, testValue3, testValue4);

        // ASSERT
        assertEquals(testValue1, ambassadorConfig.getRafParameters().defaultShareMessage);
        assertEquals(testValue2, ambassadorConfig.getRafParameters().titleText);
        assertEquals(testValue3, ambassadorConfig.getRafParameters().descriptionText);
        assertEquals(testValue4, ambassadorConfig.getRafParameters().toolbarTitle);
    }

    /** End Setter Tests */

    /**
     * Getter Tests
     */

    @Test
    public void getLinkedInTokenTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getLinkedInToken();

        // ASSERT
        verify(sharedPrefs).getString("linkedInToken", null);
    }

    @Test
    public void getTwitterAccessTokenTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getTwitterAccessToken();

        // ASSERT
        verify(sharedPrefs).getString("twitterToken", null);
    }

    @Test
    public void getTwitterAccessTokenSecretTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getTwitterAccessTokenSecret();

        // ASSERT
        verify(sharedPrefs).getString("twitterTokenSecret", null);
    }

    @Test
    public void getIdentifyObjectTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getIdentifyObject();

        // ASSERT
        verify(sharedPrefs).getString("identifyObject", null);
    }

    @Test
    public void getCampaignIDTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getCampaignID();

        // ASSERT
        verify(sharedPrefs).getString("campaignID", null);
    }

    @Test
    public void getPusherInfoTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getPusherInfo();

        // ASSERT
        verify(sharedPrefs).getString("pusherObject", null);
    }

    @Test
    public void getURLTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getURL();

        // ASSERT
        verify(sharedPrefs).getString("url", null);
    }

    @Test
    public void getUniversalKeyTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUniversalKey();

        // ASSERT
        verify(sharedPrefs).getString("universalToken", null);
    }

    @Test
    public void getUniversalIDTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUniversalID();

        // ASSERT
        verify(sharedPrefs).getString("universalID", null);
    }

    @Test
    public void getReferrerShortCodeTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getReferrerShortCode();

        // ASSERT
        verify(sharedPrefs).getString("referrerShortCode", null);
    }

    @Test
    public void getReferralShortCodeTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getReferralShortCode();

        // ASSERT
        verify(sharedPrefs).getString("referralShortCode", null);
    }

    @Test
    public void getUserFullNameTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUserFullName();

        // ASSERT
        verify(sharedPrefs).getString("fullName", null);
    }

    @Test
    public void getEmailSubjectLineTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getEmailSubjectLine();

        // ASSERT
        verify(sharedPrefs).getString("subjectLine", null);
    }

    @Test
    public void getUserEmailTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUserEmail();

        // ASSERT
        verify(sharedPrefs).getString("userEmail", null);
    }

    @Test
    public void nullifyTwitterIfInvalidNullSessionManagerTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = Mockito.spy(AmbassadorConfig.class);
        TwitterCore twitterCore = Mockito.mock(TwitterCore.class);
        Mockito.when(TwitterCore.getInstance()).thenReturn(twitterCore);
        Mockito.when(twitterCore.getSessionManager()).thenReturn(null);
        AmbassadorConfig.NullifyCompleteListener listener = Mockito.mock(AmbassadorConfig.NullifyCompleteListener.class);

        // ACT
        ambassadorConfig.nullifyTwitterIfInvalid(listener);

        // ASSERT
        Mockito.verify(ambassadorConfig).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void nullifyTwitterInvalidTest() {
        // ARRANGE
        AmbassadorConfig.NullifyCompleteListener listener = Mockito.mock(AmbassadorConfig.NullifyCompleteListener.class);

        String token = "token";
        String secret = "secret";

        AmbassadorConfig ambassadorConfig = Mockito.spy(AmbassadorConfig.class);
        TwitterCore twitterCore = Mockito.mock(TwitterCore.class);
        SessionManager sessionManager = Mockito.mock(SessionManager.class);
        TwitterSession twitterSession = Mockito.mock(TwitterSession.class);
        TwitterAuthToken authToken = Mockito.spy(new TwitterAuthToken("token", "secret"));

        Mockito.when(TwitterCore.getInstance()).thenReturn(twitterCore);
        Mockito.when(twitterCore.getSessionManager()).thenReturn(sessionManager);
        Mockito.when(sessionManager.getActiveSession()).thenReturn(twitterSession);
        Mockito.when(twitterSession.getAuthToken()).thenReturn(authToken);

        Mockito.doNothing().when(sessionManager).clearActiveSession();

        Mockito.doNothing().when(ambassadorConfig).setTwitterAccessToken(Mockito.anyString());
        Mockito.doNothing().when(ambassadorConfig).setTwitterAccessTokenSecret(Mockito.anyString());
        Mockito.when(ambassadorConfig.getTwitterAccessToken()).thenReturn(token);
        Mockito.when(ambassadorConfig.getTwitterAccessTokenSecret()).thenReturn(secret);

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
        ambassadorConfig.nullifyTwitterIfInvalid(listener);

        // ASSERT
        Mockito.verify(ambassadorConfig).setTwitterAccessToken(Mockito.eq(token));
        Mockito.verify(ambassadorConfig).setTwitterAccessTokenSecret(Mockito.eq(secret));
        Mockito.verify(sessionManager).setActiveSession(Mockito.any(TwitterSession.class));
        Mockito.verify(ambassadorConfig).callNullifyComplete(Mockito.eq(listener));

        // ACT
        ambassadorConfig.nullifyTwitterIfInvalid(listener);

        // ASSERT
        Mockito.verify(ambassadorConfig, Mockito.times(2)).setTwitterAccessToken(Mockito.eq(token));
        Mockito.verify(ambassadorConfig, Mockito.times(2)).setTwitterAccessTokenSecret(Mockito.eq(secret));
        Mockito.verify(sessionManager, Mockito.times(2)).setActiveSession(Mockito.any(TwitterSession.class));
        Mockito.verify(sessionManager).clearActiveSession();
        Mockito.verify(ambassadorConfig, Mockito.times(2)).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void nullifyLinkedInIfInvalidNullTest() {
        // ARRANGE
        AmbassadorConfig.NullifyCompleteListener listener = Mockito.mock(AmbassadorConfig.NullifyCompleteListener.class);
        AmbassadorConfig ambassadorConfig = Mockito.spy(AmbassadorConfig.class);
        Mockito.when(ambassadorConfig.getLinkedInToken()).thenReturn(null);

        // ACT
        ambassadorConfig.nullifyLinkedInIfInvalid(listener);

        // ASSERT
        Mockito.verify(ambassadorConfig).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void nullifyLinkedInIfInvalidTest() {
        // ARRANGE
        AmbassadorConfig.NullifyCompleteListener listener = Mockito.mock(AmbassadorConfig.NullifyCompleteListener.class);
        AmbassadorConfig ambassadorConfig = Mockito.spy(AmbassadorConfig.class);
        String token = "token";
        Mockito.when(ambassadorConfig.getLinkedInToken()).thenReturn(token);
        RequestManager requestManager = Mockito.mock(RequestManager.class);
        Mockito.doReturn(requestManager).when(ambassadorConfig).buildRequestManager();

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

        // ACT
        ambassadorConfig.nullifyLinkedInIfInvalid(listener);
        ambassadorConfig.nullifyLinkedInIfInvalid(listener);

        // ASSERT
        Mockito.verify(ambassadorConfig).setLinkedInToken(null);
        Mockito.verify(ambassadorConfig, Mockito.times(2)).callNullifyComplete(Mockito.eq(listener));
    }

    @Test
    public void callNullifyCompleteNotNullTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = Mockito.spy(AmbassadorConfig.class);
        AmbassadorConfig.NullifyCompleteListener listener = Mockito.mock(AmbassadorConfig.NullifyCompleteListener.class);
        Mockito.doNothing().when(listener).nullifyComplete();

        // ACT
        ambassadorConfig.callNullifyComplete(listener);

        // ASSERT
        Mockito.verify(listener).nullifyComplete();
    }

    @Test
    public void callNullifyCompleteNullTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = Mockito.spy(AmbassadorConfig.class);
        AmbassadorConfig.NullifyCompleteListener listener = null;

        // ACT
        ambassadorConfig.callNullifyComplete(listener);

        // ASSERT
        // If nothing happens (no thrown exception), all is good
    }

}
