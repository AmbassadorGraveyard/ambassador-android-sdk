package com.ambassador.ambassadorsdk.internal.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.models.WelcomeScreenData;
import com.ambassador.ambassadorsdk.internal.views.NetworkCircleImageView;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Dialog for presenting a welcome screen to referred users. Loads data from and presents a
 * WelcomeScreenData object.
 */
public class WelcomeScreenDialog extends Dialog {

    @Bind(B.id.tvClose)         protected TextView                  tvClose;
    @Bind(B.id.tvTitle)         protected TextView                  tvTitle;
    @Bind(B.id.rvAvatar)        protected RelativeLayout            rvAvatar;
    @Bind(B.id.rvWhiteCircle)   protected RelativeLayout            rvWhiteCircle;
    @Bind(B.id.ivAvatar)        protected NetworkCircleImageView    ivAvatar;
    @Bind(B.id.tvMessage)       protected TextView                  tvMessage;
    @Bind(B.id.btnMain)         protected Button                    btnMain;
    @Bind(B.id.tvLink1)         protected TextView                  tvLink1;
    @Bind(B.id.tvLink2)         protected TextView                  tvLink2;

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
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
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

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.85f;


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
            tvClose.setTextColor(welcomeScreenData.getColorTheme());
            tvClose.setOnClickListener(tvCloseOnClickListener);

            tvTitle.setText(welcomeScreenData.getTitle());
            tvMessage.setText(welcomeScreenData.getMessage());

            btnMain.setText(welcomeScreenData.getButtonText());
            btnMain.setOnClickListener(welcomeScreenData.getButtonOnClickListener());
            btnMain.setBackgroundColor(welcomeScreenData.getColorTheme());
            btnMain.setTextColor(Color.WHITE);

            tvLink1.setText(welcomeScreenData.getLink1Text());
            tvLink1.setTextColor(welcomeScreenData.getColorTheme());

            tvLink2.setText(welcomeScreenData.getLink2Text());
            tvLink2.setTextColor(welcomeScreenData.getColorTheme());

            GradientDrawable avatarBackground = new GradientDrawable();
            avatarBackground.setCornerRadius(500);
            avatarBackground.setColors(new int[]{ welcomeScreenData.getColorTheme(), getDarkenedColor(welcomeScreenData.getColorTheme()) });
            rvAvatar.setBackground(avatarBackground);

            GradientDrawable whiteBackground = new GradientDrawable();
            whiteBackground.setCornerRadius(500);
            whiteBackground.setColor(Color.WHITE);
            rvWhiteCircle.setBackground(whiteBackground);

            ivAvatar.load(welcomeScreenData.getImageUrl());
        }
    }

    /**
     * OnClickListener for the close button. Dismisses the dialog.
     */
    protected View.OnClickListener tvCloseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    /**
     * Darkens the theme color for the avatar gradient.
     * @param color the color int (not resId) to darken.
     * @return the darkened color int.
     */
    protected int getDarkenedColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.5f;
        hsv[1] *= 1.25;
        return Color.HSVToColor(hsv);
    }

}
