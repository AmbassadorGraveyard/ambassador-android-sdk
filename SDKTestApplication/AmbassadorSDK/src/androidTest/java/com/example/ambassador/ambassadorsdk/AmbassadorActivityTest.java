package com.example.ambassador.ambassadorsdk;

import android.content.Intent;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class AmbassadorActivityTest {
    private ServiceSelectorPreferences parameters;
    //TweetRequest tweetRequestSpy;

    @Rule
    public ActivityTestRule<AmbassadorActivity> mActivityTestIntentRule = new ActivityTestRule<>(AmbassadorActivity.class, true, false);

    @Mock
    //TweetDialog tweetDialog;
    TweetDialog.TweetRequest tweetRequestMock;

    @Before
    public void beforeEachTest() {
        //tweetRequestMock = new TweetRequest();
        //tweetRequestSpy = spy(tweetRequestMock);

        parameters = new ServiceSelectorPreferences();
        parameters.defaultShareMessage = "Check out this company!";
        parameters.titleText = "RAF Params Welcome Title";
        parameters.descriptionText = "RAF Params Welcome Description";
        parameters.toolbarTitle = "RAF Params Toolbar Title";

        //set Campaign ID
        AmbassadorSingleton.getInstance().setCampaignID("260");

        //save pusher data so we don't sit and wait for any unnecessary async tests to come back
        String pusher = "{\"email\":\"jake@getambassador.com\",\"firstName\":\"erer\",\"lastName\":\"ere\",\"phoneNumber\":\"null\",\"urls\":[{\"url\":\"http://staging.mbsy.co\\/jHjl\",\"short_code\":\"jHjl\",\"campaign_uid\":260,\"subject\":\"Check out BarderrTahwn ®!\"},]}";
        AmbassadorSingleton.getInstance().savePusherInfo(pusher);

        Intent intent = new Intent();
        intent.putExtra("test", parameters);
        mActivityTestIntentRule.launchActivity(intent);

        MockitoAnnotations.initMocks(this);
        //TweetDialog.TweetRequest tweetRequestMock = mock(TweetDialog.TweetRequest.class);
    }

    @After
    public void afterEachTest() {

    }

    @Test
    public void testFacebook() {
        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(0).perform(click());
        //onView(withText("You must")).check(matches(isDisplayed()));
        onView(withId(16908290)).check(matches(isDisplayed()));
        pressBack();

        //Espresso Web API to test WebViews not ready for prime time - too much trouble getting this to work - will come back
        //to this later to attempt to enter text into WebView fields to authenticate
        //onWebView().withElement(findElement(Locator.ID, "username")).perform(webKeys("test@sf.com"));
    }

    @Test
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
        onView(withId(R.id.tvWelcomeTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeTitle)).check(matches(withText(parameters.titleText)));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWelcomeDesc)).check(matches(withText(parameters.descriptionText)));
        onView(withId(R.id.etShortURL)).check(matches(isDisplayed()));
        onView(withId(R.id.etShortURL)).check(matches(withText("http://staging.mbsy.co/jHjl")));
        onView(withId(R.id.btnCopyPaste)).check(matches(isDisplayed()));
        onView(withId(R.id.gvSocialGrid)).check(matches(isDisplayed()));

        //set a token so we can test share link - this is for test embassy twitter account (developers@getambassador.com - https://twitter.com/testmbsy)
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
        //can't get the programmatically-created AlertDialog to be visible to Espresso with above two lines
        //see https://code.google.com/p/android-test-kit/issues/detail?id=60
        //instead, just make sure the twitter dialog isn't there anymore, then back out
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        pressBack();

        onView(withId(R.id.dialog_twitter_layout)).check(matches(isDisplayed()));
        pressBack();

        onData(anything()).inAdapterView(withId(R.id.gvSocialGrid)).atPosition(1).perform(click());
        //make sure message has been restored
        onView(withId(R.id.etTweetMessage)).check(matches(withText(containsString(parameters.defaultShareMessage))));

        /*doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //fakeData = "FakeDataString";
                return null;
            }
        }).when(tweetRequestMock).execute();*/
        //}).when(tweetRequestSpy).execute();


        //type a link with a random number appended to circumvent twitter complaining about duplicate postsh
        //TweetDialog.TweetRequest tweetRequestMock = mock(TweetDialog.TweetRequest.class);
        //doNothing().when(tweetRequestSpy).execute();
        //doReturn(null).when(tweetRequestMock).execute();
        //when(tweetRequestMock.execute()).thenReturn(null);

        /*String tweetText = "http://www.tester.com " + _getRandomNumber();
        tweetRequestMock.tweetString = tweetText;
        onView(withId(R.id.etTweetMessage)).perform(typeText(tweetText), closeSoftKeyboard());
        onView(withId(R.id.btnTweet)).perform(click());
        onView(withId(R.id.dialog_twitter_layout)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.loadingPanel)).check(ViewAssertions.doesNotExist());
        verify(tweetRequestMock).execute();*/

        //testing toast (didn't work)
        //onView(withText("Unable to post, please try again!")).inRoot(withDecorView(not(is(mActivityTestIntentRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private int _getRandomNumber() {
        double d = Math.random() * 100;
        return (int)d + 1;
    }
}
