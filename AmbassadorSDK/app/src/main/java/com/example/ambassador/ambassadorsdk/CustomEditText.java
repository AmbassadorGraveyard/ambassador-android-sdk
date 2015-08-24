package com.example.ambassador.ambassadorsdk;

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
 * Created by JakeDunahee on 8/10/15.
 */
public class CustomEditText extends EditText {
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Animation similiar to Mac wrong password animation
    public void shakeEditText() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationX", 0, 5);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "translationX", 5, -5);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationX", -5, 5);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(this, "translationX", 5, -5);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(this, "translationX", -5, 0);
        List<Animator> animatorItems = new ArrayList<>();
        animatorItems.add(objectAnimator);
        animatorItems.add(objectAnimator1);
        animatorItems.add(objectAnimator2);
        animatorItems.add(objectAnimator3);
        animatorItems.add(objectAnimator4);
        set.playSequentially(animatorItems);

        set.setDuration(80);
        set.start();
    }

    // Sets tint color of EditText for older devices
    public void setEditTextTint(int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(this.getBackground());
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(color));
        this.setBackground(wrappedDrawable);
    }
}
