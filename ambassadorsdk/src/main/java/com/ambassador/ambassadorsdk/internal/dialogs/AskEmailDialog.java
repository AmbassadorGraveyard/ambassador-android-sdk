package com.ambassador.ambassadorsdk.internal.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
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
    @Inject protected User user;
    @Inject protected Device device;
    // endregion

    protected OnEmailReceivedListener onEmailReceivedListener;

    public AskEmailDialog(Context context) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setDimAmount(0.75f);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(R.layout.dialog_email);
        ButterFork.bind(this);
        AmbSingleton.inject(this);

        setupTheme();
        setupButtons();
        showKeyboard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (onEmailReceivedListener != null) {
            onEmailReceivedListener.onCanceled();
        }
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
        if (onEmailReceivedListener != null) {
            onEmailReceivedListener.onCanceled();
        }
    }

    private void continueClicked() {
        if (etEmail.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), new StringResource(R.string.email_empty).getValue(), Toast.LENGTH_SHORT).show();
            etEmail.shake();
        } else {
            updateEmail(etEmail.getText().toString());
        }
    }

    private void updateEmail(@NonNull final String email) {
        if (onEmailReceivedListener != null) {
            onEmailReceivedListener.onEmailReceived(email);
        }
    }

    public void showKeyboard() {
        device.openSoftKeyboard(etEmail);
    }

    public void shake() {
        etEmail.shake();
    }

    public void setOnEmailReceivedListener(OnEmailReceivedListener onEmailReceivedListener) {
        this.onEmailReceivedListener = onEmailReceivedListener;
    }
    
    public interface OnEmailReceivedListener {
        void onEmailReceived(String email);
        void onCanceled();
    }

}