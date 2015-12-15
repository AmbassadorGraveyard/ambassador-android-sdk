package com.ambassador.ambassadorsdk;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.internal.AmbassadorApplicationModule;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

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
import static org.mockito.Mockito.mock;


/**
 * Created by dylan on 11/9/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AmbassadorApplicationModule.class})
public class AmbassadorSingletonTest {

    Context mockContext;
    AmbassadorApplicationComponent mockComponent;
    AmbassadorApplicationModule mockModule;

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
        mockComponent = mock(AmbassadorApplicationComponent.class);
        mockModule = mock(AmbassadorApplicationModule.class);

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
