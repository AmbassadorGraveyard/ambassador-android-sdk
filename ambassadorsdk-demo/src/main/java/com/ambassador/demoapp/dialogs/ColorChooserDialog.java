package com.ambassador.demoapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ColorChooserDialog extends Dialog {

    @Bind(R.id.rlColors) protected RelativeLayout rlColors;
    @Bind(R.id.flColorA) protected FrameLayout flColorA;
    @Bind(R.id.flColorB) protected FrameLayout flColorB;
    @Bind(R.id.llRainbow) protected LinearLayout llRainbow;

    @Bind(R.id.etRedValue) protected EditText etRedValue;
    @Bind(R.id.etGreenValue) protected EditText etGreenValue;
    @Bind(R.id.etBlueValue) protected EditText etBlueValue;

    @Bind(R.id.tvColorCancel) protected TextView tvColorCancel;
    @Bind(R.id.tvColorDone) protected TextView tvColorDone;

    public ColorChooserDialog(Context context) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_color_chooser);
        ButterKnife.bind(this);
        updateGradients(Color.RED);
        setUpRainbow();

        rlColors.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                rlColors.setDrawingCacheEnabled(true);
                rlColors.buildDrawingCache();
                final Bitmap colors = rlColors.getDrawingCache();

                int pixel = colors.getPixel((int) event.getX(), (int) event.getY());

                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                rlColors.setDrawingCacheEnabled(false);


                etRedValue.setText(String.valueOf(red));
                etGreenValue.setText(String.valueOf(green));
                etBlueValue.setText(String.valueOf(blue));

                return true;
            }
        });

        tvColorCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvColorDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvColorDone.setTypeface(null, Typeface.BOLD);
                dismiss();
            }
        });
    }

    protected void setUpRainbow() {
        int[] colors = new int[]{ Color.RED, Color.GREEN, Color.BLUE, Color.RED };
        for (int i = 0; i < colors.length - 1; i++) {
            View view = new View(getOwnerActivity());
            view.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            GradientDrawable background = new GradientDrawable();
            background.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            background.setColors(new int[]{ colors[i], colors[i+1] });
            view.setBackground(background);
            llRainbow.addView(view);
        }
    }

    protected void updateGradients(@ColorInt int color) {
        GradientDrawable a = new GradientDrawable();
        a.setColors(new int[]{ color, Color.WHITE });
        a.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        flColorA.setBackground(a);

        GradientDrawable b = new GradientDrawable();
        b.setColors(new int[]{ Color.BLACK, Color.TRANSPARENT });
        b.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        flColorB.setBackground(b);
    }

}
