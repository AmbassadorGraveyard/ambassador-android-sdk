package com.example.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by JakeDunahee on 8/6/15.
 */

class TweetDialog extends Dialog {
    private CustomEditText etTwitterMessage;
    private ProgressBar loader;

    public TweetDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_twitter_tweet);

        // UI Components
        etTwitterMessage = (CustomEditText) findViewById(R.id.etTweetMessage);
        Button btnTweet = (Button) findViewById(R.id.btnTweet);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        loader = (ProgressBar) findViewById(R.id.loadingPanel);

        loader.setVisibility(View.GONE);
        etTwitterMessage.setEditTextTint(context.getResources().getColor(R.color.twitter_blue));
        etTwitterMessage.setText(AmbassadorSingleton.getInstance().rafParameters.shareMessage);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _shareTweet();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void _shareTweet() {
        if (etTwitterMessage.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Cannot share a blank Tweet", Toast.LENGTH_SHORT).show();
            etTwitterMessage.shakeEditText();
        } else {
            loader.setVisibility(View.VISIBLE);
            TweetRequest tweetRequest = new TweetRequest();
            tweetRequest.tweetString = etTwitterMessage.getText().toString();
            tweetRequest.execute();
        }
    }

    private class TweetRequest extends AsyncTask<Void, Void, Void> {
        public String tweetString;
        public int postStatus;

        @Override
        protected Void doInBackground(Void... params) {
            AccessToken accessToken = new AccessToken(AmbassadorSingleton.getInstance().getTwitterAccessToken(), AmbassadorSingleton.getInstance().getTwitterAccessTokenSecret());
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
                dismiss();
            } else {
                Toast.makeText(getOwnerActivity(), "Unable to post, please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
