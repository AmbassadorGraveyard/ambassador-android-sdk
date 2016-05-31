package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.ambassador.ambassadorsdk.internal.activities.survey.SurveyModel;
import com.ambassador.ambassadorsdk.internal.data.User;
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
    public void testsUnidentifyDoesClearUser() throws Exception {
        AmbassadorSDK.user = Mockito.mock(User.class);
        AmbassadorSDK.unidentify();
        Mockito.verify(AmbassadorSDK.user).clear();
        Mockito.verify(AmbassadorSDK.user).setUserId(null);
    }

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


    @Test
    public void testsTrackEventWithInterfaceDoesCallBack() throws Exception {
        Bundle properties = Mockito.spy(Bundle.class);
        Bundle options = Mockito.spy(Bundle.class);
        Mockito.doReturn(true).when(options).getBoolean(Mockito.eq("conversion"), Mockito.anyBoolean());

        AmbConversion ambConversion = Mockito.mock(AmbConversion.class);
        PowerMockito.doReturn(ambConversion).when(AmbConversion.class, "get", Mockito.any(ConversionParameters.class), Mockito.anyBoolean(), Mockito.any(ConversionStatusListener.class));

        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ConversionStatusListener listener = (ConversionStatusListener) invocation.getArguments()[2];
                listener.success();
                return null;
            }
        }).when(AmbassadorSDK.class, "registerConversion", Mockito.any(ConversionParameters.class), Mockito.anyBoolean(), Mockito.any(ConversionStatusListener.class));

        Mockito.doReturn(260).when(properties).getInt(Mockito.eq("campaign"), Mockito.anyInt());
        Mockito.doReturn(12.50f).when(properties).getFloat(Mockito.eq("revenue"), Mockito.anyFloat());
        Mockito.doReturn(1).when(properties).getInt(Mockito.eq("commissionApproved"), Mockito.anyInt());
        Mockito.doReturn("evt1").when(properties).getString(Mockito.eq("eventData1"), Mockito.anyString());
        Mockito.doReturn("evt2").when(properties).getString(Mockito.eq("eventData2"), Mockito.anyString());
        Mockito.doReturn("evt3").when(properties).getString(Mockito.eq("eventData3"), Mockito.anyString());
        Mockito.doReturn("oid").when(properties).getString(Mockito.eq("orderId"), Mockito.anyString());

        Mockito.doReturn(false).when(options).getBoolean(Mockito.eq("restrictedToInstall"), Mockito.anyBoolean());

        ConversionStatusListener listener = Mockito.mock(ConversionStatusListener.class);

        AmbassadorSDK.trackEvent("conversion", properties, options, listener);

        Mockito.verify(listener).success();
    }

    @Test
    public void testsTrackEventDoesConvertPropertiesToConversionParameters() throws Exception {
        Bundle properties = Mockito.spy(Bundle.class);
        Bundle options = Mockito.spy(Bundle.class);
        Mockito.doReturn(true).when(options).getBoolean(Mockito.eq("conversion"), Mockito.anyBoolean());

        AmbConversion ambConversion = Mockito.mock(AmbConversion.class);
        PowerMockito.doReturn(ambConversion).when(AmbConversion.class, "get", Mockito.any(ConversionParameters.class), Mockito.anyBoolean(), Mockito.any(ConversionStatusListener.class));

        final Counter counter = new Counter();
        final ConversionParameters.Builder builder = new ConversionParameters.Builder();
        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ConversionParameters conversionParameters = (ConversionParameters) invocation.getArguments()[0];
                counter.increment();
                builder.setCampaign(conversionParameters.campaign);
                builder.setRevenue(conversionParameters.revenue);
                builder.setIsApproved(conversionParameters.isApproved);
                builder.setEventData1(conversionParameters.eventData1);
                builder.setEventData2(conversionParameters.eventData2);
                builder.setEventData3(conversionParameters.eventData3);
                builder.setTransactionUid(conversionParameters.transactionUid);
                return null;
            }
        }).when(AmbassadorSDK.class, "registerConversion", Mockito.any(ConversionParameters.class), Mockito.anyBoolean(), Mockito.any(ConversionStatusListener.class));

        Mockito.doReturn(260).when(properties).getInt(Mockito.eq("campaign"), Mockito.anyInt());
        Mockito.doReturn(12.50f).when(properties).getFloat(Mockito.eq("revenue"), Mockito.anyFloat());
        Mockito.doReturn(1).when(properties).getInt(Mockito.eq("commissionApproved"), Mockito.anyInt());
        Mockito.doReturn("evt1").when(properties).getString(Mockito.eq("eventData1"), Mockito.anyString());
        Mockito.doReturn("evt2").when(properties).getString(Mockito.eq("eventData2"), Mockito.anyString());
        Mockito.doReturn("evt3").when(properties).getString(Mockito.eq("eventData3"), Mockito.anyString());
        Mockito.doReturn("oid").when(properties).getString(Mockito.eq("orderId"), Mockito.anyString());

        Mockito.doReturn(false).when(options).getBoolean(Mockito.eq("restrictedToInstall"), Mockito.anyBoolean());

        AmbassadorSDK.trackEvent("conversion", properties, options);

        Assert.assertEquals(1, counter.getCount());

        ConversionParameters conversionParameters = builder.build();
        Assert.assertEquals(260, conversionParameters.campaign);
        Assert.assertEquals(12.50f, conversionParameters.revenue);
        Assert.assertEquals(1, conversionParameters.isApproved);
        Assert.assertEquals("evt1", conversionParameters.eventData1);
        Assert.assertEquals("evt2", conversionParameters.eventData2);
        Assert.assertEquals("evt3", conversionParameters.eventData3);
        Assert.assertEquals("oid", conversionParameters.transactionUid);
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
