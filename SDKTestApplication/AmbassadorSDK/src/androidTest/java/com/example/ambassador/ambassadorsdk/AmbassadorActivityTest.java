package com.example.ambassador.ambassadorsdk;

import android.content.Intent;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class AmbassadorActivityTest {

    @Rule
    public ActivityTestRule<AmbassadorActivity> mActivityTestIntentRule = new ActivityTestRule<>(AmbassadorActivity.class, true, false);
    public IntentsTestRule<AmbassadorActivity> mActivityRule = new IntentsTestRule<>(AmbassadorActivity.class);

    @Test
    public void testActivity() {
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
        mActivityTestIntentRule.launchActivity(intent);

        onView(withId(R.id.rlMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(withText(parameters.welcomeTitle)));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(withText(parameters.welcomeDescription)));
        onView(withId(R.id.etShortURL)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(withText("http://staging.mbsy.co/jHjl")));
        onView(withId(R.id.btnCopyPaste)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));


        //tap facebook share
        //onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(0).perform(click());

        //set a token so we can test share link - this is for test embassy twitter account (developers@getambassador.com)
        AmbassadorSingleton.getInstance().setTwitterAccessToken("2925003771-TBomtq36uThf6EqTKggITNHqOpl6DDyGMb5hLvz");
        AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret("WUg9QkrVoL3ndW6DwdpQAUvVaRcxhHUB2ED3PoUlfZFek");

        //tap twitter share
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        onView(withId(R.id.dialog_twitter_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.etTweetMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHeaderText)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHeaderText)).check(matches(isDisplayed()));
        onView(withId(R.id.ivHeaderImg)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHeaderText)).check(matches(isDisplayed()));
        onView(withId(R.id.etTweetMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.btnTweet)).check(matches(isDisplayed()));
        onView(withId(R.id.loadingPanel)).check(matches(not(isDisplayed())));
        onView(withId(R.id.tvTweet)).check(matches(isDisplayed()));

        pressBack();

        //ensure dialog fields not visible now that we've backed out
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());

        //click twitter icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //enter blank text and make sure dialog is still visible
        onView(withId(R.id.etTweetMessage)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.btnTweet)).perform(click());
        onView(withId(R.id.dialog_twitter_layout)).check(matches(isDisplayed()));

        //type a link with a random number appended to circumvent twitter complaining about duplicate posts
        onView(withId(R.id.etTweetMessage)).perform(typeText("http://www.tester.com " + _getRandomNumber()), closeSoftKeyboard());
        onView(withId(R.id.btnTweet)).perform(click());
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //enter blank text and make sure dialog is still visible
        onView(withId(R.id.etTweetMessage)).perform(clearText(), closeSoftKeyboard());
        pressBack();
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        onView(withId(R.id.etTweetMessage)).check(matches(withText(containsString(parameters.shareMessage))));

        //testing toast (didn't work)
        //onView(withText("Unable to post, please try again!")).inRoot(withDecorView(not(is(mActivityTestIntentRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));


        pressBack();

        //clear the token
        AmbassadorSingleton.getInstance().setTwitterAccessToken(null);
        AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret(null);

        //click twitter icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        intended(toPackage("com.example.ambassador.ambassadorsdk.TwitterLoginActivity"));
    }

    private int _getRandomNumber() {
        double d = Math.random() * 100;
        return (int)d + 1;
    }
}
