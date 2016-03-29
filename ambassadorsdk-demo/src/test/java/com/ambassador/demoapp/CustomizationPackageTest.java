package com.ambassador.demoapp;

import android.content.Context;
import android.content.res.AssetManager;

import com.ambassador.ambassadorsdk.RAFOptions;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        CustomizationPackage.class,
        CustomizationPackage.OptionXmlTranscriber.class,
})
public class CustomizationPackageTest {

    protected Context context;
    protected CustomizationPackage customizationPackage;

    @Before
    public void setUp() {
        this.context = Mockito.mock(Context.class);
        CustomizationPackage customizationPackage = new CustomizationPackage(this.context);
        this.customizationPackage = Mockito.spy(customizationPackage);
    }

    @Test
    public void addStringContentToFilesTest() throws Exception {
        // ARRANGE
        String path = "path";
        String content = "content";
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);
        Mockito.doReturn(fileOutputStream).when(context).openFileOutput(Mockito.eq(path), Mockito.anyInt());

        // ACT
        CustomizationPackage result = customizationPackage.add(path, content, CustomizationPackage.Directory.FILES);

        // ASSERT
        Assert.assertEquals(customizationPackage, result);
        Assert.assertEquals("path", result.files.get(0));
        Assert.assertEquals(0, result.assets.size());
        Mockito.verify(fileOutputStream).write(content.getBytes());
        Mockito.verify(fileOutputStream).close();
    }

    @Test
    public void addStringContentToAssetsTest() throws Exception {
        // ARRANGE
        String path = "path";
        String content = "content";
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);
        Mockito.doReturn(fileOutputStream).when(context).openFileOutput(Mockito.eq(path), Mockito.anyInt());

        // ACT
        CustomizationPackage result = customizationPackage.add(path, content, CustomizationPackage.Directory.ASSETS);

        // ASSERT
        Assert.assertEquals(customizationPackage, result);
        Assert.assertEquals("path", result.assets.get(0));
        Assert.assertEquals(0, result.files.size());
        Mockito.verify(fileOutputStream).write(content.getBytes());
        Mockito.verify(fileOutputStream).close();
    }

    @Test
    public void addRAFOptionsNullImageTest() throws Exception {
        // ARRANGE
        String path = "path";
        RAFOptions rafOptions = Mockito.mock(RAFOptions.class);
        Mockito.doReturn(null).when(rafOptions).getLogo();
        Mockito.doReturn(customizationPackage).when(customizationPackage).add(Mockito.anyString(), Mockito.anyString(), Mockito.any(CustomizationPackage.Directory.class));
        CustomizationPackage.OptionXmlTranscriber transcriber = Mockito.mock(CustomizationPackage.OptionXmlTranscriber.class);
        PowerMockito.whenNew(CustomizationPackage.OptionXmlTranscriber.class).withAnyArguments().thenReturn(transcriber);

        // ACT
        CustomizationPackage result = customizationPackage.add(path, rafOptions);

        // ASSERT
        Assert.assertEquals(customizationPackage, result);
        Assert.assertEquals(0, customizationPackage.assets.size());
        Assert.assertEquals(0, customizationPackage.files.size());
        Mockito.verify(customizationPackage, Mockito.never()).copyFromAssetsToInternal(Mockito.anyString());
    }

    @Test
    public void addRAFOptionsInvalidImageTest() throws Exception {
        // ARRANGE
        String path = "path";
        String image = "logo.png";
        RAFOptions rafOptions = Mockito.mock(RAFOptions.class);
        Mockito.doReturn(image).when(rafOptions).getLogo();
        Mockito.doReturn(customizationPackage).when(customizationPackage).add(Mockito.anyString(), Mockito.anyString(), Mockito.any(CustomizationPackage.Directory.class));
        Mockito.doReturn(false).when(customizationPackage).copyFromAssetsToInternal(Mockito.eq(image));
        CustomizationPackage.OptionXmlTranscriber transcriber = Mockito.mock(CustomizationPackage.OptionXmlTranscriber.class);
        PowerMockito.whenNew(CustomizationPackage.OptionXmlTranscriber.class).withAnyArguments().thenReturn(transcriber);

        // ACT
        CustomizationPackage result = customizationPackage.add(path, rafOptions);

        // ASSERT
        Assert.assertEquals(customizationPackage, result);
        Assert.assertEquals(0, customizationPackage.assets.size());
        Assert.assertEquals(0, customizationPackage.files.size());
        Mockito.verify(customizationPackage).copyFromAssetsToInternal(Mockito.eq(image));
    }

    @Test
    public void addRAFOptionsValidImageTest() throws Exception {
        // ARRANGE
        String path = "path";
        String image = "logo.png";
        RAFOptions rafOptions = Mockito.mock(RAFOptions.class);
        Mockito.doReturn(image).when(rafOptions).getLogo();
        Mockito.doReturn(customizationPackage).when(customizationPackage).add(Mockito.anyString(), Mockito.anyString(), Mockito.any(CustomizationPackage.Directory.class));
        Mockito.doReturn(true).when(customizationPackage).copyFromAssetsToInternal(Mockito.eq(image));
        CustomizationPackage.OptionXmlTranscriber transcriber = Mockito.mock(CustomizationPackage.OptionXmlTranscriber.class);
        PowerMockito.whenNew(CustomizationPackage.OptionXmlTranscriber.class).withAnyArguments().thenReturn(transcriber);

        // ACT
        CustomizationPackage result = customizationPackage.add(path, rafOptions);

        // ASSERT
        Assert.assertEquals(customizationPackage, result);
        Assert.assertEquals("logo.png", customizationPackage.assets.get(0));
        Assert.assertEquals(0, customizationPackage.files.size());
        Mockito.verify(customizationPackage).copyFromAssetsToInternal(Mockito.eq(image));
    }

    @Test
    public void copyFromAssetsToInternalNoExistsTest() throws Exception {
        // ARRANGE
        String assetsPath = "assetsPath";
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.doReturn(assetManager).when(context).getAssets();
        Mockito.doThrow(new FileNotFoundException()).when(assetManager).open(Mockito.eq(assetsPath));

        // ACT
        boolean result = customizationPackage.copyFromAssetsToInternal(assetsPath);

        // ASSERT
        Assert.assertFalse(result);
    }

}
