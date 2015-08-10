package com.example.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by JakeDunahee on 8/6/15.
 */
public class TweetActivity extends Dialog {
    private EditText etTwitterMessage;
    private Button btnTweet, btnCancel;
    private ProgressBar loader;

    public TweetActivity(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_twitter_tweet);

        etTwitterMessage = (EditText) findViewById(R.id.etTweetMessage);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        loader = (ProgressBar) findViewById(R.id.loadingPanel);

        loader.setVisibility(View.GONE);
        etTwitterMessage.setText(AmbassadorSingleton.getInstance().rafParameters.shareMessage);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTweet();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    public void shareTweet() {
        loader.setVisibility(View.VISIBLE);
        TweetRequest tweetRequest = new TweetRequest();
        tweetRequest.tweetString = etTwitterMessage.getText().toString();
        tweetRequest.execute();
    }

    class TweetRequest extends AsyncTask<Void, Void, Void> {
        public String tweetString;
        public int postStatus;

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
            loader.setVisibility(View.GONE);

            // Make sure post was successful and handle it if it wasn't
            if (postStatus < 300 && postStatus > 199) {
                Toast.makeText(getOwnerActivity(), "Posted successfully!", Toast.LENGTH_SHORT).show();
                hide();
            } else {
                Toast.makeText(getOwnerActivity(), "Unable to post, please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
