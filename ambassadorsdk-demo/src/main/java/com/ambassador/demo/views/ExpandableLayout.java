package com.ambassador.demo.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.demo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpandableLayout extends LinearLayout {

    protected ExpandableLayoutHeader header;
    protected boolean isInflated;
    protected int height;

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
        this.header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInflated) {
                    deflate();
                } else {
                    inflate();
                }
            }
        });
        addView(this.header);

        isInflated = true;

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

    public void inflate() {
        if (isInflated) return;
        isInflated = true;
        final View child = getChildAt(1);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, height);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                layoutParams.height = val;
                child.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.start();
    }

    public void deflate() {
        if (!isInflated) return;
        isInflated = false;
        final View child = getChildAt(1);
        height = child.getMeasuredHeight();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 0);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                layoutParams.height = val;
                child.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.start();
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
