package com.ambassador.ambassadorsdk.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConversionDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Conversion.db";

    public ConversionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConversionSQLStrings.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ConversionSQLStrings.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static ContentValues createValuesFromConversion(ConversionParameters parameters) {
        ContentValues values = buildContentValues();

        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN, parameters.mbsy_campaign);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL, parameters.mbsy_email);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME, parameters.mbsy_first_name);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME, parameters.mbsy_last_name);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR, parameters.mbsy_email_new_ambassador);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_UID, parameters.mbsy_uid);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1, parameters.mbsy_custom1);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2, parameters.mbsy_custom2);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3, parameters.mbsy_custom3);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE, parameters.mbsy_auto_create);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE, parameters.mbsy_revenue);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR, parameters.mbsy_deactivate_new_ambassador);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID, parameters.mbsy_transaction_uid);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID, parameters.mbsy_add_to_group_id);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1, parameters.mbsy_event_data1);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2, parameters.mbsy_event_data2);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3, parameters.mbsy_event_data3);
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED, parameters.mbsy_is_approved);

        return values;
    }

    static ContentValues buildContentValues() {
        return new ContentValues();
    }

    public static ConversionParameters createConversionParameterWithCursor(Cursor cursor) {
        ConversionParameters parameters = buildConversionParameters();

        parameters.mbsy_campaign = cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN));
        parameters.mbsy_email = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL));
        parameters.mbsy_first_name = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME));
        parameters.mbsy_last_name = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME));
        parameters.mbsy_email_new_ambassador = cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR));
        parameters.mbsy_uid = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_UID));
        parameters.mbsy_custom1 = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1));
        parameters.mbsy_custom2 = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2));
        parameters.mbsy_custom3 = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3));
        parameters.mbsy_auto_create = cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE));
        parameters.mbsy_revenue = cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE));
        parameters.mbsy_deactivate_new_ambassador = cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR));
        parameters.mbsy_transaction_uid = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID));
        parameters.mbsy_add_to_group_id = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID));
        parameters.mbsy_event_data1 = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1));
        parameters.mbsy_event_data2 = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2));
        parameters.mbsy_event_data3 = cursor.getString(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3));
        parameters.mbsy_is_approved = cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED));

        return parameters;
    }

    static ConversionParameters buildConversionParameters() {
        return new ConversionParameters();
    }

    public static void deleteRow(SQLiteDatabase db, int rowId) {
        String selection = ConversionSQLStrings.ConversionSQLEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowId) };
        db.delete(ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME, selection, selectionArgs);
        Utilities.debugLog("Conversion", "Removing row " + String.valueOf(rowId) + " with selection: " + selection);
    }

}
