package com.ambassador.ambassadorsdk;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by JakeDunahee on 9/9/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ClipData.class, Toast.class, AmbassadorActivity.class, Integer.class, AmbassadorSingleton.class})
public class AmbassadorActivityTest extends TestCase {
    AmbassadorActivity ambassadorActivity = spy(new AmbassadorActivity());

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(AmbassadorActivityTest ambassadorActivityTest);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerAmbassadorActivityTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);
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

    @Test
    public void tryAndSetURLTrueTest() throws Exception {
        // ARRANGE
        PowerMockito.mockStatic(Integer.class);
        PowerMockito.mockStatic(AmbassadorSingleton.class);
        CustomEditText mockCustomEditText = mock(CustomEditText.class);
        JSONArray mockArray = mock(JSONArray.class);
        JSONObject mockObject = mock(JSONObject.class);
        ambassadorActivity.ambassadorConfig = ambassadorConfig;
        ambassadorActivity.etShortUrl = mockCustomEditText;
        EditText mockShortURLET = mock(EditText.class);
        String pusher = "{\"email\":\"jake@getambassador.com\"," +
                "\"firstName\":\"erer\",\"lastName\":\"ere\"," +
                "\"phoneNumber\":\"null\"," +
                "\"urls\":[" +
                "{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}" +
                "]}";

        // ACT
        whenNew(JSONObject.class).withAnyArguments().thenReturn(mockObject);
        when(ambassadorConfig.getCampaignID()).thenReturn("0");
        doNothing().when(ambassadorConfig).setRafDefaultMessage(anyString());
        doNothing().when(mockShortURLET).setText(anyString());
        doNothing().when(ambassadorConfig).setURL(anyString());
        doNothing().when(ambassadorConfig).setShortCode(anyString());
        doNothing().when(ambassadorConfig).setEmailSubject(anyString());
        doNothing().when(mockCustomEditText).setText(anyString());
        when(mockObject.getString(anyString())).thenReturn("String");
        when(mockObject.getJSONArray("urls")).thenReturn(mockArray);
        when(mockArray.getJSONObject(anyInt())).thenReturn(mockObject);
        doReturn(new Integer(3)).when(mockArray).length();
        doReturn(0).when(mockObject).getInt("campaign_uid");
        PowerMockito.when(Integer.parseInt(anyString())).thenReturn(new Integer(0));

        // ASSERT
        ambassadorActivity.tryAndSetURL(pusher, "Test message");
        verify(ambassadorActivity).tryAndSetURL(pusher, "Test message");
    }
}
