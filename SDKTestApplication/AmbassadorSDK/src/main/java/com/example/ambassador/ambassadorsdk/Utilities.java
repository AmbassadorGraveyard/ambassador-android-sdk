package com.example.ambassador.ambassadorsdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by JakeDunahee on 8/31/15.
 */
class Utilities {
    interface UrlAlertInterface {
        void sendAnywayTapped(DialogInterface dialogInterface);
        void insertUrlTapped(DialogInterface dialogInterface);
    }

    public static Boolean isSuccessfulResponseCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300);
    }

    public static int getPixelSizeForDimension(int dimension) {
        Context cxt = MyApplication.getAppContext();
        return cxt.getResources().getDimensionPixelSize(dimension);
    }

    public static boolean containsURL(String message) {
        return message.contains(AmbassadorSingleton.getInstance().getURL());
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String deviceType(Context context) {
        return (Utilities.isTablet(context)) ? "Tablet" : "SmartPhone";
    }

    public static void presentUrlDialog(Context context, final EditText editText, final UrlAlertInterface alertInterface) {
        AlertDialog dialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Hold on!")
                .setMessage(context.getResources().getString(R.string.missing_url_dialog_message) + " " + AmbassadorSingleton.getInstance().getURL())
                .setPositiveButton("Continue Sending", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertInterface.sendAnywayTapped(dialog);
                    }
                })
                .setNegativeButton("Insert Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _insertURLIntoMessage(editText);
                        alertInterface.insertUrlTapped(dialog);
                    }
                }).show();

        dialogBuilder.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.twitter_blue));
        dialogBuilder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.twitter_blue));
    }

    private static void _insertURLIntoMessage(EditText editText) {
        String appendingLink = AmbassadorSingleton.getInstance().getURL();

        if (editText.getText().toString().contains("http://")) {
            String sub = editText.getText().toString().substring(editText.getText().toString().indexOf("http://"));
            String replacementSubstring;
            replacementSubstring = (sub.contains(" ")) ? sub.substring(0, sub.indexOf(' ')) : sub;
            editText.setText(editText.getText().toString().replace(replacementSubstring, appendingLink));

            return;
        }

        if (editText.getText().toString().charAt(editText.getText().toString().length() - 1) != ' ') {
            appendingLink = " " + AmbassadorSingleton.getInstance().getURL();
            editText.setText(editText.getText().append(appendingLink));
        }
    }


    public static void debugLog(String tagString, String logMessage) {
        if (!AmbassadorSingleton.isReleaseBuild) {
            Log.d(tagString, logMessage);
        }
    }

    public static float getScreenDensity() {
        return MyApplication.getAppContext().getResources().getDisplayMetrics().density;
    }
}
