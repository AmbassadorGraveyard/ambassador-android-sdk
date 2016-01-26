package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

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
        AmbassadorSingleton.class,
        AmbassadorApplicationModule.class
})
public class AmbassadorSingletonTest {

    Context context;
    AmbassadorApplicationComponent component;
    AmbassadorApplicationModule module;

    @Before
    public void setUp() {
        PowerMockito.spy(AmbassadorSingleton.class);

        context = Mockito.mock(Context.class);
        component = Mockito.mock(AmbassadorApplicationComponent.class);
        module = Mockito.mock(AmbassadorApplicationModule.class);

        Mockito.when(context.getApplicationContext()).thenReturn(context);
    }

    @Test
    public void initTest() {
        // ARRANGE
        AmbassadorSingleton singleton = Mockito.spy(AmbassadorSingleton.class);
        Mockito.when(AmbassadorSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(AmbassadorSingleton.buildAmbassadorApplicationModule()).thenReturn(module);

        // ACT
        AmbassadorSingleton.init(context);

        // ASSERT
        Assert.assertEquals(AmbassadorSingleton.getInstanceContext(), context);
        Assert.assertEquals(AmbassadorSingleton.getInstanceAmbModule(), module);
    }

    @Test
    public void isValidTestBothTrue() {
        // ARRANGE
        AmbassadorSingleton singleton = Mockito.mock(AmbassadorSingleton.class);
        Mockito.when(AmbassadorSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(singleton.getComponent()).thenReturn(component);
        Mockito.when(singleton.getAmbModule()).thenReturn(module);

        // ACT
        boolean check = AmbassadorSingleton.isValid();

        // ASSERT
        Assert.assertTrue(check);
    }

    @Test
    public void isValidTestLeftTrue() {
        // ARRANGE
        AmbassadorSingleton singleton = Mockito.mock(AmbassadorSingleton.class);
        Mockito.when(AmbassadorSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(singleton.getComponent()).thenReturn(component);
        Mockito.when(singleton.getAmbModule()).thenReturn(null);

        // ACT
        boolean check = AmbassadorSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTestRightTrue() {
        // ARRANGE
        AmbassadorSingleton singleton = Mockito.mock(AmbassadorSingleton.class);
        Mockito.when(AmbassadorSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(singleton.getComponent()).thenReturn(null);
        Mockito.when(singleton.getAmbModule()).thenReturn(module);

        // ACT
        boolean check = AmbassadorSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTestNoneTrue() {
        // ARRANGE
        AmbassadorSingleton singleton = Mockito.mock(AmbassadorSingleton.class);
        Mockito.when(AmbassadorSingleton.getInstance()).thenReturn(singleton);
        Mockito.when(singleton.getComponent()).thenReturn(null);
        Mockito.when(singleton.getAmbModule()).thenReturn(null);

        // ACT
        boolean check = AmbassadorSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void getInstanceTest() {
        Assert.assertNotNull(AmbassadorSingleton.getInstance());
        Assert.assertEquals(AmbassadorSingleton.getInstance(), AmbassadorSingleton.getInstance());
    }

}
