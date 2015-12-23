package com.ambassador.ambassadorsdk.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
import android.widget.EditText;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.R;

public class Utilities {

    interface UrlAlertInterface {
        void sendAnywayTapped(DialogInterface dialogInterface);
        void insertUrlTapped(DialogInterface dialogInterface);
    }

    public static Boolean isSuccessfulResponseCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300);
    }

    public static int getPixelSizeForDimension(int dimension) {
        Context cxt = AmbassadorSingleton.get();
        return cxt.getResources().getDimensionPixelSize(dimension);
    }
    
    public static float getDpSizeForPixels(int pixels) {
        Context cxt = AmbassadorSingleton.get();
        Resources resources = cxt.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = pixels / (metrics.densityDpi / 160f);
        return dp;
    }

    public static boolean containsURL(String message, String url) {
        if (message == null) return false;
        return message.contains(url);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String deviceType(Context context) {
        return (isTablet(context)) ? "Tablet" : "SmartPhone";
    }

    public static void presentUrlDialog(Context context, final EditText editText, final String url, final UrlAlertInterface alertInterface) {
        AlertDialog dialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Hold on!")
                .setMessage(context.getResources().getString(R.string.missing_url_dialog_message) + " " + url)
                .setPositiveButton("Continue Sending", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertInterface.sendAnywayTapped(dialog);
                    }
                })
                .setNegativeButton("Insert Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _insertURLIntoMessage(editText, url);
                        alertInterface.insertUrlTapped(dialog);
                    }
                }).show();

        dialogBuilder.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.twitter_blue));
        dialogBuilder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.twitter_blue));
    }

    public static void presentNonCancelableMessageDialog(Context context, String title, String message, DialogInterface.OnClickListener okayOnClickListener) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", okayOnClickListener)
                .setCancelable(false)
                .show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.twitter_blue));
    }

    private static void _insertURLIntoMessage(EditText editText, String url) {
        String appendingLink = url;

        if (editText.getText().toString().contains("http://")) {
            String sub = editText.getText().toString().substring(editText.getText().toString().indexOf("http://"));
            String replacementSubstring;
            replacementSubstring = (sub.contains(" ")) ? sub.substring(0, sub.indexOf(' ')) : sub;
            editText.setText(editText.getText().toString().replace(replacementSubstring, appendingLink));

            return;
        }

        if (editText.getText().toString().length() != 0 && editText.getText().toString().charAt(editText.getText().toString().length() - 1) != ' ') {
            appendingLink = " " + url;
            editText.setText(editText.getText().append(appendingLink));
        } else {
            appendingLink = url;
            editText.setText(editText.getText().append(appendingLink));
        }
    }


    public static void debugLog(String tagString, String logMessage) {
        if (!AmbassadorConfig.isReleaseBuild) {
            Log.d(tagString, logMessage);
        }
    }

    public static void debugLog(String logMessage) {
        if (!AmbassadorConfig.isReleaseBuild) {
            StackTraceElement stackTrace = new Exception().getStackTrace()[1];
            String tag = stackTrace.getClassName()
                    .substring(stackTrace.getClassName().lastIndexOf(".") + 1)
                    + "." + stackTrace.getMethodName() + "():"
                    + stackTrace.getLineNumber();
            Log.d(tag, logMessage);
        }
    }

    public static float getScreenDensity() {
        return AmbassadorSingleton.get().getResources().getDisplayMetrics().density;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public static void setStatusBar(Window window, int primaryColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            float[] hsv = new float[3];
            Color.colorToHSV(primaryColor, hsv);
            hsv[2] *= 0.8f;
            primaryColor = Color.HSVToColor(hsv);
            window.setStatusBarColor(primaryColor);
        }
    }

    public static float getTextWidthDp(String text, TextView tv) {
        Rect bounds = new Rect();
        Paint textPaint = tv.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        float width = getDpSizeForPixels(bounds.width());
        return width;
    }

    public static String cutTextToShow(String text, TextView tv, float maxWidth) {
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
