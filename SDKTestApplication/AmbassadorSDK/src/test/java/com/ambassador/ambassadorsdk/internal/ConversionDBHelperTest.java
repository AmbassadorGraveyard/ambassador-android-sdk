package com.ambassador.ambassadorsdk.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ContentValues.class,
        ConversionDBHelper.class,
        Utilities.class
})
public class ConversionDBHelperTest {


    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                Utilities.class
        );
        PowerMockito.spy(ConversionDBHelper.class);
    }

    @Test
    public void createValuesFromConversionTest() {
        // ARRANGE
        ConversionParameters conversionParameters = new ConversionParameters();
        ContentValues contentValues = Mockito.mock(ContentValues.class);
        Mockito.when(ConversionDBHelper.buildContentValues()).thenReturn(contentValues);
        
        // ACT
        ConversionDBHelper.createValuesFromConversion(conversionParameters);
        
        // ASSERT
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN, conversionParameters.mbsy_campaign);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL, conversionParameters.mbsy_email);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME, conversionParameters.mbsy_first_name);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME, conversionParameters.mbsy_last_name);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR, conversionParameters.mbsy_email_new_ambassador);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_UID, conversionParameters.mbsy_uid);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1, conversionParameters.mbsy_custom1);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2, conversionParameters.mbsy_custom2);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3, conversionParameters.mbsy_custom3);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE, conversionParameters.mbsy_auto_create);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE, conversionParameters.mbsy_revenue);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR, conversionParameters.mbsy_deactivate_new_ambassador);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID, conversionParameters.mbsy_transaction_uid);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID, conversionParameters.mbsy_add_to_group_id);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1, conversionParameters.mbsy_event_data1);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2, conversionParameters.mbsy_event_data2);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3, conversionParameters.mbsy_event_data3);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED, conversionParameters.mbsy_is_approved);
    }

    @Test
    public void createConversionParameterWithCursorTest() {
        // ARRANGE
        ConversionParameters conversionParameters = Mockito.mock(ConversionParameters.class);
        Mockito.when(ConversionDBHelper.buildConversionParameters()).thenReturn(conversionParameters);
        Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(cursor.getInt(Mockito.anyInt())).thenReturn(-1);
        Mockito.when(cursor.getString(Mockito.anyInt())).thenReturn("-1");

        // ACT
        ConversionDBHelper.createConversionParameterWithCursor(cursor);

        // ASSERT
        Assert.assertEquals(conversionParameters.mbsy_campaign, -1);
        Assert.assertEquals(conversionParameters.mbsy_email, "-1");
        Assert.assertEquals(conversionParameters.mbsy_first_name, "-1");
        Assert.assertEquals(conversionParameters.mbsy_last_name, "-1");
        Assert.assertEquals(conversionParameters.mbsy_email_new_ambassador, -1);
        Assert.assertEquals(conversionParameters.mbsy_uid, "-1");
        Assert.assertEquals(conversionParameters.mbsy_custom1, "-1");
        Assert.assertEquals(conversionParameters.mbsy_custom2, "-1");
        Assert.assertEquals(conversionParameters.mbsy_custom3, "-1");
        Assert.assertEquals(conversionParameters.mbsy_auto_create, -1);
        Assert.assertEquals(conversionParameters.mbsy_revenue, -1);
        Assert.assertEquals(conversionParameters.mbsy_deactivate_new_ambassador, -1);
        Assert.assertEquals(conversionParameters.mbsy_transaction_uid, "-1");
        Assert.assertEquals(conversionParameters.mbsy_add_to_group_id, "-1");
        Assert.assertEquals(conversionParameters.mbsy_event_data1, "-1");
        Assert.assertEquals(conversionParameters.mbsy_event_data2, "-1");
        Assert.assertEquals(conversionParameters.mbsy_event_data3, "-1");
        Assert.assertEquals(conversionParameters.mbsy_is_approved, -1);
    }

    @Test
    public void deleteRowTest() {
        // ARRANGE
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        int rowId = 55;
        Mockito.when(db.delete(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(5);

        // ACT
        ConversionDBHelper.deleteRow(db, rowId);

        // ASSERT
        Mockito.verify(db).delete(ConversionSQLStrings.ConversionSQLEntry.TABLE_NAME, ConversionSQLStrings.ConversionSQLEntry._ID + " LIKE ?", new String[]{ String.valueOf(rowId) });
    }

}
