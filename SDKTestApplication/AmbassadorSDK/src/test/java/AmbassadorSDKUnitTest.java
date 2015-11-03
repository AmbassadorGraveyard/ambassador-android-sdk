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

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
//
//    @Test
//    public void localIdentifyTest() throws Exception {
//        // ARRANGE
//        Identify mockIdentify = mock(Identify.class);
//        PowerMockito.mockStatic(AmbassadorSingleton.class);
//
//        // ACT
//        PowerMockito.when(AmbassadorSingleton.getAppContext()).thenReturn(mockContext);
//        whenNew(Identify.class).withArguments(mockContext, "test@test.com").thenReturn(mockIdentify);
//        doNothing().when(ambassadorConfig).startIdentify(mockIdentify);
//        ambassadorSDK.localIdentify("test@test.com");
//
//        // ASSERT
//        verify(ambassadorConfig).startIdentify(mockIdentify);
//        doAnswer(new Answer() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                assertEquals("test@test.com", invocation.getArguments()[0]);
//                return null;
//            }
//        }).when(ambassadorConfig).setUserEmail(anyString());
//    }

//    @Test
//    public void localPresentRAFTest() throws Exception {
//        // ARRANGE
//        Intent mockIntent = mock(Intent.class);
//
//        // ACT
//        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
//        doNothing().when(ambassadorConfig).setCampaignID(anyString());
//        doNothing().when(mockContext).startActivity(mockIntent);
//        ambassadorSDK.localPresentRAF(mockContext, "206");
//
//        // ASSERT
//        verify(ambassadorConfig).setCampaignID("206");
//        verify(mockContext).startActivity(mockIntent);
//    }
//
//    @Test
//    public void localRunWithKeysTest() {
//        // ARRANGE
//        String mockToken = "mockSDKToken";
//        String mockID = "mockID";
//
//        // ACT
//        doNothing().when(ambassadorConfig).startConversionTimer();
//        ambassadorSDK.localRunWithKeys(mockToken, mockID);
//
//        // ASSERT
//        verify(ambassadorSDK).localRunWithKeys(mockToken, mockID);
//        doAnswer(new Answer() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                assertEquals("mockSDKToken", invocation.getArguments()[0]);
//                return null;
//            }
//        }).when(ambassadorConfig).saveUniversalToken(mockToken);
//
//        doAnswer(new Answer() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                assertEquals("mockID", invocation.getArguments()[0]);
//                return null;
//            }
//        }).when(ambassadorConfig).saveUniversalID(mockID);
//    }
//
//    @Test
//    public void localRunWithKeysAndConvertOnInstallTest() {
//        // ARRANGE
//        String mockToken = "mockSDKToken";
//        String mockID = "mockID";
//        ConversionParameters mockParameters = mock(ConversionParameters.class);
//
//        // ACT
//        when(ambassadorConfig.convertedOnInstall()).thenReturn(false);
//        doNothing().when(ambassadorConfig).convertForInstallation(mockParameters);
//        doNothing().when(ambassadorConfig).startConversionTimer();
//        ambassadorSDK.localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);
//
//        // ASSERT
//        verify(ambassadorSDK).localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);
//        verify(ambassadorConfig).convertForInstallation(mockParameters);
//        doAnswer(new Answer() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                assertEquals("mockSDKToken", invocation.getArguments()[0]);
//                return null;
//            }
//        }).when(ambassadorConfig).saveUniversalToken(mockToken);
//
//        doAnswer(new Answer() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                assertEquals("mockID", invocation.getArguments()[0]);
//                return null;
//            }
//        }).when(ambassadorConfig).saveUniversalID(mockID);
//    }


    // AmbassadorSDK Static Unit Tests
    @Test
    public void presentRAFTest() throws Exception {
        // ARRANGE
        String campID = "206";

        // ACT
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);
        AmbassadorSDK.presentRAF(mockContext, campID);

        // ASSERT
        verify(ambassadorSDK).localPresentRAF(mockContext, campID);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals(mockContext, invocation.getArguments()[0]);
                assertEquals("206", invocation.getArguments()[1]);

                return null;
            }
        }).when(ambassadorSDK).localPresentRAF(mockContext, campID);
    }

    //@Test
    public void identifyTest() throws Exception {
        // ARRANGE
        String email = "test@test.com";

        // ACT
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);
        AmbassadorSDK.identify(email);

        // ASSERT
        verify(ambassadorSDK).localIdentify(email);
//        doAnswer(new Answer() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                assertEquals("test@test.com", invocation.getArguments()[0]);
//                return null;
//            }
//        }).when(ambassadorSDK).localIdentify(email);
    }

    @Test
    public void registerConversionTest() throws Exception {
        // ARRANGE
        final ConversionParameters mockParameters = mock(ConversionParameters.class);
        ConversionUtility conversionUtility = mock(ConversionUtility.class);

        // ACT
        whenNew(ConversionUtility.class).withAnyArguments().thenReturn(conversionUtility);
        doNothing().when(conversionUtility).registerConversion();
        AmbassadorSDK.registerConversion(mockParameters);

        // ASSERT
        verify(conversionUtility).registerConversion();
    }

    //@Test
    public void runWithKeysTest() throws Exception {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";

        // ACT
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);
        AmbassadorSDK.runWithKeys(mockContext, mockToken, mockID);

        // ASSERT
        verify(ambassadorSDK).localRunWithKeys(mockToken, mockID);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockSDKToken", invocation.getArguments()[0]);
                assertEquals("mockID", invocation.getArguments()[1]);
                return null;
            }
        }).when(ambassadorSDK).localRunWithKeys(mockToken, mockID);
    }

    //@Test
    public void runWithKeysAndConvertOnInstallTest() throws Exception {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";
        final ConversionParameters mockParameters = mock(ConversionParameters.class);

        // ACT
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);
        AmbassadorSDK.runWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);

        // ASSERT
        verify(ambassadorSDK).localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockSDKToken", invocation.getArguments()[0]);
                assertEquals("mockID", invocation.getArguments()[1]);
                assertEquals(mockParameters, invocation.getArguments()[2]);
                return null;
            }
        }).when(ambassadorSDK).localRunWithKeys(mockToken, mockID);
    }
}
