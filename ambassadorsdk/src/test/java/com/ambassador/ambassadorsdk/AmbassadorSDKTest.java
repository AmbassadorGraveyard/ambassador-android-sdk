package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.graphics.Color;

import com.ambassador.ambassadorsdk.internal.activities.survey.SurveyModel;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.identify.AmbassadorIdentification;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({
        AmbIdentify.class,
        Color.class
})
public class AmbassadorSDKTest {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                AmbIdentify.class,
                Color.class
        );

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
    public void testsUnidentifyDoesClearUser() throws Exception {
        AmbassadorSDK.user = Mockito.mock(User.class);
        AmbassadorSDK.unidentify();
        Mockito.verify(AmbassadorSDK.user).clear();
        Mockito.verify(AmbassadorSDK.user).setUserId(null);
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
