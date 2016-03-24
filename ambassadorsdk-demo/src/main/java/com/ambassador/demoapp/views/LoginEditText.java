package com.ambassador.demoapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * LoginEditText is a normal EditText except it takes an image to show on the left as well.
 * Under the hood is a RelativeLayout with a matching-parent EditText.
 */
public class LoginEditText extends RelativeLayout {

    public enum Position {
        TOP, MIDDLE, BOTTOM
    }

    /** The underlying EditText. */
    @Bind(R.id.editText) protected EditText editText;

    /** The ImageView that shows up to the left of the editable area. */
    @Bind(R.id.imageView) protected ImageView imageView;

    /** Default constructor. Calls through to init(). */
    public LoginEditText(Context context) {
        super(context);
        init();
    }

    /** Default constructor. Calls through to init(). */
    public LoginEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** Default constructor. Calls through to init(). */
    public LoginEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Inflates the layout and binds views.
     */
    protected void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.edit_text_login, this);
        ButterKnife.bind(this);
    }

    /**
     * Sets the hint message on the underlying EditText with the passed in String.
     * @param hint the hint to set directly onto the member EditText.
     */
    public void setHint(String hint) {
        editText.setHint(hint);
    }

    /**
     * Sets the input type of the underlying EditText with the passed in integer. Also sets typeface
     * to sans-serif to ensure that sans-serif is always the font (input type password will change it
     * to monospace).
     * @param type the integer input type to set (a static constant from InputType).
     */
    public void setInputType(int type) {
        editText.setInputType(type);
        editText.setTypeface(Typeface.SANS_SERIF);
    }

    /**
     * Sets the image that appears to the left of the editable.
     * @param resId the resource id of a drawable to set.
     */
    public void setImage(@DrawableRes int resId) {
        imageView.setImageDrawable(getResources().getDrawable(resId));
    }

    /**
     * Returns the String representation of what is in the underlying EditText
     * @return the String input.
     */
    public String getText() {
        return editText.getText().toString();
    }

    /**
     * Sets the rounded corners on the EditText appropriately for the passed in position. Top will have
     * its top corners rounded, middle no corners, bottom only the bottom corners.
     * @param position the Position enum describing the position (TOP, MIDDLE, BOTTOM).
     */
    public void setPosition(Position position) {
        switch (position) {
            case TOP:
                editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_top));
                break;
            case MIDDLE:
                editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_mid));
                break;
            case BOTTOM:
                editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_bottom));
                break;
            default:
                setPosition(Position.MIDDLE);
                break;
        }
    }

}
