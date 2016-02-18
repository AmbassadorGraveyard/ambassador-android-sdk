package com.ambassador.ambassadorsdk.internal.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

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
        AmbSingleton.class,
        Typeface.class
})
public class FontTest {

    private AssetManager assets;
    private Typeface defaultTypeface;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                Typeface.class
        );

        Context context = Mockito.mock(Context.class);
        Mockito.when(AmbSingleton.getContext()).thenReturn(context);

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
        PowerMockito.doThrow(new RuntimeException()).when(Typeface.class, "createFromAsset", Mockito.eq(assets), Mockito.anyString());

        // ACT
        Font font = new Font("path");

        // ASSERT
        Assert.assertNotNull(font.getTypeface());
        Assert.assertEquals(defaultTypeface, font.getTypeface());
    }

    @Test
    public void fontLightTest() {
        // ARRANGE
        Typeface typeface = Mockito.mock(Typeface.class);
        Mockito.when(Typeface.create(Mockito.eq("sans-serif-light"), Mockito.eq(Typeface.NORMAL))).thenReturn(typeface);

        // ACT
        Font font = new Font("sans-serif-light");

        // ASSERT
        Assert.assertNotNull(font.getTypeface());
        Assert.assertEquals(typeface, font.getTypeface());
    }

}
