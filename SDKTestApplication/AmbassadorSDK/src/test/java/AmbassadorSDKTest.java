package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

import com.example.ambassador.ambassadorsdk.AmbassadorSDK;
import com.example.ambassador.ambassadorsdk.ServiceSelectorPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by JakeDunahee on 9/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest (AmbassadorSDK.class)
public class AmbassadorSDKTest {
    private final AmbassadorSDK mockSDK = mock(AmbassadorSDK.class);
    String valueString;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(AmbassadorSDK.class);
    }

    @Test
    public void presentRAFTest() throws Exception {
        // ARRANGE
        PowerMockito.mockStatic(AmbassadorSingleton.class);
        Context mockContext = mock(Context.class);
        Intent mockIntent = mock(Intent.class);
        AmbassadorSingleton mockSingleton = mock(AmbassadorSingleton.class);
        ServiceSelectorPreferences mockPreferences = mock(ServiceSelectorPreferences.class);
        PowerMockito.whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);

        // ACT
        PowerMockito.whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        when(mockIntent.putExtra(anyString(), AmbassadorActivity.class)).thenReturn(mockIntent);
        when(AmbassadorSingleton.getInstance()).thenReturn(mockSingleton);
        PowerMockito.doNothing().when(mockSingleton).setCampaignID(anyString());
        PowerMockito.doNothing().when(mockContext).startActivity(mockIntent);
        AmbassadorSDK.presentRAF(mockContext, mockPreferences, "306");

        // ASSERT
        verify(mockContext).startActivity(mockIntent);
    }

    @Test
    public void identifyTest() {
        String identifier = "jake@getambassador.com";
    }
}
