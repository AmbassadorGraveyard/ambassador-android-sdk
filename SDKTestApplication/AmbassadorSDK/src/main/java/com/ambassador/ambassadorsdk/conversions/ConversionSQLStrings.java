package com.ambassador.ambassadorsdk.conversions;

import android.provider.BaseColumns;


/**
 * Created by JakeDunahee on 8/21/15.
 */

// Class with strings to use with SQL database
public final class ConversionSQLStrings {
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ConversionSQLEntry.TABLE_NAME + " (" +
                    ConversionSQLEntry._ID + " INTEGER PRIMARY KEY," +
                    ConversionSQLEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_CAMPAIGN + INT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_EMAIL + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR + INT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_UID + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_CUSTOM1 + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_CUSTOM2 + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_CUSTOM3 + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_AUTO_CREATE + INT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_REVENUE + INT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR + INT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_TRANSACTION_UID + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_EVENT_DATA1 + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_EVENT_DATA2 + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_EVENT_DATA3 + TEXT_TYPE + COMMA_SEP +
                    ConversionSQLEntry.MBSY_IS_APPROVED + INT_TYPE +
                    ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ConversionSQLEntry.TABLE_NAME;

    public static abstract class ConversionSQLEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversions";
        public static final String COLUMN_NAME_ENTRY_ID = "entry";
        public static final String MBSY_CAMPAIGN = "mbsy_campaign";
        public static final String MBSY_EMAIL = "mbsy_email";
        public static final String MBSY_FIRST_NAME = "mbsy_first_name";
        public static final String MBSY_LAST_NAME = "mbsy_last_name";
        public static final String MBSY_EMAIL_NEW_AMBASSADOR = "mbsy_email_new_ambassador";
        public static final String MBSY_UID = "mbsy_uid";
        public static final String MBSY_CUSTOM1 = "mbsy_custom1";
        public static final String MBSY_CUSTOM2 = "mbsy_custom2";
        public static final String MBSY_CUSTOM3 = "mbsy_custom3";
        public static final String MBSY_AUTO_CREATE = "mbsy_auto_create";
        public static final String MBSY_REVENUE = "mbsy_revenue";
        public static final String MBSY_DEACTIVATE_NEW_AMBASSADOR = "mbsy_deactivate_new_ambassador";
        public static final String MBSY_TRANSACTION_UID = "mbsy_transaction_uid";
        public static final String MBSY_ADD_TO_GROUP_ID = "mbsy_add_to_group_id";
        public static final String MBSY_EVENT_DATA1 = "mbsy_event_data1";
        public static final String MBSY_EVENT_DATA2 = "mbsy_event_data2";
        public static final String MBSY_EVENT_DATA3 = "mbsy_event_data3";
        public static final String MBSY_IS_APPROVED = "mbsy_is_approved";
    }
}
