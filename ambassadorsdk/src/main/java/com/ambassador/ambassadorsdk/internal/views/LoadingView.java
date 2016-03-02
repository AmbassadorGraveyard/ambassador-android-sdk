package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * View that presents a loading animation. Extends a ViewGroup and dimensions should match_parent within
 * the ViewGroup that is loading.
 */
public class LoadingView extends RelativeLayout {

    /** Constructor calls through to init(). */
    public LoadingView(Context context) {
        super(context);
    }

    /** Constructor calls through to init(). */
    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Constructor calls through to init(). */
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Inflates layout as well as any other initialization.
     */
    protected void init() {

    }

}
