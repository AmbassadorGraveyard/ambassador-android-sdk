package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertTrue;

/**
 * Created by dylan on 11/4/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AmbassadorSingleton.class})
public class AmbassadorConfigTest {
    private SharedPreferences mockSharedPrefs;
    private SharedPreferences.Editor mockEditor;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(AmbassadorConfigTest ambassadorConfigTest);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        PowerMockito.mockStatic(AmbassadorConfig.class);
        PowerMockito.mockStatic(AmbassadorSingleton.class);

        TestComponent component = DaggerAmbassadorConfigTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);

        Context mockContext = mock(Context.class);

        mockSharedPrefs = mock(SharedPreferences.class);
        mockEditor = mock(SharedPreferences.Editor.class);

        when(AmbassadorSingleton.get()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPrefs);
        when(mockSharedPrefs.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        doNothing().when(mockEditor).apply();
    }

    @Test
    public void staticUrlGetTests() {
        // ACT
        String identifyURL = AmbassadorConfig.identifyURL();
        String conversionURL = AmbassadorConfig.conversionURL();
        String bulkSMSShareURL = AmbassadorConfig.bulkSMSShareURL();
        String bulkEmailShareURL = AmbassadorConfig.bulkEmailShareURL();
        String shareTrackURL = AmbassadorConfig.shareTrackURL();
        String pusherChannelNameURL = AmbassadorConfig.pusherChannelNameURL();
        String pusherCallbackURL = AmbassadorConfig.pusherCallbackURL();

        // ASSERT
        assertTrue(isURLReturnGood(identifyURL));
        assertTrue(isURLReturnGood(conversionURL));
        assertTrue(isURLReturnGood(bulkSMSShareURL));
        assertTrue(isURLReturnGood(bulkEmailShareURL));
        assertTrue(isURLReturnGood(shareTrackURL));
        assertTrue(isURLReturnGood(pusherChannelNameURL));
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
        verify(mockEditor).putString("linkedInToken", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setTwitterAccessTokenTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setTwitterAccessToken(testValue);

        // ASSERT
        verify(mockEditor).putString("twitterToken", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setTwitterAccessTokenSecretTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setTwitterAccessTokenSecret(testValue);

        // ASSERT
        verify(mockEditor).putString("twitterTokenSecret", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setIdentifyObjectTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setIdentifyObject(testValue);

        // ASSERT
        verify(mockEditor).putString("identifyObject", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setCampaignIDTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setCampaignID(testValue);

        // ASSERT
        verify(mockEditor).putString("campaignID", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setPusherInfoTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setPusherInfo(testValue);

        // ASSERT
        verify(mockEditor).putString("pusherObject", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setURLTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setURL(testValue);

        // ASSERT
        verify(mockEditor).putString("url", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setUniversalTokenTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setUniversalToken(testValue);

        // ASSERT
        verify(mockEditor).putString("universalToken", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setUniversalIDTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setUniversalID(testValue);

        // ASSERT
        verify(mockEditor).putString("universalID", testValue);
        verify(mockEditor).apply();
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
        verify(mockEditor).putString("referralShortCode", testValue);
        verify(mockEditor).putString("referrerShortCode", testValue);
        verify(mockEditor, times(2)).apply();
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
        verify(mockEditor).putString("fullName", testValue1 + " " + testValue2);
        verify(mockEditor).apply();
    }

    @Test
    public void setEmailSubjectTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setEmailSubject(testValue);

        // ASSERT
        verify(mockEditor).putString("subjectLine", testValue);
        verify(mockEditor).apply();
    }

    @Test
    public void setUserEmailTest() {
        // ARRANGE
        String testValue = "test";
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.setUserEmail(testValue);

        // ASSERT
        verify(mockEditor).putString("userEmail", testValue);
        verify(mockEditor).apply();
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
        verify(mockSharedPrefs).getString("linkedInToken", null);
    }

    @Test
    public void getTwitterAccessTokenTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getTwitterAccessToken();

        // ASSERT
        verify(mockSharedPrefs).getString("twitterToken", null);
    }

    @Test
    public void getTwitterAccessTokenSecretTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getTwitterAccessTokenSecret();

        // ASSERT
        verify(mockSharedPrefs).getString("twitterTokenSecret", null);
    }

    @Test
    public void getIdentifyObjectTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getIdentifyObject();

        // ASSERT
        verify(mockSharedPrefs).getString("identifyObject", null);
    }

    @Test
    public void getCampaignIDTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getCampaignID();

        // ASSERT
        verify(mockSharedPrefs).getString("campaignID", null);
    }

    @Test
    public void getPusherInfoTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getPusherInfo();

        // ASSERT
        verify(mockSharedPrefs).getString("pusherObject", null);
    }

    @Test
    public void getURLTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getURL();

        // ASSERT
        verify(mockSharedPrefs).getString("url", null);
    }

    @Test
    public void getUniversalKeyTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUniversalKey();

        // ASSERT
        verify(mockSharedPrefs).getString("universalToken", null);
    }

    @Test
    public void getUniversalIDTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUniversalID();

        // ASSERT
        verify(mockSharedPrefs).getString("universalID", null);
    }

    @Test
    public void getReferrerShortCodeTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getReferrerShortCode();

        // ASSERT
        verify(mockSharedPrefs).getString("referrerShortCode", null);
    }

    @Test
    public void getReferralShortCodeTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getReferralShortCode();

        // ASSERT
        verify(mockSharedPrefs).getString("referralShortCode", null);
    }

    @Test
    public void getUserFullNameTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUserFullName();

        // ASSERT
        verify(mockSharedPrefs).getString("fullName", null);
    }

    @Test
    public void getEmailSubjectLineTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getEmailSubjectLine();

        // ASSERT
        verify(mockSharedPrefs).getString("subjectLine", null);
    }

    @Test
    public void getUserEmailTest() {
        // ARRANGE
        AmbassadorConfig ambassadorConfig = new AmbassadorConfig();

        // ACT
        ambassadorConfig.getUserEmail();

        // ASSERT
        verify(mockSharedPrefs).getString("userEmail", null);
    }

}
