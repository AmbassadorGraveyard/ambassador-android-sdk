package com.ambassador.ambassadorsdk.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        Typeface.class
})
public class FontTest {

    AssetManager assets;
    Typeface defaultTypeface;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class,
                Typeface.class
        );

        Context context = Mockito.mock(Context.class);
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);

        assets = Mockito.mock(AssetManager.class);
        Mockito.doReturn(assets).when(context).getAssets();

        defaultTypeface = Mockito.mock(Typeface.class);
        Whitebox.setInternalState(Typeface.class, "DEFAULT", defaultTypeface);
    }

    @Test
    public void constructorGoodTest() throws Exception {
        // ARRANGE
        Typeface typeface = Mockito.mock(Typeface.class);
        PowerMockito.doReturn(typeface).when(Typeface.class, "createFromAsset", Mockito.eq(assets), Mockito.anyString());

        // ACT
        Font font = new Font("path");

        // ASSERT
        Assert.assertEquals(typeface, font.getTypeface());
    }

    @Test
    public void constructorBadTest() throws Exception {
        // ARRANGE
        PowerMockito.doReturn(null).when(Typeface.class, "createFromAsset", Mockito.eq(assets), Mockito.anyString());

        // ACT
        Font font = new Font("path");

        // ASSERT
        Assert.assertNotNull(font.getTypeface());
        Assert.assertEquals(defaultTypeface, font.getTypeface());
    }

}
