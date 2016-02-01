package com.ambassador.demoapp;

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

    private UiObject loginTab;
    private UiObject signupTab;
    private UiObject storeTab;
    private UiObject referTab;
    private UiObject shortCodeEditText;

    @Before
    public void startMainActivityFromHomeScreen() {
        this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
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

        this.loginTab = getUi("loginTab");
        this.signupTab = getUi("signupTab");
        this.storeTab = getUi("storeTab");
        this.referTab = getUi("referTab");

        shortCodeEditText = device.findObject(new UiSelector().resourceId("com.ambassador.demoapp:id/etShortURL"));

        Demo.get().setEmail(null);
        Demo.get().setCampaignId(null);
    }

    @Test
    public void loginFilledInputsDoesIdentifyTest() throws Exception {
        // ARRANGE
        UiObject usernameField = getUi("loginUsernameField");
        UiObject passwordField = getUi("loginPasswordField");
        UiObject loginButton = getUi("loginButton");

        // ACT
        usernameField.click();
        usernameField.setText("jake@getambassador.com");

        passwordField.click();
        passwordField.setText("password");

        loginButton.click();

        // ASSERT
        Assert.assertEquals("jake@getambassador.com", Demo.get().getEmail());
    }

    @Test
    public void loginEmptyInputsFailsTest() throws Exception {
        // ARRANGE
        UiObject loginButton = getUi("loginButton");

        // ACT
        loginButton.click();

        // ASSERT
        Assert.assertNull(Demo.get().getEmail());
    }

    @Test
    public void signupFilledInputsDoesConversionTest() throws Exception {
        // ARRANGE
        UiObject emailField = getUi("signupEmailField");
        UiObject usernameField = getUi("signupUsernameField");
        UiObject passwordField = getUi("signupPasswordField");
        UiObject signupButton = getUi("signupButton");

        // ACT
        signupTab.click();

        emailField.click();
        emailField.setText("jake@getambassador.com");

        usernameField.click();
        usernameField.setText("jake");

        passwordField.click();
        passwordField.setText("password");

        signupButton.click();

        // ASSERT
        Assert.assertEquals(1, Demo.get().getConversions().size());
    }

    @Test
    public void signupEmptyInputsFailsTest() throws Exception {
        // ARRANGE
        UiObject signupButton = getUi("signupButton");

        // ACT
        signupTab.click();
        signupButton.click();

        // ASSERT
        Assert.assertEquals(0, Demo.get().getConversions().size());
    }

    @Test
    public void buyNowAuthenticatedDoesConversionTest() throws Exception {
        // ARRANGE
        UiObject storeFragment = getUi("storeFragment");
        UiObject buyButton = getUi("buyButton");
        UiObject alertTextView = device.findObject(new UiSelector().text("Purchase successful"));
        UiObject alertDoneButton = device.findObject(new UiSelector().text("Done"));

        // ACT
        Demo.get().identify("jake@getambassador.com");
        storeTab.click();
        buyButton.click();

        // ASSERT
        Assert.assertTrue(alertTextView.exists());

        // ACT
        alertDoneButton.click();

        // ASSERT
        Assert.assertTrue(storeFragment.exists());
        Assert.assertEquals(1, Demo.get().getConversions().size());
    }

    @Test
    public void buyNowUnauthenticatedFailsCancelledTest() throws Exception {
        // ARRANGE
        UiObject storeFragment = getUi("storeFragment");
        UiObject buyButton = getUi("buyButton");
        UiObject alertTextView = device.findObject(new UiSelector().text("Authentication needed"));
        UiObject alertCancelButton = device.findObject(new UiSelector().text("Cancel").resourceId("android:id/button2"));

        // ACT
        Demo.get().setEmail(null);
        storeTab.click();
        buyButton.click();

        // ASSERT
        Assert.assertTrue(alertTextView.exists());

        // ACT
        alertCancelButton.click();

        // ASSERT
        Assert.assertFalse(alertTextView.exists());
        Assert.assertTrue(storeFragment.exists());
        Assert.assertEquals(0, Demo.get().getConversions().size());
    }

    @Test
    public void buyNowUnauthenticatedFailsLoginTest() throws Exception {
        // ARRANGE
        UiObject storeFragment = getUi("storeFragment");
        UiObject loginFragment = getUi("loginFragment");
        UiObject buyButton = getUi("buyButton");
        UiObject alertTextView = device.findObject(new UiSelector().text("Authentication needed"));
        UiObject alertLoginButton = device.findObject(new UiSelector().text("Login").resourceId("android:id/button1"));

        // ACT
        Demo.get().setEmail(null);
        storeTab.click();
        buyButton.click();

        // ASSERT
        Assert.assertTrue(alertTextView.exists());

        // ACT
        alertLoginButton.click();

        // ASSERT
        Assert.assertFalse(alertTextView.exists());
        Assert.assertFalse(storeFragment.exists());
        Assert.assertTrue(loginFragment.exists());
        Assert.assertEquals(0, Demo.get().getConversions().size());
    }

    @Test
    public void rafIdentifiedSucceedsTest() throws Exception {
        // ARRANGE
        UiObject shoeRaf = getUi("shoeRaf");

        // ACT
        Demo.get().identify("jake@getambassador.com");
        referTab.click();
        shoeRaf.clickAndWaitForNewWindow();
        shortCodeEditText.waitForExists(5000);

        // ASSERT
        Assert.assertTrue(shortCodeEditText.exists());
    }

    @Test
    public void rafUnidentifiedFailsTest() throws Exception {
        // ARRANGE
        UiObject shoeRaf = getUi("shoeRaf");

        // ACT
        Demo.get().setEmail(null);
        referTab.click();
        shoeRaf.clickAndWaitForNewWindow();

        // ASSERT
        Assert.assertFalse(shortCodeEditText.exists());
    }

    @Test
    public void rafCampaignIdChangeTest() throws Exception {
        // ARRANGE
        UiObject campaignIdField = getUi("campaignIdField");
        UiObject shoeRaf = getUi("shoeRaf");

        // ACT
        Demo.get().identify("jake@getambassador.com");
        referTab.click();

        campaignIdField.click();
        campaignIdField.setText("260");
        shoeRaf.clickAndWaitForNewWindow();
        String url1 = shortCodeEditText.getText();
        device.pressBack();

        campaignIdField.click();
        campaignIdField.setText("999");
        shoeRaf.click();
        String url2 = shortCodeEditText.getText();
        device.pressBack();

        Assert.assertNotEquals(url1, url2);
        Assert.assertTrue(url1.endsWith("jzqC"));
        Assert.assertTrue(url2.endsWith("ljTq"));
    }

    @Test
    public void rafsAreStyledDifferentlyTest() throws Exception {
        // ARRANGE
        UiObject shoeRaf = getUi("shoeRaf");
        UiObject shirtRaf = getUi("shirtRaf");
        UiObject ambassadorRaf = getUi("ambassadorRaf");
        UiObject titleText = device.findObject(new UiSelector().resourceId("com.ambassador.demoapp:id/tvWelcomeTitle"));

        // ACT
        Demo.get().identify("jake@getambassador.com");
        referTab.click();

        shoeRaf.clickAndWaitForNewWindow();
        String title1 = titleText.getText();
        device.pressBack();

        shirtRaf.clickAndWaitForNewWindow();
        String title2 = titleText.getText();
        device.pressBack();

        ambassadorRaf.clickAndWaitForNewWindow();
        String title3 = titleText.getText();
        device.pressBack();

        // ASSERT
        Assert.assertNotEquals(title1, title2);
        Assert.assertNotEquals(title2, title3);
        Assert.assertNotEquals(title3, title1);
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
        // ARRANGE
        UiObject loginUsernameField = getUi("loginUsernameField");
        UiObject loginPasswordField = getUi("loginPasswordField");
        UiObject signupEmailField = getUi("signupEmailField");
        UiObject signupPasswordField = getUi("signupPasswordField");

        // ACT & ASSERT
       // Assert.assertFalse(keyboardIsOpen());

        Assert.assertTrue(loginUsernameField.isFocused());
        loginPasswordField.click();
       // Assert.assertTrue(keyboardIsOpen());
        loginPasswordField.setText("password");
        swipeToRightPage();

       // Assert.assertFalse(keyboardIsOpen());
        Assert.assertTrue(signupEmailField.isFocused());
        signupPasswordField.click();
        //Assert.assertTrue(keyboardIsOpen());
        signupPasswordField.setText("password");
        swipeToLeftPage();

        //Assert.assertFalse(keyboardIsOpen());
        Assert.assertTrue(loginUsernameField.isFocused());
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
