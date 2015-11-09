package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by dylan on 11/9/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AmbassadorApplicationModule.class})
public class AmbassadorSingletonTest {

    Context mockContext;
    AmbassadorApplicationComponent mockComponent = mock(AmbassadorApplicationComponent.class);
    AmbassadorApplicationModule mockModule = mock(AmbassadorApplicationModule.class);

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(com.ambassador.ambassadorsdk.AmbassadorSingletonTest ambassadorSingletonTest);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerAmbassadorSingletonTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);

        mockContext = mock(Context.class);

        PowerMockito.mock(AmbassadorApplicationModule.class);
    }

    @Test
    public void initTest() throws Exception {
        // ARRANGE
        AmbassadorSingleton.setComponent(mockComponent);
        AmbassadorSingleton ambassadorSingleton = AmbassadorSingleton.getInstance();
        PowerMockito.whenNew(AmbassadorApplicationModule.class).withAnyArguments().thenReturn(mockModule);

        // ACT
        ambassadorSingleton.init(mockContext);

        // ASSERT
        assertEquals(AmbassadorSingleton.get(), mockContext);
    }

}
