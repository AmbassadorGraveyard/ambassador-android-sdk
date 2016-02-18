package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

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
        AmbSingleton.class,
        Log.class,
        Color.class
})
public class UtilitiesTest {

    private Context context;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                Color.class
        );

        PowerMockito.spy(Utilities.class);
        context = Mockito.mock(Context.class);
    }

    @Test
    public void getPixelSizeForDimensionTest() {
        // ARRANGE
        int parameter = android.R.dimen.app_icon_size;
        int expected = 25;
        Resources mockResources = Mockito.mock(Resources.class);
        PowerMockito.when(AmbSingleton.getInstanceContext()).thenReturn(context);
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
        PowerMockito.when(AmbSingleton.getInstanceContext()).thenReturn(context);
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
    public void getScreenDensityTest() {
        // ARRANGE
        Resources resources = Mockito.mock(Resources.class);
        DisplayMetrics displayMetrics = Mockito.mock(DisplayMetrics.class);
        float density = 0.5f;
        displayMetrics.density = density;
        PowerMockito.when(AmbSingleton.getInstanceContext()).thenReturn(context);
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

    @Test
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

    }

}
