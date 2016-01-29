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
    private int width;
    private int height;

    @Before
    public void startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        this.width = device.getDisplayWidth();
        this.height = device.getDisplayWidth();

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
        UiObject usernameField = device.findObject(new UiSelector().description("loginUsernameField").className("android.widget.EditText"));
        UiObject passwordField = device.findObject(new UiSelector().description("loginPasswordField").className("android.widget.EditText"));
        UiObject loginButton = device.findObject(new UiSelector().description("loginButton").className("android.widget.Button"));

        // ACT
        usernameField.click();
        usernameField.setText("jake@getambassador.com");

        passwordField.click();
        passwordField.setText("password");

        loginButton.click();

        // ASSERT
    }

    @Test
    public void loginEmptyInputsFailsTest() throws Exception {
        // ARRANGE
        UiObject loginButton = device.findObject(new UiSelector().description("loginButton").className("android.widget.Button"));

        // ACT
        loginButton.click();

        // ASSERT
    }

    @Test
    public void signupFilledInputsDoesConversionTest() throws Exception {

    }

    @Test
    public void signupEmptyInputsFailsTest() throws Exception {

    }

    @Test
    public void buyNowAuthenticatedDoesConversionTest() throws Exception {

    }

    @Test
    public void buyNowUnauthenticatedFailsCancelledTest() throws Exception {

    }

    @Test
    public void rafIdentifiedSucceedsTest() throws Exception {

    }

    @Test
    public void rafUnidentifiedFailsTest() throws Exception {

    }

    @Test
    public void rafCampaignIdChangeTest() throws Exception {

    }

    @Test
    public void rafsAreStyledDifferentlyTest() throws Exception {

    }

    @Test
    public void pagesDoSwipeTest() throws Exception {
        // ARRANGE
        UiObject loginFragment = getUi("loginFragment");
        UiObject singupFragment = getUi("signupFragment");
        UiObject storeFragment = getUi("storeFragment");
        UiObject referFragment = getUi("referFragment");

        // ACT & ASSERT
        Assert.assertTrue(loginFragment.exists());
        swipeToRightPage();

        Assert.assertTrue(singupFragment.exists());
        swipeToRightPage();

        Assert.assertTrue(storeFragment.exists());
        swipeToRightPage();

        Assert.assertTrue(referFragment.exists());
        swipeToLeftPage();

        Assert.assertTrue(storeFragment.exists());
        swipeToLeftPage();

        Assert.assertTrue(singupFragment.exists());
        swipeToLeftPage();

        Assert.assertTrue(loginFragment.exists());
    }

    @Test
    public void keyboardClosesAndFocusResetsOnPageChangeTest() throws Exception {

    }

    private UiObject getUi(String contentDescription) {
        return device.findObject(new UiSelector().description(contentDescription));
    }

    private void swipeToRightPage() {
        device.swipe(width - 100, height / 2, 100, height / 2, 10);
    }

    private void swipeToLeftPage() {
        device.swipe(100, height / 2, width - 100, height / 2, 10);
    }

}
