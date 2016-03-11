package com.ambassador.ambassadorsdk.internal;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

/**
 * Content observer that will listen for an SMS being sent to a specific phone number, and will notify
 * via SmsSendListener interface callback.
 */
public class SmsSendObserver extends ContentObserver {

    /** The uri being listened for changes in. This is SMS. */
    protected static final Uri uri;
    static {
        uri = Uri.parse("content://sms/");
    }

    /** Cursor column name for the phone number. */
    protected static final String COLUMN_ADDRESS = "address";

    /** Cursor column name for type of record. */
    protected static final String COLUMN_TYPE = "type";

    /** Constant for the record type that we want: a message being sent. */
    protected static final int MESSAGE_TYPE_SENT = 2;

    /** Reference to context. Used to work with ContentResolver. */
    protected Context context = null;

    /** ContentResolver used to register and unregister observer. */
    protected ContentResolver resolver = null;

    /** The phone number that the observer is listening for an SMS to. */
    protected String phoneNumber = null;

    /** The SmsSendListener interface to callback through when an SMS is sent to phoneNumber. */
    protected SmsSendListener smsSendListener;

    /**
     * Sets dependencies and fields and creates the ContentResolver.
     * @param context the context to be stored and used to create the ContentResolver.
     * @param phoneNumber the phone number to listener for SMS being sent to.
     */
    public SmsSendObserver(Context context, String phoneNumber) {
        super(new Handler());
        this.context = context;
        this.resolver = context.getContentResolver();
        this.phoneNumber = phoneNumber;
    }

    /**
     * Registers the ContentObserver on the context.
     */
    public void start() {
        if (resolver != null) {
            resolver.registerContentObserver(uri, true, this);
        }
    }

    /**
     * Unregisters the ContentObserver and nullifies dependencies.
     */
    public void stop() {
        if (resolver != null) {
            resolver.unregisterContentObserver(this);
            resolver = null;
            context = null;
        }
    }

    /**
     * Method override that is called whenever the contact that is being observed changes, so we check
     * the content here.
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, new String[]{ COLUMN_ADDRESS, COLUMN_TYPE }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));

                if (phoneNumber.equals(address) && MESSAGE_TYPE_SENT == type && smsSendListener != null) {
                    smsSendListener.onSmsSent();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Sets the SMS listener interface to callback through.
     * @param smsSendListener the SmsSendListener to use.
     */
    public void setSmsSendListener(SmsSendListener smsSendListener) {
        this.smsSendListener = smsSendListener;
    }

    /**
     * Interface to callback through when an SMS is sent to the phone number in the constructor.
     */
    public interface SmsSendListener {
        void onSmsSent();
    }

}
