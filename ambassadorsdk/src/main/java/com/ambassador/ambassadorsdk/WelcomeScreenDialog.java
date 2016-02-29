package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.internal.models.WelcomeScreenData;
import com.ambassador.ambassadorsdk.internal.views.NetworkCircleImageView;

import java.lang.ref.WeakReference;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Dialog for presenting a welcome screen to referred users. Loads data from and presents a
 * WelcomeScreenData object.
 */
public class WelcomeScreenDialog extends Dialog {

    /** The activity reference that the default empty constructor will attempt to instantiate with. */
    protected static WeakReference<Activity> activityWeakReference;

    /** The availability callback reference that is stored statically for later use. */
    protected static AvailabilityCallback availabilityCallback;

    /** The parameters specified by the 3rd party to add to the WelcomeScreenData used. */
    protected static Parameters parameters;

    @Bind(B.id.tvClose)         protected TextView                  tvClose;
    @Bind(B.id.tvWelcome)       protected TextView                  tvWelcome;
    @Bind(B.id.tvTitle)         protected TextView                  tvTitle;
    @Bind(B.id.rvAvatar)        protected RelativeLayout            rvAvatar;
    @Bind(B.id.rvWhiteCircle)   protected RelativeLayout            rvWhiteCircle;
    @Bind(B.id.pbLoading)       protected ProgressBar               pbLoading;
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
            pbLoading.getIndeterminateDrawable().setColorFilter(getDarkenedColor(welcomeScreenData.getColorTheme()), PorterDuff.Mode.SRC_IN);

            tvWelcome.setText(welcomeScreenData.getTopBarText());

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
            tvLink1.setOnClickListener(welcomeScreenData.getLink1OnClickListener());

            tvLink2.setText(welcomeScreenData.getLink2Text());
            tvLink2.setTextColor(welcomeScreenData.getColorTheme());
            tvLink2.setOnClickListener(welcomeScreenData.getLink2OnClickListener());

            rvAvatar.setBackground(getGradientCircleBackground());
            rvWhiteCircle.setBackground(getWhiteCircleBackground());

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
     * Generates a white, circular Drawable to place onto the gradient before the avatar is loaded
     * online.
     * @return a white, circular GradientDrawable.
     */
    protected GradientDrawable getWhiteCircleBackground() {
        GradientDrawable whiteBackground = new GradientDrawable();
        whiteBackground.setCornerRadius(500);
        whiteBackground.setColor(Color.WHITE);
        return whiteBackground;
    }

    /**
     * Generates a circular GradientDrawable with a gradient background to place behind the avatar.
     * @return a circular GradientDrawable with a gradient based on the color theme.
     */
    protected GradientDrawable getGradientCircleBackground() {
        GradientDrawable avatarBackground = new GradientDrawable();
        avatarBackground.setCornerRadius(500);
        avatarBackground.setColors(new int[]{ welcomeScreenData.getColorTheme(), getDarkenedColor(welcomeScreenData.getColorTheme()) });
        return avatarBackground;
    }

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

    /**
     * Callback interface usable by the 3rd party developer. We pass them the dialog pre-loaded with
     * data to do what they need to because there is a large number of ways to handle configuration
     * changes such as orientation, so we leave it in their hands to deal with the dialog.
     */
    public interface AvailabilityCallback {
        void available(WelcomeScreenDialog welcomeScreenDialog);
    }

    /**
     * Returns the stored Activity, null or not.
     * @return static Activity activity, originating from WeakReference.
     */
    @Nullable
    public static Activity getActivity() {
        return activityWeakReference != null ? activityWeakReference.get() : null;
    }

    /**
     * Statically stores a WeakReference to an activity that the dialog will later attempt to present with.
     * @param activity the activity to later attempt presentation with.
     */
    public static void setActivity(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    /**
     * Returns the stored availability callback, null or not.
     * @return static AvailabilityCallback availabilityCallback.
     */
    @Nullable
    public static AvailabilityCallback getAvailabilityCallback() {
        return availabilityCallback;
    }

    /**
     * Statically stores a WeakReference to an activity that the will later be accessed by the InstallReceiver.
     * @param availabilityCallback the availability callback to callback to the activity with.
     */
    public static void setAvailabilityCallback(@NonNull AvailabilityCallback availabilityCallback) {
        WelcomeScreenDialog.availabilityCallback = availabilityCallback;
    }

    /**
     * Returns the stored parameters, null or not.
     * @return static Parameters parameters.
     */
    @Nullable
    public static Parameters getParameters() {
        return parameters;
    }

    /**
     * Statically stores Parameters that the 3rd party wants added to the WelcomeScreenData presented
     * onto the dialog.
     * @param parameters the parameters to add to generated WelcomeScreenData.
     */
    public static void setParameters(@NonNull Parameters parameters) {
        WelcomeScreenDialog.parameters = parameters;
    }

    /**
     * Parameter builder accessible by the 3rd party dev for specifying values inside of a WelcomeScreenData
     * object.
     */
    public static class Parameters {

        protected View.OnClickListener buttonOnClickListener;
        protected View.OnClickListener link1OnClickListener;
        protected View.OnClickListener link2OnClickListener;
        protected String topBarText;
        protected String titleText;
        protected String messageText;
        protected String buttonText;
        protected String link1Text;
        protected String link2Text;
        protected int colorTheme;

        public View.OnClickListener getButtonOnClickListener() {
            return buttonOnClickListener;
        }

        public Parameters setButtonOnClickListener(View.OnClickListener buttonOnClickListener) {
            this.buttonOnClickListener = buttonOnClickListener;
            return this;
        }

        public View.OnClickListener getLink1OnClickListener() {
            return link1OnClickListener;
        }

        public Parameters setLink1OnClickListener(View.OnClickListener link1OnClickListener) {
            this.link1OnClickListener = link1OnClickListener;
            return this;
        }

        public View.OnClickListener getLink2OnClickListener() {
            return link2OnClickListener;
        }

        public Parameters setLink2OnClickListener(View.OnClickListener link2OnClickListener) {
            this.link2OnClickListener = link2OnClickListener;
            return this;
        }

        public String getTopBarText() {
            return topBarText;
        }

        public Parameters setTopBarText(String topBarText) {
            this.topBarText = topBarText;
            return this;
        }

        public String getTitleText() {
            return titleText;
        }

        public Parameters setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public String getMessageText() {
            return messageText;
        }

        public Parameters setMessageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public String getButtonText() {
            return buttonText;
        }

        public Parameters setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public String getLink1Text() {
            return link1Text;
        }

        public Parameters setLink1Text(String link1Text) {
            this.link1Text = link1Text;
            return this;
        }

        public String getLink2Text() {
            return link2Text;
        }

        public Parameters setLink2Text(String link2Text) {
            this.link2Text = link2Text;
            return this;
        }

        public int getColorTheme() {
            return colorTheme;
        }

        public Parameters setColorTheme(int colorTheme) {
            this.colorTheme = colorTheme;
            return this;
        }

    }

    /**
     * Parameter build that we use internally to append data from the backend.
     */
    public static class BackendData {

        protected String imageUrl;
        protected String firstName;
        protected String lastName;

        public String getImageUrl() {
            return imageUrl;
        }

        public BackendData setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public String getFirstName() {
            return firstName;
        }

        public BackendData setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public String getLastName() {
            return lastName;
        }

        public BackendData setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

    }

}
