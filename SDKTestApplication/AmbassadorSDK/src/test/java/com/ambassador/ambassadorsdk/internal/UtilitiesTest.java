package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        Log.class
})
public class UtilitiesTest {

    Context context;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class
        );

        context = mock(Context.class);
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
        assertTrue(successBottomResult);
        assertTrue(successTopResult);
        assertFalse(failBottomResult);
        assertFalse(failLowerTopResult);
        assertFalse(negativeResult);
    }

    @Test
    public void getPixelSizeForDimensionTest() {
        // ARRANGE
        int parameter = android.R.dimen.app_icon_size;
        int expected = 25;
        Resources mockResources = mock(Resources.class);
        PowerMockito.when(AmbassadorSingleton.get()).thenReturn(context);
        when(context.getResources()).thenReturn(mockResources);
        when(mockResources.getDimensionPixelSize(anyInt())).thenReturn(expected);

        // ACT
        int ret = Utilities.getPixelSizeForDimension(parameter);

        // ASSERT
        assertEquals(expected, ret);
    }

    @Test
    public void getDpSizeForPixelsTest() {
        // ARRANGE
        int parameter = 200;
        Resources resources = mock(Resources.class);
        DisplayMetrics displayMetrics = mock(DisplayMetrics.class);
        PowerMockito.when(AmbassadorSingleton.get()).thenReturn(context);
        when(context.getResources()).thenReturn(resources);
        when(resources.getDisplayMetrics()).thenReturn(displayMetrics);
        displayMetrics.densityDpi = 320;

        // ACT
        float ret = Utilities.getDpSizeForPixels(parameter);

        // ASSERT
        assertEquals(100f, ret);
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
        assertTrue(successResult);
        assertFalse(failResult);
        assertFalse(nullResult);
    }

    @Test
    public void isTabletTest() {
        // ARRANGE
        Resources resources = mock(Resources.class);
        Configuration configuration = mock(Configuration.class);
        when(context.getResources()).thenReturn(resources);
        when(resources.getConfiguration()).thenReturn(configuration);

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
        assertFalse(smallCase);
        assertFalse(normalCase);
        assertTrue(largeCase);
        assertTrue(xlargeCase);
    }

    @Test
    public void deviceTypeTest() {
        // ARRANGE
        Resources resources = mock(Resources.class);
        Configuration mockConfiguration = mock(Configuration.class);
        when(context.getResources()).thenReturn(resources);
        when(resources.getConfiguration()).thenReturn(mockConfiguration);

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
        assertEquals(smallCase, "SmartPhone");
        assertEquals(normalCase, "SmartPhone");
        assertEquals(largeCase, "Tablet");
        assertEquals(xlargeCase, "Tablet");
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
        Resources resources = mock(Resources.class);
        DisplayMetrics displayMetrics = mock(DisplayMetrics.class);
        float density = 0.5f;
        displayMetrics.density = density;
        PowerMockito.when(AmbassadorSingleton.get()).thenReturn(context);
        when(context.getResources()).thenReturn(resources);
        when(resources.getDisplayMetrics()).thenReturn(displayMetrics);

        // ACT
        float ret = Utilities.getScreenDensity();

        // ASSERT
        assertEquals(density, ret);
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
    public void setStatusBarTest() {

    }

    @Test
    public void getTextWidthDpTest() {

    }

    @Test
    public void cutTextToShowTest() {

    }

}
