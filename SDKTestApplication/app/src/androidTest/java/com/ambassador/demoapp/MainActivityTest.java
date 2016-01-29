package com.ambasasdor.demoapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import com.ambassador.demoapp.MainActivity;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class MainActivityTest {

    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String PACKAGE_NAME = "com.ambassador.demoapp";

    private UiDevice device;

    @Before
    public void startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressHome();

        String launcherPackage = device.getLauncherPackageName();
        Assert.assertThat(launcherPackage, Matchers.notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        Context context = InstrumentationRegistry.getContext();
        Intent intent  = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void loginFilledInputsDoesIdentifyTest() throws Exception {
        // ARRANGE
        UiObject usernameField = device.findObject(new UiSelector().description("usernameField").className("android.widget.EditText"));
        UiObject passwordField = device.findObject(new UiSelector().description("passwordField").className("android.widget.EditText"));
        UiObject loginButton = device.findObject(new UiSelector().description("loginButton").className("android.widget.Button"));

        // ACT
        usernameField.click();
        usernameField.setText("jake@getambassador.com");

        passwordField.click();
        passwordField.setText("password");

        loginButton.click();

        // ASSERT
    }

}
