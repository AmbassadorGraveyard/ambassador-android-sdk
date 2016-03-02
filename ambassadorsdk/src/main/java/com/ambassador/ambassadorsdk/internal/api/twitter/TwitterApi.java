package com.ambassador.ambassadorsdk.internal.api.twitter;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.auth.RequestToken;

/**
 * Handles Twitter API using Twitter4j.
 */
public final class TwitterApi {

    /** The AsyncTwitter Twitter4j object used to make requests. */
    protected AsyncTwitter twitter;

    /**
     * Default constructor.
     * Sets up the AsyncTwitter object to use on all requests.
     */
    public TwitterApi() {
        twitter = new AsyncTwitterFactory().getInstance();
        String twitterConsumerKey = new StringResource(R.string.twitter_consumer_key).getValue();
        String twitterConsumerSecret = new StringResource(R.string.twitter_consumer_secret).getValue();
        twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);    }

    /**
     * Requests a url from Twitter to use for OAuth authentication on a WebView.
     * @param requestCompletion the request completion callback.
     */
    public void getLoginUrl(final RequestManager.RequestCompletion requestCompletion) {
        twitter.addListener(new TwitterAdapter() {

            protected boolean complete = false;

            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                super.gotOAuthRequestToken(token);
                if (complete) return;
                requestCompletion.onSuccess(token.getAuthenticationURL());
                complete = true;
            }

        });
    }

}
