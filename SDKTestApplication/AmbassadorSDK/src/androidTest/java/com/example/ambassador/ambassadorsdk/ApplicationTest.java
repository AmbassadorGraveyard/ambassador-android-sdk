package com.example.ambassador.ambassadorsdk;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

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

        //set Campaign ID
        AmbassadorSingleton.getInstance().setCampaignID("260");

        //save pusher data so we don't sit and wait for any unnecessary async tests to come back
        String pusher = "{\"email\":\"jake@getambassador.com\",\"firstName\":\"erer\",\"lastName\":\"ere\",\"phoneNumber\":\"null\",\"urls\":[{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"},]}";
        AmbassadorSingleton.getInstance().savePusherInfo(pusher);

        Intent intent = new Intent();
        intent.putExtra("test", parameters);
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.rlMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(withText(parameters.welcomeTitle)));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(withText(parameters.welcomeDescription)));
        onView(withId(R.id.etShortURL)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(withText("http://staging.mbsy.co/jHjl")));
        onView(withId(R.id.btnCopyPaste)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));
    }
}
