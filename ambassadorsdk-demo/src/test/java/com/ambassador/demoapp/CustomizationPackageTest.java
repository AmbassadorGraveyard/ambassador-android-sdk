package com.ambassador.demoapp;

import android.content.Context;

import com.ambassador.ambassadorsdk.RAFOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    CustomizationPackage.class
})
public class CustomizationPackageTest {

    protected Context context;
    protected CustomizationPackage customizationPackage;

    @Before
    public void setUp() {
        this.context = Mockito.mock(Context.class);
        this.customizationPackage = Mockito.spy(CustomizationPackage.class);
    }

    @Test
    public void addStringContentToFilesTest() {
        // ARRANGE
        String path = "path";
        String content = "content";

        // ACT
        CustomizationPackage result = customizationPackage.add(path, content, CustomizationPackage.Directory.FILES);

        // ASSERT

    }

    @Test
    public void addStringContentToAssetsTest() {
        // ARRANGE
        String path = "path";
        String content = "content";

        // ACT
        CustomizationPackage result = customizationPackage.add(path, content, CustomizationPackage.Directory.ASSETS);

        // ASSERT

    }

    @Test
    public void addRAFOptionsNullImageTest() {
        // ARRANGE
        String path = "path";
        RAFOptions rafOptions = null;

        // ACT
        CustomizationPackage result = customizationPackage.add(path, rafOptions);

        // ASSERT

    }

    @Test
    public void addRAFOptionsInvalidImageTest() {
        // ARRANGE
        String path = "path";
        RAFOptions rafOptions = null;

        // ACT
        CustomizationPackage result = customizationPackage.add(path, rafOptions);

        // ASSERT

    }

    @Test
    public void addRAFOptionsValidImageTest() {
        // ARRANGE
        String path = "path";
        RAFOptions rafOptions = null;

        // ACT
        CustomizationPackage result = customizationPackage.add(path, rafOptions);

        // ASSERT

    }

    @Test
    public void zipTest() {
        // ARRANGE

        // ACT
        String result = customizationPackage.zip();

        // ASSERT

    }

    @Test
    public void copyFromAssetsToInternalNoExistsTest() {
        // ARRANGE
        String assetsPath = "assetsPath";

        // ACT
        boolean result = customizationPackage.copyFromAssetsToInternal(assetsPath);

        // ASSERT

    }

    @Test
    public void copyFromAssetsToInternalExistsTest() {
        // ARRANGE
        String assetsPath = "assetsPath";

        // ACT
        boolean result = customizationPackage.copyFromAssetsToInternal(assetsPath);

        // ASSERT

    }

    @Test
    public void optionTranscriberTest() {
        // ARRANGE
        RAFOptions rafOptions = null;

        // ACT
        String result = new CustomizationPackage.OptionXmlTranscriber(rafOptions).transcribe();

        // ASSERT

    }

}
