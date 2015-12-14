package com.ambassador.ambassadorsdk.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;


/**
 * Created by JakeDunahee on 8/21/15.
 */
public class ConversionUtility {

    private ConversionParameters parameters;
    private ConversionDBHelper helper;
    private SQLiteDatabase db;

    @Inject
    RequestManager requestManager;

    @Inject
    AmbassadorConfig ambassadorConfig;

    // Constructors for ConversionUtility
    public ConversionUtility(Context context) {
        helper = new ConversionDBHelper(context);
        db = helper.getWritableDatabase();
        AmbassadorSingleton.getComponent().inject(this);
    }

    public ConversionUtility(Context context, ConversionParameters parameters) {
        this.parameters = parameters;
        helper = new ConversionDBHelper(context);
        db = helper.getWritableDatabase();
        AmbassadorSingleton.getComponent().inject(this);
    }

    public void registerConversion() {
        //if either augur identify or install intent hasn't come back yet, insert into DB for later retry
        if (ambassadorConfig.getIdentifyObject() == null || ambassadorConfig.getReferralShortCode() == null) {
            ContentValues values = ConversionDBHelper.createValuesFromConversion(parameters);
            db.insert(ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME, null, values);
            Utilities.debugLog("Conversion", "Inserted row into table");
            return;
        }

        try {
            if (parameters.isValid()) {
                makeConversionRequest(parameters);
            } else {
                throw new ConversionParametersException();
            }
        } catch (ConversionParametersException ex) {
            Log.e("Conversion", ex.toString());
        }
    }

    // Creates a JSON object from Conversion Params and some Augur data
    public static JSONObject createJSONConversion(ConversionParameters parameters, String identifyObject) {
        JSONObject conversionObject = new JSONObject();
        JSONObject fp = new JSONObject();
        JSONObject consumerObject = new JSONObject();
        JSONObject deviceObject = new JSONObject();
        JSONObject fieldObject = new JSONObject();

        try {
            JSONObject identity = new JSONObject(identifyObject);
            JSONObject consumer= identity.getJSONObject("consumer");
            JSONObject device = identity.getJSONObject("device");
            consumerObject.put("UID", consumer.getString("UID"));
            deviceObject.put("type", device.getString("type"));
            deviceObject.put("ID", device.getString("ID"));

            fp.put("consumer", consumerObject);
            fp.put("device", deviceObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN, parameters.mbsy_campaign);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL, parameters.mbsy_email);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME, parameters.mbsy_first_name);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME, parameters.mbsy_last_name);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR, parameters.mbsy_email_new_ambassador);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_UID, parameters.mbsy_uid);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1, parameters.mbsy_custom1);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2, parameters.mbsy_custom2);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3, parameters.mbsy_custom3);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE, parameters.mbsy_auto_create);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE, parameters.mbsy_revenue);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR, parameters.mbsy_deactivate_new_ambassador);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID, parameters.mbsy_transaction_uid);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID, parameters.mbsy_add_to_group_id);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1, parameters.mbsy_event_data1);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2, parameters.mbsy_event_data2);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3, parameters.mbsy_event_data3);
            fieldObject.put(ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED, parameters.mbsy_is_approved);
            fieldObject.put("mbsy_short_code", parameters.mbsy_short_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            conversionObject.put("fp", fp);
            conversionObject.put("fields", fieldObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return conversionObject;
    }

    // Attempts to register conversions stored in database
    public void readAndSaveDatabaseEntries() {
        //if we don't have an identify object yet, that means neither the intent nor augur has returned
        //so bail out and try again later
        if (ambassadorConfig.getIdentifyObject() == null || ambassadorConfig.getReferralShortCode() == null) return;

        String[] projection = {
                ConversionSQLStrings.ConversionSQLEntry._ID,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_UID,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3,
                ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED,
        };

        String sortOrder = ConversionSQLStrings.ConversionSQLEntry._ID + " DESC";

        Cursor cursor = db.query(
                ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);

        //nothing in the database, bail out
        if (!cursor.moveToFirst()) return;

        //if anything in the database, cycles through and makes requests to register conversions
        do {
            final ConversionParameters DBparameters = ConversionDBHelper.createConversionParameterWithCursor(cursor);
            makeConversionRequest(DBparameters);

            // Deletes row after registering conversion and will resave if failure occurs
            ConversionDBHelper.deleteRow(db, cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry._ID)));
        } while (cursor.moveToNext());
    }

    public void makeConversionRequest(final ConversionParameters newParameters) {
        //in the case of an install conversion, we didn't have the shortCode right away, so that conversion got stored in the database.
        //now that we know we have it (wouldn't have gotten this far if we didn't) set that parameter value.
        newParameters.mbsy_short_code = ambassadorConfig.getReferralShortCode();

        requestManager.registerConversionRequest(newParameters, new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                Utilities.debugLog("Conversion", "Conversion Registered Successfully!");
            }

            @Override
            public void onFailure(Object failureResponse) {
                ContentValues values = ConversionDBHelper.createValuesFromConversion(newParameters);
                db.insert(ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME, null, values);
                Utilities.debugLog("Conversion", "Inserted row into table");
            }
        });
    }

    class ConversionParametersException extends Exception {
        public ConversionParametersException() {
            super("Conversion parameters must have set values for 'mbsy_revenue," +
                    "'mbsy_campaign', and 'mbsy_email.");
        }
    }


}