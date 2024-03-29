package com.ambassador.ambassadorsdk.internal.utils.res;

import android.content.Context;
import android.content.res.Resources;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
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
public class ColorResourceTest {

    private Resources resources;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class
        );

        Context context = Mockito.mock(Context.class);
        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(context);
        resources = Mockito.mock(Resources.class);
        Mockito.when(context.getResources()).thenReturn(resources);
    }

    @Ignore
    @Test
    public void constructorTest() {
        // ARRANGE
        Mockito.when(resources.getColor(Mockito.eq(5))).thenReturn(15);

        // ACT
        int color = new ColorResource(5).getColor();

        // ASSERT
        Assert.assertEquals(color, 15);
    }

}
