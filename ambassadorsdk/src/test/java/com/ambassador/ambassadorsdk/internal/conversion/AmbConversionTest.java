package com.ambassador.ambassadorsdk.internal.conversion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

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
@PrepareForTest({
        AmbSingleton.class,
        Gson.class,
        Log.class,
})
public class AmbConversionTest {

    protected Context context;
    protected Campaign campaign;
    protected User user;

    @Before
    public void setup() {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                Log.class
        );

        context = Mockito.mock(Context.class);
        PowerMockito.when(AmbSingleton.getContext()).thenReturn(context);

        campaign = Mockito.mock(Campaign.class);
        user = Mockito.mock(User.class);
    }

    @Test
    public void testsSaveDoesAddToNonEmptyArray() {
        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn("[{\"cats\":\"cats\"}]");
        final SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = (String) invocation.getArguments()[1];
                Assert.assertEquals(2, new JsonParser().parse(value).getAsJsonArray().size());
                return editor;
            }
        }).when(editor).putString(Mockito.anyString(), Mockito.anyString());

        AmbConversion ambConversion = AmbConversion.get(new ConversionParameters(), false, Mockito.mock(ConversionStatusListener.class));
        ambConversion.save();
    }

    @Test
    public void testsSaveDoesAddToEmptyArray() {
        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn("[]");
        final SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = (String) invocation.getArguments()[1];
                Assert.assertEquals(1, new JsonParser().parse(value).getAsJsonArray().size());
                return editor;
            }
        }).when(editor).putString(Mockito.anyString(), Mockito.anyString());

        AmbConversion ambConversion = AmbConversion.get(new ConversionParameters(), false, Mockito.mock(ConversionStatusListener.class));
        ambConversion.save();
    }

    @Test
    public void testsExecuteDoesFailOnInvalidCampaign() {
        ConversionParameters conversionParameters = new ConversionParameters.Builder()
                .setRevenue(15)
                .build();
        ConversionStatusListener conversionStatusListener = Mockito.mock(ConversionStatusListener.class);

        AmbConversion.get(conversionParameters, false, conversionStatusListener).execute();

        Mockito.verify(conversionStatusListener).error();
    }

    @Test
    public void testsExecuteDoesFailOnInvalidRevenue() {
        ConversionParameters conversionParameters = new ConversionParameters.Builder()
                .setCampaign(260)
                .setRevenue(-1)
                .build();
        ConversionStatusListener conversionStatusListener = Mockito.mock(ConversionStatusListener.class);

        AmbConversion.get(conversionParameters, false, conversionStatusListener).execute();

        Mockito.verify(conversionStatusListener).error();
    }

    @Test
    public void testsExecuteDoesPendingOnNullShortCode() {
        ConversionParameters conversionParameters = new ConversionParameters.Builder()
                .setCampaign(260)
                .setRevenue(12)
                .build();
        ConversionStatusListener conversionStatusListener = Mockito.mock(ConversionStatusListener.class);

        AmbConversion ambConversion = Mockito.spy(AmbConversion.get(conversionParameters, false, conversionStatusListener));

        Mockito.doNothing().when(ambConversion).save();

        user.setUserId("user");
        campaign.setReferredByShortCode(null);

        ambConversion.user = user;
        ambConversion.campaign = campaign;
        ambConversion.execute();

        Mockito.verify(conversionStatusListener).pending();
        Mockito.verify(ambConversion).save();
    }

    @Test
    public void testsExecuteDoesPendingOnEmptyShortCode() {
        ConversionParameters conversionParameters = new ConversionParameters.Builder()
                .setCampaign(260)
                .setRevenue(12)
                .build();
        ConversionStatusListener conversionStatusListener = Mockito.mock(ConversionStatusListener.class);

        AmbConversion ambConversion = Mockito.spy(AmbConversion.get(conversionParameters, false, conversionStatusListener));

        Mockito.doNothing().when(ambConversion).save();

        user.setUserId("user");
        campaign.setReferredByShortCode("");

        ambConversion.user = user;
        ambConversion.campaign = campaign;
        ambConversion.execute();

        Mockito.verify(conversionStatusListener).pending();
        Mockito.verify(ambConversion).save();
    }

    @Test
    public void testsExecuteDoesPendingOnOnNullUserId() {
        ConversionParameters conversionParameters = new ConversionParameters.Builder()
                .setCampaign(260)
                .setRevenue(12)
                .build();
        ConversionStatusListener conversionStatusListener = Mockito.mock(ConversionStatusListener.class);

        AmbConversion ambConversion = Mockito.spy(AmbConversion.get(conversionParameters, false, conversionStatusListener));

        Mockito.doNothing().when(ambConversion).save();

        user.setUserId(null);
        campaign.setReferredByShortCode("abcd");

        ambConversion.user = user;
        ambConversion.campaign = campaign;
        ambConversion.execute();

        Mockito.verify(conversionStatusListener).pending();
        Mockito.verify(ambConversion).save();
    }

}
