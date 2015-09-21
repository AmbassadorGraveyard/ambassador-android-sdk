package com.example.ambassador.ambassadorsdk;

import android.os.Handler;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by coreyfields on 9/19/15.
 */
public class TweetRequest {
    public AsyncResponse mCallback = null;
    private int postStatus;
    final Handler mHandler = new Handler();

    public interface AsyncResponse {
        void processTweetRequest(int postStatus);
    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            mCallback.processTweetRequest(postStatus);
        }
    };

    public void tweet(final String tweetString) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                AccessToken accessToken = new AccessToken(AmbassadorSingleton.getInstance().getTwitterAccessToken(),
                        AmbassadorSingleton.getInstance().getTwitterAccessTokenSecret());
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);
                twitter.setOAuthAccessToken(accessToken);

                try {
                    twitter.updateStatus(tweetString);
                    postStatus = 200;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    postStatus = 400;
                }

                mHandler.post(mUpdateResults);
            }
        });

        thread.start();
    }
}