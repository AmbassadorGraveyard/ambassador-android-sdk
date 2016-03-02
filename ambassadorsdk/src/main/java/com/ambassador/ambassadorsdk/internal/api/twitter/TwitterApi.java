package com.ambassador.ambassadorsdk.internal.api.twitter;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.auth.RequestToken;

/**
 * Handles Twitter API using Twitter4j.
 */
public final class TwitterApi {

    /**
     * Default constructor.
     */
    public TwitterApi() {

    }

    /**
     * Sets up a new AsyncTwitter object and sets API credentials.
     * @return the new AsyncTwitter object with credentials set.
     */
    protected AsyncTwitter getTwitter() {
        AsyncTwitter twitter = new AsyncTwitterFactory().getInstance();
        String twitterConsumerKey = new StringResource(R.string.twitter_consumer_key).getValue();
        String twitterConsumerSecret = new StringResource(R.string.twitter_consumer_secret).getValue();
        twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);

        return twitter;
    }

    /**
     * Requests a url from Twitter to use for OAuth authentication on a WebView.
     * @param requestCompletion the request completion callback.
     */
    public void getLoginUrl(final RequestManager.RequestCompletion requestCompletion) {
        AsyncTwitter twitter = getTwitter();
        twitter.addListener(new TwitterAdapter() {

            protected boolean complete = false;

            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                super.gotOAuthRequestToken(token);
                if (complete) return;
                requestCompletion.onSuccess(token.getAuthenticationURL());
                complete = true;
            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                super.onException(te, method);
                requestCompletion.onFailure(null);
            }

        });

        twitter.getOAuthRequestTokenAsync();
    }

}
