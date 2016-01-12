package com.ambassador.ambassadorsdk.internal;

public final class SocialGridModel implements Comparable<SocialGridModel> {

    interface OnClickListener {
        void onClick();
    }

    private SocialGridModel() {}

    private String name;
    private int iconDrawable;
    private int backgroundColor;
    private boolean drawBorder;
    private OnClickListener onClickListener;
    private boolean disabled = false;
    private int weight = 1000;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void click() {
        if (onClickListener != null) {
            onClickListener.onClick();
        }
    }

    public String getName() {
        return name;
    }

    public int getIconDrawable() {
        return iconDrawable;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean willDrawBorder() {
        return drawBorder;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getWeight() {
        return weight;
    }

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

    public static class Builder {

        private String name;
        private int iconDrawable;
        private int backgroundColor;
        private boolean drawBorder;
        private OnClickListener onClickListener;
        private boolean disabled = false;
        private int weight = 1000;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setIconDrawable(int iconDrawable) {
            this.iconDrawable = iconDrawable;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setDrawBorder(boolean drawBorder) {
            this.drawBorder = drawBorder;
            return this;
        }

        public Builder setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public Builder setDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public SocialGridModel build() {
            SocialGridModel tmp = new SocialGridModel();
            tmp.name = this.name;
            tmp.iconDrawable = this.iconDrawable;
            tmp.backgroundColor = this.backgroundColor;
            tmp.drawBorder = this.drawBorder;
            tmp.onClickListener = this.onClickListener;
            tmp.disabled = this.disabled;
            tmp.weight = this.weight;
            return tmp;
        }

    }

}
