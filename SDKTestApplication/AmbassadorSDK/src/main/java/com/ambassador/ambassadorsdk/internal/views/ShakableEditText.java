package com.ambassador.ambassadorsdk.internal.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;

/**
 * EditText that can shake to provide negative feedback.
 */
public final class ShakableEditText extends EditText {

    private AnimatorSet shakeAnimation;

    public ShakableEditText(Context context) {
        super(context);
        init();
    }

    public ShakableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShakableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        shakeAnimation = new AnimatorSet();
        ObjectAnimator originToRight = ObjectAnimator.ofFloat(this, "translationX", 0, 5);
        ObjectAnimator rightToLeft = ObjectAnimator.ofFloat(this, "translationX", 5, -5);
        ObjectAnimator leftToRight = ObjectAnimator.ofFloat(this, "translationX", -5, 5);
        ObjectAnimator leftToOrigin = ObjectAnimator.ofFloat(this, "translationX", -5, 0);

        List<Animator> animatorItems = new ArrayList<>();
        animatorItems.add(originToRight);
        animatorItems.add(rightToLeft);
        animatorItems.add(leftToRight);
        animatorItems.add(rightToLeft);
        animatorItems.add(leftToOrigin);

        shakeAnimation.playSequentially(animatorItems);
        shakeAnimation.setDuration(80);
    }

    public void shake() {
        shakeAnimation.start();
    }

    public void setTint(int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(this.getBackground());
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(color));
        this.setBackground(wrappedDrawable);
    }

}
