package com.example.ambassador.ambassadorsdk;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

/**
 * Created by JakeDunahee on 9/9/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AmbassadorActivityTest {
    @Mock
    private AmbassadorActivity ambassadorActivity;

    String valueString;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void copyToClipboardTest() {
        ClipboardManager mockClipboardMgr = mock(ClipboardManager.class);
        ClipData mockClipData = mock(ClipData.class);

        when(mockClipboardMgr.getPrimaryClip()).thenReturn(mockClipData);

        mockClipboardMgr.setPrimaryClip(mockClipData);

        verify(mockClipboardMgr).setPrimaryClip(mockClipData);
        assertEquals("Expecting same clipData values", mockClipData, mockClipboardMgr.getPrimaryClip());
    }

    @Test
    public void respondToGridViewTest() {
        when(ambassadorActivity.respondToGridViewClick(lt(5))).then(returnsFirstArg());

        // Int failureClick should be 0 although were passing 5 because respondToGridview only takes int <4
        int failureClick = ambassadorActivity.respondToGridViewClick(5);
        int successClick = ambassadorActivity.respondToGridViewClick(3);

        assertEquals("Expected 0(null) and got " + failureClick, 0, failureClick);
        assertEquals("Expected number greater than 0 and got " + successClick, 3, successClick);
    }

    @Test
    public void goToContactsPageTest() {
        Intent mockIntent = mock(Intent.class);
        when(mockIntent.getBooleanExtra("showPhoneNumbers", false)).thenReturn(true);

        mockIntent.putExtra("showPhoneNumbers", true);
        ambassadorActivity.startActivity(mockIntent);

        verify(mockIntent).putExtra("showPhoneNumbers", true);
        verify(ambassadorActivity).startActivity(mockIntent);

        assertEquals("Expected true value", true, mockIntent.getBooleanExtra("showPhoneNumbers", false));
    }

    @Test
    public void facebookShareTest() {
        ambassadorActivity.shareWithFacebook();
        verify(ambassadorActivity).shareWithFacebook();
    }

    @Test
    public void twitterShareTest() {
        ambassadorActivity.shareWithTwitter(true);
        ambassadorActivity.shareWithTwitter(false);
        verify(ambassadorActivity).shareWithTwitter(true);
        verify(ambassadorActivity).shareWithTwitter(false);
    }

    @Test
    public void linkedInShareTest() {
        ambassadorActivity.shareWithLinkedIn(true);
        ambassadorActivity.shareWithLinkedIn(false);
        verify(ambassadorActivity).shareWithLinkedIn(true);
        verify(ambassadorActivity).shareWithLinkedIn(false);
    }

    @Test
    public void tryAndSetURLTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ambassadorActivity.setUrlText(anyString(), anyInt());
                return null;
            }
        }).when(ambassadorActivity).tryAndSetURL(true);

        ambassadorActivity.tryAndSetURL(true);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ambassadorActivity.showLoader();
                return null;
            }
        }).when(ambassadorActivity).tryAndSetURL(false);

        ambassadorActivity.tryAndSetURL(false);

        verify(ambassadorActivity).setUrlText(anyString(), anyInt());
        verify(ambassadorActivity).showLoader();
    }

    @Test
    public void setUrlTextTest() {
        when(ambassadorActivity.setUrlText(anyString(), anyInt())).then(returnsFirstArg());
        assertEquals("test", ambassadorActivity.setUrlText("test", 201));
    }
//
//    @Test (expected = JSONException.class)
//    public void urlJsonExceptionTest() throws JSONException {
//        String pusherTestData = "{\"email";
////
////        String pusherTestData = "{\"email\":\"jake@getambassador.com\"," +
////                "\"firstName\":\"erer\"," +
////                "\"lastName\":\"ere\"," +
////                "\"phoneNumber\":\"null\"," +
////                "\"urls\":[" +
////                "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
////                "\"short_code\":\"jHjl\"," +
////                "\"campaign_uid\":250,\"subject\":\"Check out BarderrTahwn ®!\"}," +
////                "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
////                "\"short_code\":\"jHjl\"," +
////                "\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}," +
////                "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
////                "\"short_code\":\"jHjl\"," +
////                "\"campaign_uid\":270,\"subject\":\"Check out BarderrTahwn ®!\"}," +
////                "]}";
//
//        int testCampaignID = 260;
//
//        doThrow(new JSONException(pusherTestData)).when(ambassadorActivity).setUrlText(pusherTestData, testCampaignID);
//        ambassadorActivity.setUrlText(pusherTestData, testCampaignID);
//    }
}
