package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorApplicationModule.class
})
public class AmbassadorSingletonTest {

    Context context;
    AmbassadorApplicationComponent component;
    AmbassadorApplicationModule module;

    @Before
    public void setUp() {
        context = mock(Context.class);
        component = mock(AmbassadorApplicationComponent.class);
        module = mock(AmbassadorApplicationModule.class);
    }

    @Test
    public void initTest() {
        // ARRANGE
        AmbassadorSingleton ambassadorSingleton = Mockito.spy(AmbassadorSingleton.getInstance());

        // ACT
        ambassadorSingleton.init(context);

        // ASSERT
        Mockito.verify(ambassadorSingleton).setContext(Mockito.eq(context));
        Mockito.verify(ambassadorSingleton).setComponent(Mockito.any(AmbassadorApplicationComponent.class));
    }

    @Test
    public void getInstanceTest() {
        // ACT
        AmbassadorSingleton singleton1 = AmbassadorSingleton.getInstance();
        AmbassadorSingleton singleton2 = AmbassadorSingleton.getInstance();
        AmbassadorSingleton singleton3 = AmbassadorSingleton.getInstance();

        // ASSERT
        Assert.assertEquals(singleton1, singleton2);
        Assert.assertEquals(singleton2, singleton3);
    }

    @Test
    public void isValidFalseTest() {
        // ARRANGE
        PowerMockito.spy(AmbassadorSingleton.class);
        AmbassadorSingleton.setInstanceComponent(null);
        AmbassadorSingleton.setInstanceAmbModule(null);

        // ACT
        boolean check = AmbassadorSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTrueTest() {
        // ARRANGE
        PowerMockito.spy(AmbassadorSingleton.class);
        AmbassadorApplicationComponent component = Mockito.spy(AmbassadorApplicationComponent.class);
        AmbassadorSingleton.setInstanceComponent(component);
        AmbassadorApplicationModule module = Mockito.spy(AmbassadorApplicationModule.class);
        AmbassadorSingleton.setInstanceAmbModule(module);

        // ACT
        boolean check = AmbassadorSingleton.isValid();

        // ASSERT
        Assert.assertTrue(check);
    }

}
