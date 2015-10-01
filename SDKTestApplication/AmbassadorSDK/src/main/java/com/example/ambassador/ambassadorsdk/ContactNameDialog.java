package com.example.ambassador.ambassadorsdk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

/**
 * Created by CoreyFields on 8/21/15.
 */
class ContactNameDialog extends Dialog {
    private CustomEditText etFirstName, etLastName;
    private ContactNameListener mCallback;
    private JSONObject pusherData;
    private ProgressDialog pd;

    @Inject
    AmbassadorSingleton ambassadorSingleton;

    @Inject
    RequestManager requestManager;

    // Interface
    public interface ContactNameListener {
        void namesHaveBeenUpdated();
    }

    // Constuctor
    public ContactNameDialog(Context context, ProgressDialog pd) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        MyApplication.getComponent().inject(this);
        this.pd = pd;
        mCallback = (ContactNameListener)getOwnerActivity();
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_contact_name);

        etFirstName = (CustomEditText)findViewById(R.id.etFirstName);
        etFirstName.setEditTextTint(context.getResources().getColor(R.color.ambassador_blue));
        etLastName = (CustomEditText)findViewById(R.id.etLastName);
        etLastName.setEditTextTint(context.getResources().getColor(R.color.ambassador_blue));
        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _continueSending();
            }
        });
    }

    public void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etFirstName, 0);
    }

    private void _continueSending() {
        if (etFirstName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Hmm, your entry is suspiciously blank", Toast.LENGTH_SHORT).show();
            etFirstName.shakeEditText();
        } else if (etLastName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Hmm, your entry is suspiciously blank", Toast.LENGTH_SHORT).show();
            etLastName.shakeEditText();
        } else {
            _handleNameInput(etFirstName.getText().toString(), etLastName.getText().toString());
        }
    }

    private void _handleNameInput(String firstName, String lastName) {
        try {
            pusherData = new JSONObject(ambassadorSingleton.getPusherInfo());
            pusherData.put("firstName", firstName);
            pusherData.put("lastName", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        //this shouldn't happen because UI enforces entry, but check anyway unless UI validation is removed
        if (firstName == null || lastName == null) return;

        pd.show();
        ambassadorSingleton.savePusherInfo(pusherData.toString());

        // Call api - on success we'll initiate the bulk share
        try {
            requestManager.updateNameRequest(pusherData.getString("email"), firstName, lastName, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    mCallback.namesHaveBeenUpdated();
                    ambassadorSingleton.saveUserFullName(etFirstName.getText().toString(), etLastName.getText().toString());
                    hide();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    Toast.makeText(getOwnerActivity(), "Unable to update information.  Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private class UpdateNameRequest extends AsyncTask<Void, Void, Void> {
//        int statusCode;
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            String url = "http://dev-ambassador-api.herokuapp.com/universal/action/identify/";
//            JSONObject dataObject = new JSONObject();
//            JSONObject nameObject = new JSONObject();
//
//            try {
//                dataObject.put("email", pusherData.getString("email"));
//                nameObject.put("first_name", pusherData.getString("firstName"));
//                nameObject.put("last_name", pusherData.getString("lastName"));
//                dataObject.put("update_data", nameObject);
//
//                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setRequestProperty("Authorization", ambassadorSingleton.getUniversalKey());
////                connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.MBSY_UNIVERSAL_ID);
//
//                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//                wr.writeBytes(dataObject.toString());
//                wr.flush();
//                wr.close();
//
//                statusCode = connection.getResponseCode();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (Utilities.isSuccessfulResponseCode(statusCode)) {
//                mCallback.namesHaveBeenUpdated();
//                ambassadorSingleton.saveUserFullName(etFirstName.getText().toString(), etLastName.getText().toString());
//                hide();
//            } else {
//                Toast.makeText(getOwnerActivity(), "Unable to update information.  Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
