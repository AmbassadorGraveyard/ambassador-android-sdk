package com.ambassador.ambassadorsdk.internal.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.utils.ColorResource;
import com.ambassador.ambassadorsdk.internal.utils.StringResource;

public final class AskUrlDialog extends AlertDialog {

    protected String url;
    protected OnCompleteListener listener;

    public AskUrlDialog(Context context, String url) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupText();
        setupButtons();
    }

    private void setupText() {
        setTitle(new StringResource(R.string.hold_on).getValue());
        setMessage(new StringResource(R.string.url_missing).getValue() + " " + url);
    }

    private void setupButtons() {
        setButton(BUTTON_POSITIVE, "Continue Sending", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.dontAdd();
                }
            }
        });

        setButton(BUTTON_NEGATIVE, "Insert Link", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.doAdd();
                }
            }
        });

        getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(new ColorResource(R.color.twitter_blue).getColor());
        getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(new ColorResource(R.color.twitter_blue).getColor());
    }

    @NonNull
    public AskUrlDialog setOnCompleteListener(@Nullable OnCompleteListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnCompleteListener {
        void dontAdd();
        void doAdd();
    }

}
