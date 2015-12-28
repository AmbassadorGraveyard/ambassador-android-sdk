package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

import com.ambassador.ambassadorsdk.internal.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.Utilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({
        AmbassadorSDK.class,
        AmbassadorConfig.class,
        AmbassadorSingleton.class,
        Utilities.class
})
public class AmbassadorSDKTest {

    AmbassadorConfig ambassadorConfig;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class,
                Utilities.class
        );

        PowerMockito.spy(AmbassadorSDK.class);

        ambassadorConfig = Mockito.mock(AmbassadorConfig.class);
        AmbassadorSDK.ambassadorConfig = ambassadorConfig;
    }

    @Test
    public void presentRAFTest() {
        // ARRANGE
        Context context = Mockito.mock(Context.class);
        String campaignId = "260";
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(AmbassadorSDK.buildIntent(Mockito.eq(context), Mockito.eq(AmbassadorActivity.class))).thenReturn(intent);
        Mockito.doNothing().when(ambassadorConfig).setCampaignID(Mockito.eq(campaignId));
        Mockito.doNothing().when(context).startActivity(Mockito.eq(intent));

        // ACT
        AmbassadorSDK.presentRAF(context, campaignId);

        // ASSERT
        Mockito.verify(ambassadorConfig).setCampaignID(Mockito.eq(campaignId));
        Mockito.verify(context).startActivity(Mockito.eq(intent));
    }

    @Test
    public void identifyTest() throws Exception {

    }

    @Test
    public void registerConversionRestrictToInstallTest() {
        // ARRANGE
        ConversionParameters conversionParameters = Mockito.mock(ConversionParameters.class);
        boolean restrictToInstall = true;
        Mockito.when(ambassadorConfig.getConvertedOnInstall()).thenReturn(true);

        // ACT
        AmbassadorSDK.registerConversion(conversionParameters, restrictToInstall);

        // ASSERT
        Mockito.verify(ambassadorConfig, Mockito.times(2)).getConvertedOnInstall();
    }

    @Test
    public void registerConversionNonInstallTest() throws Exception {


    }

    @Test
    public void runWithKeysTest() throws Exception {

    }

}
