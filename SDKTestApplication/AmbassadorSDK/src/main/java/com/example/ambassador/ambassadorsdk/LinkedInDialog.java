package com.example.ambassador.ambassadorsdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JakeDunahee on 7/27/15.
 */
class LinkedInDialog extends Dialog implements LinkedInRequest.AsyncResponse {
    Button btnPost, btnCancel;
    CustomEditText etMessage;
    AmbassadorActivity activity;
    ProgressBar loader;

    public LinkedInDialog(Activity activity) {
        super(activity);
        this.activity = (AmbassadorActivity)activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_linkedin_post);

        // UI Components
        btnPost = (Button) findViewById(R.id.btnTweet);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etMessage = (CustomEditText) findViewById(R.id.etTweetMessage);

        loader = (ProgressBar)findViewById(R.id.loadingPanel);
        loader.setVisibility(View.GONE);

        etMessage.setEditTextTint(getContext().getResources().getColor(R.color.linkedin_blue));
        etMessage.setText(AmbassadorSingleton.getInstance().rafParameters.defaultShareMessage);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnPostClicked();
            }
        });
    }

    private void _postToLinkedIn() {
        if (etMessage.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Cannot share blank message", Toast.LENGTH_SHORT).show();
            etMessage.shakeEditText();
        } else {
            loader.setVisibility(View.VISIBLE);
            LinkedInRequest linkedInRequest = new LinkedInRequest();

            String userMessage = etMessage.getText().toString();

            try {
                // Create JSON post object
                JSONObject object = new JSONObject("{" +
                        "\"comment\": \"" + userMessage + "\"," +
                        "\"visibility\": " + "{ \"code\": \"anyone\" }" +
                        "}");

                linkedInRequest.mCallback = this;
                linkedInRequest.send(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void _btnPostClicked() {
        if (Utilities.containsURL(etMessage.getText().toString())) {
            _postToLinkedIn();
        } else {
            Utilities.presentUrlDialog(this.getOwnerActivity(), etMessage, new Utilities.UrlAlertInterface() {
                @Override
                public void sendAnywayTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    _postToLinkedIn();
                }

                @Override
                public void insertUrlTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    public void processLinkedInRequest(int postStatus) {
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


