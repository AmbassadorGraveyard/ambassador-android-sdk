package com.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by JakeDunahee on 8/6/15.
 */

@Singleton
class TweetDialog extends Dialog {
    private CustomEditText etTwitterMessage;
    private ProgressBar loader;

    @Inject
    RequestManager requestManager;

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Inject
    public TweetDialog(@ForActivity Context context) {
        super(context);

        //get injected modules we need
        AmbassadorSingleton.getComponent().inject(this);

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
                etTwitterMessage.setText(ambassadorConfig.getRafParameters().defaultShareMessage);
            }
        });
    }

    private void _shareTweet() {
        if (etTwitterMessage.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Cannot share a blank Tweet", Toast.LENGTH_SHORT).show();
            etTwitterMessage.shakeEditText();
        } else {
            loader.setVisibility(View.VISIBLE);
            requestManager.postToTwitter(etTwitterMessage.getText().toString(), new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getOwnerActivity(), "Posted successfully!", Toast.LENGTH_SHORT).show();
                    requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.TWITTER);
                    dismiss();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getOwnerActivity(), "Unable to post, please try again!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void _btnTweetClicked() {
        if (Utilities.containsURL(etTwitterMessage.getText().toString(), ambassadorConfig.getURL())) {
            _shareTweet();
        } else {
            Utilities.presentUrlDialog(this.getOwnerActivity(), etTwitterMessage, ambassadorConfig.getURL(), new Utilities.UrlAlertInterface() {
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
}
