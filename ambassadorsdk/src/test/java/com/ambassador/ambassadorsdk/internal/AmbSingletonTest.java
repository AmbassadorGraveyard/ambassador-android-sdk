package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.injection.AmbModule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import dagger.ObjectGraph;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbSingleton.class,
        AmbModule.class,
        ObjectGraph.class,
})
public class AmbSingletonTest {

    private Context context;
    private AmbModule module;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                ObjectGraph.class
        );

        PowerMockito.spy(AmbSingleton.class);

        context = Mockito.mock(Context.class);
        module = Mockito.mock(AmbModule.class);

        Mockito.when(context.getApplicationContext()).thenReturn(context);
    }

    @Test
    public void initTest() throws Exception {
        // ARRANGE
        AmbSingleton.module = Mockito.mock(AmbModule.class);
        ObjectGraph objectGraph = Mockito.mock(ObjectGraph.class);
        PowerMockito.doReturn(objectGraph).when(ObjectGraph.class, "create", Mockito.eq(AmbSingleton.module));

        // ACT
        AmbSingleton.init(context);

        // ASSERT
        Assert.assertNotNull(AmbSingleton.context);
        Assert.assertNotNull(AmbSingleton.module);
        Assert.assertNotNull(AmbSingleton.graph);
    }

    @Test
    public void isValidTestAllTrue() {
        // ARRANGE
        AmbSingleton.context = Mockito.mock(Context.class);
        AmbSingleton.module = Mockito.mock(AmbModule.class);
        AmbSingleton.graph = Mockito.mock(ObjectGraph.class);

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertTrue(check);
    }

    @Test
    public void isValidTestLeftTrue() {
        // ARRANGE
        AmbSingleton.context = Mockito.mock(Context.class);
        AmbSingleton.module = null;
        AmbSingleton.graph = null;

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTestMiddleTrue() {
        // ARRANGE
        AmbSingleton.context = null;
        AmbSingleton.module = Mockito.mock(AmbModule.class);
        AmbSingleton.graph = null;

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTestRightTrue() {
        // ARRANGE
        AmbSingleton.context = null;
        AmbSingleton.module = null;
        AmbSingleton.graph = Mockito.mock(ObjectGraph.class);

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void isValidTestNoneTrue() {
        // ARRANGE
        AmbSingleton.context = null;
        AmbSingleton.module = null;
        AmbSingleton.graph = null;

        // ACT
        boolean check = AmbSingleton.isValid();

        // ASSERT
        Assert.assertFalse(check);
    }

    @Test
    public void injectTest() {
        // ARRANGE
        AmbSingleton.graph = Mockito.mock(ObjectGraph.class);
        Object object = Mockito.mock(Object.class);

        // ACT
        AmbSingleton.inject(object);

        // ASSERT
        Mockito.verify(AmbSingleton.graph).inject(object);
    }

}
