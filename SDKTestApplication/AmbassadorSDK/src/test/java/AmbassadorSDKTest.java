package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

import com.example.ambassador.ambassadorsdk.AmbassadorSDK;
import com.example.ambassador.ambassadorsdk.ServiceSelectorPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by JakeDunahee on 9/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest ({AmbassadorSDK.class, AmbassadorConfig.class, MyApplication.class})
public class AmbassadorSDKTest {
    Context mockContext = mock(Context.class);
    AmbassadorConfig mockSingleton = mock(AmbassadorConfig.class);

    @Before
    public void setUp() {
        PowerMockito.mockStatic(AmbassadorConfig.class);
        when(AmbassadorConfig.getInstance()).thenReturn(mockSingleton);
    }

    @Test
    public void presentRAFTest() throws Exception {
        // ARRANGE
        Intent mockIntent = mock(Intent.class);
        ServiceSelectorPreferences mockPreferences = mock(ServiceSelectorPreferences.class);

        // ACT
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        when(mockIntent.putExtra("rafParameters", mockPreferences)).thenReturn(mockIntent);
        PowerMockito.doNothing().when(mockSingleton).setCampaignID(anyString());
        PowerMockito.doNothing().when(mockContext).startActivity(mockIntent);
        AmbassadorSDK.presentRAF(mockContext, "306");

        // ASSERT
        assertEquals(mockIntent, mockIntent.putExtra("rafParameters", mockPreferences));
        verify(mockContext).startActivity(mockIntent);
    }

    @Test
    public void identifyTest() throws Exception {
        // ARRANGE
        PowerMockito.mockStatic(MyApplication.class);
        Identify mockIdentify = mock(Identify.class);
        String identifyString = "jake@testambassador.com";

        // ACT
        when(MyApplication.getAppContext()).thenReturn(mockContext);
        whenNew(Identify.class).withAnyArguments().thenReturn(mockIdentify);
        doNothing().when(mockSingleton).startIdentify(mockIdentify);
        AmbassadorSDK.identify(identifyString);

        // ASSERT
        verify(mockSingleton).startIdentify(mockIdentify);
    }

    @Test
    public void registerConversionTest() {
        // ARRANGE
        ConversionParameters mockParameters = mock(ConversionParameters.class);

        // ACT
        doNothing().when(mockSingleton).registerConversion(mockParameters);
        AmbassadorSDK.registerConversion(mockParameters);

        // ASSERT
        verify(mockSingleton).registerConversion(mockParameters);
    }

    @Test
    public void runWithKeysTest() {
        // ARRANGE
        String fakeUniversalToken = "SDKToken djjfivklsd-sdf-2rwlkj";
        String fakeUniversalId = "blah blah";

        // ACT
        doNothing().when(mockSingleton).setUniversalToken(fakeUniversalToken);
        doNothing().when(mockSingleton).startConversionTimer();
        AmbassadorSDK.runWithKeys(fakeUniversalToken, fakeUniversalId);
        doNothing().when(mockSingleton).setUniversalID(fakeUniversalId);

        // ASSERT
        verify(mockSingleton).setUniversalToken(fakeUniversalToken);
        verify(mockSingleton).startConversionTimer();
    }

    @Test
    public void runWithKeysAndConvertOnInstallTest() {
        // ARRANGE
        String fakeUniversalToken = "SDKToken sdlksjfl-sd-2-sdfsdf-33";
        String fakeUniversalId = "blah blah";
        ConversionParameters mockParameters = mock(ConversionParameters.class);

        // ACT
        doNothing().when(mockSingleton).setUniversalToken(fakeUniversalToken);
        doNothing().when(mockSingleton).startConversionTimer();
        when(mockSingleton.convertedOnInstall()).thenReturn(false);
        doNothing().when(mockSingleton).convertForInstallation(mockParameters);
        AmbassadorSDK.runWithKeysAndConvertOnInstall(fakeUniversalToken, fakeUniversalToken, mockParameters);

        // ASSERT
        assertEquals(false, mockSingleton.convertedOnInstall());
        verify(mockSingleton).setUniversalToken(fakeUniversalToken);
        verify(mockSingleton).startConversionTimer();
        verify(mockSingleton).convertForInstallation(mockParameters);
    }
}
