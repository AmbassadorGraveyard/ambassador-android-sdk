package com.example.ambassador.ambassadorsdk;

import android.content.Intent;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class AmbassadorActivityTest {
    private ServiceSelectorPreferences parameters;
    private static final String EMAIL_PATTERN = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\\b";
    private static final String SMS_PATTERN = "Mobile (.*)";
    //TweetRequest tweetRequestSpy;

    @Inject
    TweetRequest tweetRequestMock;

    @Singleton
    @Component(modules = {MockTweetRequestModule.class})
    public interface TweetRequestComponent {
        TweetRequest provideMockTweetRequest();
    }

    @Rule
    public ActivityTestRule<AmbassadorActivity> mActivityTestIntentRule = new ActivityTestRule<>(AmbassadorActivity.class, true, false);

    //@Mock
    //TweetDialog tweetDialog;
    //TweetRequest tweetRequestMock;

    @Before
    public void beforeEachTest() {
        //tweetRequestMock = new TweetRequest();
        //tweetRequestSpy = spy(tweetRequestMock);

        parameters = new ServiceSelectorPreferences();
        parameters.defaultShareMessage = "Check out this company!";
        parameters.titleText = "RAF Params Welcome Title";
        parameters.descriptionText = "RAF Params Welcome Description";
        parameters.toolbarTitle = "RAF Params Toolbar Title";

        AmbassadorSingleton.getInstance().setCampaignID("260");
        AmbassadorSingleton.getInstance().saveAPIKey("UniversalToken ***REMOVED***");

        //save pusher data so we don't sit and wait for any unnecessary async tests to come back
        String pusher = "{\"email\":\"jake@getambassador.com\",\"firstName\":\"erer\",\"lastName\":\"ere\",\"phoneNumber\":\"null\",\"urls\":[{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"},]}";
        AmbassadorSingleton.getInstance().savePusherInfo(pusher);

        Intent intent = new Intent();
        intent.putExtra("test", parameters);
        mActivityTestIntentRule.launchActivity(intent);

        //MockitoAnnotations.initMocks(this);
        //TweetDialog.TweetRequest tweetRequestMock = mock(TweetDialog.TweetRequest.class);
    }

    @After
    public void afterEachTest() {
        AmbassadorSingleton.getInstance().savePusherInfo(null);
    }

    @Test
    public void testTest() {
        TweetRequestComponent component = DaggerAmbassadorActivityTest_TweetRequestComponent.builder().mockTweetRequestModule(new MockTweetRequestModule()).build();
        tweetRequestMock = component.provideMockTweetRequest();
        when(tweetRequestMock.testMethod()).thenReturn("mock");
        AmbassadorSingleton.getInstance().setTwitterAccessToken("2925003771-TBomtq36uThf6EqTKggITNHqOpl6DDyGMb5hLvz");
        AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret("WUg9QkrVoL3ndW6DwdpQAUvVaRcxhHUB2ED3PoUlfZFek");

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());

        //http://stackoverflow.com/questions/18074212/mockito-mock-async-method
        //doNothing().when(tweetRequestMock).execute();
        String tweetText = "http://www.tester.com " + _getRandomNumber();
        tweetRequestMock.tweetString = tweetText;
        onView(withId(R.id.etTweetMessage)).perform(typeText(tweetText), closeSoftKeyboard());
        onView(withId(R.id.btnTweet)).perform(click());
        //onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        //onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());
        //verify(tweetRequestMock).execute();
        verify(tweetRequestMock).testMethod();

        //now call callback onPostExecute
        //http://stackoverflow.com/questions/13616547/calling-callbacks-with-mockito
    }

    //@Test
    public void testMainLayout() {
        onView(withId(R.id.rlMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(withText(parameters.titleText)));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(withText(parameters.descriptionText)));
        onView(withId(R.id.etShortURL)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(withText("http://staging.mbsy.co/jHjl")));
        onView(withId(R.id.btnCopyPaste)).check(matches(isDisplayed()));
    }

    //@Test
    public void testFacebook() {
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(0).perform(click());
        //onView(withText("You must")).check(matches(isDisplayed()));
        onView(withId(16908290)).check(matches(isDisplayed()));
        pressBack();

        //TODO: Espresso Web API to test WebViews not ready for prime time - too much trouble getting this to work - will come back
        //to this later to attempt to enter text into WebView fields to authenticate
        //onWebView().withElement(findElement(Locator.ID, "username")).perform(webKeys("test@sf.com"));
    }

    //@Test
    public void testContactsEmail() {
        //start recording fired Intents
        Intents.init();
        //click email icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(3).perform(click());
        //check that the Intent fired
        intended(hasComponent(ContactSelectorActivity.class.getName()));
        //stop recording Intents
        Intents.release();

        pressBack();
        //make sure after we backed out that expected views are there
        onView(withId(R.id.rlMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));
        onView(withId(R.id.lvContacts)).check(ViewAssertions.doesNotExist());

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(3).perform(click());

        onView(withId(R.id.rlMaster)).check(matches(isDisplayed()));
        onView(withId(R.id.rlSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.etSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnDoneSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.lvContacts)).check(matches(isDisplayed()));
        onView(withId(R.id.llSendView)).check(matches(isDisplayed()));
        onView(withId(R.id.etShareMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.etShareMessage)).check(matches(withText(containsString("http://staging.mbsy.co/jHjl"))));
        onView(withId(R.id.btnEdit)).check(matches(isDisplayed()));
        onView(withId(R.id.btnDone)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnSend)).check(matches(not(isEnabled())));
        onView(withId(R.id.tvNoContacts)).check(matches(not(isDisplayed())));

        //TODO: test search bar
        //COMMENTED OUT - CAN'T GET TOOLBAR TO SHOW UP IN TESTS - RESEARCH THIS - maybe find some other way to open the textbox
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
        onView(withId(R.id.btnSend)).perform(click());
        //TODO: test to make sure mock didn't get fired

        //test to make sure you're seeing email and not SMS
        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(0).onChildView(withId(R.id.tvNumberOrEmail)).check(matches(_withRegex(EMAIL_PATTERN)));

        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(0).perform(click());
        //onData(is(instanceOf(ContactObject.class))).inAdapterView(withId(R.id.lvContacts)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(0).onChildView(withId(R.id.ivCheckMark)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSend)).check(matches(isEnabled()));

        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(0).onChildView(withId(R.id.ivCheckMark)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnSend)).check(matches(not(isEnabled())));

        //select a contact
        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(1).perform(click());
        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(1).onChildView(withId(R.id.ivCheckMark)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(0).onChildView(withId(R.id.ivCheckMark)).check(matches(not(isDisplayed())));
        onView(withId(R.id.etShareMessage)).check(matches(not(isEnabled())));
        onView(withId(R.id.btnEdit)).perform(click());
        onView(withId(R.id.etShareMessage)).check(matches(isEnabled()));
        //nothing should happen when no contacts selected
        onView(withId(R.id.btnSend)).perform(click());
        onView(withId(R.id.btnDone)).perform(click());
        //share message should not be editable after done button clicked
        onView(withId(R.id.etShareMessage)).check(matches(not(isEnabled())));
        onView(withId(R.id.btnEdit)).perform(click());
        onView(withId(R.id.etShareMessage)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.etShareMessage)).perform(typeText("test"), closeSoftKeyboard());
        onView(withId(R.id.btnDone)).perform(click());
        onView(withId(R.id.btnSend)).perform(click());
        //dialog "url not entered" should be showing at this point - since I can't check that programmatically-created dialog, just check that underlying views are not present
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        pressBack();
        onView(withId(R.id.btnEdit)).perform(click());
        onView(withId(R.id.etShareMessage)).perform(typeText("http://staging.mbsy.co/jHjl"), closeSoftKeyboard());
        onView(withId(R.id.btnDone)).perform(click());

        //onView(withId(R.id.btnSend)).perform(click());
        //TODO: test actually sending (mock?)

        //TODO: after figuring out how to use mock list of contacts, test deleting one to make sure NO CONTACTS textview is shown
    }

    //@Test
    public void testContactsSMS() {
        //start recording fired Intents
        Intents.init();
        //click sms icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(4).perform(click());
        //check that the Intent fired
        intended(hasComponent(ContactSelectorActivity.class.getName()));
        //stop recording Intents
        Intents.release();

        pressBack();
        //make sure after we backed out that expected views are there
        onView(withId(R.id.rlMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));
        onView(withId(R.id.lvContacts)).check(ViewAssertions.doesNotExist());

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(4).perform(click());

        //test to make sure you're seeing SMS and not email
        onData(anything()).inAdapterView(withId(R.id.lvContacts)).atPosition(0).onChildView(withId(R.id.tvNumberOrEmail)).check(matches(_withRegex(SMS_PATTERN)));
    }

    //@Test
    public void testLinkedIn() {

    }

    //@Test
    public void testTwitter() {
        //clear the token
        AmbassadorSingleton.getInstance().setTwitterAccessToken(null);
        AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret(null);
        //start recording fired Intents
        Intents.init();
        //click twitter icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        intended(hasComponent(TwitterLoginActivity.class.getName()));
        //stop recording Intents
        Intents.release();

        pressBack();

        onView(withId(R.id.rlMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        //set a token so we can test share link - this is for test embassy twitter account (developers@getambassador.com - https://twitter.com/testmbsy)
        AmbassadorSingleton.getInstance().setTwitterAccessToken("2925003771-TBomtq36uThf6EqTKggITNHqOpl6DDyGMb5hLvz");
        AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret("WUg9QkrVoL3ndW6DwdpQAUvVaRcxhHUB2ED3PoUlfZFek");

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
        //onView(withId(R.id.tvTweet)).check(matches(isDisplayed()));
        onView(withText("Twitter Post")).check(matches(isDisplayed()));
        pressBack();

        //ensure dialog fields not visible now that we've backed out
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());

        //click twitter icon
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //enter blank text and make sure dialog is still visible
        onView(withId(R.id.etTweetMessage)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.btnTweet)).perform(click());

        //onView(withText("INSERT LINK")).perform(click());
        //onView(withId(android.R.id.button2)).perform(click());
        //TODO: can't get the programmatically-created AlertDialog to be visible to Espresso with above two lines
        //see https://code.google.com/p/android-test-kit/issues/detail?id=60
        //instead, just make sure the twitter dialog isn't there anymore, then back out
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        pressBack();

        onView(withId(R.id.dialog_twitter_layout)).check(matches(isDisplayed()));
        pressBack();

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //make sure message has been restored
        onView(withId(R.id.etTweetMessage)).check(matches(withText(containsString(parameters.defaultShareMessage))));

        //TODO: THE BELOW CODE WORKS, BUT SINCE I COULDN'T GET MOCKING TO WORK ON TWEETREQUEST, IT WILL ACTUALLY SEND TWEETS
        //THE VERIFY TEST PASSES, BUT IT'S COMMENTED OUT TO AVOID SPAMMING THE TWITTER
        //WILL REVISIT ONCE WE CAN FIGURE OUT MOCKING TWEETREQUEST

        /*doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //fakeData = "FakeDataString";
                return null;
            }
        }).when(tweetRequestMock).execute();*/
        //}).when(tweetRequestSpy).execute();

        //TweetDialog.TweetRequest tweetRequestMock = mock(TweetDialog.TweetRequest.class);
        //doNothing().when(tweetRequestSpy).execute();
        doNothing().when(tweetRequestMock).execute();
        //when(tweetRequestMock.execute()).thenReturn(null);

        //type a link with a random number appended to circumvent twitter complaining about duplicate post
        /*String tweetText = "http://www.tester.com " + _getRandomNumber();
        tweetRequestMock.tweetString = tweetText;
        onView(withId(R.id.etTweetMessage)).perform(typeText(tweetText), closeSoftKeyboard());
        onView(withId(R.id.btnTweet)).perform(click());
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());
        verify(tweetRequestMock).execute();*/

        //TODO: testing toast (didn't work)
        //onView(withText("Unable to post, please try again!")).inRoot(withDecorView(not(is(mActivityTestIntentRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private int _getRandomNumber() {
        double d = Math.random() * 100;
        return (int)d + 1;
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
