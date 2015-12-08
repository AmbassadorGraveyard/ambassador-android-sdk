package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

import com.example.ambassador.ambassadorsdk.AmbassadorSDK;
import com.example.ambassador.ambassadorsdk.ServiceSelectorPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by JakeDunahee on 9/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest ({AmbassadorSDK.class, MyApplication.class})
public class AmbassadorSDKUnitTest {
    AmbassadorSDK ambassadorSDK;
    Context mockContext = mock(Context.class);

    @Inject
    AmbassadorSingleton ambassadorSingleton;

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

        PowerMockito.mockStatic(MyApplication.class);
        AmbassadorApplicationComponent application = mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(MyApplication.getComponent()).thenReturn(application);
        doNothing().when(application).inject(any(AmbassadorSDK.class));
        ambassadorSDK = Mockito.spy(AmbassadorSDK.class);
        ambassadorSDK.ambassadorSingleton = ambassadorSingleton;
    }

    @Test
    public void localPresentRAFTest() throws Exception {
        // ARRANGE
        Intent mockIntent = mock(Intent.class);

        // ACT
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        doNothing().when(ambassadorSingleton).setCampaignID(anyString());
        doNothing().when(mockContext).startActivity(mockIntent);
        ambassadorSDK.localPresentRAF(mockContext, "206");

        // ASSERT
        verify(ambassadorSingleton).setCampaignID("206");
        verify(mockContext).startActivity(mockIntent);
    }

    @Test
    public void localIdentifyTest() throws Exception {
        // ARRANGE
        Identify mockIdentify = mock(Identify.class);
        PowerMockito.mockStatic(MyApplication.class);

        // ACT
        PowerMockito.when(MyApplication.getAppContext()).thenReturn(mockContext);
        whenNew(Identify.class).withArguments(mockContext, "test@test.com").thenReturn(mockIdentify);
        doNothing().when(ambassadorSingleton).startIdentify(mockIdentify);
        ambassadorSDK.localIdentify("test@test.com");

        // ASSERT
        verify(ambassadorSingleton).startIdentify(mockIdentify);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("test@test.com", invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSingleton).setUserEmail(anyString());
    }

    @Test
    public void localRegisterConversionTest() {
        // ARRANGE
        final ConversionParameters parameters = mock(ConversionParameters.class);

        // ACT
        ambassadorSDK.localRegisterConversion(parameters);

        // ASSERT
        verify(ambassadorSDK).localRegisterConversion(parameters);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals(parameters, invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSingleton).registerConversion(parameters);
    }

    @Test
    public void localRunWithKeysTest() {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";

        // ACT
        doNothing().when(ambassadorSingleton).startConversionTimer();
        ambassadorSDK.localRunWithKeys(mockToken, mockID);

        // ASSERT
        verify(ambassadorSDK).localRunWithKeys(mockToken, mockID);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockSDKToken", invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSingleton).saveUniversalToken(mockToken);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockID", invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSingleton).saveUniversalID(mockID);
    }

    @Test
    public void localRunWithKeysAndConvertOnInstallTest() {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";
        ConversionParameters mockParameters = mock(ConversionParameters.class);

        // ACT
        when(ambassadorSingleton.getConvertedOnInstall()).thenReturn(false);
        doNothing().when(ambassadorSingleton).convertForInstallation(mockParameters);
        doNothing().when(ambassadorSingleton).startConversionTimer();
        ambassadorSDK.localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);

        // ASSERT
        verify(ambassadorSDK).localRunWithKeysAndConvertOnInstall(mockToken, mockID, mockParameters);
        verify(ambassadorSingleton).convertForInstallation(mockParameters);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockSDKToken", invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSingleton).saveUniversalToken(mockToken);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("mockID", invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSingleton).saveUniversalID(mockID);
    }


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

    @Test
    public void identifyTest() throws Exception {
        // ARRANGE
        String email = "test@test.com";

        // ACT
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);
        AmbassadorSDK.identify(email);

        // ASSERT
        verify(ambassadorSDK).localIdentify(email);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("test@test.com", invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSDK).localIdentify(email);
    }

    @Test
    public void registerConversionTest() throws Exception {
        // ARRANGE
        final ConversionParameters mockParameters = mock(ConversionParameters.class);

        // ACT
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);
        AmbassadorSDK.registerConversion(mockParameters);

        // ASSERT
        verify(ambassadorSDK).localRegisterConversion(mockParameters);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals(mockParameters, invocation.getArguments()[0]);
                return null;
            }
        }).when(ambassadorSDK).localRegisterConversion(mockParameters);
    }

    @Test
    public void runWithKeysTest() throws Exception {
        // ARRANGE
        String mockToken = "mockSDKToken";
        String mockID = "mockID";

        // ACT
        whenNew(AmbassadorSDK.class).withAnyArguments().thenReturn(ambassadorSDK);
        AmbassadorSDK.runWithKeys(mockToken, mockID);

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

    @Test
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
