package com.ambassador.ambassadorsdk;

import android.graphics.drawable.Drawable;

/**
 * Created by dylan on 11/6/15.
 */
public class SocialGridModel {

    interface OnClickListener {
        void onClick();
    }

    private String title;
    private Drawable drawable;
    private int color;
    private boolean drawBorder;
    private OnClickListener onClickListener;

    public SocialGridModel(String title, Drawable drawable, int color) {
        this.title = title;
        this.drawable = drawable;
        this.color = color;
        this.drawBorder = false;
    }

    public SocialGridModel(String title, Drawable drawable, int color, boolean drawBorder) {
        this.title = title;
        this.drawable = drawable;
        this.color = color;
        this.drawBorder = drawBorder;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void click() {
        if (onClickListener != null) {
            onClickListener.onClick();
        }
    }

    /**
     * Getters
     */

    public String getTitle() {
        return title;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public int getColor() {
        return color;
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    /** ---------------------------- */

}
