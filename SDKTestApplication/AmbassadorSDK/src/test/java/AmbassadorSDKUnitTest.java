package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

import com.ambassador.ambassadorsdk.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.AmbassadorApplicationModule;
import com.ambassador.ambassadorsdk.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.ConversionParameters;

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

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by JakeDunahee on 9/11/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest ({AmbassadorSDK.class, AmbassadorSingleton.class})
public class AmbassadorSDKUnitTest {

    AmbassadorSDK ambassadorSDK;
    Context mockContext = mock(Context.class);

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(AmbassadorSDKUnitTest ambassadorSDKUnitTest);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerAmbassadorSDKUnitTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);

        PowerMockito.mockStatic(AmbassadorSingleton.class);
        AmbassadorApplicationComponent application = mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(AmbassadorSingleton.getComponent()).thenReturn(application);
        doNothing().when(application).inject(any(AmbassadorSDK.class));
        ambassadorSDK = Mockito.spy(AmbassadorSDK.class);
        ambassadorSDK.ambassadorConfig = ambassadorConfig;
    }

    /**
     * Static method tests
     */

    @Test
    public void presentRAFTest() throws Exception {
        // ARRANGE
        String campID = "206";
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals(mockContext, invocation.getArguments()[0]);
                assertEquals("206", invocation.getArguments()[1]);

                return null;
            }
        }).when(ambassadorSDK).localPresentRAF(mockContext, campID);
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);

        // ACT
        AmbassadorSDK.presentRAF(mockContext, campID);

        // ASSERT
        verify(ambassadorSDK).localPresentRAF(mockContext, campID);
    }

    @Test
    public void identifyTest() throws Exception {
        // ARRANGE
        String email = "test@test.com";
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("test@test.com", invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSDK).localIdentify(email);
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);

        // ACT
        AmbassadorSDK.identify(email);

        // ASSERT
        verify(ambassadorSDK).localIdentify(email);
    }

    @Test
    public void registerConversionTest() throws Exception {
        // ARRANGE
        final ConversionParameters parameters = new ConversionParameters();
        ConversionUtility conversionUtility = mock(ConversionUtility.class);
        whenNew(ConversionUtility.class).withAnyArguments().thenReturn(conversionUtility);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(conversionUtility).registerConversion();

        // ACT
        AmbassadorSDK.registerConversion(parameters);

        // ASSERT
        verify(conversionUtility).registerConversion();
    }

    @Test
    public void runWithKeysTest() throws Exception {
        // ARRANGE
        AmbassadorSingleton mockAmbassadorSingleton = mock(AmbassadorSingleton.class);
        when(AmbassadorSingleton.getInstance()).thenReturn(mockAmbassadorSingleton);
        doNothing().when(mockAmbassadorSingleton).init(mockContext);
        String mockToken = "mockSDKToken";
        String mockID = "mockID";
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockSDKToken", invocation.getArguments()[0]);
                assertEquals("mockID", invocation.getArguments()[1]);
                return null;
            }
        }).when(ambassadorSDK).localRunWithKeys(mockToken, mockID);
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);

        // ACT
        AmbassadorSDK.runWithKeys(mockContext, mockToken, mockID);

        // ASSERT
        verify(ambassadorSDK).localRunWithKeys(mockToken, mockID);
    }

    @Test
    public void runWithKeysAndConvertOnInstallTest() throws Exception {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";
        final ConversionParameters mockParameters = mock(ConversionParameters.class);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockSDKToken", invocation.getArguments()[0]);
                assertEquals("mockID", invocation.getArguments()[1]);
                assertEquals(mockParameters, invocation.getArguments()[2]);
                return null;
            }
        }).when(ambassadorSDK).localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);

        // ACT
        AmbassadorSDK.runWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);

        // ASSERT
        verify(ambassadorSDK).localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);
    }

    /**
     * Local method tests
     */

    @Test
    public void localPresentRAFTest() throws Exception {
        // ARRANGE
        Intent mockIntent = mock(Intent.class);
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        doNothing().when(ambassadorConfig).setCampaignID(anyString());
        doNothing().when(mockContext).startActivity(mockIntent);

        // ACT
        ambassadorSDK.localPresentRAF(mockContext, "206");

        // ASSERT
        verify(ambassadorConfig).setCampaignID("206");
        verify(mockContext).startActivity(mockIntent);
    }

    @Test
    public void localIdentifyTest() throws Exception {
        // ARRANGE
        final String email = "test@test.com";
        doNothing().when(ambassadorConfig).setUserEmail(email);
        Identify mockIdentify = mock(Identify.class);
        doNothing().when(mockIdentify).getIdentity();
        whenNew(Identify.class).withAnyArguments().thenReturn(mockIdentify);
        PusherSDK mockPusherSDK = mock(PusherSDK.class);
        whenNew(PusherSDK.class).withAnyArguments().thenReturn(mockPusherSDK);
        doNothing().when(mockPusherSDK).createPusher(any(PusherSDK.PusherSubscribeCallback.class));

        // ACT
        ambassadorSDK.localIdentify(email);

        // ASSERT
        verify(ambassadorConfig).setUserEmail(email);
        verify(mockIdentify).getIdentity();
    }

    @Test
    public void localRunWithKeysTest() {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";
        doNothing().when(ambassadorSDK).startConversionTimer();

        // ACT
        ambassadorSDK.localRunWithKeys(mockToken, mockID);

        // ASSERT
        verify(ambassadorConfig).setUniversalToken(mockToken);
        verify(ambassadorConfig).setUniversalID(mockID);
        verify(ambassadorSDK).startConversionTimer();
    }

    @Test
    public void localRunWithKeysAndConvertOnInstallTest() {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";
        final ConversionParameters mockParameters = mock(ConversionParameters.class);
        doNothing().when(ambassadorSDK).startConversionTimer();
        when(ambassadorConfig.convertedOnInstall()).thenReturn(true);

        PowerMockito.mockStatic(AmbassadorSDK.class);

        // ACT
        ambassadorSDK.localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);

        // ASSERT
        verify(ambassadorConfig, times(1)).setUniversalToken(mockToken);
        verify(ambassadorConfig, times(1)).setUniversalID(mockID);
        verify(ambassadorSDK, times(1)).startConversionTimer();
        verify(ambassadorConfig, times(0)).setConvertForInstall();

        // ARRANGE
        when(ambassadorConfig.convertedOnInstall()).thenReturn(false);

        // ACT
        ambassadorSDK.localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);

        // ASSERT
        verify(ambassadorConfig, times(2)).setUniversalToken(mockToken);
        verify(ambassadorConfig, times(2)).setUniversalID(mockID);
        verify(ambassadorSDK, times(2)).startConversionTimer();
        verify(ambassadorConfig, times(1)).setConvertForInstall();
    }

    @Test
    public void startConversionTimerTest() throws Exception {
        // ARRANGE
        ConversionUtility mockConversionUtility = mock(ConversionUtility.class);
        whenNew(ConversionUtility.class).withAnyArguments().thenReturn(mockConversionUtility);
        Timer mockTimer = mock(Timer.class);
        whenNew(Timer.class).withAnyArguments().thenReturn(mockTimer);
        final TimerTask mockTimerTask = mock(TimerTask.class);
        whenNew(TimerTask.class).withAnyArguments().thenReturn(mockTimerTask);
        doNothing().when(mockTimer).scheduleAtFixedRate(any(TimerTask.class), anyInt(), anyInt());

        // ACT
        ambassadorSDK.startConversionTimer();

        // ASSERT
        verify(mockTimer).scheduleAtFixedRate(any(TimerTask.class), anyInt(), anyInt());
    }

}
