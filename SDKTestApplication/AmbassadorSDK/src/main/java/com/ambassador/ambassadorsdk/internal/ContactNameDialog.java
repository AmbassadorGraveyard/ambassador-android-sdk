package com.ambassador.ambassadorsdk.internal;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.R;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

/**
 * Created by CoreyFields on 8/21/15.
 */
public class ContactNameDialog extends Dialog {
    private CustomEditText etFirstName, etLastName;
    private ContactNameListener mCallback;
    private ProgressDialog pd;

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Inject
    RequestManager requestManager;

    public interface ContactNameListener {
        void namesHaveBeenUpdated();
    }

    public ContactNameDialog(Context context, ProgressDialog pd) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        AmbassadorSingleton.getComponent().inject(this);
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

        Button btnContinue = (Button)findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueSending();
            }
        });
    }

    public void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etFirstName, 0);
    }

    void continueSending() {
        if (etFirstName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Hmm, your entry is suspiciously blank", Toast.LENGTH_SHORT).show();
            etFirstName.shakeEditText();
        } else if (etLastName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Hmm, your entry is suspiciously blank", Toast.LENGTH_SHORT).show();
            etLastName.shakeEditText();
        } else {
            handleNameInput(etFirstName.getText().toString(), etLastName.getText().toString());
        }
    }

    void handleNameInput(final String firstName, final String lastName) {
        //this shouldn't happen because UI enforces entry, but check anyway in case UI validation is removed
        if (firstName == null || lastName == null) return;

        JSONObject pusherData;
        try {
            pusherData = new JSONObject(ambassadorConfig.getPusherInfo());
            pusherData.put("firstName", firstName);
            pusherData.put("lastName", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        pd.show();
        ambassadorConfig.setPusherInfo(pusherData.toString());

        try {
            requestManager.updateNameRequest(pusherData.getString("email"), firstName, lastName, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    mCallback.namesHaveBeenUpdated();
                    ambassadorConfig.setUserFullName(etFirstName.getText().toString(), etLastName.getText().toString());
                    hide();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    Toast.makeText(getOwnerActivity(), "Unable to update information.  Please try again.", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
