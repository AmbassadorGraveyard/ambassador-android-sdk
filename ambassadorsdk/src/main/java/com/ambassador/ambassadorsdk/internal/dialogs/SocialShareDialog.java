package com.ambassador.ambassadorsdk.internal.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.injection.ForActivity;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.ambassadorsdk.internal.views.ShakableEditText;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Dialog to handle sharing to Twitter or LinkedIn.
 */
public final class SocialShareDialog extends Dialog {
    @Bind(B.id.tvHeaderText)    protected TextView          tvHeaderText;
    @Bind(B.id.ivHeaderImg)     protected ImageView         ivHeaderImg;
    @Bind(B.id.tvSend)          protected TextView          tvSend;
    @Bind(B.id.etMessage)       protected ShakableEditText  etMessage;
    @Bind(B.id.btnSend)         protected Button            btnSend;
    @Bind(B.id.btnCancel)       protected Button            btnCancel;
    @Bind(B.id.pbLoading)       protected ProgressBar       pbLoading;

    @Inject protected RequestManager    requestManager;
    @Inject protected Campaign          campaign;
    @Inject protected RAFOptions        raf;
    @Inject protected Utilities         Utilities;

    protected ShareDialogEventListener  eventListener;
    protected SocialNetwork             socialNetwork;

    public enum SocialNetwork {
        FACEBOOK("facebook"),
        TWITTER("twitter"),
        LINKEDIN("linkedin");

        private String stringValue;
        SocialNetwork(String toString) {
            stringValue = toString;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public SocialShareDialog(@ForActivity Context context) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_social_share);

        AmbSingleton.getInstance().getAmbComponent().inject(this);
        ButterFork.bind(this);

        setupButtons();
        configureViews();

        switch (socialNetwork) {
            case FACEBOOK:
                styleFacebook();
                break;
            case TWITTER:
                styleTwitter();
                break;
            case LINKEDIN:
                styleLinkedIn();
                break;
            default:
                break;
        }
    }

    private void setupButtons() {
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
    }

    private void configureViews() {
        pbLoading.setVisibility(View.GONE);
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                etMessage.setText(raf.getDefaultShareMessage());
            }
        });
    }

    private void styleFacebook() {
        tvHeaderText.setText(new StringResource(R.string.facebook_share_dialog_title).getValue());
        tvHeaderText.setBackgroundColor(getContext().getResources().getColor(R.color.facebook_blue));
        ivHeaderImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.facebook_icon));
        tvSend.setText("Share");
        btnSend.setText("Share");
    }

    private void styleTwitter() {
        tvHeaderText.setText(new StringResource(R.string.twitter_share_dialog_title).getValue());
        tvHeaderText.setBackgroundColor(getContext().getResources().getColor(R.color.twitter_blue));
        ivHeaderImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.twitter_icon));
        tvSend.setText("Tweet");
        btnSend.setText("Tweet");
    }

    private void styleLinkedIn() {
        tvHeaderText.setText("LinkedIn Post");
        tvHeaderText.setBackgroundColor(getContext().getResources().getColor(R.color.linkedin_blue));
        ivHeaderImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.linkedin_icon));
        tvSend.setText("Send");
        btnSend.setText("Share");
    }

    private void shareClicked() {
        if (Utilities.containsURL(etMessage.getText().toString(), campaign.getUrl())) {
            share();
        } else {
            askForUrl();
        }
    }

    private void share() {
        if (etMessage.getText().toString().isEmpty()) {
            String blankErrorText;
            switch (socialNetwork) {
                case FACEBOOK:
                    blankErrorText = new StringResource(R.string.blank_facebook).getValue();
                    break;
                case TWITTER:
                    blankErrorText = new StringResource(R.string.blank_twitter).getValue();
                    break;
                case LINKEDIN:
                    blankErrorText = new StringResource(R.string.blank_linkedin).getValue();
                    break;
                default:
                    blankErrorText = "An error occurred";
                    break;
            }
            Toast.makeText(getOwnerActivity(), blankErrorText, Toast.LENGTH_SHORT).show();
            etMessage.shake();
        } else {
            pbLoading.setVisibility(View.VISIBLE);
            requestManager.shareWithEnvoy(socialNetwork.stringValue, etMessage.getText().toString(), requestCompletion);
        }
    }

    private void askForUrl() {
        final String url = campaign.getUrl();
        new AskUrlDialog(getContext(), url)
                .setOnCompleteListener(new AskUrlDialog.OnCompleteListener() {
                    @Override
                    public void dontAdd() {
                        share();
                    }

                    @Override
                    public void doAdd() {
                        insertURLIntoMessage(etMessage, url);
                    }
                })
                .show();
    }

    private void insertURLIntoMessage(EditText editText, String url) {
        String appendingLink = url;

        if (editText.getText().toString().contains("http://")) {
            String sub = editText.getText().toString().substring(editText.getText().toString().indexOf("http://"));
            String replacementSubstring;
            replacementSubstring = (sub.contains(" ")) ? sub.substring(0, sub.indexOf(' ')) : sub;
            editText.setText(editText.getText().toString().replace(replacementSubstring, appendingLink));
            return;
        }

        if (editText.getText().toString().length() != 0 && editText.getText().toString().charAt(editText.getText().toString().length() - 1) != ' ') {
            appendingLink = " " + url;
            editText.setText(editText.getText().append(appendingLink));
        } else {
            appendingLink = url;
            editText.setText(editText.getText().append(appendingLink));
        }
    }

    private RequestManager.RequestCompletion requestCompletion = new RequestManager.RequestCompletion() {
        @Override
        public void onSuccess(Object successResponse) {
            getOwnerActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();

                    BulkShareHelper.SocialServiceTrackType trackType;
                    switch(socialNetwork) {
                        case FACEBOOK:
                            trackType = BulkShareHelper.SocialServiceTrackType.FACEBOOK;
                            break;
                        case TWITTER:
                            trackType = BulkShareHelper.SocialServiceTrackType.TWITTER;
                            break;
                        case LINKEDIN:
                            trackType = BulkShareHelper.SocialServiceTrackType.LINKEDIN;
                            break;
                        default:
                            onFailure(null);
                            return;
                    }

                    requestManager.bulkShareTrack(trackType);

                    dismiss();
                    attemptNotifySuccess();
                }
            });
        }

        @Override
        public void onFailure(final Object failureResponse) {
            getOwnerActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
                    attemptNotifyFailed();
                }
            });
        }
    };

    public void setSocialNetwork(@NonNull SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public interface ShareDialogEventListener {
        void postSuccess();
        void postFailed();
        void postCancelled();
        void needAuth();
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

    public void setSocialDialogEventListener(@Nullable ShareDialogEventListener listener) {
        this.eventListener = listener;
    }

}
