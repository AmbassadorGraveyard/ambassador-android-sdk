package com.example.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 8/6/15.
 */

class TweetDialog extends Dialog implements TweetRequest.AsyncResponse {
    private CustomEditText etTwitterMessage;
    private ProgressBar loader;

    @Inject
    TweetRequest tweetRequest;

    public TweetDialog(Context context) {
        super(context);
        MyApplication.component().inject(this);

        //TweetDialogComponent component = DaggerTweetDialogComponent.builder().tweetDialogModule(new TweetDialogModule(this)).build();
        //tweetDialog = component.provideTweetDialog();
        //TweetRequestComponent component = DaggerTweetRequestComponent.builder().tweetRequestModule(new TweetRequestModule()).build();
        //tweetRequest = component.provideTweetRequest();
        //this.tweetRequest = tweetRequest;

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_twitter_tweet);

        // UI Components
        etTwitterMessage = (CustomEditText) findViewById(R.id.etTweetMessage);
        Button btnTweet = (Button) findViewById(R.id.btnTweet);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        loader = (ProgressBar) findViewById(R.id.loadingPanel);

        loader.setVisibility(View.GONE);
        etTwitterMessage.setEditTextTint(context.getResources().getColor(R.color.twitter_blue));

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnTweetClicked();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                etTwitterMessage.setText(AmbassadorSingleton.getInstance().rafParameters.defaultShareMessage);
            }
        });
    }

    private void _shareTweet() {
        if (etTwitterMessage.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Cannot share a blank Tweet", Toast.LENGTH_SHORT).show();
            etTwitterMessage.shakeEditText();
        } else {
            loader.setVisibility(View.VISIBLE);
            //TweetRequest tweetRequest = new TweetRequest();
            tweetRequest.mCallback = this;
            tweetRequest.tweetString = etTwitterMessage.getText().toString();
            tweetRequest.execute();
            //tweetRequest.testMethod();
        }
    }

    private void _btnTweetClicked() {
        if (Utilities.containsURL(etTwitterMessage.getText().toString())) {
            _shareTweet();
        } else {
            Utilities.presentUrlDialog(this.getOwnerActivity(), etTwitterMessage, new Utilities.UrlAlertInterface() {
                @Override
                public void sendAnywayTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    _shareTweet();
                }

                @Override
                public void insertUrlTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    //@Override
    public void processTweetRequest(int postStatus) {
        loader.setVisibility(View.GONE);

        // Make sure post was successful and handle it if it wasn't
        if (postStatus < 300 && postStatus > 199) {
            Toast.makeText(getOwnerActivity(), "Posted successfully!", Toast.LENGTH_SHORT).show();
            hide();
            dismiss();
        } else {
            Toast.makeText(getOwnerActivity(), "Unable to post, please try again!", Toast.LENGTH_SHORT).show();
        }
    }
}
