package com.ambassador.ambassadorsdk.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ambassador.ambassadorsdk.ConversionParameters;

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

        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN, parameters.getCampaign());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL, parameters.getEmail());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME, parameters.getFirstName());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME, parameters.getLastName());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR, parameters.getEmailNewAmbassador());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_UID, parameters.getUid());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1, parameters.getCustom1());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2, parameters.getCustom2());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3, parameters.getCustom3());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE, parameters.getAutoCreate());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE, parameters.getRevenue());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR, parameters.getDeactivateNewAmbassador());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID, parameters.getTransactionUid());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID, parameters.getAddToGroupId());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1, parameters.getEventData1());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2, parameters.getEventData2());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3, parameters.getEventData3());
        values.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED, parameters.getIsApproved());

        return values;
    }

    static ContentValues buildContentValues() {
        return new ContentValues();
    }

    public static ConversionParameters createConversionParameterWithCursor(Cursor cursor) {
        return ConversionParameters.Builder.newInstance()
                .setCampaign(
                        cursor.getInt(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN)
                        )
                ).setEmail(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL)
                        )
                ).setFirstName(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME)
                        )
                )
                .setLastName(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME)
                        )
                )
                .setEmailNewAmbassador(
                        cursor.getInt(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR)
                        )
                )
                .setUid(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_UID)
                        )
                )
                .setCustom1(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1)
                        )
                )
                .setCustom2(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2)
                        )
                )
                .setCustom3(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3)
                        )
                )
                .setAutoCreate(
                        cursor.getInt(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE)
                        )
                )
                .setRevenue(
                        cursor.getInt(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE)
                        )
                )
                .setDeactivateNewAmbassador(
                        cursor.getInt(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR)
                        )
                )
                .setTransactionUid(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID)
                        )
                )
                .setAddToGroupId(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID)
                        )
                )
                .setEventData1(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1)
                        )
                )
                .setEventData2(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2)
                        )
                )
                .setEventData3(
                        cursor.getString(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3)
                        )
                )
                .setIsApproved(
                        cursor.getInt(
                                cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED)
                        )
                )
                .build();
    }

    public static void deleteRow(SQLiteDatabase db, int rowId) {
        String selection = ConversionSQLStrings.ConversionSQLEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowId) };
        db.delete(ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME, selection, selectionArgs);
        Utilities.debugLog("Conversion", "Removing row " + String.valueOf(rowId) + " with selection: " + selection);
    }

}
