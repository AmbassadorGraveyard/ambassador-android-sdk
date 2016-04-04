package com.ambassador.demoapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.demoapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ColorChooserDialog extends Dialog {

    protected boolean inflated = false;

    @ColorInt protected int color;
    @ColorInt protected int hueExtreme;
    protected int r;
    protected int g;
    protected int b;
    protected float h;
    protected float s;
    protected float v;

    protected int colorX = 50;
    protected int colorY = 50;

    @Bind(R.id.viewColorSpot) protected View colorSpot;
    @Bind(R.id.rlColors) protected RelativeLayout rlColors;
    @Bind(R.id.flColorA) protected FrameLayout flColorA;
    @Bind(R.id.flColorB) protected FrameLayout flColorB;
    @Bind(R.id.llRainbow) protected LinearLayout llRainbow;
    @Bind(R.id.hueTracker) protected View hueTracker;
    @Bind(R.id.etRedValue) protected EditText etRedValue;
    @Bind(R.id.etGreenValue) protected EditText etGreenValue;
    @Bind(R.id.etBlueValue) protected EditText etBlueValue;
    @Bind(R.id.etHexValue) protected EditText etHexValue;
    @Bind(R.id.viewPreview) protected View viewPreview;
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
        inflated = true;
        updateGradients(hueExtreme);
        setUpRainbow();

        GradientDrawable colorSpotBackground = new GradientDrawable();
        colorSpotBackground.setStroke(2, Color.BLACK);
        hueTracker.setBackground(colorSpotBackground);

        flColorA.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int colorSpotX = (int) (flColorA.getWidth() * s);
                int colorSpotY = flColorA.getHeight() - (int) (flColorA.getHeight() * v);
                colorSpot.setTranslationX(colorSpotX - colorSpot.getWidth() / 2);
                colorSpot.setTranslationY(colorSpotY - colorSpot.getHeight() / 2);

                if (colorSpotY < flColorA.getHeight() / 2) {
                    GradientDrawable colorSpotBackground = new GradientDrawable();
                    colorSpotBackground.setStroke(2, Color.BLACK);
                    colorSpotBackground.setCornerRadius(100);
                    colorSpot.setBackground(colorSpotBackground);
                } else {
                    GradientDrawable colorSpotBackground = new GradientDrawable();
                    colorSpotBackground.setStroke(2, Color.WHITE);
                    colorSpotBackground.setCornerRadius(100);
                    colorSpot.setBackground(colorSpotBackground);
                }

                colorX = colorSpotX;
                colorY = colorSpotY;
                updateColor();

                flColorA.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        rlColors.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < 0 || event.getX() > flColorA.getWidth() || event.getY() < 0 || event.getY() > flColorA.getHeight()) {
                    return false;
                }

                colorX = (int) event.getX();
                colorY = (int) event.getY();

                if (event.getY() < flColorA.getHeight() / 2) {
                    GradientDrawable colorSpotBackground = new GradientDrawable();
                    colorSpotBackground.setStroke(2, Color.BLACK);
                    colorSpotBackground.setCornerRadius(100);
                    colorSpot.setBackground(colorSpotBackground);
                } else {
                    GradientDrawable colorSpotBackground = new GradientDrawable();
                    colorSpotBackground.setStroke(2, Color.WHITE);
                    colorSpotBackground.setCornerRadius(100);
                    colorSpot.setBackground(colorSpotBackground);
                }

                colorSpot.setTranslationX(event.getX() - colorSpot.getWidth() / 2);
                colorSpot.setTranslationY(event.getY() - colorSpot.getHeight() / 2);

                updateColor();
                return true;
            }
        });

        llRainbow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < 0 || event.getX() > llRainbow.getWidth() || event.getY() < 0 || event.getY() > llRainbow.getHeight()) {
                    return false;
                }

                llRainbow.setDrawingCacheEnabled(true);
                llRainbow.buildDrawingCache();
                final Bitmap colors = llRainbow.getDrawingCache();
                int pixel = colors.getPixel((int) event.getX(), (int) event.getY());
                updateGradients(pixel);

                llRainbow.setDrawingCacheEnabled(false);
                hueTracker.setTranslationX(event.getX());

                updateColor();

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
                tvColorDone.setBackgroundColor(Color.parseColor("#eeeeee"));
                dismiss();
            }
        });

        etRedValue.addTextChangedListener(rgbTextWatcher);
        etGreenValue.addTextChangedListener(rgbTextWatcher);
        etBlueValue.addTextChangedListener(rgbTextWatcher);

        //etHexValue.addTextChangedListener(hexTextWatcher);
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
        a.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        flColorA.setBackground(a);

        GradientDrawable b = new GradientDrawable();
        b.setColors(new int[]{ Color.BLACK, Color.TRANSPARENT });
        b.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        flColorB.setBackground(b);
    }

    protected void updateColor() {
        rlColors.setDrawingCacheEnabled(true);
        rlColors.buildDrawingCache();
        final Bitmap colors = rlColors.getDrawingCache();
        color = colors.getPixel(colorX, colorY);
        rlColors.setDrawingCacheEnabled(false);

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        etRedValue.setText(String.valueOf(red));
        etGreenValue.setText(String.valueOf(green));
        etBlueValue.setText(String.valueOf(blue));
        etHexValue.setText(String.format("%06X", (0xFFFFFF & color)));

        viewPreview.setBackgroundColor(color);
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;

        r = Color.red(color);
        g = Color.green(color);
        b = Color.blue(color);

        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);

        h = hsv[0];
        s = hsv[1];
        v = hsv[2];

        hueExtreme = Color.HSVToColor(new float[]{ h, 1, 1 });
    }

    protected TextWatcher rgbTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    protected TextWatcher hexTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            List<String> allowedCharacters = Arrays.asList("A", "B", "C", "D", "E", "F", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
            String hex = etHexValue.getText().toString();
            if (hex.length() == 6) {
                boolean validHex = true;
                for (int i = 0; i < hex.length(); i++) {
                    String character = String.valueOf(hex.charAt(i));
                    if (!allowedCharacters.contains(character)) {
                        validHex = false;
                        break;
                    }
                }

                if (validHex) {
                    int color = Color.parseColor("#" + hex);
                    setColor(color);
                    updateColor();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

}
