package com.ambassador.ambassadorsdk;

/**
 * Created by dylan on 11/6/15.
 */
public class SocialGridModel implements Comparable<SocialGridModel> {

    interface OnClickListener {
        void onClick();
    }

    private String title;
    private int drawable;
    private int color;
    private boolean drawBorder;
    private OnClickListener onClickListener;
    private boolean disabled;
    private int weight = 1000;

    public SocialGridModel(String title, int drawable, int color) {
        this.title = title;
        this.drawable = drawable;
        this.color = color;
        this.drawBorder = false;
    }

    public SocialGridModel(String title, int drawable, int color, boolean drawBorder) {
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

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Getters
     */

    public String getTitle() {
        return title;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getColor() {
        return color;
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getWeight() {
        return weight;
    }

    /** ---------------------------- */

    @Override
    public int compareTo(SocialGridModel another) {
        if (another.getWeight() > weight) {
            return -1;
        } else if (another.getWeight() < weight) {
            return 1;
        } else {
            return 0;
        }
    }

}
