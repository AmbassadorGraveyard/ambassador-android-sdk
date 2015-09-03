package com.example.ambassador.ambassadorsdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
                .setMessage("Your URL is not included in the message: " + AmbassadorSingleton.getInstance().getURL())
                .setPositiveButton("Send anyways", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertInterface.sendAnywayTapped(dialog);
                    }
                })
                .setNegativeButton("Insert URL", new DialogInterface.OnClickListener() {
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
        if (editText.getText().toString().contains("http://")) {
            editText.getText().replace(editText.getText().toString().indexOf("http://"),
                    editText.getText().toString().indexOf("http://") +
                            editText.getText().toString().substring(editText.getText().toString().indexOf("http://")).length(), "");
        }

        editText.setText(editText.getText().append(" " + AmbassadorSingleton.getInstance().getURL()));
    }
}
