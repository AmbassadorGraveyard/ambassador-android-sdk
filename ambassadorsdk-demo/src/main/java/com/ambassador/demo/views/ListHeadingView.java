package com.ambassador.demo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.demo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListHeadingView extends RelativeLayout {

    @Bind(R.id.tvHeaderText) protected TextView tvHeaderText;

    protected String text;

    public ListHeadingView(Context context) {
        super(context);
        init(null);
    }

    public ListHeadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ListHeadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_list_heading, this);
        ButterKnife.bind(this);

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ListHeadingView, 0, 0);
            try {
                text = a.getString(R.styleable.ListHeadingView_text);
                setText(text);
            } finally {
                a.recycle();
            }
        }
    }

    public void setText(CharSequence charSequence) {
        text = charSequence.toString();
        tvHeaderText.setText(charSequence);
    }

}
