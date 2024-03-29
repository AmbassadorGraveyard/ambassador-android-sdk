package com.ambassador.ambassadorsdk.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import javax.inject.Inject;

public class Utilities {
    @Inject
    protected AmbSingleton AmbSingleton;

    public interface UrlAlertInterface {
        void sendAnywayTapped(DialogInterface dialogInterface);
        void insertUrlTapped(DialogInterface dialogInterface);
    }

    public int getPixelSizeForDimension(int dimension) {
        Context cxt =AmbSingleton.getInstance().getContext();
        return cxt.getResources().getDimensionPixelSize(dimension);
    }
    
    public float getDpSizeForPixels(int pixels) {
        Context cxt =AmbSingleton.getInstance().getContext();
        Resources resources = cxt.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = pixels / (metrics.densityDpi / 160f);
        return dp;
    }

    public boolean containsURL(String message, String url) {
        if (message == null) return false;
        return message.contains(url);
    }

    public void presentNonCancelableMessageDialog(Context context, String title, String message, DialogInterface.OnClickListener okayOnClickListener) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(new StringResource(R.string.ok).getValue(), okayOnClickListener)
                .setCancelable(false)
                .show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.twitter_blue));
    }

    public void debugLog(String tagString, String logMessage) {
        if (!BuildConfig.IS_RELEASE_BUILD) {
            Log.d(tagString, logMessage);
        }
    }

    public void debugLog(String logMessage) {
        if (!BuildConfig.IS_RELEASE_BUILD) {
            StackTraceElement stackTrace = new Exception().getStackTrace()[1];
            String tag = stackTrace.getClassName()
                    .substring(stackTrace.getClassName().lastIndexOf(".") + 1)
                    + "." + stackTrace.getMethodName() + "():"
                    + stackTrace.getLineNumber();
            Log.d(tag, logMessage);
        }
    }

    public float getScreenDensity() {
        return AmbSingleton.getInstance().getContext().getResources().getDisplayMetrics().density;
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.isConnected()) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("all")
    public void setStatusBar(Window window, int primaryColor) {
        if (getSdkInt() >= 21) {
            float[] hsv = new float[3];
            Color.colorToHSV(primaryColor, hsv);
            hsv[2] *= 0.8f;
            primaryColor = Color.HSVToColor(hsv);
            window.setStatusBarColor(primaryColor);
        }
    }

    public int getSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    public float getTextWidthDp(String text, TextView tv) {
        Rect bounds = buildRect();
        Paint textPaint = tv.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        float width = getDpSizeForPixels(bounds.width());
        return width;
    }

    protected Rect buildRect() {
        return new Rect();
    }

    public String cutTextToShow(String text, TextView tv, float maxWidth) {
        String cut;
        for (int i = 0; i < text.length() + 1; i++) {
            cut = text.substring(0, i);
            if (getTextWidthDp(cut, tv) > maxWidth) {
                return cut.substring(0, cut.length() - 1);
            }
        }

        return "";
    }

}
