package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * View that presents a loading animation. Extends a ViewGroup and dimensions should match_parent within
 * the ViewGroup that is loading.
 */
public class LoadingView extends LinearLayout {

    /** ProgressBar displayed in the center of the layout. */
    @Bind(B.id.pbLoading) protected ProgressBar pbLoading;

    /** Constructor calls through to init(). */
    public LoadingView(Context context) {
        super(context);
        init();
    }

    /** Constructor calls through to init(). */
    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** Constructor calls through to init(). */
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Inflates layout as well as any other initialization.
     */
    protected void init() {
        inflate(getContext(), R.layout.view_loading, this);
        ButterFork.bind(this);
        pbLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }

}
