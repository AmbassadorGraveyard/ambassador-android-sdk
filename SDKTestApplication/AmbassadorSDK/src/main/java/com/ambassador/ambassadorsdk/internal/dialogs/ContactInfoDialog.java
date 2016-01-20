package com.ambassador.ambassadorsdk.internal.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.models.Contact;

import java.io.IOException;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Dialog to show an enhanced view of a contact's info.
 */
public final class ContactInfoDialog extends Dialog {

    // region Views
    @Bind(B.id.ivPhoto)             protected ImageView     ivPhoto;
    @Bind(B.id.tvName)              protected TextView      tvName;
    @Bind(B.id.tvNumberOrEmail)     protected TextView      tvNumberOrEmail;
    @Bind(B.id.ivExit)              protected ImageView     ivExit;
    // endregion

    // region Local members
    protected RAFOptions raf;
    // endregion

    public ContactInfoDialog(Context context) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        raf = RAFOptions.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contact_info);
        ButterFork.bind(this);

        configureDialog();
        setupButtons();
    }

    private void configureDialog() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.7f;
    }

    private void setupButtons() {
        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Nullable
    private Bitmap loadBitmap(@NonNull String uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getOwnerActivity().getContentResolver(), Uri.parse(uri));
        } catch (IOException e) {
            return null;
        }
    }

    public void setContactObject(@NonNull Contact contact, boolean isPhone) {
        if (contact.getPictureBitmap() != null) {
            ivPhoto.setImageBitmap(contact.getPictureBitmap());
        } else if (contact.getPictureUri() != null) {
            Bitmap bmp = loadBitmap(contact.getPictureUri());
            if (bmp != null) {
                contact.setPictureBitmap(bmp);
                ivPhoto.setImageBitmap(bmp);
            } else {
                ivPhoto.setBackground(new ColorDrawable(raf.getContactsToolbarColor()));
                ivPhoto.setImageDrawable(getOwnerActivity().getResources().getDrawable(R.drawable.big_no_contact));
            }
        } else {
            ivPhoto.setBackground(new ColorDrawable(raf.getContactsToolbarColor()));
            ivPhoto.setImageDrawable(getOwnerActivity().getResources().getDrawable(R.drawable.big_no_contact));
        }
        tvName.setText(contact.getName());

        if (isPhone) {
            String text = contact.getType() + " - " + contact.getPhoneNumber();
            tvNumberOrEmail.setText(text);
        } else {
            tvNumberOrEmail.setText(contact.getEmailAddress());
        }
    }

}
