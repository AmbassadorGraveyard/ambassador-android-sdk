package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.injection.AmbModule;

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
        AmbSingleton.class,
        AmbModule.class
})
public class AmbSingletonTest {

    private Context context;
    private AmbModule module;

    @Before
    public void setUp() {
        PowerMockito.spy(AmbSingleton.class);

        context = Mockito.mock(Context.class);
        module = Mockito.mock(AmbModule.class);

        Mockito.when(context.getApplicationContext()).thenReturn(context);
    }

    @Test
    public void initTest() {
        // ARRANGE
        AmbSingleton singleton = Mockito.spy(AmbSingleton.class);
        Mockito.when(AmbSingleton.getInstance()).thenReturn(singleton);
       // Mockito.when(AmbSingleton.buildAmbassadorApplicationModule()).thenReturn(module);

        // ACT
        AmbSingleton.init(context);

        // ASSERT
        Assert.assertEquals(AmbSingleton.getContext(), context);
        Assert.assertEquals(AmbSingleton.getModule(), module);
    }

    @Test
    public void isValidTestBothTrue() {
        // ARRANGE
        AmbSingleton singleton = Mockito.mock(AmbSingleton.class);
        Mockito.when(AmbSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(AmbSingleton.getModule()).thenReturn(module);

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertTrue(check);
    }

    @Test
    public void isValidTestLeftTrue() {
        // ARRANGE
        AmbSingleton singleton = Mockito.mock(AmbSingleton.class);
        Mockito.when(AmbSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(AmbSingleton.getModule()).thenReturn(null);

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTestRightTrue() {
        // ARRANGE
        AmbSingleton singleton = Mockito.mock(AmbSingleton.class);
        Mockito.when(AmbSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(AmbSingleton.getModule()).thenReturn(module);

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTestNoneTrue() {
        // ARRANGE
        AmbSingleton singleton = Mockito.mock(AmbSingleton.class);
        Mockito.when(AmbSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(AmbSingleton.getModule()).thenReturn(null);

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void getInstanceTest() {
        Assert.assertNotNull(AmbSingleton.getInstance());
        Assert.assertEquals(AmbSingleton.getInstance(), AmbSingleton.getInstance());
    }

}
