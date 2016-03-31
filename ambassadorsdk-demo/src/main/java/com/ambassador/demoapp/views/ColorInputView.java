package com.ambassador.demoapp.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ColorInputView extends RelativeLayout {

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
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(500);
        gradientDrawable.setColor(Color.parseColor("#4198d1"));
        setBackground(gradientDrawable);
    }

}
