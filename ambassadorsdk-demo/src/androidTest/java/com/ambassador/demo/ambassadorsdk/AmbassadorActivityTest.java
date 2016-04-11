package com.ambassador.demo.ambassadorsdk;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
import com.ambassador.demo.R;
import com.ambassador.demo.TestModule;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AmbassadorActivityTest {

    @Rule public ActivityTestRule<AmbassadorActivity> activityTestRule = new ActivityTestRule<>(AmbassadorActivity.class, true, false);

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
    public void testsIdentifyDialogOpensWithNoEmail() throws Exception {
        // Set sdk stored email to null.
        AmbassadorSDK.identify(null);

        // Launch AmbassadorActivity.
        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);

        // Verify AskEmailDialog presents.
        onView(withId(R.id.dialog_get_email)).check(matches(isDisplayed()));
    }

    @Test
    public void testsIdentifyDialogIsNotDismissedOnTouchOutside() throws Exception {

    }

    @Test
    public void testsIdentifyDialogBackButtonDoesCloseActivity() throws Exception {
        // Set sdk stored email to null.
        AmbassadorSDK.identify(null);

        // Launch AmbassadorActivity.
        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);

        // Press back and check that activity closes.
        boolean intendedException = false;
        try {
            Espresso.pressBack();
        } catch (NoActivityResumedException e) {
            intendedException = true;
        }

        Assert.assertTrue(intendedException);
    }

}
