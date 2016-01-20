package com.ambassador.ambassadorsdk.internal.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({

})
public class DeviceTest {

    Context context;
    Resources resources;
    Configuration configuration;

    @Before
    public void setUp() {
        context = Mockito.mock(Context.class);
        resources = Mockito.mock(Resources.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        configuration = Mockito.mock(Configuration.class);
        Mockito.when(resources.getConfiguration()).thenReturn(configuration);
    }

    @Test
    public void isTabletSizeSmallTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;

        // ACT
        boolean smallCase = new Device(context).isTablet();

        // ASSERT
        Assert.assertFalse(smallCase);
    }

    @Test
    public void isTabletSizeNormalTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;

        // ACT
        boolean normalCase = new Device(context).isTablet();

        // ASSERT
        Assert.assertFalse(normalCase);
    }

    @Test
    public void isTabletSizeLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;

        // ACT
        boolean largeCase = new Device(context).isTablet();

        // ASSERT
        Assert.assertTrue(largeCase);
    }

    @Test
    public void isTabletSizeXLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;

        // ACT
        boolean xlargeCase = new Device(context).isTablet();

        // ASSERT
        Assert.assertTrue(xlargeCase);

    }

    @Test
    public void deviceTypeSizeSmallTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;

        // ACT
        String smallCase = new Device(context).getType();

        // ASSERT
        Assert.assertEquals(smallCase, "SmartPhone");
    }

    @Test
    public void deviceTypeSizeNormalTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;

        // ACT
        String normalCase = new Device(context).getType();

        // ASSERT
        Assert.assertEquals(normalCase, "SmartPhone");
    }

    @Test
    public void deviceTypeSizeLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;

        // ACT
        String largeCase = new Device(context).getType();

        // ASSERT
        Assert.assertEquals(largeCase, "Tablet");
    }

    @Test
    public void deviceTypeSizeXLargeTest() {
        // ARRANGE
        configuration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;

        // ACT
        String xlargeCase = new Device(context).getType();

        // ASSERT
        Assert.assertEquals(xlargeCase, "Tablet");

    }

}
