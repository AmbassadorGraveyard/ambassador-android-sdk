package com.example.ambassador.ambassadorsdk;

import android.os.AsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TweetRequest extends AsyncTask<Void, Void, Void> {
    public String tweetString;
    public int postStatus;
    public AsyncResponse mCallback = null;

    public interface AsyncResponse {
        void processTweetRequest(int postStatus);
    }

    @Override
    protected Void doInBackground(Void... params) {
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

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mCallback.processTweetRequest(postStatus);
    }
}