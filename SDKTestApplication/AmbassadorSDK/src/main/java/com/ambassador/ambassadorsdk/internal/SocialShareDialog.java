package com.ambassador.ambassadorsdk.internal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInApi;
import com.ambassador.ambassadorsdk.utils.StringResource;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

/**
 * Created by dylan on 12/7/15.
 */
public class SocialShareDialog extends Dialog {

    private ShareDialogEventListener eventListener;

    private TextView tvHeaderText;
    private ImageView ivHeaderImage;
    private TextView tvSend;
    private CustomEditText etMessage;
    private Button btnSend, btnCancel;
    private ProgressBar loader;

    @Inject
    RequestManager requestManager;

    @Inject
    AmbassadorConfig ambassadorConfig;

    public enum SocialNetwork {
        TWITTER, LINKEDIN
    }

    private SocialNetwork socialNetwork;

    private String blankErrorText;

    public SocialShareDialog(@ForActivity Context context) {
        super(context);
        AmbassadorSingleton.getInstanceComponent().inject(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_social_share);

        tvHeaderText = (TextView) findViewById(R.id.tvHeaderText);
        ivHeaderImage = (ImageView) findViewById(R.id.ivHeaderImg);

        tvSend = (TextView) findViewById(R.id.tvSend);

        etMessage = (CustomEditText) findViewById(R.id.etMessage);

        btnSend = (Button) findViewById(R.id.btnSend);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        loader = (ProgressBar) findViewById(R.id.loadingPanel);
        loader.setVisibility(View.GONE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareClicked();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                attemptNotifyCancelled();
            }
        });

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                etMessage.setText(ambassadorConfig.getRafParameters().defaultShareMessage);
            }
        });
    }

    public void setSocialNetwork(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        switch (socialNetwork) {
            case TWITTER:
                styleTwitter();
                break;
            case LINKEDIN:
                styleLinkedIn();
                break;
        }
    }

    private void styleTwitter() {
        tvHeaderText.setText("Twitter Post");
        tvHeaderText.setBackgroundColor(getContext().getResources().getColor(R.color.twitter_blue));
        ivHeaderImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.twitter_icon));
        tvSend.setText("Tweet");
        btnSend.setText("Tweet");

        blankErrorText = new StringResource(R.string.blank_twitter).getValue();
    }

    private void styleLinkedIn() {
        tvHeaderText.setText("LinkedIn Post");
        tvHeaderText.setBackgroundColor(getContext().getResources().getColor(R.color.linkedin_blue));
        ivHeaderImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.linkedin_icon));
        tvSend.setText("Send");
        btnSend.setText("Share");

        blankErrorText = new StringResource(R.string.blank_linkedin).getValue();
    }

    private void shareClicked() {
        if (Utilities.containsURL(etMessage.getText().toString(), ambassadorConfig.getURL())) {
            share();
        } else {
            Utilities.presentUrlDialog(this.getOwnerActivity(), etMessage, ambassadorConfig.getURL(), new Utilities.UrlAlertInterface() {
                @Override
                public void sendAnywayTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    share();
                }

                @Override
                public void insertUrlTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    private void share() {
        if (etMessage.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), blankErrorText, Toast.LENGTH_SHORT).show();
            etMessage.shakeEditText();
        } else {
            loader.setVisibility(View.VISIBLE);
            switch (socialNetwork) {
                case TWITTER:
                    requestManager.postToTwitter(etMessage.getText().toString(), twitterCompletion);
                    break;
                case LINKEDIN:
                    try {
                        JSONObject object = new JSONObject("{" +
                                "\"comment\": \"" + etMessage.getText().toString() + "\"," +
                                "\"visibility\": " + "{ \"code\": \"anyone\" }" +
                                "}");


                        LinkedInApi.LinkedInPostRequest request = new LinkedInApi.LinkedInPostRequest(etMessage.getText().toString());
                        requestManager.postToLinkedIn(request, linkedInCompletion);
                    } catch (JSONException e) {
                        Log.e(this.getClass().getSimpleName(), e.toString());
                    }
                    break;
            }
        }
    }

    private RequestManager.RequestCompletion twitterCompletion = new RequestManager.RequestCompletion() {
        @Override
        public void onSuccess(Object successResponse) {
            loader.setVisibility(View.GONE);
            Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
            requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.TWITTER);
            dismiss();
            attemptNotifySuccess();
        }

        @Override
        public void onFailure(Object failureResponse) {
            loader.setVisibility(View.GONE);
            if (((String) failureResponse).equals("auth")) {
                dismiss();
                attemptNotifyReauth();
            } else {
                Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
                attemptNotifyFailed();
            }
        }
    };

    private RequestManager.RequestCompletion linkedInCompletion = new RequestManager.RequestCompletion() {
        @Override
        public void onSuccess(Object successResponse) {
            loader.setVisibility(View.GONE);
            Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
            requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.LINKEDIN);
            dismiss();
            attemptNotifySuccess();
        }

        @Override
        public void onFailure(Object failureResponse) {
            loader.setVisibility(View.GONE);
            if (((String) failureResponse).contains("No authentication")) {
                dismiss();
                attemptNotifyReauth();
            } else {
                Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
                attemptNotifyFailed();
            }
        }
    };

    public interface ShareDialogEventListener {
        void postSuccess();
        void postFailed();
        void postCancelled();
        void needAuth();
    }

    public void setSocialDialogEventListener(ShareDialogEventListener listener) {
        this.eventListener = listener;
    }

    private void attemptNotifySuccess() {
        if (eventListener != null) {
            eventListener.postSuccess();
        }
    }

    private void attemptNotifyFailed() {
        if (eventListener != null) {
            eventListener.postFailed();
        }
    }

    private void attemptNotifyCancelled() {
        if (eventListener != null) {
            eventListener.postCancelled();
        }
    }

    private void attemptNotifyReauth() {
        if (eventListener != null) {
            eventListener.needAuth();
        }
    }

}
