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
import android.widget.TextView;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.activities.ambassador.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.contacts.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.activities.oauth.SocialOAuthActivity;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.inject.Inject;

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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class AmbassadorActivityTest {
    private static final String EMAIL_PATTERN = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\\b";
    private static final String SMS_PATTERN = "(Home|Work|Mobile|Other) (.*)";
    private Context context;

    //tell Dagger this code will participate in dependency injection
    @Inject
    public RequestManager requestManager;

    @Inject
    public BulkShareHelper bulkShareHelper;

    @Inject public User user;
    @Inject public Campaign campaign;
    @Inject public Auth auth;

    @Inject
    public PusherManager pusher;

    @Inject
    public RAFOptions raf;

    @Rule
    public ActivityTestRule<AmbassadorActivity> mActivityTestIntentRule = new ActivityTestRule<>(AmbassadorActivity.class, true, false);

    @Before
    public void beforeEachTest() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext().getApplicationContext();

        SystemAnimations systemAnimations = new SystemAnimations(context);
        systemAnimations.disableAll();

        //tell the application which component we want to use - in this case use the the one created above instead of the
        //application component which is created in the Application (and uses the real tweetRequest)
        AmbSingleton.init(context);
        TestModule amb = new TestModule();
        AmbSingleton.init(context, amb);
        AmbSingleton.inject(this);

        String pusherResponse = "{\"email\":\"jake@getambassador.com\",\"firstName\":\"\",\"lastName\":\"ere\",\"phoneNumber\":\"null\",\"urls\":[{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}]}";
        doNothing().when(campaign).setUrl(anyString());
        doNothing().when(campaign).setShortCode(anyString());
        doNothing().when(campaign).setEmailSubject(anyString());

        when(campaign.getId()).thenReturn("260");
        when(campaign.getUrl()).thenReturn("http://staging.mbsy.co/jHjl");
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(pusherResponse).getAsJsonObject();
        when(user.getPusherInfo()).thenReturn(o);
        when(auth.getSdkToken()).thenReturn("SDKToken ***REMOVED***");
        when(auth.getUniversalId()).thenReturn("***REMOVED***");

        //app workflow is identify -> backend calls pusher and triggers a response which is received by our app and
        //calls tryAndSetURL
        //if the app has a channel and it's not expired and connected, identify will be called right away.
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                _sendPusherIntent();
                return null;
            }
        })
        .when(requestManager).identifyRequest(Mockito.any(RequestManager.RequestCompletion.class));

        //if the app has a channel and it's not expired but it's not currently connected, it will connectAndSubscribe to the existing channel
        //mock the connectAndSubscribe call, bypass identify in the callback, instead send the intent which will call tryAndSetURL
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                _sendPusherIntent();
                return null;
            }
        }).when(pusher).subscribeChannelToAmbassador();

        //otherwise, the app will resubscribe to pusher and then call identify
        //mock the createPusher call, bypass identify in the callback, instead send the intent which will call tryAndSetURL
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                _sendPusherIntent();
                return null;
            }
        }).when(pusher).startNewChannel();

        Intent intent = new Intent();
        mActivityTestIntentRule.launchActivity(intent);
    }

    private void _sendPusherIntent() {
        Intent intent = new Intent("pusherData");
        LocalBroadcastManager.getInstance(AmbSingleton.getInstance().getContext()).sendBroadcast(intent);
    }

    @After
    public void afterEachTest() {
    }

    @Test
    public void testMainLayout() {
        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(isDisplayed()));
        // TODO: mark
        onView(withId(R.id.tvWelcomeTitle)).check(matches(withText(raf.getTitleText())));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(withText(raf.getDescriptionText())));
        onView(withId(R.id.etShortURL)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(withText("http://staging.mbsy.co/jHjl")));
        onView(withId(R.id.btnCopy)).check(matches(isDisplayed()));
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
        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));
        onView(withId(R.id.rvContacts)).check(ViewAssertions.doesNotExist());

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(3).perform(click());

        onView(withId(R.id.rlParent)).check(matches(isDisplayed()));
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
        //onData(is(instanceOf(Contact.class))).inAdapterView(withId(R.id.rvContacts)).atPosition(0).perform(click());
        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

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
        onView(withId(R.id.rlParent)).check(matches(isDisplayed()));
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
        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
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
        JsonParser parser = new JsonParser();
        String pusherResponse = "{\"email\":\"jake@getambassador.com\",\"firstName\":\"\",\"lastName\":\"\",\"phoneNumber\":\"null\",\"urls\":[{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"}]}";
        JsonObject o = parser.parse(pusherResponse).getAsJsonObject();
        when(user.getPusherInfo()).thenReturn(o);

        onView(withId(R.id.rvContacts)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.ivCheckMark)).check(matches(isDisplayed()));

        onView(withId(R.id.rlSend)).perform(click());
        //contact name dialog should be displayed
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        pressBack();
        Thread.sleep(120);
        onView(withId(R.id.dialog_contact_name)).check(ViewAssertions.doesNotExist());

        onView(withId(R.id.rlSend)).perform(click());
        //contact name dialog should be displayed
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        Espresso.closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.btnContinue)).perform(click());
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));

        onView(withId(R.id.btnCancel)).perform(click());
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.tvNumberOrEmail)).check(matches(_withRegex(SMS_PATTERN)));
        onView(TestUtils.withRecyclerView(R.id.rvContacts).atPositionOnView(0, R.id.ivCheckMark)).check(matches(isDisplayed()));
        onView(withId(R.id.rlSend)).perform(click());

        //contact name dialog should be displayed
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        onView(withId(R.id.etFirstName)).perform(typeText("Test"));
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        onView(withId(R.id.btnContinue)).perform(click());
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));
        onView(withId(R.id.etLastName)).perform(typeText("User"));
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);

        doNothing().when(user).setPusherInfo(any(JsonObject.class));
        doNothing().when(user).setFirstName(anyString());
        doNothing().when(user).setLastName(anyString());

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

        ///this call will fail, so make sure the dialog is still present and the mocks never get called
        onView(withId(R.id.btnContinue)).perform(click());
        onView(withId(R.id.dialog_contact_name)).check(matches(isDisplayed()));
        verify(bulkShareHelper, never()).bulkShare(anyString(), anyList(), anyBoolean(), any(BulkShareHelper.BulkShareCompletion.class));

        //this call will succeed, so make sure the mocks get called the appropriate number of times, the dialog is not present, and the main layout appears
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);
        onView(withId(R.id.btnContinue)).perform(click());
        Thread.sleep(250);
        onView(withId(R.id.dialog_contact_name)).check(ViewAssertions.doesNotExist());
        verify(requestManager, times(2)).updateNameRequest(anyString(), anyString(), anyString(), any(RequestManager.RequestCompletion.class));

        //since we never actually send the request, the identified callback never gets sent
        //so this tests is mimicing if the identify response from updateName never came back
        //in that case the view will stay on the list of contacts
        onView(withId(R.id.rvContacts)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.llParent)).check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testLinkedIn() {
        //TODO: test linkedInLoginActivity (see strategy in testTwitter)

        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        when(user.getLinkedInAccessToken()).thenReturn("AQV6mLXj7R7mEh88l_wPxg8x7V4ExwgQVFW0tcYHBoxaEP6KpzENTFQl-K1h0_V05pBNyTZlo0KDNQm3ZLPf62DjZxwfkLNhjeGLobVQUaMAseP8jdIQW_kKpMy7uIxr4T8PjrK8QP7XBsy3ibeuV2yhLrOJrOFA6LarWBcm0YGArhY1Wx8");

        onView(withText("LINKEDIN")).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.etMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHeaderText)).check(matches(isDisplayed()));
        onView(withId(R.id.ivHeaderImg)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSend)).check(matches(isDisplayed()));
        onView(withId(R.id.pbLoading)).check(matches(not(isDisplayed())));
        onView(withText("LinkedIn Post")).check(matches(isDisplayed()));
        Espresso.closeSoftKeyboard();
        pressBack();
        verify(requestManager, never()).shareWithEnvoy(eq("linkedin"), anyString(), any(RequestManager.RequestCompletion.class));

        //ensure dialog fields not visible now that we've backed out
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());

        //click linkedin icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(2).perform(click());
        //enter blank text and make sure dialog is still visible
        onView(withId(R.id.etMessage)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.btnSend)).perform(click());
        verify(requestManager, never()).shareWithEnvoy(eq("linkedin"), anyString(), any(RequestManager.RequestCompletion.class));

        //since text was cleared, ensure dialog is present, then click "send anyway"
        onView(withText("Hold on!")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        pressBack();
        verify(requestManager, never()).shareWithEnvoy(eq("linkedin"), anyString(), any(RequestManager.RequestCompletion.class));

        //test sending a successful (mocked) post
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(2).perform(click());
        //make sure message has been restored
        // TODO: mark
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(raf.getDefaultShareMessage()))));

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
        .when(requestManager).shareWithEnvoy(eq("linkedin"), anyString(), any(RequestManager.RequestCompletion.class));


        onView(withId(R.id.btnSend)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.pbLoading)).check(ViewAssertions.doesNotExist());

        //test sending an unsuccessful (mocked) post
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(2).perform(click());
        //make sure message has been restored
        // TODO: mark
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(raf.getDefaultShareMessage()))));
        //type a link with a random number appended to circumvent twitter complaining about duplicate post
        onView(withId(R.id.etMessage)).perform(typeTextIntoFocusedView(linkedInText), closeSoftKeyboard());

        onView(withId(R.id.btnSend)).perform(click());
        //failure shouldn't dismiss the dialog
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.pbLoading)).check(matches(not(isDisplayed())));
        verify(requestManager, times(2)).shareWithEnvoy(eq("linkedin"), anyString(), any(RequestManager.RequestCompletion.class));


        onView(withId(R.id.btnCancel)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.pbLoading)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

//        //TODO: testing toast (didn't work)
//        //onView(withText("Unable to post, please try again!")).inRoot(withDecorView(not(is(mActivityTestIntentRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void testTwitter() throws Exception {
        //TODO: finish the test for twitterloginactivity once we can type into webviews, strategy outlined below

        //start recording fired Intents
        Intents.init();
        //click twitter icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        intended(hasComponent(SocialOAuthActivity.class.getName()));
        //stop recording Intents
        Intents.release();

        //TODO: Espresso Web API to test WebViews not ready for prime time - too much trouble getting this to work - will come back
        //TODO: to this later to attempt to enter text into WebView fields to authenticate
        //onWebView().withElement(findElement(Locator.ID, "username")).perform(webKeys("test@sf.com"));

        //onView(withId(R.id.llParent)).check(matches(not(isDisplayed())));
        //pressBack();

        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        when(user.getTwitterAccessToken()).thenReturn("2925003771-TBomtq36uThf6EqTKggITNHqOpl6DDyGMb5hLvz");

        onView(withText("TWITTER")).perform(click());

        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.etMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHeaderText)).check(matches(isDisplayed()));
        onView(withId(R.id.ivHeaderImg)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSend)).check(matches(isDisplayed()));
        onView(withId(R.id.pbLoading)).check(matches(not(isDisplayed())));
        onView(withText("Twitter Post")).check(matches(isDisplayed()));

        Espresso.closeSoftKeyboard();
        pressBack();
        verify(requestManager, never()).shareWithEnvoy(Mockito.eq("twitter"), Mockito.anyString(), Mockito.any(RequestManager.RequestCompletion.class));

        //ensure dialog fields not visible now that we've backed out
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());

        //click twitter icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //enter blank text and make sure dialog is still visible
        onView(withId(R.id.etMessage)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.btnSend)).perform(click());
        verify(requestManager, never()).shareWithEnvoy(eq("Twitter"), anyString(), any(RequestManager.RequestCompletion.class));

        //since text was cleared, ensure dialog is present, then click "send anyway"
        onView(withText("Hold on!")).check(matches(isDisplayed()));
        Thread.sleep(200);
        Espresso.closeSoftKeyboard();
        Thread.sleep(200);
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        pressBack();
        verify(requestManager, never()).shareWithEnvoy(eq("Twitter"), anyString(), any(RequestManager.RequestCompletion.class));

        //test sending a successful (mocked) tweet
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //make sure message has been restored
        // TODO: mark
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(raf.getDefaultShareMessage()))));

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
        .when(requestManager).shareWithEnvoy(Mockito.eq("Twitter"), anyString(), any(RequestManager.RequestCompletion.class));

        onView(withId(R.id.btnSend)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.pbLoading)).check(ViewAssertions.doesNotExist());

        //test sending an unsuccessful (mocked) tweet
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //make sure message has been restored

        // TODO: mark
        onView(withId(R.id.etMessage)).check(matches(withText(containsString(raf.getDefaultShareMessage()))));

        //type a link with a random number appended to circumvent twitter complaining about duplicate post
        onView(withId(R.id.etMessage)).perform(typeTextIntoFocusedView(tweetText), closeSoftKeyboard());

        onView(withId(R.id.btnSend)).perform(click());
        //failure shouldn't dismiss the dialog
        onView(withId(R.id.dialog_social_share_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.pbLoading)).check(matches(not(isDisplayed())));
        verify(requestManager, times(2)).shareWithEnvoy(eq("Twitter"), anyString(), any(RequestManager.RequestCompletion.class));

        onView(withId(R.id.btnCancel)).perform(click());
        onView(withId(R.id.dialog_social_share_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.pbLoading)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.llParent)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        //TODO: testing toast (didn't work)
        //onView(withText("Unable to post, please try again!")).inRoot(withDecorView(not(is(mActivityTestIntentRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
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

}
