package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ambassador.ambassadorsdk.internal.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.internal.AmbassadorApplicationModule;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({
        AmbassadorSDK.class,
        AmbassadorSingleton.class,
        Utilities.class
})
public class AmbassadorSDKTest {

    AmbassadorSDK ambassadorSDK;
    Context mockContext = mock(Context.class);

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(AmbassadorSDKTest ambassadorSDKTest);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerAmbassadorSDKTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);

        PowerMockito.mockStatic(AmbassadorSingleton.class);
        AmbassadorApplicationComponent application = mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(AmbassadorSingleton.getComponent()).thenReturn(application);
        AmbassadorSingleton mockSingleton = mock(AmbassadorSingleton.class);
        PowerMockito.when(AmbassadorSingleton.getInstance()).thenReturn(mockSingleton);
        doNothing().when(mockSingleton).init(any(Context.class));
        doNothing().when(application).inject(any(AmbassadorSDK.class));
        ambassadorSDK = Mockito.spy(AmbassadorSDK.class);
        ambassadorSDK.ambassadorConfig = ambassadorConfig;
    }

    @Test
    public void presentRAFTest() throws Exception {
        // ARRANGE
        Intent mockIntent = mock(Intent.class);
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        doNothing().when(ambassadorConfig).setCampaignID(anyString());
        doNothing().when(mockContext).startActivity(mockIntent);

        // ACT
        ambassadorSDK.presentRAF(mockContext, "206");

        // ASSERT
        verify(ambassadorConfig).setCampaignID("206");
        verify(mockContext).startActivity(mockIntent);
    }

    @Test
    public void identifyTest() throws Exception {
        // ARRANGE
        final String email = "test@test.com";
        doNothing().when(ambassadorConfig).setUserEmail(email);
        IdentifyAugurSDK mockIdentify = mock(IdentifyAugurSDK.class);
        doNothing().when(mockIdentify).getIdentity();
        whenNew(IdentifyAugurSDK.class).withAnyArguments().thenReturn(mockIdentify);
        PusherSDK mockPusherSDK = mock(PusherSDK.class);
        whenNew(PusherSDK.class).withAnyArguments().thenReturn(mockPusherSDK);
        doNothing().when(mockPusherSDK).createPusher(any(PusherSDK.PusherSubscribeCallback.class));

        // ACT
        ambassadorSDK.identify(email);

        // ASSERT
        verify(ambassadorConfig).setUserEmail(email);
        verify(mockIdentify).getIdentity();
        verify(mockPusherSDK).createPusher(any(PusherSDK.PusherSubscribeCallback.class));
    }

    @Test
    public void runWithKeysTest() throws Exception {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";
        ConversionUtility mockConversionUtility = mock(ConversionUtility.class);
        whenNew(ConversionUtility.class).withAnyArguments().thenReturn(mockConversionUtility);

        Timer mockTimer = mock(Timer.class);
        whenNew(Timer.class).withAnyArguments().thenReturn(mockTimer);
        final TimerTask mockTimerTask = mock(TimerTask.class);
        whenNew(TimerTask.class).withAnyArguments().thenReturn(mockTimerTask);
        doNothing().when(mockTimer).scheduleAtFixedRate(any(TimerTask.class), anyInt(), anyInt());

        IntentFilter mockIntentFilter = mock(IntentFilter.class);
        whenNew(IntentFilter.class).withNoArguments().thenReturn(mockIntentFilter);
        doNothing().when(mockIntentFilter).addAction(anyString());

        // ACT
        ambassadorSDK.runWithKeys(mockContext, mockToken, mockID);

        // ASSERT
        verify(ambassadorConfig, times(1)).setUniversalToken(mockToken);
        verify(ambassadorConfig, times(1)).setUniversalID(mockID);
        verify(mockTimer).scheduleAtFixedRate(any(TimerTask.class), anyInt(), anyInt());
        verify(mockIntentFilter, times(1)).addAction(anyString());
        verify(mockContext, times(1)).registerReceiver(any(InstallReceiver.class), any(IntentFilter.class));
    }

    @Test
    public void registerConversionTest() throws Exception {
        // ARRANGE
        final ConversionParameters parameters = new ConversionParameters();
        ConversionUtility conversionUtility = mock(ConversionUtility.class);
        whenNew(ConversionUtility.class).withAnyArguments().thenReturn(conversionUtility);
        PowerMockito.mockStatic(Utilities.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(conversionUtility).registerConversion();

        // TEST REGULAR CONVERSION (NOT RESTRICTED TO INSTALL)
        // ACT
        AmbassadorSDK.registerConversion(parameters, false);

        // ASSERT
        verify(conversionUtility).registerConversion();
        verify(ambassadorConfig, never()).setConvertOnInstall();

        // TEST RESTRICT TO INSTALL CONVERSION FIRST TIME
        // ARRANGE
        when(ambassadorConfig.getConvertedOnInstall()).thenReturn(false);

        // ACT
        AmbassadorSDK.registerConversion(parameters, true);

        // ASSERT
        verify(conversionUtility, times(2)).registerConversion();
        verify(ambassadorConfig).setConvertOnInstall();

        // TEST RESTRICT TO INSTALL CONVERSION *NOT* FIRST TIME
        // ARRANGE
        when(ambassadorConfig.getConvertedOnInstall()).thenReturn(true);

        // ACT
        AmbassadorSDK.registerConversion(parameters, true);

        // ASSERT
        verify(conversionUtility, times(2)).registerConversion();
        verify(ambassadorConfig).setConvertOnInstall();
    }
}
