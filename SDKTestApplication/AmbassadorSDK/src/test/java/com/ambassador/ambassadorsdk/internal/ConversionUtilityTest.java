package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.inject.Singleton;

import dagger.Component;

import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ConversionUtility.class
})
public class ConversionUtilityTest {

    Context mockContext;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(ConversionUtilityTest conversionUtilityTest);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        mockContext = mock(Context.class);

        TestComponent component = DaggerConversionUtilityTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);
    }

    @Test
    public void registerConversionTest() {

    }

    @Test
    public void createJSONConversionTest() {

    }

    @Test
    public void readAndSaveDatabaseEntriesTest() {

    }

    @Test
    public void makeConversionRequestTest() {

    }

}
