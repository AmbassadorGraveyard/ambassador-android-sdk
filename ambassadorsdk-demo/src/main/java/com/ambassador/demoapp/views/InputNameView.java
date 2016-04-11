package com.ambassador.demoapp.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

public class InputNameView extends TextView {

    public InputNameView(Context context) {
        super(context);
        init();
    }

    public InputNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputNameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setTextSize(15);
        setTextColor(Color.BLACK);
    }

}
