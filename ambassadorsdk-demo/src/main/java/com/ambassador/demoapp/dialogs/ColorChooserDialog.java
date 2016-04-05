package com.ambassador.demoapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import com.ambassador.demoapp.R;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Dialog that allows the user to select a color. For proper experience you need to use setColor(...)
 * once before showing the dialog. Selected color can be retrieved on dismiss with getColor().
 */
public class ColorChooserDialog extends Dialog implements DialogInterface.OnKeyListener {

    /** Boolean telling if views inflated and binded. */
    protected boolean inflated = false;

    /** The color int that the dialog launched with. */
    @ColorInt protected int launchColor = -1;

    /** The currently selected color. */
    @ColorInt protected int color;

    /** Red component value of color. Between 0 and 255. */
    protected int r;

    /** Green component value of color. Between 0 and 255. */
    protected int g;

    /** Blue component value of color. Between 0 and 255. */
    protected int b;

    /** Hue component value of color. Between 0 and 360. */
    protected float h;

    /** Saturation component value of color. Between 0 and 1. */
    protected float s;

    /** Value component value of color. Between 0 and 1. */
    protected float v;

    /** The color to draw the gradient with. The hue extreme that allows you to select color. */
    @ColorInt protected int hueExtreme;

    /** X position of the current selected color on the chooser. */
    protected int colorX = 50;

    /** Y position of the current selected color on the chooser. */
    protected int colorY = 50;

    protected GradientDrawable currentColorMarker;

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

        // Setup the rainbow gradient for selecting hue.
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

        GradientDrawable colorSpotBackground = new GradientDrawable();
        colorSpotBackground.setStroke(2, Color.BLACK);
        hueTracker.setBackground(colorSpotBackground);

        // Wait until color gradients inflated and set the current colorX and colorY and update views.
        rlColors.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rearrangeForExteriorInput();
                rlColors.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // Get new color when click on colorGradient.
        rlColors.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < 0 || event.getX() > flColorA.getWidth() || event.getY() < 0 || event.getY() > flColorA.getHeight()) {
                    return false;
                }

                colorX = (int) event.getX();
                colorY = (int) event.getY();

                updateCurrentColorMarker();
                updateColor();
                return true;
            }
        });

        // Get new hue and update everything when click on hue slider.
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
                llRainbow.setDrawingCacheEnabled(false);

                hueTracker.setTranslationX(event.getX() - hueTracker.getWidth() / 2);

                updateGradients(pixel);
                updateColor();

                return true;
            }
        });

        // Reset to launch color and dismiss when cancel clicked.
        tvColorCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(launchColor);
                dismiss();
            }
        });

        // Dismiss when done clicked.
        tvColorDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setOnKeyListener(this);
    }

    /**
     * Returns the selected ColorInt.
     */
    @ColorInt
    public int getColor() {
        return color;
    }

    /**
     * Updates the color selection gradient with a certain color. Draws a gradient with this color to
     * white from right to left. Draws a gradient with black to transparent from the bottom up.
     * @param color the ColorInt to base the gradients on. Should have S and V values of 1.
     */
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

    /**
     * Sets the current color on the dialog. This sets all member fields according to that color.
     * Sets the color field. Sets the launchColor field if not set yet. Saves R,G,B values and H,S,V
     * values. Saves the hue extreme ColorInt.
     * Does not modify any views.
     * @param color the ColorInt integer to set.
     */
    public void setColor(@ColorInt int color) {
        this.color = color;

        if (this.launchColor == -1) {
            this.launchColor = color;
        }

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

    /**
     * Uses colorX and colorY to determine the current selected color. Sets the RGB and hex values
     * on the inputs and updates the preview rectangle.
     */
    protected void updateColor() {
        rlColors.setDrawingCacheEnabled(true);
        rlColors.buildDrawingCache();
        final Bitmap colors = rlColors.getDrawingCache();
        setColor(colors.getPixel(colorX, colorY));
        rlColors.setDrawingCacheEnabled(false);

        etRedValue.setText(String.valueOf(r));
        etGreenValue.setText(String.valueOf(g));
        etBlueValue.setText(String.valueOf(b));
        etHexValue.setText(String.format("%06X", (0xFFFFFF & color)));

        viewPreview.setBackgroundColor(color);
    }

    protected GradientDrawable getCurrentColorMarker(@ColorInt int color) {
        if (currentColorMarker == null) {
            currentColorMarker = new GradientDrawable();
            currentColorMarker.setCornerRadius(100);
        }

        currentColorMarker.setStroke(2, color);
        return currentColorMarker;
    }

    protected void updateCurrentColorMarker() {
        colorSpot.setTranslationX(colorX - colorSpot.getWidth() / 2);
        colorSpot.setTranslationY(colorY - colorSpot.getHeight() / 2);
        if (colorY < flColorA.getHeight() / 2) {
            colorSpot.setBackground(getCurrentColorMarker(Color.BLACK));
        } else {
            colorSpot.setBackground(getCurrentColorMarker(Color.WHITE));
        }
    }

    protected void rearrangeForExteriorInput() {
        colorX = (int) (flColorA.getWidth() * s);
        colorY = flColorA.getHeight() - (int) (flColorA.getHeight() * v);
        updateCurrentColorMarker();
        updateColor();

        float hueSliderX = llRainbow.getWidth() * (h / 360f);
        hueTracker.setTranslationX(hueSliderX - hueTracker.getWidth() / 2);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (hexKeyEntered(keyCode) && (etRedValue.isFocused() || etGreenValue.isFocused() || etBlueValue.isFocused())) {
            try {
                int red = Integer.parseInt(etRedValue.getText().toString());
                int green = Integer.parseInt(etGreenValue.getText().toString());
                int blue = Integer.parseInt(etBlueValue.getText().toString());

                if (red > 0 && red <= 255 && green > 0 && green < 255 && blue > 0 && blue < 255) {
                    float[] hsv = new float[3];
                    Color.RGBToHSV(red, green, blue, hsv);
                    color = Color.HSVToColor(hsv);
                    setColor(color);
                    updateGradients(hueExtreme);
                    rearrangeForExteriorInput();
                }
            } catch (Exception e) {
                // Don't crash and ignore.
            }

        } else if (hexKeyEntered(keyCode) && etHexValue.isFocused()) {
            String input = etHexValue.getText().toString();
            if (!input.startsWith("#")) {
                input = "#" + input;
            }
            try {
                int color = Color.parseColor(input);
                setColor(color);

                updateGradients(hueExtreme);

                rearrangeForExteriorInput();
                Log.v("amb-color", "good!");
                //setColor(color);
                //rearrangeForExteriorInput();
            } catch (Exception e) {
                // no updating when not parsed.
                if (input.length() == 7) {
                    Log.e("amb-color", e.toString());
                }
                Log.v("amb-color", "bad!");
            }

            return false;
        }

        Log.v("amb-color", "none!");
        return false;
    }

    protected boolean hexKeyEntered(int keyCode) {
        List<Integer> allowedCodes = Arrays.asList(
                KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_C, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_F,
                KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9
        );

        return allowedCodes.contains(keyCode);
    }

}
