package com.ambassador.demoapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Root;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.demoapp.activities.LoginActivity;
import com.ambassador.demoapp.activities.MainActivity;
import com.ambassador.demoapp.api.Requests;
import com.ambassador.demoapp.api.pojo.LoginResponse;

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

import retrofit.Callback;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class, true, false);

    protected Requests requests;

    protected Context context;

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
    public void test() {

    }

    //@Test
//    public void testsEmptyEmailValidation() throws Exception {
//        // Focus password input and type "password".
//        onView(withInputHint("Password")).perform(ViewActions.click());
//        onView(withInputHint("Password")).perform(ViewActions.typeTextIntoFocusedView("password"));
//
//        // Click the login button.
//        onView(withId(R.id.btnLogin)).perform(ViewActions.click());
//
//        // Verify no login request.
//        Mockito.verify(requests, Mockito.never()).login(Mockito.anyString(), Mockito.anyString(), Mockito.any(Callback.class));
//
//        // Verify toast.
//        onView(withText("Please enter an email and password!")).inRoot(isToast()).check(matches(isDisplayed()));
//    }
//
//    //@Test
//    public void testsEmptyPasswordValidation() throws Exception {
//        // Focus email input and type a valid email.
//        onView(withInputHint("Email address")).perform(ViewActions.click());
//        onView(withInputHint("Email address")).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));
//
//        // Click the login button.
//        onView(withId(R.id.btnLogin)).perform(ViewActions.click());
//
//        // Verify no login request.
//        Mockito.verify(requests, Mockito.never()).login(Mockito.anyString(), Mockito.anyString(), Mockito.any(Callback.class));
//
//        // Verify toast.
//        onView(withText("Please enter an email and password!")).inRoot(isToast()).check(matches(isDisplayed()));
//    }
//
//    //@Test
//    public void testsInvalidEmailValidation() throws Exception {
//        // Focus email input and type an invalid email.
//        onView(withInputHint("Email address")).perform(ViewActions.click());
//        onView(withInputHint("Email address")).perform(ViewActions.typeTextIntoFocusedView("jake$$"));
//
//        // Focus password input and type "password".
//        onView(withInputHint("Password")).perform(ViewActions.click());
//        onView(withInputHint("Password")).perform(ViewActions.typeTextIntoFocusedView("password"));
//
//        // Click the login button.
//        onView(withId(R.id.btnLogin)).perform(ViewActions.click());
//
//        // Verify no login request.
//        Mockito.verify(requests, Mockito.never()).login(Mockito.anyString(), Mockito.anyString(), Mockito.any(Callback.class));
//
//        // Verify toast.
//        onView(withText("Please enter a valid email address!")).inRoot(isToast()).check(matches(isDisplayed()));
//    }
//
//    //@Test
//    public void testsIncorrectPassword() throws Exception {
//        // Arrange login callback.
//        Mockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Callback<LoginResponse> loginResponseCallback = (Callback<LoginResponse>) invocation.getArguments()[2];
//                loginResponseCallback.failure(null);
//                return null;
//            }
//        }).when(requests).login(Mockito.anyString(), Mockito.anyString(), Mockito.any(Callback.class));
//
//        // Focus email input and type a valid email.
//        onView(withInputHint("Email address")).perform(ViewActions.click());
//        onView(withInputHint("Email address")).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));
//
//        // Focus password input and type "password".
//        onView(withInputHint("Password")).perform(ViewActions.click());
//        onView(withInputHint("Password")).perform(ViewActions.typeTextIntoFocusedView("password"));
//
//        // Click the login button.
//        onView(withId(R.id.btnLogin)).perform(ViewActions.click());
//
//        // Verify login request.
//        Mockito.verify(requests, Mockito.times(1)).login(Mockito.anyString(), Mockito.anyString(), Mockito.any(Callback.class));
//
//        // Verify toast.
//        onView(withText("Incorrect email/password!")).inRoot(isToast()).check(matches(isDisplayed()));
//    }
//
//    //@Test
//    public void testsCorrectLogin() throws Exception {
//        // Arrange login callback.
//        Mockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Callback<LoginResponse> loginResponseCallback = (Callback<LoginResponse>) invocation.getArguments()[2];
//                LoginResponse loginResponse = new LoginResponse();
//                loginResponse.company = new LoginResponse.Company();
//                loginResponse.company.universal_id = "uid";
//                loginResponse.company.universal_token = "ut";
//                loginResponseCallback.success(loginResponse, null);
//                return null;
//            }
//        }).when(requests).login(Mockito.anyString(), Mockito.anyString(), Mockito.any(Callback.class));
//
//        // Focus email input and type a valid email.
//        onView(withInputHint("Email address")).perform(ViewActions.click());
//        onView(withInputHint("Email address")).perform(ViewActions.typeTextIntoFocusedView("jake@getambassador.com"));
//
//        // Focus password input and type "password".
//        onView(withInputHint("Password")).perform(ViewActions.click());
//        onView(withInputHint("Password")).perform(ViewActions.typeTextIntoFocusedView("password"));
//
//        // Click the login button.
//        onView(withId(R.id.btnLogin)).perform(ViewActions.click());
//
//        // Give 500ms to change activities.
//        Thread.sleep(500);
//
//        // Verify login request.
//        Mockito.verify(requests, Mockito.times(1)).login(Mockito.anyString(), Mockito.anyString(), Mockito.any(Callback.class));
//
//        // Verify change to MainActivity.
//        final Activity[] activity = new Activity[1];
//        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                activity[0] = Iterables.getOnlyElement(ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED));
//            }
//        });
//        Assert.assertTrue(activity[0] instanceof MainActivity);
//    }

    private static Matcher<View> withInputHint(final String name) {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                if (view.getId() == R.id.editText && view instanceof EditText) {
                    EditText editText = (EditText) view;
                    return editText.getHint().toString().equals(name);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                // Not needed.
            }

        };
    }

    private static Matcher<Root> isToast() {
        return new TypeSafeMatcher<Root>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("is toast");
            }

            @Override
            public boolean matchesSafely(Root root) {
                int type = root.getWindowLayoutParams().get().type;
                if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                    IBinder windowToken = root.getDecorView().getWindowToken();
                    IBinder appToken = root.getDecorView().getApplicationWindowToken();
                    return windowToken == appToken;
                }
                return false;
            }
        };
    }

}
