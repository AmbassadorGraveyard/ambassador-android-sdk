package com.ambassador.demoapp;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.demoapp.activities.CustomizationActivity;
import com.ambassador.demoapp.api.Requests;
import com.ambassador.demoapp.api.pojo.GetCampaignsResponse;
import com.mobeta.android.dslv.DragSortItemView;

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

import retrofit.Callback;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class CustomizationActivityTest {

    @Rule public ActivityTestRule<CustomizationActivity> activityTestRule = new ActivityTestRule<>(CustomizationActivity.class, true, false);

    protected Context context;

    protected Requests requests;

    @Before
    public void beforeEachTest() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext().getApplicationContext();

        requests = Mockito.mock(Requests.class);
        Requests.instance = requests;

        AmbSingleton.init(context, new TestModule());
        AmbassadorSDK.runWithKeys(context, "ut", "uid");
        AmbSingleton.inject(this);

        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);
    }

    @Test
    public void testsColorInputsDoOpenColorChooserDialog() {
        // Scroll to and click header color input.
        onView(withId(R.id.civHeader)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Verify ColorChooserDialog opens.
        onView(withId(R.id.rlColors)).check(matches(isDisplayed()));

        // Close dialog.
        Espresso.pressBack();

        // Scroll to and click text field 1 color input.
        onView(withId(R.id.civTextField1)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Verify ColorChooserDialog opens.
        onView(withId(R.id.rlColors)).check(matches(isDisplayed()));

        // Close dialog.
        Espresso.pressBack();

        // Scroll to and click text field 2 color input.
        onView(withId(R.id.civTextField2)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Verify ColorChooserDialog opens.
        onView(withId(R.id.rlColors)).check(matches(isDisplayed()));

        // Close dialog.
        Espresso.pressBack();

        // Scroll to and click buttons color input.
        onView(withId(R.id.civButtons)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Verify ColorChooserDialog opens.
        onView(withId(R.id.rlColors)).check(matches(isDisplayed()));

        // Close dialog.
        Espresso.pressBack();
    }

    @Test
    public void testsCampaignChooserWithOptionsDoesSetInput() {
        // Mock campaigns success response
        mockCampaignsResponse();

        // Scroll to campaign chooser.
        onView(withId(R.id.rvCampaignChooser)).perform(ViewActions.scrollTo());

        // Verify default text.
        onView(withId(R.id.tvSelectedCampaign)).check(matches(withText("Select a Campaign")));

        // Click the campaign chooser.
        onView(withId(R.id.rvCampaignChooser)).perform(ViewActions.click());

        // Confirm dialog launches.
        onView(withId(R.id.rvCampaignChooserTitle)).check(matches(isDisplayed()));

        // Click the 2nd campaign.
        onData(anything()).inAdapterView(withId(R.id.lvCampaignChooser)).atPosition(1).perform(ViewActions.click());

        // Confirm text set on textview.
        onView(withId(R.id.tvSelectedCampaign)).check(matches(withText("Test campaign 2")));
    }

    @Test
    public void testsCampaignChooserCanceledDoesNotSetInput() {
        // Mock campaigns success response
        mockCampaignsResponse();

        // Scroll to campaign chooser.
        onView(withId(R.id.rvCampaignChooser)).perform(ViewActions.scrollTo());

        // Verify default text.
        onView(withId(R.id.tvSelectedCampaign)).check(matches(withText("Select a Campaign")));

        // Click the campaign chooser.
        onView(withId(R.id.rvCampaignChooser)).perform(ViewActions.click());

        // Confirm dialog launches.
        onView(withId(R.id.rvCampaignChooserTitle)).check(matches(isDisplayed()));

        // Press back.
        Espresso.pressBack();

        // Confirm text set on textview.
        onView(withId(R.id.tvSelectedCampaign)).check(matches(withText("Select a Campaign")));
    }

    @Test
    public void testsCampaignChooserProgressBarBehavior() throws Exception {
        // Mock campaigns success response
        mockCampaignsResponse();

        // Scroll to campaign chooser.
        onView(withId(R.id.rvCampaignChooser)).perform(ViewActions.scrollTo());

        // Verify default text.
        onView(withId(R.id.tvSelectedCampaign)).check(matches(withText("Select a Campaign")));

        // Click the campaign chooser.
        onView(withId(R.id.rvCampaignChooser)).perform(ViewActions.click());

        // Check progress bar gone after request answered.
        onView(withId(R.id.pbCampaignsLoading)).check(matches(not(isDisplayed())));

        // Confirm dialog launches.
        onView(withId(R.id.rvCampaignChooserTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testsSocialChannelListIsReArrangeable() {
        // Scroll to social channel list.
        onView(withId(R.id.lvChannels)).perform(ViewActions.scrollTo());

        // Drag position 0 down a spot.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(0).perform(dragDown());

        // Drag position 2 up a spot.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(2).perform(dragUp());

        // Verify Twitter now at position 1.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(0).check(matches(isDragItem("Twitter")));

        // Verify LinkedIn now at position 2.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(1).check(matches(isDragItem("LinkedIn")));

        // Verify Facebook now at position 3.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(2).check(matches(isDragItem("Facebook")));
    }

    @Test
    public void testsSocialChannelListRetainsSwitchStateThroughReordering() {
        // Scroll to social channel list.
        onView(withId(R.id.lvChannels)).perform(ViewActions.scrollTo());

        // Toggle switch on Facebook.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(0).perform(subViewClick(R.id.swChannel));

        // Toggle switch on Twitter.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(1).perform(subViewClick(R.id.swChannel));

        // Drag position 0 down a spot.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(0).perform(dragDown());

        // Drag position 2 up a spot.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(2).perform(dragUp());

        // Verify Twitter now at position 1.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(0).check(matches(isDragItem("Twitter")));

        // Verify LinkedIn now at position 2.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(1).check(matches(isDragItem("LinkedIn")));

        // Verify Facebook now at position 3.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(2).check(matches(isDragItem("Facebook")));

        // Verify Facebook is unchecked.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(2).check(matches(not(isChannelChecked())));

        // Verify Twitter is unchecked.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(0).check(matches(not(isChannelChecked())));

        // Verify LinkedIn is checked.
        onData(anything()).inAdapterView(withId(R.id.lvChannels)).atPosition(1).check(matches(isChannelChecked()));
    }

    @Test
    public void testsInputValidationAndResponseLine() {
        // Mock campaigns response.
        mockCampaignsResponse();

        // Scroll to bottom.
        onView(withId(R.id.civButtons)).perform(ViewActions.scrollTo());

        // Click save.
        onView(withId(R.id.action_save)).perform(ViewActions.click());

        // Verify SnackBar.
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Please enter an integration name!"))).check(matches(isDisplayed()));

        // Click "OK".
        onView(withId(android.support.design.R.id.snackbar_action)).perform(ViewActions.click());

        // Verify input focused and visible.
        onView(withId(R.id.inputIntegrationName)).check(matches(isFocused()));
        onView(withId(R.id.inputIntegrationName)).check(matches(isDisplayed()));

        // Type text into input.
        onView(withId(R.id.inputIntegrationName)).perform(ViewActions.typeTextIntoFocusedView("Integration name"));

        // Scroll to bottom.
        onView(withId(R.id.civButtons)).perform(ViewActions.scrollTo());

        // Click save.
        onView(withId(R.id.action_save)).perform(ViewActions.click());

        // Verify SnackBar.
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Please choose a campaign!"))).check(matches(isDisplayed()));

        // Click "OK".
        onView(withId(android.support.design.R.id.snackbar_action)).perform(ViewActions.click());

        // Verify campaign dialog launches.
        onView(withId(R.id.rvCampaignChooserTitle)).check(matches(isDisplayed()));

        // Select 1st campaign.
        onData(anything()).inAdapterView(withId(R.id.lvCampaignChooser)).atPosition(0).perform(ViewActions.click());

        // Scroll to bottom.
        onView(withId(R.id.civButtons)).perform(ViewActions.scrollTo());

        // Click save.
        onView(withId(R.id.action_save)).perform(ViewActions.click());

        // Verify SnackBar.
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Please enter text for the header!"))).check(matches(isDisplayed()));

        // Click "OK".
        onView(withId(android.support.design.R.id.snackbar_action)).perform(ViewActions.click());

        // Verify input focused and visible.
        onView(withId(R.id.inputHeaderText)).check(matches(isFocused()));
        onView(withId(R.id.inputHeaderText)).check(matches(isDisplayed()));

        // Type text into input.
        onView(withId(R.id.inputHeaderText)).perform(ViewActions.typeTextIntoFocusedView("Header text"));

        // Scroll to bottom.
        onView(withId(R.id.civButtons)).perform(ViewActions.scrollTo());

        // Click save.
        onView(withId(R.id.action_save)).perform(ViewActions.click());

        // Verify SnackBar.
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Please enter text for field one!"))).check(matches(isDisplayed()));

        // Click "OK".
        onView(withId(android.support.design.R.id.snackbar_action)).perform(ViewActions.click());

        // Verify input focused and visible.
        onView(withId(R.id.inputTextField1)).check(matches(isFocused()));
        onView(withId(R.id.inputTextField1)).check(matches(isDisplayed()));

        // Type text into input.
        onView(withId(R.id.inputTextField1)).perform(ViewActions.typeTextIntoFocusedView("Title text"));

        // Scroll to bottom.
        onView(withId(R.id.civButtons)).perform(ViewActions.scrollTo());

        // Click save.
        onView(withId(R.id.action_save)).perform(ViewActions.click());

        // Verify SnackBar.
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Please enter text for field two!"))).check(matches(isDisplayed()));

        // Click "OK".
        onView(withId(android.support.design.R.id.snackbar_action)).perform(ViewActions.click());

        // Verify input focused and visible.
        onView(withId(R.id.inputTextField2)).check(matches(isFocused()));
        onView(withId(R.id.inputTextField2)).check(matches(isDisplayed()));

        // Type text into input.
        onView(withId(R.id.inputTextField2)).perform(ViewActions.typeTextIntoFocusedView("Description text"));
    }

    private static ViewAction subViewClick(final int subId) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Dragging item down";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View subView = view.findViewById(subId);
                ViewActions.click().perform(uiController, subView);
            }

        };
    }

    private static ViewAction dragDown() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Dragging item down";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ImageView handle = (ImageView) view.findViewById(R.id.ivDrag);
                ViewActions.swipeDown().perform(uiController, handle);
            }

        };
    }

    private static ViewAction dragUp() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Dragging item down";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ImageView handle = (ImageView) view.findViewById(R.id.ivDrag);
                ViewActions.swipeUp().perform(uiController, handle);
            }

        };
    }

    private static Matcher<View> isDragItem(final String name) {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof DragSortItemView) {
                    DragSortItemView dragSortItemView = (DragSortItemView) view;
                    TextView tvChannelName = (TextView) dragSortItemView.findViewById(R.id.tvChannelName);
                    return tvChannelName.getText().toString().equals(name);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                // Not needed.
            }

        };
    }

    private static Matcher<View> isChannelChecked() {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof DragSortItemView) {
                    DragSortItemView dragSortItemView = (DragSortItemView) view;
                    SwitchCompat swChannel = (SwitchCompat) dragSortItemView.findViewById(R.id.swChannel);
                    return swChannel.isChecked();
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                // Not needed.
            }

        };
    }

    private static Matcher<View> isFocused() {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                return view.isFocused();
            }

            @Override
            public void describeTo(Description description) {
                // Not needed.
            }

        };
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
