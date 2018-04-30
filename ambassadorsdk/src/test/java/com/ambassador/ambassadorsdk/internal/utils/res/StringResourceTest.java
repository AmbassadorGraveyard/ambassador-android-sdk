package com.ambassador.ambassadorsdk.internal.utils.res;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

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
    AmbSingleton.class
})
public class StringResourceTest {

    private Context context;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class
        );

        context = Mockito.mock(Context.class);
        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(context);
    }

    @Test
    public void constructorTest() {
        // ARRANGE
        String value = "value";
        Mockito.when(context.getString(Mockito.anyInt())).thenReturn(value);

        // ACT
        String ret1 = new StringResource(5).toString();
        String ret2 = new StringResource(6).getValue();

        // ASSERT
        Assert.assertEquals(value, ret1);
        Assert.assertEquals(value, ret2);
    }

}
