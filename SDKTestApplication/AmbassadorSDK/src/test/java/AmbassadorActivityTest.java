package com.example.ambassador.ambassadorsdk;
import com.example.ambassador.ambassadorsdk.AmbassadorActivity;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

/**
 * Created by JakeDunahee on 9/9/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AmbassadorActivityTest {
    @Mock AmbassadorActivity ambassadorActivity;

    String fakeData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void copyToClipboardTest() {
        when(ambassadorActivity.copyShortURLToClipboard(anyString())).then(returnsFirstArg());

        String stringCopied =  ambassadorActivity.copyShortURLToClipboard("test");

        assertNotNull(stringCopied);
    }

    @Test
    public void respondToGridViewTest() {
        when(ambassadorActivity.respondToGridViewClick(lt(5))).then(returnsFirstArg());

        // Int failureClick should be 0 although were passing 5 because respondToGridview only takes int <4
        int failureClick = ambassadorActivity.respondToGridViewClick(5);
        int successClick = ambassadorActivity.respondToGridViewClick(3);

        assertEquals(failureClick, 0);
        assertEquals(successClick, 3);
    }

    @Test
    public void goToContactsPageTest() {
        when(ambassadorActivity.goToContactsPage(anyBoolean())).then(returnsFirstArg());

        String pageType = ambassadorActivity.goToContactsPage(true);

        assertEquals(pageType, "phone");
    }

    @Test
    public void setUrlTextTest() {
        String pusherTestData = "{\"email\":\"jake@getambassador.com\"," +
                "\"firstName\":\"erer\"," +
                "\"lastName\":\"ere\"," +
                "\"phoneNumber\":\"null\"," +
                "\"urls\"" +
                    "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
                    "\"short_code\":\"jHjl\"," +
                    "\"campaign_uid\":250,\"subject\":\"Check out BarderrTahwn ®!\"}," +
                    "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
                    "\"short_code\":\"jHjl\"," +
                    "\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}," +
                    "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
                    "\"short_code\":\"jHjl\"," +
                    "\"campaign_uid\":270,\"subject\":\"Check out BarderrTahwn ®!\"}," +
                "]}";

        int testCampaignID = 260;

//        doAnswer(new Answer<Void>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                fakeData = "FakeDataString";
//                return null;
//            }
//        }).when(ambassadorActivity).setUrlText();

        ambassadorActivity.setUrlText(pusherTestData, testCampaignID);

        assertEquals("FakeDataString", fakeData);
    }

    @Test (expected = JSONException.class)
    public void urlJsonExceptionTest() throws JSONException {
        String pusherTestData = "{\"email";
//
//        String pusherTestData = "{\"email\":\"jake@getambassador.com\"," +
//                "\"firstName\":\"erer\"," +
//                "\"lastName\":\"ere\"," +
//                "\"phoneNumber\":\"null\"," +
//                "\"urls\":[" +
//                "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
//                "\"short_code\":\"jHjl\"," +
//                "\"campaign_uid\":250,\"subject\":\"Check out BarderrTahwn ®!\"}," +
//                "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
//                "\"short_code\":\"jHjl\"," +
//                "\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}," +
//                "{\"url\":\"http://staging.mbsy.co\\/jHjl\"," +
//                "\"short_code\":\"jHjl\"," +
//                "\"campaign_uid\":270,\"subject\":\"Check out BarderrTahwn ®!\"}," +
//                "]}";

        int testCampaignID = 260;

        doThrow(new JSONException(pusherTestData)).when(ambassadorActivity).setUrlText(pusherTestData, testCampaignID);
        ambassadorActivity.setUrlText(pusherTestData, testCampaignID);
    }
}
