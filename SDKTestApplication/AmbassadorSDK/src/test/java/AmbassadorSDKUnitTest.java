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
//        MockitoAnnotations.initMocks(this);'
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerAmbassadorSDKUnitTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);
        PowerMockito.mockStatic(AmbassadorSDK.class);
        PowerMockito.mockStatic(MyApplication.class);
        AmbassadorApplicationComponent application = mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(MyApplication.getAppContext()).thenReturn(mockContext);
        doNothing().when(application).inject(ambassadorSDK);
        ambassadorSDK = Mockito.spy(AmbassadorSDK.class);



//        PowerMockito.mockStatic(AmbassadorSDK.class);
    }

    @Test
    public void localPresentRAFTest() throws Exception {
        // ARRANGE
        Intent mockIntent = mock(Intent.class);
        ServiceSelectorPreferences mockPreferences = mock(ServiceSelectorPreferences.class);

        // ACT
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        when(mockIntent.putExtra("rafParameters", mockPreferences)).thenReturn(mockIntent);
        PowerMockito.doNothing().when(ambassadorSingleton).setCampaignID(anyString());
        PowerMockito.doNothing().when(mockContext).startActivity(mockIntent);
        ambassadorSDK.localPresentRAF(mockContext, "206");

        // ASSERT
        assertEquals(mockIntent, mockIntent.putExtra("rafParameters", mockPreferences));
        verify(ambassadorSingleton).setCampaignID("206");
        verify(mockContext).startActivity(mockIntent);
    }

//    @Test
//    public void identifyTest() throws Exception {
//        // ARRANGE
//        PowerMockito.mockStatic(MyApplication.class);
//        Identify mockIdentify = mock(Identify.class);
//        String identifyString = "jake@testambassador.com";
//
//        // ACT
//        when(MyApplication.getAppContext()).thenReturn(mockContext);
//        whenNew(Identify.class).withAnyArguments().thenReturn(mockIdentify);
//        doNothing().when(mockSingleton).startIdentify(mockIdentify);
//        AmbassadorSDK.identify(identifyString);
//
//        // ASSERT
//        verify(mockSingleton).startIdentify(mockIdentify);
//    }
//
//    @Test
//    public void registerConversionTest() {
//        // ARRANGE
//        ConversionParameters mockParameters = mock(ConversionParameters.class);
//
//        // ACT
//        doNothing().when(mockSingleton).registerConversion(mockParameters);
//        AmbassadorSDK.registerConversion(mockParameters);
//
//        // ASSERT
//        verify(mockSingleton).registerConversion(mockParameters);
//    }
//
//    @Test
//    public void runWithKeysTest() {
//        // ARRANGE
//        String fakeUniversalToken = "SDKToken djjfivklsd-sdf-2rwlkj";
//        String fakeUniversalId = "blah blah";
//
//        // ACT
//        doNothing().when(mockSingleton).saveUniversalToken(fakeUniversalToken);
//        doNothing().when(mockSingleton).startConversionTimer();
//        AmbassadorSDK.runWithKeys(fakeUniversalToken, fakeUniversalId);
//        doNothing().when(mockSingleton).saveUniversalID(fakeUniversalId);
//
//        // ASSERT
//        verify(mockSingleton).saveUniversalToken(fakeUniversalToken);
//        verify(mockSingleton).startConversionTimer();
//    }
//
//    @Test
//    public void runWithKeysAndConvertOnInstallTest() {
//        // ARRANGE
//        String fakeUniversalToken = "SDKToken sdlksjfl-sd-2-sdfsdf-33";
//        String fakeUniversalId = "blah blah";
//        ConversionParameters mockParameters = mock(ConversionParameters.class);
//
//        // ACT
//        doNothing().when(mockSingleton).saveUniversalToken(fakeUniversalToken);
//        doNothing().when(mockSingleton).startConversionTimer();
//        when(mockSingleton.convertedOnInstall()).thenReturn(false);
//        doNothing().when(mockSingleton).convertForInstallation(mockParameters);
//        AmbassadorSDK.runWithKeysAndConvertOnInstall(fakeUniversalToken, fakeUniversalToken, mockParameters);
//
//        // ASSERT
//        assertEquals(false, mockSingleton.convertedOnInstall());
//        verify(mockSingleton).saveUniversalToken(fakeUniversalToken);
//        verify(mockSingleton).startConversionTimer();
//        verify(mockSingleton).convertForInstallation(mockParameters);
//    }
}
