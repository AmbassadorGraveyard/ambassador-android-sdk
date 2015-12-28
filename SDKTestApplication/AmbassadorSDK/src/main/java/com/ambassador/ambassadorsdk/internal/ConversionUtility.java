package com.ambassador.ambassadorsdk.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        AmbassadorSingleton.getInstanceComponent().inject(this);
    }

    public ConversionUtility(Context context, ConversionParameters parameters) {
        this.parameters = parameters;
        helper = new ConversionDBHelper(context);
        db = helper.getWritableDatabase();
        AmbassadorSingleton.getInstanceComponent().inject(this);
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

    public static ConversionsApi.RegisterConversionRequestBody createConversionRequestBody(ConversionParameters parameters, String identifyObject) {
        Gson gson = new Gson();
        JsonObject augur = gson.fromJson(identifyObject, JsonElement.class).getAsJsonObject();
        JsonObject augurConsumer = augur.getAsJsonObject("consumer");
        JsonObject augurDevice = augur.getAsJsonObject("device");

        String augurUid = augurConsumer.get("UID").getAsString();
        String augurType = augurDevice.get("type").getAsString();
        String augurId = augurDevice.get("ID").getAsString();

        ConversionsApi.RegisterConversionRequestBody.AugurObject augurObject = new ConversionsApi.RegisterConversionRequestBody.AugurObject(augurUid, augurType, augurId);
        ConversionsApi.RegisterConversionRequestBody.FieldsObject fieldsObject = new ConversionsApi.RegisterConversionRequestBody.FieldsObject(parameters, parameters.mbsy_short_code);
        return new ConversionsApi.RegisterConversionRequestBody(augurObject, fieldsObject);
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
