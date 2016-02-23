package com.ambassador.ambassadorsdk.internal.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.models.WelcomeScreenData;

import butterfork.ButterFork;

/**
 * Dialog for presenting a welcome screen to referred users. Loads data from and presents a
 * WelcomeScreenData object.
 */
public class WelcomeScreenDialog extends Dialog {

    /** Object containing the data to display. */
    protected WelcomeScreenData welcomeScreenData;

    /** Boolean to determine if member view references are inflated. */
    protected boolean isInflated;

    /**
     * Instantiates the WelcomeScreenDialog and sets the owner activity, and any pre-inflation setup.
     * @param context should be a Context object parent to an Activity.
     */
    public WelcomeScreenDialog(Context context) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        isInflated = false;
    }

    /**
     * Inflates the layout and binds view references. Does any setup that has view inflation as a
     * prerequisite.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_welcome_screen);
        ButterFork.bind(this);
        isInflated = true;

        if (welcomeScreenData != null) {
            load(welcomeScreenData);
        }
    }

    /**
     * Sets the WelcomeScreenData member.  If views are inflated it will set the data onto the views.
     * @param welcomeScreenData the WelcomeScreenData instantiation to store and display data from.
     */
    public void load(WelcomeScreenData welcomeScreenData) {
        this.welcomeScreenData = welcomeScreenData;
        if (isInflated) {

        }
    }

}
