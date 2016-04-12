package com.ambassador.demo.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

import com.ambassador.demo.R;

public class InputView extends EditText {

    public enum Type {
        TEXT, TEXT_AREA
    }

    public InputView(Context context) {
        super(context);
        init();
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.et_only));
        setHintTextColor(Color.parseColor("#e6e6e6"));

        Resources r = getResources();

        int sp14 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, r.getDisplayMetrics());
        setTextSize(14);

        int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
        int dp12 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());
        setPadding(dp10, dp12, dp10, dp12);
    }

}
