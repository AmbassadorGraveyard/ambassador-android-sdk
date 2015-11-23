package com.ambassador.ambassadorsdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;


/**
 * Created by JakeDunahee on 9/11/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AmbassadorSingleton.class, Log.class})
public class UtilitiesTest {

    Context mockContext;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(UtilitiesTest utilitiesTest);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        PowerMockito.mockStatic(Utilities.class);
        PowerMockito.mockStatic(AmbassadorSingleton.class);

        mockContext = mock(Context.class);

        TestComponent component = DaggerUtilitiesTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);
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
        PowerMockito.when(AmbassadorSingleton.get()).thenReturn(mockContext);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getDimensionPixelSize(anyInt())).thenReturn(expected);

        // ACT
        int ret = Utilities.getPixelSizeForDimension(parameter);

        // ASSERT
        assertEquals(expected, ret);
    }

    @Test
    public void getDpSizeForPixelsTest() {
        //ARRANGE
        int parameter = 200;
        Resources mockResources = mock(Resources.class);
        PowerMockito.when(AmbassadorSingleton.get()).thenReturn(mockContext);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getDisplayMetrics().densityDpi).thenReturn(320);

        //ACT
        float ret = Utilities.getDpSizeForPixels(parameter);

        //ASSERT
        assertEquals(100, ret);
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
        //boolean nullResult = Utilities.containsURL(nullCase, url);

        // ASSERT
        assertTrue(successResult);
        assertFalse(failResult);
        //assertFalse(nullResult);
    }

    @Test
    public void isTabletTest() {
        // ARRANGE
        Resources mockResources = mock(Resources.class);
        Configuration mockConfiguration = mock(Configuration.class);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getConfiguration()).thenReturn(mockConfiguration);

        // ACT
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;
        boolean smallCase = Utilities.isTablet(mockContext);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;
        boolean normalCase = Utilities.isTablet(mockContext);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
        boolean largeCase = Utilities.isTablet(mockContext);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;
        boolean xlargeCase = Utilities.isTablet(mockContext);

        // ASSERT
        assertFalse(smallCase);
        assertFalse(normalCase);
        assertTrue(largeCase);
        assertTrue(xlargeCase);
    }

    @Test
    public void deviceTypeTest() {
        // ARRANGE
        Resources mockResources = mock(Resources.class);
        Configuration mockConfiguration = mock(Configuration.class);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getConfiguration()).thenReturn(mockConfiguration);

        // ACT
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL;
        String smallCase = Utilities.deviceType(mockContext);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;
        String normalCase = Utilities.deviceType(mockContext);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
        String largeCase = Utilities.deviceType(mockContext);
        mockConfiguration.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;
        String xlargeCase = Utilities.deviceType(mockContext);

        // ASSERT
        assertEquals(smallCase, "SmartPhone");
        assertEquals(normalCase, "SmartPhone");
        assertEquals(largeCase, "Tablet");
        assertEquals(xlargeCase, "Tablet");
    }

    @Test
    public void presentUrlDialogTest() throws Exception {

    }

    @Test
    public void debugLogTest() throws Exception {

    }

    @Test
    public void getScreenDensityTest() {
        // ARRANGE
        Resources mockResources = mock(Resources.class);
        DisplayMetrics mockDisplayMetrics = mock(DisplayMetrics.class);
        float density = 0.5f;
        mockDisplayMetrics.density = density;
        PowerMockito.when(AmbassadorSingleton.get()).thenReturn(mockContext);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getDisplayMetrics()).thenReturn(mockDisplayMetrics);

        // ACT
        float ret = Utilities.getScreenDensity();

        // ASSERT
        assertEquals(density, ret);
    }

    @Test
    public void setStatusBarTest() throws Exception {

    }
}
