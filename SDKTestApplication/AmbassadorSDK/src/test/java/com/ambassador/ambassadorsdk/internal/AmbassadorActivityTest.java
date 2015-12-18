package com.ambassador.ambassadorsdk.internal;

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

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ClipData.class,
        Toast.class,
        AmbassadorActivity.class,
        Integer.class,
        AmbassadorSingleton.class
})
public class AmbassadorActivityTest extends TestCase {

    AmbassadorActivity ambassadorActivity;

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

        ambassadorActivity = spy(new AmbassadorActivity());
    }

    @Test
    public void copyToClipboardTest() {
        // ARRANGE
        PowerMockito.mockStatic(ClipData.class);
        PowerMockito.mockStatic(Toast.class);
        Context mockContext = mock(Context.class);
        ClipboardManager mockClipboard = mock(ClipboardManager.class);
        ClipData mockClipData = mock(ClipData.class);
        Toast mockToast = mock(Toast.class);
        String textToCopy = "random text";
        when(ClipData.newPlainText("simpleText", textToCopy)).thenReturn(mockClipData);
        when(mockContext.getSystemService(Context.CLIPBOARD_SERVICE)).thenReturn(mockClipboard);
        when(Toast.makeText(mockContext, "Copied to clipboard", Toast.LENGTH_SHORT)).thenReturn(mockToast);
        when(mockClipData.toString()).thenReturn(textToCopy);
        doNothing().when(mockToast).show();

        // ACT
        String test = ambassadorActivity.copyShortURLToClipboard(textToCopy, mockContext);

        // ASSERT
        assertNotNull(test);
        assertEquals(textToCopy, test);
    }

    @Test
    public void goToContactsPageTest() throws Exception{
        // ARRANGE
        Intent mockIntent = mock(Intent.class);
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        when(mockIntent.putExtra("showPhoneNumbers", true)).thenReturn(mockIntent);
        doNothing().when(ambassadorActivity).startActivity(mockIntent);

        // ACT
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
        whenNew(JSONObject.class).withAnyArguments().thenReturn(mockObject);
        when(ambassadorConfig.getCampaignID()).thenReturn("0");
        doNothing().when(ambassadorConfig).setRafDefaultMessage(anyString());
        doNothing().when(mockShortURLET).setText(anyString());
        doNothing().when(ambassadorConfig).setURL(anyString());
        doNothing().when(ambassadorConfig).setReferrerShortCode(anyString());
        doNothing().when(ambassadorConfig).setEmailSubject(anyString());
        doNothing().when(mockCustomEditText).setText(anyString());
        when(mockObject.getString(anyString())).thenReturn("String");
        when(mockObject.getJSONArray("urls")).thenReturn(mockArray);
        when(mockArray.getJSONObject(anyInt())).thenReturn(mockObject);
        doReturn(new Integer(3)).when(mockArray).length();
        doReturn(0).when(mockObject).getInt("campaign_uid");
        PowerMockito.when(Integer.parseInt(anyString())).thenReturn(new Integer(0));

        // ACT
        ambassadorActivity.tryAndSetURL("test", "Test message");

        // ASSERT
        verify(ambassadorActivity).tryAndSetURL(anyString(), anyString());
    }

}
