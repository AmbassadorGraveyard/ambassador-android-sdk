package com.ambassador.ambassadorsdk.internal.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbSingleton.class
})
public class DeviceTest {

    private Configuration configuration;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class
        );

        Context context = Mockito.mock(Context.class);
        Resources resources = Mockito.mock(Resources.class);
        configuration = Mockito.mock(Configuration.class);

        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(context);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.getConfiguration()).thenReturn(configuration);
    }

    @Test
    public void isTabletSizeSmallTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;

        // ACT
        boolean smallCase = new Device().isTablet();

        // ASSERT
        Assert.assertFalse(smallCase);
    }

    @Test
    public void isTabletSizeNormalTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;

        // ACT
        boolean normalCase = new Device().isTablet();

        // ASSERT
        Assert.assertFalse(normalCase);
    }

    @Test
    public void isTabletSizeLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;

        // ACT
        boolean largeCase = new Device().isTablet();

        // ASSERT
        Assert.assertTrue(largeCase);
    }

    @Test
    public void isTabletSizeXLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;

        // ACT
        boolean xlargeCase = new Device().isTablet();

        // ASSERT
        Assert.assertTrue(xlargeCase);

    }

    @Test
    public void deviceTypeSizeSmallTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;

        // ACT
        String smallCase = new Device().getType();

        // ASSERT
        Assert.assertEquals(smallCase, "SmartPhone");
    }

    @Test
    public void deviceTypeSizeNormalTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;

        // ACT
        String normalCase = new Device().getType();

        // ASSERT
        Assert.assertEquals(normalCase, "SmartPhone");
    }

    @Test
    public void deviceTypeSizeLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;

        // ACT
        String largeCase = new Device().getType();

        // ASSERT
        Assert.assertEquals(largeCase, "Tablet");
    }

    @Test
    public void deviceTypeSizeXLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;

        // ACT
        String xlargeCase = new Device().getType();

        // ASSERT
        Assert.assertEquals(xlargeCase, "Tablet");

    }

}
