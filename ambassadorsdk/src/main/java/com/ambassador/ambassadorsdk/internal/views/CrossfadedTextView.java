package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;

import butterfork.Bind;
import butterfork.ButterFork;

public final class CrossfadedTextView extends RelativeLayout {

    protected final int ANIMATION_DURATION = 125;

    @Bind(B.id.tvA) protected TextView tvA;
    @Bind(B.id.tvB) protected TextView tvB;

    protected int toggle = 1;
    protected Handler handler;
    protected Runnable currentRunnable;

    public CrossfadedTextView(Context context) {
        super(context);
        init();
    }

    public CrossfadedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CrossfadedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_crossfaded_text, this);
        ButterFork.bind(this);
        handler = new Handler();
    }

    public void setText(@NonNull String text) {
        handler.removeCallbacks(currentRunnable);
        if (toggle > 0) {
            tvB.setText(text);
            animateOut(tvA);
            animateIn(tvB);

        } else if (toggle < 0) {
            tvA.setText(text);
            animateOut(tvB);
            animateIn(tvA);
        }

        toggle *= -1;
    }

    public void setTextNoAnimation(@NonNull String text) {
        if (toggle > 0) {
            tvB.setText(text);
            tvB.setAlpha(1);
            tvA.setAlpha(0);

        } else if (toggle < 0) {
            tvA.setText(text);
            tvA.setAlpha(1);
            tvB.setAlpha(0);
        }

        toggle *= -1;
    }

    private void animateOut(final TextView textView) {
        textView.animate()
                .alpha(0)
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    private void animateIn(final TextView textView) {
        currentRunnable = new Runnable() {
            @Override
            public void run() {
                textView.animate()
                        .alpha(1)
                        .setDuration(ANIMATION_DURATION)
                        .start();
            }
        };
        handler.postDelayed(currentRunnable, ANIMATION_DURATION);
    }

    public void setTextColor(int color) {
        tvA.setTextColor(color);
        tvB.setTextColor(color);
    }

    public void setGravity(Gravity gravity) {
        switch (gravity) {
            case LEFT:
                setTextViewAlignmentLeft(tvA);
                setTextViewAlignmentLeft(tvB);
                break;

            case CENTER:
                setTextViewAlignmentCenter(tvA);
                setTextViewAlignmentCenter(tvB);
                break;

            default:
                break;
        }
    }

    public enum Gravity {
        LEFT, CENTER
    }

    private void setTextViewAlignmentLeft(TextView textView) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        if (Build.VERSION.SDK_INT >= 17) {
            params.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
        }
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
        textView.setLayoutParams(params);
    }

    private void setTextViewAlignmentCenter(TextView textView) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        if (Build.VERSION.SDK_INT >= 17) {
            params.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
        }
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
        textView.setLayoutParams(params);
    }

}
