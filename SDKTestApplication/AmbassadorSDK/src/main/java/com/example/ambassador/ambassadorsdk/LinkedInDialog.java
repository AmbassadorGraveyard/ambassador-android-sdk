package com.example.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 7/27/15.
 */
class LinkedInDialog extends Dialog implements LinkedInRequest.AsyncResponse {
    CustomEditText etMessage;
    ProgressBar loader;

    @Inject
    LinkedInRequest linkedInRequest;

    @Inject
    public LinkedInDialog(@ForActivity Context context) {
        super(context);

        //get injected modules we need
        MyApplication.getComponent().inject(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_linkedin_post);

        // UI Components
        Button btnPost = (Button)findViewById(R.id.btnPost);
        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        etMessage = (CustomEditText)findViewById(R.id.etLinkedInMessage);

        loader = (ProgressBar)findViewById(R.id.loadingPanel);
        loader.setVisibility(View.GONE);

        etMessage.setEditTextTint(context.getResources().getColor(R.color.linkedin_blue));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnPostClicked();
            }
        });

        this.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                etMessage.setText(AmbassadorSingleton.getInstance().rafParameters.defaultShareMessage);
            }
        });
    }

    private void _postToLinkedIn() {
        if (etMessage.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Cannot share blank message", Toast.LENGTH_SHORT).show();
            etMessage.shakeEditText();
        } else {
            loader.setVisibility(View.VISIBLE);
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
            dismiss();
        } else {
            Toast.makeText(getOwnerActivity(), "Unable to post, please try again!", Toast.LENGTH_SHORT).show();
        }
    }
}


