package com.example.ambassador.sdktestapplication;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testPresentRaf() {
        //onView(withId(R.id.btnPresentRAF1)).perform(click());
        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(not(withText(""))));
        onView(withId(R.id.btnCopy)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));
    }
}