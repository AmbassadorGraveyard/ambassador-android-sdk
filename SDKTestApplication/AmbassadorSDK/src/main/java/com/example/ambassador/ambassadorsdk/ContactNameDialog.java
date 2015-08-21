package com.example.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


/**
 * Created by CoreyFields on 8/21/15.
 */
public class ContactNameDialog extends Dialog {
    private CustomEditText etTwitterMessage;
    private Button btnTweet, btnCancel;
    private ProgressBar loader;

    public ContactNameDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the default title bar
        setContentView(R.layout.dialog_contact_name);
    }
}
