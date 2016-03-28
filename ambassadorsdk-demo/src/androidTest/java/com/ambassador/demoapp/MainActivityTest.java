package com.ambassador.demoapp;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.User;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Inject protected ConversionUtility conversionUtility;
    @Inject protected RequestManager requestManager;
    @Inject protected PusherManager pusherManager;
    @Inject protected User user;

    protected Context context;

    @Before
    public void beforeEachTest() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext().getApplicationContext();

        AmbSingleton.init(context, new TestModule());
        AmbassadorSDK.runWithKeys(context, "ut", "uid");
        AmbSingleton.inject(this);

        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);
    }

    @Test
    public void testsSwipingTabs() throws Exception {
        // Verify starts on 1st tab.
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(0)));

        // Swipe left and verify 2nd tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(1)));

        // Swipe left and verify 3rd tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(2)));

        // Swipe left and verify 4th tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(3)));

        // Swipe left again and verify still 4th tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(3)));

        // Swipe right and verify 3rd tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeRight());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(2)));

        // Swipe right and verify 2nd tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeRight());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(1)));

        // Swipe right and verify 1st tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeRight());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(0)));

        // Swipe right again and verify still 1st tab.
        onView(withId(R.id.vpPages)).perform(ViewActions.swipeRight());
        onView(withId(R.id.tlTabs)).check(matches(withTabActivated(0)));
    }

    @Test
    public void testsIdentifyWithValidInput() throws Exception {
        // Select Identify tab.
        onView(withTabName("Identify")).perform(ViewActions.click());

        // Focus identify input and type a valid email address.
        onView(withId(R.id.etIdentify)).perform(ViewActions.click());
        onView(withId(R.id.etIdentify)).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));

        // Click the identify button.
        onView(withId(R.id.btnIdentify)).perform(ViewActions.click());

        // Verify identify happens.
        Mockito.verify(user).setEmail(Mockito.eq("jake@getambassador.com"));
    }

    @Test
    public void testsIdentifyWithInvalidInput() throws Exception {
        // Select Identify tab.
        onView(withTabName("Identify")).perform(ViewActions.click());

        // Focus identify input and type a valid email address.
        onView(withId(R.id.etIdentify)).perform(ViewActions.click());
        onView(withId(R.id.etIdentify)).perform(ViewActions.typeTextIntoFocusedView("jake$$"));

        // Click the submit button.
        onView(withId(R.id.btnIdentify)).perform(ViewActions.click());

        // Verify identify does not happen.
        Mockito.verify(user, Mockito.never()).setEmail(Mockito.anyString());
        Mockito.verify(pusherManager, Mockito.never()).startNewChannel();
    }

    @Test
    public void testsIdentifyWithNoInput() throws Exception {
        // Select Identify tab.
        onView(withTabName("Identify")).perform(ViewActions.click());

        // Click the submit button.
        onView(withId(R.id.btnIdentify)).perform(ViewActions.click());

        // Verify identify does not happen.
        Mockito.verify(user, Mockito.never()).setEmail(Mockito.anyString());
        Mockito.verify(pusherManager, Mockito.never()).startNewChannel();
    }

    @Test
    public void testsConversionWithValidInputAndNotApproved() throws Exception {
        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus email input and type a valid email address.
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.click());
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.click());
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Focus campaign ID input and type a valid campaign ID.
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.click());
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.typeTextIntoFocusedView("260"));

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.click());

        // Verify conversion details and confirm fires.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ConversionParameters conversionParameters = (ConversionParameters) invocation.getArguments()[0];
                Assert.assertEquals("jake@getambassador.com", conversionParameters.getEmail());
                Assert.assertEquals(25.55, conversionParameters.getRevenue());
                Assert.assertEquals(260, conversionParameters.getCampaign());
                Assert.assertEquals(0, conversionParameters.getIsApproved());
                return null;
            }
        }).when(conversionUtility).setParameters(Mockito.any(ConversionParameters.class));
        Mockito.verify(conversionUtility).registerConversion();
    }

    @Test
    public void testsConversionWithValidInputAndApproved() throws Exception {
        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus email input and type a valid email address.
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.click());
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.click());
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Focus campaign ID input and type a valid campaign ID.
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.click());
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.typeTextIntoFocusedView("260"));

        // Activate is approved switch.
        onView(withId(R.id.swConversionApproved)).perform(ViewActions.click());

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.click());

        // Verify conversion details and confirm fires.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ConversionParameters conversionParameters = (ConversionParameters) invocation.getArguments()[0];
                Assert.assertEquals("jake@getambassador.com", conversionParameters.getEmail());
                Assert.assertEquals(25.55, conversionParameters.getRevenue());
                Assert.assertEquals(260, conversionParameters.getCampaign());
                Assert.assertEquals(1, conversionParameters.getIsApproved());
                return null;
            }
        }).when(conversionUtility).setParameters(Mockito.any(ConversionParameters.class));
        Mockito.verify(conversionUtility).registerConversion();
    }

    @Test
    public void testsConversionWithNoEmail() throws Exception {
        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.click());
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Focus campaign ID input and type a valid campaign ID.
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.click());
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.typeTextIntoFocusedView("260"));

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.click());
    }

    @Test
    public void testsConversionWithNoRevenue() {
        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus email input and type a valid email address.
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.click());
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));

        // Focus campaign ID input and type a valid campaign ID.
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.click());
        onView(withId(R.id.etConversionCampaign)).perform(ViewActions.typeTextIntoFocusedView("260"));

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.click());

        // Verify no conversion fires.
        Mockito.verify(conversionUtility, Mockito.never()).registerConversion();
    }

    @Test
    public void testsConversionWithNoCampaignID() {
        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus email input and type a valid email address.
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.click());
        onView(withId(R.id.etConversionEmail)).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.click());
        onView(withId(R.id.etConversionRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.click());

        // Verify no conversion fires.
        Mockito.verify(conversionUtility, Mockito.never()).registerConversion();
    }

    private static Matcher<View> withTabActivated(final int index) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TabLayout)) {
                    return false;
                }

                TabLayout tabLayout = (TabLayout) view;
                return tabLayout.getSelectedTabPosition() == index;
            }

            @Override
            public void describeTo(Description description) {
                // Not needed.
            }

        };
    }

    private static Matcher<View> withTabName(final String name) {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                if (view.getId() == R.id.tvTabTitle && view instanceof TextView) {
                    TextView textView = (TextView) view;
                    return textView.getText().toString().equals(name);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                // Not needed.
            }

        };
    }
}
