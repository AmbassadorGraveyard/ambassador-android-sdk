package com.ambassador.ambassadorsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ambassador.ambassadorsdk.internal.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IIdentify;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Timer;
import java.util.TimerTask;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({
        AmbassadorSDK.class,
        AmbassadorConfig.class,
        AmbassadorSingleton.class,
        Utilities.class,
        InstallReceiver.class
})
public class AmbassadorSDKTest {

    Context context;
    AmbassadorConfig ambassadorConfig;
    PusherSDK pusherSDK;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class,
                Utilities.class,
                InstallReceiver.class
        );

        context = Mockito.mock(Context.class);
        TestUtils.mockStrings(context);

        PowerMockito.spy(AmbassadorSDK.class);

        ambassadorConfig = Mockito.mock(AmbassadorConfig.class);
        AmbassadorSDK.ambassadorConfig = ambassadorConfig;

        pusherSDK = Mockito.mock(PusherSDK.class);
        AmbassadorSDK.pusherSDK = pusherSDK;
    }

    @Test
    public void presentRAFTest() throws Exception {
        // ARRANGE
        String campaignId = "260";
        Intent intent = Mockito.mock(Intent.class);
        PowerMockito.doReturn(intent).when(AmbassadorSDK.class, "buildIntent", context, AmbassadorActivity.class);
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
        // ARRANGE
        String email = "email";
        IIdentify identify = Mockito.mock(IIdentify.class);
        PowerMockito.doReturn(identify).when(AmbassadorSDK.class, "buildIdentify");

        // ACT
        AmbassadorSDK.identify(email);

        // ASSERT
        Mockito.verify(ambassadorConfig).setUserEmail(Mockito.eq(email));
        Mockito.verify(identify).getIdentity();
        Mockito.verify(pusherSDK).createPusher(Mockito.any(PusherSDK.PusherSubscribeCallback.class));

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
        // ARRANGE
        ConversionParameters conversionParameters = Mockito.mock(ConversionParameters.class);
        boolean restrictToInstall = false;
        Mockito.when(ambassadorConfig.getConvertedOnInstall()).thenReturn(false);
        ConversionUtility conversionUtility = Mockito.mock(ConversionUtility.class);
        PowerMockito.doReturn(conversionUtility).when(AmbassadorSDK.class, "buildConversionUtility", conversionParameters);
        Mockito.doNothing().when(conversionUtility).registerConversion();

        // ACT
        AmbassadorSDK.registerConversion(conversionParameters, restrictToInstall);

        // ASSERT
        Mockito.verify(conversionUtility).registerConversion();
    }

    @Test
    public void runWithKeysTest() throws Exception {
        // ARRANGE
        String universalToken = "universalToken";
        String universalID = "universalID";
        AmbassadorApplicationComponent component = Mockito.mock(AmbassadorApplicationComponent.class);
        PowerMockito.doReturn(component).when(AmbassadorSingleton.class, "getInstanceComponent");
        Mockito.doNothing().when(component).inject(Mockito.any(AmbassadorSDK.class));
        PowerMockito.doNothing().when(AmbassadorSDK.class, "registerInstallReceiver", context);
        PowerMockito.doNothing().when(AmbassadorSDK.class, "startConversionTimer");
        Mockito.doNothing().when(ambassadorConfig).setUniversalToken(Mockito.anyString());
        Mockito.doNothing().when(ambassadorConfig).setUniversalID(Mockito.anyString());

        // ACT
        AmbassadorSDK.runWithKeys(context, universalToken, universalID);

        // ASSERT
        Mockito.verify(ambassadorConfig).setUniversalToken(Mockito.eq(universalToken));
        Mockito.verify(ambassadorConfig).setUniversalID(Mockito.eq(universalID));
    }

    @Test
    public void registerInstallReceiverTest() throws Exception {
        // ARRANGE
        IntentFilter intentFilter = Mockito.mock(IntentFilter.class);
        PowerMockito.doReturn(intentFilter).when(AmbassadorSDK.class, "buildIntentFilter");
        Mockito.doNothing().when(intentFilter).addAction(Mockito.anyString());
        Mockito.doReturn(null).when(context).registerReceiver(Mockito.any(BroadcastReceiver.class), Mockito.any(IntentFilter.class));
        InstallReceiver broadcastReceiver = Mockito.mock(InstallReceiver.class);
        PowerMockito.doReturn(broadcastReceiver).when(InstallReceiver.class, "getInstance");

        // ACT
        AmbassadorSDK.registerInstallReceiver(context);

        // ASSERT
        Mockito.verify(intentFilter).addAction(Mockito.eq("com.android.vending.INSTALL_REFERRER"));
        Mockito.verify(context).registerReceiver(Mockito.eq(broadcastReceiver), Mockito.eq(intentFilter));
    }

    @Test
    public void startConversionTimerTest() throws Exception {
        // ARRANGE
        ConversionUtility conversionUtility = Mockito.mock(ConversionUtility.class);
        Mockito.doNothing().when(conversionUtility).readAndSaveDatabaseEntries();
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);
        PowerMockito.doReturn(conversionUtility).when(AmbassadorSDK.class, "buildConversionUtility", context);
        Timer timer = Mockito.mock(Timer.class);
        PowerMockito.doReturn(timer).when(AmbassadorSDK.class, "buildTimer");
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TimerTask timerTask = (TimerTask) invocation.getArguments()[0];
                timerTask.run();
                return null;
            }
        }).when(timer).scheduleAtFixedRate(Mockito.any(TimerTask.class), Mockito.eq(10000L), Mockito.eq(10000L));

        // ACT
        AmbassadorSDK.startConversionTimer();

        // ASSERT
        Mockito.verify(timer).scheduleAtFixedRate(Mockito.any(TimerTask.class), Mockito.eq(10000L), Mockito.eq(10000L));
        Mockito.verify(conversionUtility).readAndSaveDatabaseEntries();
    }

}
