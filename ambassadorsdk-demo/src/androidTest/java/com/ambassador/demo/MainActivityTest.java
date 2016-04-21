package com.ambassador.demo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.demo.activities.LoginActivity;
import com.ambassador.demo.activities.MainActivity;
import com.ambassador.demo.api.Requests;
import com.ambassador.demo.api.pojo.GetCampaignsResponse;
import com.ambassador.demo.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.demo.api.pojo.LoginResponse;

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

import retrofit.Callback;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Inject protected ConversionUtility conversionUtility;
    @Inject protected RequestManager requestManager;
    @Inject protected PusherManager pusherManager;
    @Inject protected User user;

    protected Context context;

    protected Requests requests;

    @Before
    public void beforeEachTest() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext().getApplicationContext();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.company = new LoginResponse.Company();
        loginResponse.company.first_name = "kitty";
        loginResponse.company.last_name = "cat";
        loginResponse.company.universal_id = "universal_id";
        loginResponse.company.sdk_token = "sdk_token";
        loginResponse.company.avatar_url = "http://catfacts.jazzychad.net/img/cat.jpg";
        com.ambassador.demo.data.User.get().load(loginResponse);

        requests = Mockito.mock(Requests.class);
        Requests.instance = requests;

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

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

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

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

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
        mockCampaignsResponse();

        // Mock get short code.
        mockShortCodeResponse();

        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus referrer email input and type a valid email address.
        onView(withId(R.id.etAmbassadorEmail)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etAmbassadorEmail)).perform(ViewActions.typeTextIntoFocusedView("jake1@getambassador.com"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus email input and type a valid email address.
        onView(withId(R.id.etCustomerEmail)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etCustomerEmail)).perform(ViewActions.typeTextIntoFocusedView("jake2@getambassador.com"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etRevenue)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus campaign ID input and type a valid campaign ID.
        onView(withId(R.id.rlCampaignChooser)).perform(ViewActions.scrollTo(), ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.lvChooser)).atPosition(1).perform(ViewActions.click());

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Verify conversion details and confirm fires.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ConversionParameters conversionParameters = (ConversionParameters) invocation.getArguments()[0];
                Assert.assertEquals("jake2@getambassador.com", conversionParameters.getEmail());
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
        mockCampaignsResponse();

        // Mock get short code.
        mockShortCodeResponse();

        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus referrer email input and type a valid email address.
        onView(withId(R.id.etAmbassadorEmail)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etAmbassadorEmail)).perform(ViewActions.typeTextIntoFocusedView("jake1@getambassador.com"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus email input and type a valid email address.
        onView(withId(R.id.etCustomerEmail)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etCustomerEmail)).perform(ViewActions.typeTextIntoFocusedView("jake2@getambassador.com"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etRevenue)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus campaign ID input and type a valid campaign ID.
        onView(withId(R.id.rlCampaignChooser)).perform(ViewActions.scrollTo(), ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.lvChooser)).atPosition(1).perform(ViewActions.click());

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Activate is approved switch.
        onView(withId(R.id.swConversionApproved)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.scrollTo(), ViewActions.click());

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
        mockCampaignsResponse();

        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etRevenue)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus campaign ID input and type a valid campaign ID.
        onView(withId(R.id.rlCampaignChooser)).perform(ViewActions.scrollTo(), ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.lvChooser)).atPosition(1).perform(ViewActions.click());

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.scrollTo(), ViewActions.click());
    }

    @Test
    public void testsConversionWithNoRevenue() throws Exception {
        mockCampaignsResponse();

        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus email input and type a valid email address.
        onView(withId(R.id.etCustomerEmail)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etCustomerEmail)).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus campaign ID input and click a campaign.
        onView(withId(R.id.rlCampaignChooser)).perform(ViewActions.scrollTo(), ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.lvChooser)).atPosition(1).perform(ViewActions.click());

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Verify no conversion fires.
        Mockito.verify(conversionUtility, Mockito.never()).registerConversion();
    }

    @Test
    public void testsConversionWithNoCampaignID() throws Exception {
        // Select Conversion tab.
        onView(withTabName("Conversion")).perform(ViewActions.click());

        // Focus email input and type a valid email address.
        onView(withId(R.id.etAmbassadorEmail)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etAmbassadorEmail)).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Focus revenue input and type a valid currency amount.
        onView(withId(R.id.etRevenue)).perform(ViewActions.scrollTo(), ViewActions.click());
        onView(withId(R.id.etRevenue)).perform(ViewActions.typeTextIntoFocusedView("25.55"));

        // Close the keyboard to ensure other views all visible.
        Espresso.closeSoftKeyboard();

        // Click the submit button.
        onView(withId(R.id.btnConversion)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Verify no conversion fires.
        Mockito.verify(conversionUtility, Mockito.never()).registerConversion();
    }

    @Test
    public void testsSettingsCopyUniversalId() throws Exception {
        // Select Settings tab.
        onView(withTabName("Settings")).perform(ViewActions.click());

        // Click copy button for universal ID.
        onView(withId(R.id.ivCopyUniversalId)).perform(ViewActions.click());

        // Verify clip data.
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                String clipboardData = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                Assert.assertEquals("universal_id", clipboardData);
            }
        });
    }

    @Test
    public void testsSettingsCopySdkToken() throws Exception {
        // Select Settings tab.
        onView(withTabName("Settings")).perform(ViewActions.click());

        // Click copy button for sdk token.
        onView(withId(R.id.ivCopySdkToken)).perform(ViewActions.click());

        // Verify clip data.
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                String clipboardData = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                Assert.assertEquals("sdk_token", clipboardData);
            }
        });
    }

    @Test
    public void testsSettingsLogout() throws Exception {
        // Select Settings tab.
        onView(withTabName("Settings")).perform(ViewActions.click());

        // Click the logout button.
        onView(withId(R.id.rlLogout)).perform(ViewActions.click());

        // Sleep 500ms to compensate for LaunchActivity, and verify LoginActivity.
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

    protected void mockShortCodeResponse() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<GetShortCodeFromEmailResponse> callback = (Callback) invocation.getArguments()[3];
                GetShortCodeFromEmailResponse response = new GetShortCodeFromEmailResponse();
                response.count = 1;
                response.results = new GetShortCodeFromEmailResponse.Result[1];
                response.results[0] = new GetShortCodeFromEmailResponse.Result();
                response.results[0].short_code = "abcd";
                callback.success(response, null);
                return null;
            }
        }).when(requests).getShortCodeFromEmail(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(Callback.class));
    }

    protected void mockCampaignsResponse() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<GetCampaignsResponse> responseCallback = (Callback) invocation.getArguments()[1];
                GetCampaignsResponse getCampaignsResponse = new GetCampaignsResponse();
                getCampaignsResponse.results = new GetCampaignsResponse.CampaignResponse[3];
                getCampaignsResponse.results[0] = new GetCampaignsResponse.CampaignResponse();
                getCampaignsResponse.results[1] = new GetCampaignsResponse.CampaignResponse();
                getCampaignsResponse.results[2] = new GetCampaignsResponse.CampaignResponse();
                getCampaignsResponse.results[0].name = "Test campaign 1";
                getCampaignsResponse.results[0].uid = 123;
                getCampaignsResponse.results[1].name = "Test campaign 2";
                getCampaignsResponse.results[1].uid = 124;
                getCampaignsResponse.results[2].name = "Test campaign 3";
                getCampaignsResponse.results[2].uid = 125;
                responseCallback.success(getCampaignsResponse, null);
                return null;
            }
        }).when(requests).getCampaigns(Mockito.anyString(), Mockito.any(Callback.class));
    }

}