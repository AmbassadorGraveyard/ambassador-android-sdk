package com.ambassador.demoapp;

import android.support.annotation.NonNull;
import android.support.test.uiautomator.UiDevice;

import java.io.File;

/**
 * Collection of static utilities useful for instrumentation testing.
 */
public class Utils {

    /**
     * Takes a screenshot of the device and saves it to /sdcard/test-screenshots.
     * @param device the UiDevice object to use to take the screenshot.
     * @param key a key-name to classify the screenshot.
     * @param description a description of what the screenshot is/should be.
     */
    public static void screenshot(@NonNull UiDevice device, @NonNull String key, @NonNull String description) {
        String path = "/sdcard/test-screenshots";
        String name =
                processStringForFilename(key)
                + "-"
                + processStringForFilename(description);
        device.takeScreenshot(new File(path, name));
    }

    /**
     * Removes spaces and converts to camel case.
     * @return processed Strings.
     */
    @NonNull
    protected static String processStringForFilename(@NonNull String string) {
        StringBuilder outputBuilder = new StringBuilder();
        String[] split = string.split(" ");
        outputBuilder.append(split[0]);
        for (int i = 1; i < split.length; i++) {
            String segment = split[i];
            outputBuilder.append(segment.toUpperCase());
        }

        return outputBuilder.toString();
    }

}
