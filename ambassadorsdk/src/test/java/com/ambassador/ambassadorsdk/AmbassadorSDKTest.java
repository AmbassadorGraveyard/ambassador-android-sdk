package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.ambassador.ambassadorsdk.internal.activities.survey.SurveyModel;
import com.ambassador.ambassadorsdk.internal.conversion.AmbConversion;
import com.ambassador.ambassadorsdk.internal.conversion.ConversionStatusListener;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.identify.AmbassadorIdentification;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({
        AmbassadorSDK.class,
        AmbIdentify.class,
        AmbConversion.class,
        Color.class
})
public class AmbassadorSDKTest {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                AmbIdentify.class,
                AmbConversion.class,
                Color.class
        );

        PowerMockito.spy(AmbassadorSDK.class);

        PowerMockito.when(AmbIdentify.class, "get", Mockito.anyString(), Mockito.any(AmbassadorIdentification.class)).thenReturn(Mockito.mock(AmbIdentify.class));
    }

    @Test
    public void testsIdentifyWithValidEmail() throws Exception {
        String email = "email@gmail.com";

        boolean result = AmbassadorSDK.identify(email);

        Assert.assertTrue(result);
    }

    @Test
    public void testsIdentifyWithInvalidEmail() throws Exception {
        String email = "email";

        boolean result = AmbassadorSDK.identify(email);

        Assert.assertFalse(result);
    }

    @Test
    public void testsIdentifyWithNullEmail() throws Exception {
        String email = null;

        boolean result = AmbassadorSDK.identify(email);

        Assert.assertFalse(result);
    }

    @Test
    public void testsTrackEventThatIsNotConversionDoesNothing() throws Exception {
        Bundle properties = Mockito.spy(Bundle.class);
        Bundle options = Mockito.spy(Bundle.class);
        Mockito.doReturn(false).when(options).getBoolean(Mockito.eq("conversion"), Mockito.anyBoolean());

        AmbConversion ambConversion = Mockito.mock(AmbConversion.class);
        PowerMockito.doReturn(ambConversion).when(AmbConversion.class, "get", Mockito.any(ConversionParameters.class), Mockito.anyBoolean(), Mockito.any(ConversionStatusListener.class));

        final Counter counter = new Counter();
        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                counter.increment();
                return null;
            }
        }).when(AmbassadorSDK.class, "registerConversion", Mockito.any(ConversionParameters.class), Mockito.anyBoolean(), Mockito.any(ConversionStatusListener.class));

        AmbassadorSDK.trackEvent("test", properties, options);

        Assert.assertEquals(0, counter.getCount());
    }

    protected class Counter {
        protected int count = 0;
        public void increment() {
            count++;
        }
        public int getCount() {
            return count;
        }
    }

    @Test
    public void testsTrackEventWithInterfaceDoesCallBack() {

    }

    @Test
    public void testsTrackEventDoesConvertPropertiesToConversionParameters() {

    }

    @Test
    public void testsPresentWelcomeScreenDoesSetFields() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        WelcomeScreenDialog.AvailabilityCallback availabilityCallback = Mockito.mock(WelcomeScreenDialog.AvailabilityCallback.class);
        WelcomeScreenDialog.Parameters parameters = Mockito.mock(WelcomeScreenDialog.Parameters.class);

        AmbassadorSDK.presentWelcomeScreen(activity, availabilityCallback, parameters);

        Assert.assertEquals(activity, WelcomeScreenDialog.getActivity());
        Assert.assertEquals(availabilityCallback, WelcomeScreenDialog.getAvailabilityCallback());
        Assert.assertEquals(parameters, WelcomeScreenDialog.getParameters());
    }

    @Test
    public void testsConfigureSurveyDoesSetColors() throws Exception {
        AmbassadorSDK.configureSurvey(Color.RED, Color.BLUE, Color.GREEN);

        Assert.assertEquals(Color.RED, new SurveyModel().getBackgroundColor());
        Assert.assertEquals(Color.BLUE, new SurveyModel().getContentColor());
        Assert.assertEquals(Color.GREEN, new SurveyModel().getButtonColor());
    }

}
