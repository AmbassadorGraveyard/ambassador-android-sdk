package com.ambassador.demo;

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
        Zipper.class,
        Zipper.OptionXmlTranscriber.class,
})
public class ZipperTest {

    protected Context context;
    protected Zipper zipper;

    @Before
    public void setUp() {
        this.context = Mockito.mock(Context.class);
        Zipper zipper = new Zipper(this.context);
        this.zipper = Mockito.spy(zipper);
    }

    @Test
    public void addStringContentToFilesTest() throws Exception {
        // ARRANGE
        String path = "path";
        String content = "content";
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);
        Mockito.doReturn(fileOutputStream).when(context).openFileOutput(Mockito.eq(path), Mockito.anyInt());

        // ACT
        Zipper result = zipper.add(path, content, Zipper.Directory.FILES);

        // ASSERT
        Assert.assertEquals(zipper, result);
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
        Zipper result = zipper.add(path, content, Zipper.Directory.ASSETS);

        // ASSERT
        Assert.assertEquals(zipper, result);
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
        Mockito.doReturn(zipper).when(zipper).add(Mockito.anyString(), Mockito.anyString(), Mockito.any(Zipper.Directory.class));
        Zipper.OptionXmlTranscriber transcriber = Mockito.mock(Zipper.OptionXmlTranscriber.class);
        PowerMockito.whenNew(Zipper.OptionXmlTranscriber.class).withAnyArguments().thenReturn(transcriber);

        // ACT
        Zipper result = zipper.add(path, rafOptions);

        // ASSERT
        Assert.assertEquals(zipper, result);
        Assert.assertEquals(0, zipper.assets.size());
        Assert.assertEquals(0, zipper.files.size());
        Mockito.verify(zipper, Mockito.never()).copyFromAssetsToInternal(Mockito.anyString());
    }

    @Test
    public void addRAFOptionsInvalidImageTest() throws Exception {
        // ARRANGE
        String path = "path";
        String image = "logo.png";
        RAFOptions rafOptions = Mockito.mock(RAFOptions.class);
        Mockito.doReturn(image).when(rafOptions).getLogo();
        Mockito.doReturn(zipper).when(zipper).add(Mockito.anyString(), Mockito.anyString(), Mockito.any(Zipper.Directory.class));
        Mockito.doReturn(false).when(zipper).copyFromAssetsToInternal(Mockito.eq(image));
        Zipper.OptionXmlTranscriber transcriber = Mockito.mock(Zipper.OptionXmlTranscriber.class);
        PowerMockito.whenNew(Zipper.OptionXmlTranscriber.class).withAnyArguments().thenReturn(transcriber);

        // ACT
        Zipper result = zipper.add(path, rafOptions);

        // ASSERT
        Assert.assertEquals(zipper, result);
        Assert.assertEquals(0, zipper.assets.size());
        Assert.assertEquals(0, zipper.files.size());
        Mockito.verify(zipper).copyFromAssetsToInternal(Mockito.eq(image));
    }

    @Test
    public void addRAFOptionsValidImageTest() throws Exception {
        // ARRANGE
        String path = "path";
        String image = "logo.png";
        RAFOptions rafOptions = Mockito.mock(RAFOptions.class);
        Mockito.doReturn(image).when(rafOptions).getLogo();
        Mockito.doReturn(zipper).when(zipper).add(Mockito.anyString(), Mockito.anyString(), Mockito.any(Zipper.Directory.class));
        Mockito.doReturn(true).when(zipper).copyFromAssetsToInternal(Mockito.eq(image));
        Zipper.OptionXmlTranscriber transcriber = Mockito.mock(Zipper.OptionXmlTranscriber.class);
        PowerMockito.whenNew(Zipper.OptionXmlTranscriber.class).withAnyArguments().thenReturn(transcriber);

        // ACT
        Zipper result = zipper.add(path, rafOptions);

        // ASSERT
        Assert.assertEquals(zipper, result);
        Assert.assertEquals("logo.png", zipper.assets.get(0));
        Assert.assertEquals(0, zipper.files.size());
        Mockito.verify(zipper).copyFromAssetsToInternal(Mockito.eq(image));
    }

    @Test
    public void copyFromAssetsToInternalNoExistsTest() throws Exception {
        // ARRANGE
        String assetsPath = "assetsPath";
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.doReturn(assetManager).when(context).getAssets();
        Mockito.doThrow(new FileNotFoundException()).when(assetManager).open(Mockito.eq(assetsPath));

        // ACT
        boolean result = zipper.copyFromAssetsToInternal(assetsPath);

        // ASSERT
        Assert.assertFalse(result);
    }

}
