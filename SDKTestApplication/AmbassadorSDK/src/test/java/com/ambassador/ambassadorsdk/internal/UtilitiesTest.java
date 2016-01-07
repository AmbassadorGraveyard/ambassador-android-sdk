package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Utilities.class,
        AmbassadorSingleton.class,
        Log.class,
        Color.class
})
public class UtilitiesTest {

    Context context;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class,
                Color.class
        );

        PowerMockito.spy(Utilities.class);
        context = Mockito.mock(Context.class);
    }

    @Test
    public void isSuccessfulResponseCodeTest() {
        // ARRANGE
        int successBottomCase = 200;
        int successTopCase = 299;
        int failBottomCase = 300;
        int failLowerTopCase = 199;
        int negativeCase = -1;

        // ACT
        boolean successBottomResult = Utilities.isSuccessfulResponseCode(successBottomCase);
        boolean successTopResult = Utilities.isSuccessfulResponseCode(successTopCase);
        boolean failBottomResult = Utilities.isSuccessfulResponseCode(failBottomCase);
        boolean failLowerTopResult = Utilities.isSuccessfulResponseCode(failLowerTopCase);
        boolean negativeResult = Utilities.isSuccessfulResponseCode(negativeCase);

        // ASSERT
        Assert.assertTrue(successBottomResult);
        Assert.assertTrue(successTopResult);
        Assert.assertFalse(failBottomResult);
        Assert.assertFalse(failLowerTopResult);
        Assert.assertFalse(negativeResult);
    }

    @Test
    public void getPixelSizeForDimensionTest() {
        // ARRANGE
        int parameter = android.R.dimen.app_icon_size;
        int expected = 25;
        Resources mockResources = Mockito.mock(Resources.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);
        Mockito.when(context.getResources()).thenReturn(mockResources);
        Mockito.when(mockResources.getDimensionPixelSize(Mockito.anyInt())).thenReturn(expected);

        // ACT
        int ret = Utilities.getPixelSizeForDimension(parameter);

        // ASSERT
        Assert.assertEquals(expected, ret);
    }

    @Test
    public void getDpSizeForPixelsTest() {
        // ARRANGE
        int parameter = 200;
        Resources resources = Mockito.mock(Resources.class);
        DisplayMetrics displayMetrics = Mockito.mock(DisplayMetrics.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.getDisplayMetrics()).thenReturn(displayMetrics);
        displayMetrics.densityDpi = 320;

        // ACT
        float ret = Utilities.getDpSizeForPixels(parameter);

        // ASSERT
        Assert.assertEquals(100f, ret);
    }

    @Test
    public void containsURLTest() {
        // ARRANGE
        String url = "google.com";
        String successCase = "http://google.com";
        String failCase = "google.ca";
        String nullCase = null;

        // ACT
        boolean successResult = Utilities.containsURL(successCase, url);
        boolean failResult = Utilities.containsURL(failCase, url);
        boolean nullResult = Utilities.containsURL(nullCase, url);

        // ASSERT
        Assert.assertTrue(successResult);
        Assert.assertFalse(failResult);
        Assert.assertFalse(nullResult);
    }

    @Test
    public void isTabletTest() {
        // ARRANGE
        Resources resources = Mockito.mock(Resources.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.getConfiguration()).thenReturn(configuration);

        // ACT
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;
        boolean smallCase = Utilities.isTablet(context);
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;
        boolean normalCase = Utilities.isTablet(context);
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
        boolean largeCase = Utilities.isTablet(context);
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;
        boolean xlargeCase = Utilities.isTablet(context);

        // ASSERT
        Assert.assertFalse(smallCase);
        Assert.assertFalse(normalCase);
        Assert.assertTrue(largeCase);
        Assert.assertTrue(xlargeCase);
    }

    @Test
    public void deviceTypeTest() {
        // ARRANGE
        Resources resources = Mockito.mock(Resources.class);
        Configuration mockConfiguration = Mockito.mock(Configuration.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.getConfiguration()).thenReturn(mockConfiguration);

        // ACT
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;
        String smallCase = Utilities.deviceType(context);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;
        String normalCase = Utilities.deviceType(context);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
        String largeCase = Utilities.deviceType(context);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;
        String xlargeCase = Utilities.deviceType(context);

        // ASSERT
        Assert.assertEquals(smallCase, "SmartPhone");
        Assert.assertEquals(normalCase, "SmartPhone");
        Assert.assertEquals(largeCase, "Tablet");
        Assert.assertEquals(xlargeCase, "Tablet");
    }

    @Test
    public void presentUrlDialogTest() {

    }

    @Test
    public void presentNonCancelableMessageDialogTest() {

    }

    @Test
    public void debugLogTest() {

    }

    @Test
    public void getScreenDensityTest() {
        // ARRANGE
        Resources resources = Mockito.mock(Resources.class);
        DisplayMetrics displayMetrics = Mockito.mock(DisplayMetrics.class);
        float density = 0.5f;
        displayMetrics.density = density;
        PowerMockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.getDisplayMetrics()).thenReturn(displayMetrics);

        // ACT
        float ret = Utilities.getScreenDensity();

        // ASSERT
        Assert.assertEquals(density, ret);
    }

    @Test
    public void isConnectedTrueTest() {
        // ARRANGE
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        NetworkInfo[] networkInfos = new NetworkInfo[1];
        networkInfos[0] = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfos[0].isConnected()).thenReturn(true);
        Mockito.when(connectivityManager.getAllNetworkInfo()).thenReturn(networkInfos);

        // ACT
        boolean connected = Utilities.isConnected(context);

        // ASSERT
        Assert.assertTrue(connected);
    }

    public void isConnectedFalseTest() {
        // ARRANGE
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        NetworkInfo[] networkInfos = new NetworkInfo[1];
        networkInfos[0] = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfos[0].isConnected()).thenReturn(false);
        Mockito.when(connectivityManager.getAllNetworkInfo()).thenReturn(networkInfos);

        // ACT
        boolean connected = Utilities.isConnected(context);

        // ASSERT
        Assert.assertFalse(connected);
    }

    @Test
    @SuppressWarnings("all")
    public void setStatusBarTest() {
        // ARRANGE
        Window window = Mockito.mock(Window.class);
        int primaryColor = 5555;
        Mockito.when(Utilities.getSdkInt()).thenReturn(21);
        Mockito.doNothing().when(window).setStatusBarColor(Mockito.anyInt());

        // ACT
        Utilities.setStatusBar(window, primaryColor);

        // ASSERT
        Mockito.verify(window).setStatusBarColor(Mockito.anyInt());
    }

    @Test
    public void getTextWidthDpTest() throws Exception {

    }

    @Test
    public void cutTextToShowTest() {
        // ARRANGE
        String text = "ambassador";
        TextView textView = Mockito.mock(TextView.class);
        float maxWidth = 120f;


        // ACT
        //Utilities.cutTextToShow(text, textView, maxWidth);

        // ASSERT
    }

}
