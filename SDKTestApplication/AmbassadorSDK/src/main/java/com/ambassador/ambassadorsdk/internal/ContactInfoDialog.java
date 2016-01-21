package com.ambassador.ambassadorsdk.internal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;

import java.io.IOException;

/**
 * Created by dylan on 11/20/15.
 */
public class ContactInfoDialog extends Dialog {

    private RAFOptions raf = RAFOptions.get();

    private ContactObject contactObject;

    private ImageView ivPhoto;
    private TextView tvName;
    private TextView tvNumberOrEmail;
    private ImageView ivExit;

    public ContactInfoDialog(Context context) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contact_info);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.7f;

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        tvName = (TextView) findViewById(R.id.tvName);
        tvNumberOrEmail = (TextView) findViewById(R.id.tvNumberOrEmail);
        ivExit = (ImageView) findViewById(R.id.ivExit);
        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public void setContactObject(ContactObject contactObject, boolean isPhone) {
        this.contactObject = contactObject;
        if (contactObject.getPictureBitmap() != null) {
            ivPhoto.setImageBitmap(contactObject.getPictureBitmap());
        } else if (contactObject.getPictureUri() != null) {
            Bitmap bmp = loadBmp(contactObject.getPictureUri());
            if (bmp != null) {
                contactObject.setPictureBitmap(bmp);
                ivPhoto.setImageBitmap(bmp);
            } else {
                ivPhoto.setBackground(new ColorDrawable(raf.getContactsToolbarColor()));
                ivPhoto.setImageDrawable(getOwnerActivity().getResources().getDrawable(R.drawable.big_no_contact));
            }
        } else {
            ivPhoto.setBackground(new ColorDrawable(raf.getContactsToolbarColor()));
            ivPhoto.setImageDrawable(getOwnerActivity().getResources().getDrawable(R.drawable.big_no_contact));
        }
        tvName.setText(contactObject.getName());

        if (isPhone) {
            String separator = " - ";
            tvNumberOrEmail.setText(contactObject.getType() + separator + contactObject.getPhoneNumber());
        } else {
            tvNumberOrEmail.setText(contactObject.getEmailAddress());
        }
    }

    private Bitmap loadBmp(String uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getOwnerActivity().getContentResolver(), Uri.parse(uri));
        } catch (IOException e) {
            return null;
        }
    }

}