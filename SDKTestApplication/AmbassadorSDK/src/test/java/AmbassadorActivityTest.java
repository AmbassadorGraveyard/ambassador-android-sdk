package com.example.ambassador.ambassadorsdk;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.test.mock.MockContext;
import android.widget.EditText;
import android.widget.Toast;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by JakeDunahee on 9/9/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ClipData.class, Toast.class, AmbassadorActivity.class, Integer.class, AmbassadorSingleton.class})
public class AmbassadorActivityTest extends TestCase {
    @Mock
    private AmbassadorActivity ambassadorActivity;

    String valueString;

    @Before
    public void setup() {
//        MockitoAnnotations.initMocks(this);
        ambassadorActivity = spy(AmbassadorActivity.class);

    }


    @Test
    public void testcopyToClipboardTest() {
        // ARRANGE
        PowerMockito.mockStatic(ClipData.class);
        PowerMockito.mockStatic(Toast.class);
        Context mockContext = mock(Context.class);
        ClipboardManager mockClipboard = mock(ClipboardManager.class);
        ClipData mockClipData = mock(ClipData.class);
        Toast mockToast = mock(Toast.class);
        String textToCopy = "random text";

        // ACT
        when(ClipData.newPlainText("simpleText", textToCopy)).thenReturn(mockClipData);
        when(mockContext.getSystemService(Context.CLIPBOARD_SERVICE)).thenReturn(mockClipboard);
        when(Toast.makeText(mockContext, "Copied to clipboard", Toast.LENGTH_SHORT)).thenReturn(mockToast);
        when(mockClipData.toString()).thenReturn(textToCopy);
        doNothing().when(mockToast).show();

        // ASSERT
        assertNotNull(ambassadorActivity.copyShortURLToClipboard(textToCopy, mockContext));
        assertEquals(textToCopy, ambassadorActivity.copyShortURLToClipboard(textToCopy, mockContext));
    }

    @Test
    public void respondToGridViewTest() {
        // ARRANGE
        Random random = new Random();
        int randomNum = random.nextInt(4 - 0) + 0;

        // ACT
        doNothing().when(ambassadorActivity).shareWithLinkedIn();
        doNothing().when(ambassadorActivity).shareWithFacebook();
        doNothing().when(ambassadorActivity).shareWithTwitter();
        doNothing().when(ambassadorActivity).goToContactsPage(anyBoolean());

        // ASSERT
        assertEquals("Expected response of " + randomNum, randomNum, ambassadorActivity.respondToGridViewClick(randomNum));
        assertNotEquals(-1, ambassadorActivity.respondToGridViewClick(randomNum));
    }

    @Test
    public void failedGridViewTest() {
        // ASSERT
        assertEquals(-1, ambassadorActivity.respondToGridViewClick(5));
    }

    @Test
    public void goToContactsPageTest() throws Exception{
        // ARRANGE
        Intent mockIntent = mock(Intent.class);

        // ACT
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        when(mockIntent.putExtra("showPhoneNumbers", true)).thenReturn(mockIntent);
        doNothing().when(ambassadorActivity).startActivity(mockIntent);
        ambassadorActivity.goToContactsPage(true);

        // ASSERT
        verify(ambassadorActivity).startActivity(mockIntent);
    }

//    @Test
//    public void facebookShareTest() {
//        ambassadorActivity.shareWithFacebook();
//        verify(ambassadorActivity).shareWithFacebook();
//    }

//    @Test
//    public void twitterShareTest() {
//        ambassadorActivity.shareWithTwitter(true);
//        ambassadorActivity.shareWithTwitter(false);
//        verify(ambassadorActivity).shareWithTwitter(true);
//        verify(ambassadorActivity).shareWithTwitter(false);
//    }
//
//    @Test
//    public void linkedInShareTest() {
//        ambassadorActivity.shareWithLinkedIn(true);
//        ambassadorActivity.shareWithLinkedIn(false);
//        verify(ambassadorActivity).shareWithLinkedIn(true);
//        verify(ambassadorActivity).shareWithLinkedIn(false);
//    }

    @Test
    public void tryAndSetURLTrueTest() throws Exception {
        // ARRANGE
        PowerMockito.mockStatic(Integer.class);
        PowerMockito.mockStatic(AmbassadorSingleton.class);
        JSONArray mockArray = mock(JSONArray.class);
        JSONObject mockObject = mock(JSONObject.class);
        AmbassadorSingleton mockSingleton = mock(AmbassadorSingleton.class);
        EditText mockShortURLET = mock(EditText.class);
        String pusher = "{\"email\":\"jake@getambassador.com\"," +
                "\"firstName\":\"erer\",\"lastName\":\"ere\"," +
                "\"phoneNumber\":\"null\"," +
                "\"urls\":[" +
                "{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}" +
                "]}";

        // ACT
        whenNew(JSONObject.class).withAnyArguments().thenReturn(mockObject);
        when(mockSingleton.getInstance()).thenReturn(mockSingleton);
        when(mockSingleton.getCampaignID()).thenReturn("0");
        doNothing().when(mockSingleton).setRafDefaultMessage(anyString());
        doNothing().when(mockShortURLET).setText(anyString());
        doNothing().when(mockSingleton).saveURL(anyString());
        doNothing().when(mockSingleton).saveShortCode(anyString());
        doNothing().when(mockSingleton).saveEmailSubject(anyString());
        when(mockObject.getString(anyString())).thenReturn("String");
        when(mockObject.getJSONArray("urls")).thenReturn(mockArray);
        when(mockArray.getJSONObject(anyInt())).thenReturn(mockObject);
        doReturn(new Integer(3)).when(mockArray).length();
        doReturn(0).when(mockObject).getInt("campaign_uid");
        PowerMockito.when(Integer.parseInt(anyString())).thenReturn(new Integer(0));

        // ASSERT
        ambassadorActivity.tryAndSetURL(true, pusher, "Test message", mockShortURLET);
        verify(ambassadorActivity).tryAndSetURL(true, pusher, "Test message", mockShortURLET);
    }

    @Test
    public void tryAndSetURLFalseTest() throws Exception {
        ProgressDialog mockDialog = mock(ProgressDialog.class);
        EditText mockShortURLET = mock(EditText.class);
        DialogInterface.OnCancelListener mockListener = mock(DialogInterface.OnCancelListener.class);
        String pusher = "{\"email\":\"jake@getambassador.com\"," +
                "\"firstName\":\"erer\",\"lastName\":\"ere\"," +
                "\"phoneNumber\":\"null\"," +
                "\"urls\":[" +
                "{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}" +
                "]}";

        whenNew(ProgressDialog.class).withAnyArguments().thenReturn(mockDialog);
        doNothing().when(mockDialog).setMessage(anyString());
        doNothing().when(mockDialog).setOwnerActivity(ambassadorActivity);
        doNothing().when(mockDialog).setCanceledOnTouchOutside(anyBoolean());
        doNothing().when(mockDialog).setOnCancelListener(mockListener);
        doNothing().when(mockDialog).show();

        ambassadorActivity.tryAndSetURL(false, pusher, "Test", mockShortURLET);
    }

    @Test (expected = JSONException.class)
    public void tryAndSetURLWithException() throws Exception {
        EditText mockShortURLET = mock(EditText.class);
        JSONObject mockObject = mock(JSONObject.class);
        String pusher = "{\"email\"\"jake@getambassador.com\"," +
                "\"firstName:\"erer\",\"lastName\":\"ere\"," +
                "\"phoneNumber\":\"null\"," +
                "\"urls\":[" +
                "{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}" +
                "]}";

        whenNew(JSONObject.class).withArguments(pusher).thenThrow(new JSONException(pusher));

        ambassadorActivity.tryAndSetURL(true, pusher, "Test", mockShortURLET);
    }

//    @Test (expected = MockitoException.class)
//    public void tryAndSetURLWithException() throws JSONException {
//        doThrow(new JSONException("exceptionString")).when(ambassadorActivity).tryAndSetURL(true);
//        ambassadorActivity.tryAndSetURL(true);
//        verify(ambassadorActivity).tryAndSetURL(true);
//    }
}
