package com.ambassador.demoapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.ambassador.demoapp.dialogs.ColorChooserDialog;

public class ColorInputView extends RelativeLayout implements View.OnClickListener {

    protected Activity activity;

    @ColorInt protected int color;

    public ColorInputView(Context context) {
        super(context);
        init();
    }

    public ColorInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setColor(Color.parseColor("#4198d1"));
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (activity != null) {
            final ColorChooserDialog colorChooserDialog = new ColorChooserDialog(activity);
            colorChooserDialog.setColor(color);
            colorChooserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    int color = colorChooserDialog.getColor();
                    setColor(color);
                }
            });
            colorChooserDialog.show();
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(500);
        gradientDrawable.setColor(color);
        gradientDrawable.setStroke(3, Color.parseColor("#e6e6e6"));
        setBackground(gradientDrawable);
    }

}
