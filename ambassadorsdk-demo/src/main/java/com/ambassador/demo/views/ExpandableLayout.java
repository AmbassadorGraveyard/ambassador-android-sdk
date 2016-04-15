package com.ambassador.demo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.demo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpandableLayout extends LinearLayout {

    protected ExpandableLayoutHeader header;

    public ExpandableLayout(Context context) {
        super(context);
        init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setOrientation(VERTICAL);
        this.header = new ExpandableLayoutHeader(getContext());
        addView(this.header);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getChildCount() > 2) {
                    throw new RuntimeException("ExpandableLayout can have at most ONE child.");
                }
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (getChildCount() > 2) {
            throw new RuntimeException("ExpandableLayout can have at most ONE child.");
        }
    }

    protected static class ExpandableLayoutHeader extends RelativeLayout {

        protected String text;

        @Bind(R.id.tvHeaderText) protected TextView tvHeaderText;

        public ExpandableLayoutHeader(Context context) {
            super(context);
            init();
        }

        public ExpandableLayoutHeader(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ExpandableLayoutHeader(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        protected void init() {
            LayoutInflater.from(getContext()).inflate(R.layout.view_expandable_layout_header, this);
            ButterKnife.bind(this);
            setText("Expandable Layout");
            tvHeaderText.setText(text);
        }

        public void setText(String text) {
            this.text = text;
            if (tvHeaderText != null) {
                tvHeaderText.setText(text);
            }
        }

    }

}
