package com.example.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by JakeDunahee on 8/6/15.
 */
public class TweetActivity extends Dialog {
    private EditText etTwitterMessage;
    private Button btnTweet, btnCancel;

    public TweetActivity(Context context) {
        super(context);
        setContentView(R.layout.dialog_twitter_tweet);

        etTwitterMessage = (EditText) findViewById(R.id.etTweetMessage);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    public void shareTweet() {
        AccessToken accessToken = new AccessToken(AmbassadorSingleton.getInstance().getTwitterToken(getContext()),
                AmbassadorSingleton.getInstance().getTwitterTokenSecret(getContext()));
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);
        twitter.setOAuthAccessToken(accessToken);

        try {
            twitter.updateStatus(etTwitterMessage.getText().toString());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
