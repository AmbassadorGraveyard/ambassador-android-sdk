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
import com.mobeta.android.dslv.DragSortItemView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class CustomizationActivityTest {

    @Rule public ActivityTestRule<CustomizationActivity> activityTestRule = new ActivityTestRule<>(CustomizationActivity.class, true, false);

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

}
