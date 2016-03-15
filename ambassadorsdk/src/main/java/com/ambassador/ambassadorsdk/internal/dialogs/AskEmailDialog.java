package com.ambassador.ambassadorsdk.internal.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.res.ColorResource;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.ambassadorsdk.internal.views.ShakableEditText;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Dialog to ask the user for his/her email if we haven't identified.
 */
public final class AskEmailDialog extends Dialog {

    // region Views
    @Bind(B.id.etEmail)     protected ShakableEditText  etEmail;
    @Bind(B.id.btnCancel)   protected Button            btnCancel;
    @Bind(B.id.btnContinue) protected Button            btnContinue;
    // endregion

    // region Dependencies
    @Inject
    protected RequestManager requestManager;
    @Inject protected User user;
    @Inject protected Device device;
    // endregion

    // region Local members
    protected ProgressDialog pd;
    // endregion

    public AskEmailDialog(Context context, ProgressDialog pd) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        this.pd = pd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_email);
        ButterFork.bind(this);
        AmbSingleton.inject(this);

        setupTheme();
        setupButtons();
        showKeyboard();
    }

    private void setupTheme() {
        etEmail.setTint(new ColorResource(R.color.ambassador_blue).getColor());
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClicked();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueClicked();
            }
        });
    }

    private void cancelClicked() {
        hide();
    }

    private void continueClicked() {
        if (etEmail.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), new StringResource(R.string.first_name_empty).getValue(), Toast.LENGTH_SHORT).show();
            etEmail.shake();
        } else {
            updateEmail(etEmail.getText().toString());
        }
    }

    private void updateEmail(@NonNull final String email) {
        AmbassadorSDK.identify(email);
    }

    public void showKeyboard() {
        device.openSoftKeyboard(etEmail);
    }

}