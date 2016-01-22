package com.ambassador.ambassadorsdk.internal.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represents a sharing method displayed in AmbassadorActivity.
 */
public final class ShareMethod implements Comparable<ShareMethod> {

    protected String name;
    protected int weight;
    protected int iconDrawable;
    protected int backgroundColor;
    protected boolean drawBorder;
    protected ShareAction shareAction;

    protected ShareMethod() {}

    public void click() {
        if (shareAction != null) {
            shareAction.share();
        }
    }

    @NonNull
    public String getName() {
        return name != null ? name : "Unknown";
    }

    public int getWeight() {
        return weight;
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

    @Override
    public int compareTo(@NonNull ShareMethod another) {
        if (another.getWeight() > weight) {
            return -1;
        } else if (another.getWeight() < weight) {
            return 1;
        } else {
            return 0;
        }
    }

    public interface ShareAction {
        void share();
    }

    public static final class Builder {

        private String name;
        private int weight;
        private int iconDrawable;
        private int backgroundColor;
        private boolean drawBorder;
        private ShareAction shareAction;

        public Builder() {}

        @NonNull
        public Builder setName(@Nullable String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        @NonNull
        public Builder setIconDrawable(int iconDrawable) {
            this.iconDrawable = iconDrawable;
            return this;
        }

        @NonNull
        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @NonNull
        public Builder setDrawBorder(boolean drawBorder) {
            this.drawBorder = drawBorder;
            return this;
        }

        @NonNull
        public Builder setShareAction(@Nullable ShareAction shareAction) {
            this.shareAction = shareAction;
            return this;
        }

        @NonNull
        public ShareMethod build() {
            ShareMethod tmp = new ShareMethod();
            tmp.name = this.name;
            tmp.weight = this.weight;
            tmp.iconDrawable = this.iconDrawable;
            tmp.backgroundColor = this.backgroundColor;
            tmp.drawBorder = this.drawBorder;
            tmp.shareAction = this.shareAction;
            return tmp;
        }

    }

}
