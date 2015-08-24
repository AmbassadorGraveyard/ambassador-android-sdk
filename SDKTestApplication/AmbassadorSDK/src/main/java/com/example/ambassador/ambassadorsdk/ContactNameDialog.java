package com.example.ambassador.ambassadorsdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * Created by CoreyFields on 8/21/15.
 */
public class ContactNameDialog extends Dialog {
    private CustomEditText etContactName;
    private Button btnSend;
    private ProgressBar loader;
    private ContactNameListener mCallback;

    public interface ContactNameListener {
        void handleNameInput(String name);
    }

    public ContactNameDialog(Context context) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        mCallback = (ContactNameListener)getOwnerActivity();
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_contact_name);

        etContactName = (CustomEditText)findViewById(R.id.etContactName);
        etContactName.setEditTextTint(context.getResources().getColor(R.color.twitter_blue));
        btnSend = (Button)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueSending();
            }
        });
    }

    private void continueSending() {
        if (etContactName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), "Hmm, your entry is suspiciously blank", Toast.LENGTH_SHORT).show();
            etContactName.shakeEditText();
        } else {
            mCallback.handleNameInput(etContactName.getText().toString());
            hide();
        }
    }
}
