package com.ambassador.app;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.app.activities.LaunchActivity;
import com.ambassador.app.activities.LoginActivity;
import com.ambassador.app.activities.MainActivity;
import com.ambassador.app.api.pojo.LoginResponse;
import com.ambassador.app.data.User;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LaunchActivityTest {

    @Rule public ActivityTestRule<LaunchActivity> activityTestRule = new ActivityTestRule<>(LaunchActivity.class, true, false);

    protected Context context;

    @Before
    public void beforeEachTest() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext().getApplicationContext();

        AmbSingleton.init(context, new TestModule());
        AmbassadorSDK.runWithKeys(context, "ut", "uid");
        AmbSingleton.inject(this);
    }

    @Test
    public void testsLoginActivityDoesLaunchWhenUserIsNotStored() throws Exception {
        // Arrange User data to be logged out.
        User.logout();

        // Launch the activity.
        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);

        // Sleep 500ms to compensate for activity change, and verify LoginActivity.
        Thread.sleep(500);
        final Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity[0] = Iterables.getOnlyElement(ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED));
            }
        });
        Assert.assertTrue(activity[0] instanceof LoginActivity);
    }

    @Test
    public void testsMainActivityDoesLaunchWhenUserIsStored() throws Exception {
        // Arrange User data to be logged in.
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.company = new LoginResponse.Company();
        loginResponse.company.universal_id = "universal_id";
        loginResponse.company.sdk_token = "sdk_token";
        User.get().load(loginResponse);

        // Launch the activity.
        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);

        // Sleep 500ms to compensate for activity change, and verify MainActivity.
        Thread.sleep(500);
        final Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity[0] = Iterables.getOnlyElement(ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED));
            }
        });
        Assert.assertTrue(activity[0] instanceof MainActivity);
    }

}
