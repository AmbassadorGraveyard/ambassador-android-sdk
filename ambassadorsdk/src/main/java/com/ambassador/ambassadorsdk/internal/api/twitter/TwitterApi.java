package com.ambassador.ambassadorsdk.internal.api.twitter;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Handles Twitter API using Twitter4j.
 */
public final class TwitterApi {

    /** Keep track of a requestToken for authentication requests. */
    protected RequestToken requestToken;

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
                requestToken = token;
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

    /**
     * Trades an oauthVerifier token for an AccessToken using Twitter4j.
     * @param oauthVerifier the verifier token received from OAuth authenticating.
     * @param requestCompletion the request completion callback to hand back the AccessToken.
     */
    public void getAccessToken(String oauthVerifier, final RequestManager.RequestCompletion requestCompletion) {
        AsyncTwitter twitter = getTwitter();
        twitter.addListener(new TwitterAdapter() {

            @Override
            public void gotOAuthAccessToken(AccessToken token) {
                super.gotOAuthAccessToken(token);
                requestCompletion.onSuccess(token);
            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                super.onException(te, method);
                requestCompletion.onFailure(null);
            }

        });

        if (requestToken == null) return;
        twitter.getOAuthAccessTokenAsync(requestToken, oauthVerifier);
    }

}
