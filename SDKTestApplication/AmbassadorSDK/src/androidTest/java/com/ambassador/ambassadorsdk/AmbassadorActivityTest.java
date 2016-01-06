package com.ambassador.ambassadorsdk;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.LocalBroadcastManager;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.internal.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.internal.AmbassadorApplicationModule;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.ServiceSelectorPreferences;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInApi;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class AmbassadorActivityTest {
    private ServiceSelectorPreferences parameters;
    private static final String EMAIL_PATTERN = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\\b";
    private static final String SMS_PATTERN = "(Home|Work|Mobile|Other) (.*)";
    private Context context;

    //tell Dagger this code will participate in dependency injection
    @Inject
    RequestManager requestManager;

    @Inject
    BulkShareHelper bulkShareHelper;

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Inject
    PusherSDK pusher;

    //set up inject method, which will inject the above into whatever is passed in (in this case, the test class)
    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent extends AmbassadorApplicationComponent {
        void inject(AmbassadorActivityTest ambassadorActivityTest);
    }

    @Rule
    public ActivityTestRule<AmbassadorActivity> mActivityTestIntentRule = new ActivityTestRule<>(AmbassadorActivity.class, true, false);

    @Before
    public void beforeEachTest() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext().getApplicationContext();

        SystemAnimations systemAnimations = new SystemAnimations(context);
        systemAnimations.disableAll();

        parameters = new ServiceSelectorPreferences();
        parameters.defaultShareMessage = "Check out this company!";
        parameters.titleText = "RAF Params Welcome Title";
        parameters.descriptionText = "RAF Params Welcome Description";
        parameters.toolbarTitle = "RAF Params Toolbar Title";

        //tell the application which component we want to use - in this case use the the one created above instead of the
        //application component which is created in the Application (and uses the real tweetRequest)
        AmbassadorSingleton.getInstance().init(context);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);
        AmbassadorSingleton.setAmbModule(amb);
        TestComponent component = DaggerAmbassadorActivityTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        AmbassadorSingleton.setComponent(component);
        //perform injection
        component.inject(this);

        String pusherResponse = "{\"email\":\"jake@getambassador.com\",\"firstName\":\"\",\"lastName\":\"ere\",\"phoneNumber\":\"null\",\"urls\":[{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}]}";
        doNothing().when(ambassadorConfig).setRafParameters(anyString(), anyString(), anyString(), anyString());
        doNothing().when(ambassadorConfig).setURL(anyString());
        doNothing().when(ambassadorConfig).setReferrerShortCode(anyString());
        doNothing().when(ambassadorConfig).setEmailSubject(anyString());

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                parameters.defaultShareMessage += " http://staging.mbsy.co/jHjl";
                return null;
            }
        })
        .when(ambassadorConfig).setRafDefaultMessage(anyString());

        when(ambassadorConfig.getCampaignID()).thenReturn("260");
        when(ambassadorConfig.getURL()).thenReturn("http://staging.mbsy.co/jHjl");
        when(ambassadorConfig.getPusherInfo()).thenReturn(pusherResponse);
        when(ambassadorConfig.getRafParameters()).thenReturn(parameters);
        when(ambassadorConfig.getUniversalKey()).thenReturn("SDKToken ***REMOVED***");
        when(ambassadorConfig.getUniversalID()).thenReturn("***REMOVED***");

        //app workflow is identify -> backend calls pusher and triggers a response which is received by our app and
        //calls tryAndSetURL
        //if the app has a channel and it's not expired and connected, identify will be called right away.
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                _sendPusherIntent();
                return null;
            }
        })
        .when(requestManager).identifyRequest();

        //if the app has a channel and it's not expired but it's not currently connected, it will subscribe to the existing channel
        //mock the subscribe call, bypass identify in the callback, instead send the intent which will call tryAndSetURL
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                _sendPusherIntent();
                return null;
            }
        }).when(pusher).subscribePusher(any(PusherSDK.PusherSubscribeCallback.class));

        //otherwise, the app will resubscribe to pusher and then call identify
        //mock the createPusher call, bypass identify in the callback, instead send the intent which will call tryAndSetURL
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                _sendPusherIntent();
                return null;
            }
        }).when(pusher).createPusher(any(PusherSDK.PusherSubscribeCallback.class));

        Intent intent = new Intent();
        mActivityTestIntentRule.launchActivity(intent);
    }

    private void _sendPusherIntent() {
        Intent intent = new Intent("pusherData");
        LocalBroadcastManager.getInstance(AmbassadorSingleton.get()).sendBroadcast(intent);
    }

    @After
    public void afterEachTest() {
    }

    @Test
    public void testMainLayout() {
        onView(withId(R.id.llMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(withText(parameters.titleText)));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(withText(parameters.descriptionText)));
        onView(withId(R.id.etShortURL)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(withText("http://staging.mbsy.co/jHjl")));
        onView(withId(R.id.btnCopyPaste)).check(matches(isDisplayed()));
    }

    @Test
    public void testFacebook() {
        //TODO: remove hardcoded id check, try to get withText working
        //onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(0).perform(click());
        onView(withText("FACEBOOK")).perform(click());


        onView(withId(16908290)).check(matches(isDisplayed()));
        pressBack();

        //TODO: Espresso Web API to test WebViews not ready for prime time - too much trouble getting this to work - will come back
        //TODO: to this later to attempt to enter text into WebView fields to authenticate
        //onWebView().withElement(findElement(Locator.ID, "username")).perform(webKeys("test@sf.com"));
    }

    @Test
    public void testContactsEmail() throws Exception {
        //start recording fired Intents
        Intents.init();
        //click email icon
        onView(withText("EMAIL")).perform(click());

        //check that the Intent fired
        intended(hasComponent(ContactSelectorActivity.class.getName()));
        //stop recording Intents
        Intents.release();

        pressBack();
        //make sure after we backed out that expected views are there
        onView(withId(R.id.llMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));
        onView(withId(R.id.rvContacts)).check(ViewAssertions.doesNotExist());

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(3).perform(click());

        onView(withId(R.id.rlMaster)).check(matches(isDisplayed()));
        onView(withId(R.id.rlSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.etSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnDoneSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.rvContacts)).check(matches(isDisplayed()));
        onView(withId(R.id.llSendView)).check(matches(isDisplayed()));
        onView(withId(R.id.etShareMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.etShareMessage)).check(matches(withText(containsString("http://staging.mbsy.co/jHjl"))));
        onView(withId(R.id.btnEdit)).check(matches(isDisplayed()));
        onView(withId(R.id.btnDone)).check(matches(not(isDisplayed())));
        onView(withId(R.id.tvNoContacts)).check(matches(not(isDisplayed())));

        //TODO: test search bar
        //TODO: COMMENTED OUT - CAN'T GET TOOLBAR TO SHOW UP IN TESTS - RESEARCH THIS - maybe find some other way to open the textbox
        /*Activity act = mActivityTestIntentRule.getActivity();
        View view = act.findViewById(R.id.action_search);
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.rlSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.etSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.btnDoneSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.rlSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.etSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnDoneSearch)).check(matches(not(isDisplayed())));*/
        //TODO: test actually filtering contacts*/

        //TODO: test by using a mock list of contacts instead of phone contacts - these tests are dependent on there actually being contacts stored on the device

        //nothing should happen when no contacts selected
        onView(withId(R.id.rlSend)).perform(click());
        //make sure mock didn't get got fired
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));

        //test to make sure you're seeing email and not SMS
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(1, R.id.tvNumberOrEmail)).check(matches(_withRegex(EMAIL_PATTERN)));

        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //onData(is(instanceOf(ContactObject.class))).inAdapterView(withId(R.id.rvContacts)).atPosition(0).perform(click());
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.ivCheckMark)).check(matches(isDisplayed()));

        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.ivCheckMark)).check(matches(not(isDisplayed())));

        // long press dialog
        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.dialog_contact_info)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.dialog_contact_info)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.dialog_contact_info)).check(matches(isDisplayed()));
        onView(withId(R.id.ivExit)).perform(click());
        onView(withId(R.id.dialog_contact_info)).check(ViewAssertions.doesNotExist());

        //select a contact
        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(1, R.id.ivCheckMark)).check(matches(isDisplayed()));
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.ivCheckMark)).check(matches(not(isDisplayed())));
        onView(withId(R.id.etShareMessage)).check(matches(not(isEnabled())));
        onView(withId(R.id.btnEdit)).perform(click());
        onView(withId(R.id.etShareMessage)).check(matches(isEnabled()));
        //nothing should happen when no contacts selected
        onView(withId(R.id.rlSend)).perform(click());
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));
        onView(withId(R.id.btnDone)).perform(click());
        //share message should not be editable after done button clicked
        onView(withId(R.id.etShareMessage)).check(matches(not(isEnabled())));
        onView(withId(R.id.btnEdit)).perform(click());
        onView(withId(R.id.etShareMessage)).perform(clearText());
        onView(withId(R.id.etShareMessage)).perform(typeText("test"), closeSoftKeyboard());
        onView(withId(R.id.btnDone)).perform(click());
        onView(withId(R.id.rlSend)).perform(click());
        //dialog "url not entered" should be showing at this point - since I can't check that programmatically-created dialog, just check that underlying views are not present
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        Espresso.closeSoftKeyboard();
        pressBack();
        onView(withId(R.id.btnEdit)).perform(click());
        onView(withId(R.id.etShareMessage)).perform(typeText("http://staging.mbsy.co/jHjl"));
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        onView(withId(R.id.btnDone)).perform(click());

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                BulkShareHelper.BulkShareCompletion completion = (BulkShareHelper.BulkShareCompletion)object[3];
                completion.bulkShareFailure();
                return null;
            }
        })
        .doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                BulkShareHelper.BulkShareCompletion completion = (BulkShareHelper.BulkShareCompletion)object[3];
                completion.bulkShareSuccess();
                return null;
            }
        })
        .when(bulkShareHelper).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));

        onView(withId(R.id.rlSend)).perform(click());
        onView(withId(R.id.rlMaster)).check(matches(isDisplayed()));
        onView(withId(R.id.rlSend)).perform(click());
        verify(bulkShareHelper, times(2)).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));

        //TODO: after figuring out how to use mock list of contacts, test deleting one to make sure NO CONTACTS textview is shown
    }

    @Test
    public void testContactsSMS() throws Exception {
        //start recording fired Intents
        Intents.init();
        //click sms icon
        onView(withText("SMS")).perform(click());
        //check that the Intent fired
        intended(hasComponent(ContactSelectorActivity.class.getName()));
        //stop recording Intents
        Intents.release();

        pressBack();
        //make sure after we backed out that expected views are there
        onView(withId(R.id.llMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));
        onView(withId(R.id.rvContacts)).check(ViewAssertions.doesNotExist());

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(4).perform(click());

        //test to make sure you're seeing SMS and not email
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(1, R.id.tvNumberOrEmail)).check(matches(_withRegex(SMS_PATTERN)));

        // long press dialog
        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.dialog_contact_info)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.dialog_contact_info)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.dialog_contact_info)).check(matches(isDisplayed()));
        onView(withId(R.id.ivExit)).perform(click());
        onView(withId(R.id.dialog_contact_info)).check(ViewAssertions.doesNotExist());

        //this email will force the contact name dialog to come up when submitting
        String pusherResponse = "{\"email\":\"jake@getambassador.com\",\"firstName\":\"\",\"lastName\":\"\",\"phoneNumber\":\"null\",\"urls\":[{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}]}";
        when(ambassadorConfig.getPusherInfo()).thenReturn(pusherResponse);

        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.ivCheckMark)).check(matches(isDisplayed()));

        onView(withId(R.id.rlSend)).perform(click());
        //contact name dialog should be displayed
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        pressBack();
        Thread.sleep(100);
        onView(withId(R.id.dialog_contact_name)).check(ViewAssertions.doesNotExist());

        onView(withId(R.id.rlSend)).perform(click());
        //contact name dialog should be displayed
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        onView(withId(R.id.btnContinue)).perform(click());
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));

        onView(withId(R.id.btnCancel)).perform(click());
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.tvNumberOrEmail)).check(matches(_withRegex(SMS_PATTERN)));
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.ivCheckMark)).check(matches(isDisplayed()));
        onView(withId(R.id.rlSend)).perform(click());

        //contact name dialog should be displayed
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        onView(withId(R.id.etFirstName)).perform(typeText("Test"), closeSoftKeyboard());
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        onView(withId(R.id.btnContinue)).perform(click());
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));
        onView(withId(R.id.etLastName)).perform(typeText("User"));
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);

        doNothing().when(ambassadorConfig).setPusherInfo(anyString());
        doNothing().when(ambassadorConfig).setUserFullName(anyString(), anyString());

        //test will call updateNameRequest twice (fail and success)
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) object[3];
                completion.onFailure("blah");
                return null;
            }
        })
        .doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) object[3];
                completion.onSuccess("cool");
                return null;
            }
        })
        .when(requestManager).updateNameRequest(anyString(), anyString(), anyString(), any(RequestManager.RequestCompletion.class));

        //this call will fail, so make sure the dialog is still present and the mocks never get called
        onView(withId(R.id.btnContinue)).perform(click());
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));

        //this call will succeed, so make sure the mocks get called the appropriate number of times, the dialog is not present, and the main layout appears
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        onView(withId(R.id.btnContinue)).perform(click());
        Thread.sleep(100);
        onView(withId(R.id.dialog_contact_name)).check(ViewAssertions.doesNotExist());
        verify(requestManager, times(2)).updateNameRequest(anyString(), anyString(), anyString(), any(RequestManager.RequestCompletion.class));

        //since we never actually send the request, the identified callback never gets sent
        //so this tests is mimicing if the identify response from updateName never came back
        //in that case the view will stay on the list of contacts
        onView(withId(R.id.rvContacts)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.llMainLayout)).check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testLinkedIn() {
        //TODO: test linkedInLoginActivity (see strategy in testTwitter)

        onView(withId(R.id.llMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        when(ambassadorConfig.getLinkedInToken()).thenReturn("AQV6mLXj7R7mEh88l_wPxg8x7V4ExwgQVFW0tcYHBoxaEP6KpzENTFQl-K1h0_V05pBNyTZlo0KDNQm3ZLPf62DjZxwfkLNhjeGLobVQUaMAseP8jdIQW_kKpMy7uIxr4T8PjrK8QP7XBsy3ibeuV2yhLrOJrOFA6LarWBcm0YGArhY1Wx8");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] object = invocation.getArguments();
                AmbassadorConfig.NullifyCompleteListener completion = (AmbassadorConfig.NullifyCompleteListener) object[0];
                completion.nullifyComplete();
                return null;
            }
        }).when(ambassadorConfig).nullifyLinkedInIfInvalid(any(AmbassadorConfig.NullifyCompleteListener.class));

        onView(withText("LINKEDIN")).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.etMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHeaderText)).check(matches(isDisplayed()));
        onView(withId(R.id.ivHeaderImg)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSend)).check(matches(isDisplayed()));
        onView(withId(R.id.loadingPanel)).check(matches(not(isDisplayed())));
        onView(withText("LinkedIn Post")).check(matches(isDisplayed()));
        Espresso.closeSoftKeyboard();
        pressBack();
        verify(requestManager, never()).postToLinkedIn(any(LinkedInApi.LinkedInPostRequest.class), any(RequestManager.RequestCompletion.class));

        //ensure dialog fields not visible now that we've backed out
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());

        //click linkedin icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(2).perform(click());
        //enter blank text and make sure dialog is still visible
        onView(withId(R.id.etMessage)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.btnSend)).perform(click());
        verify(requestManager, never()).postToLinkedIn(any(LinkedInApi.LinkedInPostRequest.class), any(RequestManager.RequestCompletion.class));

        //since text was cleared, ensure dialog is present, then click "send anyway"
        onView(withText("Hold on!")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        pressBack();
        verify(requestManager, never()).postToLinkedIn(any(LinkedInApi.LinkedInPostRequest.class), any(RequestManager.RequestCompletion.class));

        //test sending a successful (mocked) post
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(2).perform(click());
        //make sure message has been restored
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(parameters.defaultShareMessage))));

        //type a link with a random number appended to circumvent twitter complaining about duplicate post
        String linkedInText = _getRandomNumber();
        onView(withId(R.id.etMessage)).perform(typeTextIntoFocusedView(linkedInText), closeSoftKeyboard());

        doNothing().when(requestManager).bulkShareTrack(any(BulkShareHelper.SocialServiceTrackType.class));
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion)object[1];
                completion.onSuccess("success");
                return null;
            }
        })
        .doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion)object[1];
                completion.onFailure("fail");
                return null;
            }
        })
        .when(requestManager).postToLinkedIn(any(LinkedInApi.LinkedInPostRequest.class), any(RequestManager.RequestCompletion.class));

        onView(withId(R.id.btnSend)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());

        //test sending an unsuccessful (mocked) post
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(2).perform(click());
        //make sure message has been restored
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(parameters.defaultShareMessage))));
        //type a link with a random number appended to circumvent twitter complaining about duplicate post
        onView(withId(R.id.etMessage)).perform(typeTextIntoFocusedView(linkedInText), closeSoftKeyboard());

        onView(withId(R.id.btnSend)).perform(click());
        //failure shouldn't dismiss the dialog
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.loadingPanel)).check(matches(not(isDisplayed())));
        verify(requestManager, times(2)).postToLinkedIn(any(LinkedInApi.LinkedInPostRequest.class), any(RequestManager.RequestCompletion.class));

        onView(withId(R.id.btnCancel)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.llMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        //TODO: testing toast (didn't work)
        //onView(withText("Unable to post, please try again!")).inRoot(withDecorView(not(is(mActivityTestIntentRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void testTwitter() {
        //TODO: finish the test for twitterloginactivity once we can type into webviews, strategy outlined below
        //mock token as null so login screen gets presented
        //when(ambassadorConfig.getTwitterAccessToken()).thenReturn(null);

        //when twitterLoginRequest gets called, return a mock of the request object
/*        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion)object[0];
                //return a mock of the request object
                //completion.onSuccess("success");
                return null;
            }
        })
        .when(requestManager).twitterLoginRequest(any(RequestManager.RequestCompletion.class));*/

        //then, mock the response of requestToken.getAuthenticationUrl() so webview comes up

        //start recording fired Intents
        //Intents.init();
        //click twitter icon
        //onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //intended(hasComponent(TwitterLoginActivity.class.getName()));
        //stop recording Intents
        //Intents.release();

        //TODO: Espresso Web API to test WebViews not ready for prime time - too much trouble getting this to work - will come back
        //TODO: to this later to attempt to enter text into WebView fields to authenticate
        //onWebView().withElement(findElement(Locator.ID, "username")).perform(webKeys("test@sf.com"));

        //onView(withId(R.id.llMainLayout)).check(matches(not(isDisplayed())));
        //pressBack();

        onView(withId(R.id.llMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        when(ambassadorConfig.getTwitterAccessToken()).thenReturn("2925003771-TBomtq36uThf6EqTKggITNHqOpl6DDyGMb5hLvz");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] object = invocation.getArguments();
                AmbassadorConfig.NullifyCompleteListener completion = (AmbassadorConfig.NullifyCompleteListener) object[0];
                completion.nullifyComplete();
                return null;
            }
        }).when(ambassadorConfig).nullifyTwitterIfInvalid(any(AmbassadorConfig.NullifyCompleteListener.class));

        onView(withText("TWITTER")).perform(click());

        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.etMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHeaderText)).check(matches(isDisplayed()));
        onView(withId(R.id.ivHeaderImg)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSend)).check(matches(isDisplayed()));
        onView(withId(R.id.loadingPanel)).check(matches(not(isDisplayed())));onView(withText("Twitter Post")).check(matches(isDisplayed()));

        Espresso.closeSoftKeyboard();
        pressBack();
        verify(requestManager, never()).postToTwitter(anyString(), any(RequestManager.RequestCompletion.class));

        //ensure dialog fields not visible now that we've backed out
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());

        //click twitter icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //enter blank text and make sure dialog is still visible
        onView(withId(R.id.etMessage)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.btnSend)).perform(click());
        verify(requestManager, never()).postToTwitter(anyString(), any(RequestManager.RequestCompletion.class));

        //since text was cleared, ensure dialog is present, then click "send anyway"
        onView(withText("Hold on!")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        pressBack();
        verify(requestManager, never()).postToTwitter(anyString(), any(RequestManager.RequestCompletion.class));

        //test sending a successful (mocked) tweet
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //make sure message has been restored
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(parameters.defaultShareMessage))));

        //type a link with a random number appended to circumvent twitter complaining about duplicate post
        String tweetText = _getRandomNumber();
        onView(withId(R.id.etMessage)).perform(typeTextIntoFocusedView(tweetText), closeSoftKeyboard());

        doNothing().when(requestManager).bulkShareTrack(any(BulkShareHelper.SocialServiceTrackType.class));
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion)object[1];
                completion.onSuccess("success");
                return null;
            }
        })
        .doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] object = invocation.getArguments();
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion)object[1];
                completion.onFailure("fail");
                return null;
            }
        })
        .when(requestManager).postToTwitter(anyString(), any(RequestManager.RequestCompletion.class));

        onView(withId(R.id.btnSend)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());

        //test sending an unsuccessful (mocked) tweet
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //make sure message has been restored
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(parameters.defaultShareMessage))));

        //type a link with a random number appended to circumvent twitter complaining about duplicate post
        onView(withId(R.id.etMessage)).perform(typeTextIntoFocusedView(tweetText), closeSoftKeyboard());

        onView(withId(R.id.btnSend)).perform(click());
        //failure shouldn't dismiss the dialog
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.loadingPanel)).check(matches(not(isDisplayed())));
        verify(requestManager, times(2)).postToTwitter(anyString(), any(RequestManager.RequestCompletion.class));

        onView(withId(R.id.btnCancel)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.llMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        //TODO: testing toast (didn't work)
        //onView(withText("Unable to post, please try again!")).inRoot(withDecorView(not(is(mActivityTestIntentRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void testCustomImages() {
        int drawableId = context.getResources().getIdentifier("raf_logo", "drawable", context.getPackageName());
        int pos;
        try {
            pos = Integer.parseInt(context.getString(R.string.RAFLogoPosition));
        }
        catch (NumberFormatException e) {
            pos = 0;
        }

        if (drawableId != 0 && pos >= 1 && pos <= 5) {
            onView(withId(drawableId)).check(matches(isDisplayed()));
        }
        else {
            onView(withId(drawableId)).check(ViewAssertions.doesNotExist());
        }
    }

    private String _getRandomNumber() {
        double d = (Math.random() * 100)+1;
        d = Math.round(d);
        return Double.toString(d);
    }

    private static Matcher<View> _withRegex(final String regex) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View v) {
                String s = ((TextView) v).getText() != null ? ((TextView) v).getText().toString() : null;
                return (s != null && s.matches(regex));
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private boolean keyboardIsOpen() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }

}
