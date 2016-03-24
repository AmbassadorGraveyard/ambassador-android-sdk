package com.ambassador.ambassadorsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;

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

import dagger.ObjectGraph;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({
        AmbassadorSDK.class,
        Auth.class,
        User.class,
        Campaign.class,
        AmbSingleton.class,
        Utilities.class,
        InstallReceiver.class,
        ConversionParameters.class
})
public class AmbassadorSDKTest {

    private Context context;

    private Auth auth;
    private User user;
    private Campaign campaign;

    private PusherManager pusherManager;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                Utilities.class,
                InstallReceiver.class
        );

        context = Mockito.mock(Context.class);
        TestUtils.mockStrings(context);

        PowerMockito.spy(AmbassadorSDK.class);

        auth = Mockito.mock(Auth.class);
        AmbassadorSDK.auth = auth;

        user = Mockito.mock(User.class);
        AmbassadorSDK.user = user;

        campaign = Mockito.mock(Campaign.class);
        AmbassadorSDK.campaign = campaign;

        pusherManager = Mockito.mock(PusherManager.class);
        AmbassadorSDK.pusherManager = pusherManager;
    }

    @Test
    public void presentRAFTest() throws Exception {

    }

    @Test
    public void identifyTest() throws Exception {
        // ARRANGE
        String email = "email@gmail.com";
        IdentifyAugurSDK identify = Mockito.mock(IdentifyAugurSDK.class);
        PowerMockito.doReturn(identify).when(AmbassadorSDK.class, "buildIdentify");

        // ACT
        AmbassadorSDK.identify(email);

        // ASSERT
        Mockito.verify(user).setEmail(Mockito.eq(email));
        Mockito.verify(identify).getIdentity();
        Mockito.verify(pusherManager).startNewChannel();

    }

    @Test
    public void registerConversionRestrictToInstallTest() {
        // ARRANGE
        ConversionParameters conversionParameters = PowerMockito.mock(ConversionParameters.class);
        boolean restrictToInstall = true;
        Mockito.when(campaign.isConvertedOnInstall()).thenReturn(true);

        // ACT
        AmbassadorSDK.registerConversion(conversionParameters, restrictToInstall);

        // ASSERT
        Mockito.verify(campaign).isConvertedOnInstall();
    }

    @Test
    public void registerConversionNonInstallTest() throws Exception {
        // ARRANGE
        ConversionParameters conversionParameters = PowerMockito.mock(ConversionParameters.class);
        boolean restrictToInstall = false;
        Mockito.when(campaign.isConvertedOnInstall()).thenReturn(false);
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
        ObjectGraph objectGraph = Mockito.mock(ObjectGraph.class);
        PowerMockito.doReturn(objectGraph).when(AmbSingleton.class, "getGraph");
        Mockito.doNothing().when(objectGraph).injectStatics();
        PowerMockito.doNothing().when(AmbassadorSDK.class, "registerInstallReceiver", context);
        PowerMockito.doNothing().when(AmbassadorSDK.class, "startConversionTimer");
        PowerMockito.doNothing().when(AmbassadorSDK.class, "setupGcm", context);
        Mockito.doNothing().when(auth).setUniversalToken(Mockito.anyString());
        Mockito.doNothing().when(auth).setUniversalId(Mockito.anyString());

        // ACT
        AmbassadorSDK.runWithKeys(context, universalToken, universalID);

        // ASSERT
        Mockito.verify(auth).setUniversalToken(Mockito.eq(universalToken));
        Mockito.verify(auth).setUniversalId(Mockito.eq(universalID));
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
        Mockito.when(AmbSingleton.getContext()).thenReturn(context);
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
