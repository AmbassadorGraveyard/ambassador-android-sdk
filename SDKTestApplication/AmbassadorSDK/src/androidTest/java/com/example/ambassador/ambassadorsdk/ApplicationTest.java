package com.example.ambassador.ambassadorsdk;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.ambassador.ambassadorsdk.AmbassadorActivity;
import com.example.ambassador.ambassadorsdk.RAFParameters;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplicationTest {

    @Rule
    public ActivityTestRule<AmbassadorActivity> mActivityRule = new ActivityTestRule(AmbassadorActivity.class, true, false);

    @Test
    public void demonstrateIntentPrep() {
        RAFParameters parameters = new RAFParameters();
        parameters.shareMessage = "Check out this company!";
        parameters.welcomeTitle = "RAF Params Welcome Title";
        parameters.welcomeDescription = "RAF Params Welcome Description";
        parameters.toolbarTitle = "RAF Params Toolbar Title";

        Intent intent = new Intent();
        intent.putExtra("test", parameters);
        mActivityRule.launchActivity(intent);
        //onView(withId(R.id.rlMainLayout)).check(matches(isDisplayed()));
    }
}
