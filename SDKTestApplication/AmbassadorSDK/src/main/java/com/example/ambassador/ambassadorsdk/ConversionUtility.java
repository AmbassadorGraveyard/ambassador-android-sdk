package com.example.ambassador.ambassadorsdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by JakeDunahee on 8/21/15.
 */
class ConversionUtility {
    private ConversionParameters parameters;
    private ConversionDBHelper helper;
    private SQLiteDatabase db;

    // Constructors for ConversionUtility
    public ConversionUtility(Context context) {
        helper = new ConversionDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public ConversionUtility(Context context, ConversionParameters parameters) {
        this.parameters = parameters;
        helper = new ConversionDBHelper(context);
        db = helper.getWritableDatabase();
    }

    // Function used by ambassador Singleton to register a conversion
    public void registerConversion() {
        if (AmbassadorSingleton.getInstance().getPusherInfo() != null) {
            try {
                if (parameters.isValid()) {
                    ConversionRequest request = new ConversionRequest();
                    request.conversionParameters = parameters;
                    request.execute();
                } else {
                    throw new ConversionParametersException();
                }
            } catch (ConversionParametersException ex) {
                Log.e("Conversion", ex.toString());
            }
        } else {
            ContentValues values = ConversionDBHelper.createValuesFromConversion(parameters);
            db.insert(ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME, null, values);
            Log.d("Conversion", "Inserted row into table");
        }
    }

    // Creates a JSON object from Conversion Params and some Augur data
    public JSONObject createJSONConversion(ConversionParameters parameters) {
        JSONObject conversionObject = new JSONObject();
        JSONObject fp = new JSONObject();
        JSONObject consumerObject = new JSONObject();
        JSONObject deviceObject = new JSONObject();
        JSONObject fieldObject = new JSONObject();

        try {
            JSONObject identity = new JSONObject(AmbassadorSingleton.getInstance().getIdentifyObject());
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
        if (AmbassadorSingleton.getInstance().getPusherInfo() != null) {
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

            // If there is anything in the database, cycles through and makes request to register conversions
            if (cursor.moveToFirst()) {
                do {
                    ConversionParameters parameters = ConversionDBHelper.createConversionParameterWithCursor(cursor);
                    ConversionRequest request = new ConversionRequest();
                    request.conversionParameters = parameters;
                    request.execute();

                    // Deletes row after registering conversion and will resave if failure occurs
                    ConversionDBHelper.deleteRow(db, cursor.getInt(cursor.getColumnIndex(ConversionSQLStrings.ConversionSQLEntry._ID)));
                } while (cursor.moveToNext());
            }
        }
    }

    // Call to register conversion
    private class ConversionRequest extends AsyncTask<Void, Void, Void> {
        public int statusCode;
        public ConversionParameters conversionParameters;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://dev-ambassador-api.herokuapp.com/universal/action/conversion/?u=***REMOVED***/";

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getAPIKey());

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(createJSONConversion(conversionParameters).toString());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                Log.d("Conversion", "Conversion successful: " + response.toString());
            } catch (IOException e) {
                e.printStackTrace();
                statusCode = 1;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // If the response fails, the conversion is stored to a database
            if (statusCode < 200 || statusCode > 299) {
                ContentValues values = ConversionDBHelper.createValuesFromConversion(conversionParameters);
                db.insert(ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME, null, values);
                Log.d("Conversion", "Inserted row into table");
            }
        }
    }

    class ConversionParametersException extends Exception {
        public ConversionParametersException() {
            super("Conversion parameters must have set values for 'mbsy_revenue," +
                    "'mbsy_campaign', and 'mbsy_email.");
        }
    }
}
