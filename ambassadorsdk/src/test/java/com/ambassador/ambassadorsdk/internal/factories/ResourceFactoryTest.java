package com.ambassador.ambassadorsdk.internal.factories;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.ambassador.ambassadorsdk.R;
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

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbSingleton.class,
        ContextCompat.class
})
public class ResourceFactoryTest {

    private Context context;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                ContextCompat.class
        );

        context = Mockito.mock(Context.class);
        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(context);
    }

    @Test
    public void getColorTest() {
        // ARRANGE
        PowerMockito.when(ContextCompat.getColor(Mockito.eq(context), Mockito.eq(R.color.ambassador_blue))).thenReturn(15);

        // ACT
        int value = ResourceFactory.getColor(R.color.ambassador_blue).getColor();

        // ASSERT
        Assert.assertEquals(15, value);
    }

    @Test
    public void getStringTest() {
        // ARRANGE
        Mockito.when(context.getString(R.string.post_failure)).thenReturn("ok");

        // ACT
        String value = ResourceFactory.getString(R.string.post_failure).getValue();

        // ASSERT
        Assert.assertEquals("ok", value);
    }

}
