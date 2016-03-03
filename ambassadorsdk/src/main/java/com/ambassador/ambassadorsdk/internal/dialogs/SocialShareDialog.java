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
import com.ambassador.ambassadorsdk.internal.api.linkedin.LinkedInApi;
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

    // region Views
    @Bind(B.id.tvHeaderText)    protected TextView          tvHeaderText;
    @Bind(B.id.ivHeaderImg)     protected ImageView         ivHeaderImg;
    @Bind(B.id.tvSend)          protected TextView          tvSend;
    @Bind(B.id.etMessage)       protected ShakableEditText  etMessage;
    @Bind(B.id.btnSend)         protected Button            btnSend;
    @Bind(B.id.btnCancel)       protected Button            btnCancel;
    @Bind(B.id.pbLoading)       protected ProgressBar       pbLoading;
    // endregion

    // region Dependencies
    @Inject protected RequestManager    requestManager;
    @Inject protected Campaign          campaign;
    @Inject protected RAFOptions        raf;
    // endregion

    // region Local members
    protected ShareDialogEventListener  eventListener;
    protected SocialNetwork             socialNetwork;
    // endregion

    public enum SocialNetwork {
        FACEBOOK, TWITTER, LINKEDIN
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
        AmbSingleton.inject(this);
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
            switch (socialNetwork) {
                case FACEBOOK:
                    requestManager.postToFacebook(etMessage.getText().toString(), facebookCompletion);
                    break;
                case TWITTER:
                    requestManager.postToTwitter(etMessage.getText().toString(), twitterCompletion);
                    break;
                case LINKEDIN:
                    LinkedInApi.LinkedInPostRequest request = new LinkedInApi.LinkedInPostRequest(etMessage.getText().toString());
                    requestManager.postToLinkedIn(request, linkedInCompletion);
                    break;
                default:
                    dismiss();
                    break;
            }
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

    private RequestManager.RequestCompletion facebookCompletion = new RequestManager.RequestCompletion() {
        @Override
        public void onSuccess(Object successResponse) {
            getOwnerActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
                    requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.FACEBOOK);
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
                    if (failureResponse != null && ((String) failureResponse).equals("auth")) {
                        dismiss();
                        attemptNotifyReauth();
                    } else {
                        Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
                        attemptNotifyFailed();
                    }
                }
            });
        }
    };

    private RequestManager.RequestCompletion twitterCompletion = new RequestManager.RequestCompletion() {
        @Override
        public void onSuccess(Object successResponse) {
            getOwnerActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
                    requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.TWITTER);
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
                    if (((String) failureResponse).equals("auth")) {
                        dismiss();
                        attemptNotifyReauth();
                    } else {
                        Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
                        attemptNotifyFailed();
                    }
                }
            });
        }
    };

    private RequestManager.RequestCompletion linkedInCompletion = new RequestManager.RequestCompletion() {
        @Override
        public void onSuccess(Object successResponse) {
            pbLoading.setVisibility(View.GONE);
            Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
            requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.LINKEDIN);
            dismiss();
            attemptNotifySuccess();
        }

        @Override
        public void onFailure(Object failureResponse) {
            pbLoading.setVisibility(View.GONE);
            if (((String) failureResponse).contains("No authentication")) {
                dismiss();
                attemptNotifyReauth();
            } else {
                Toast.makeText(getOwnerActivity(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
                attemptNotifyFailed();
            }
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

    private void attemptNotifyReauth() {
        if (eventListener != null) {
            eventListener.needAuth();
        }
    }

    public void setSocialDialogEventListener(@Nullable ShareDialogEventListener listener) {
        this.eventListener = listener;
    }

}
