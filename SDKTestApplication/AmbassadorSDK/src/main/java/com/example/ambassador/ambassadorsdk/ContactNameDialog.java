package com.example.ambassador.ambassadorsdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by CoreyFields on 8/21/15.
 */
public class ContactNameDialog extends Dialog {
    private CustomEditText etFirstName, etLastName;
    private ContactNameListener mCallback;

    public interface ContactNameListener {
        void handleNameInput(String firstname, String lastname);
    }

    public ContactNameDialog(Context context) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        mCallback = (ContactNameListener)getOwnerActivity();
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_contact_name);

        etFirstName = (CustomEditText)findViewById(R.id.etFirstName);
        etFirstName.setEditTextTint(context.getResources().getColor(R.color.twitter_blue));
        etLastName = (CustomEditText)findViewById(R.id.etLastName);
        etLastName.setEditTextTint(context.getResources().getColor(R.color.twitter_blue));
        Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueSending();
            }
        });
    }

    private void continueSending() {
        if (etFirstName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Hmm, your entry is suspiciously blank", Toast.LENGTH_SHORT).show();
            etFirstName.shakeEditText();
        } else if (etLastName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Hmm, your entry is suspiciously blank", Toast.LENGTH_SHORT).show();
            etLastName.shakeEditText();
        } else {
            mCallback.handleNameInput(etFirstName.getText().toString(), etLastName.getText().toString());
            hide();
        }
    }
}
