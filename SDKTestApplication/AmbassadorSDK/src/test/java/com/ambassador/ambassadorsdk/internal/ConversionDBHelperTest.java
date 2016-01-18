package com.ambassador.ambassadorsdk.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ambassador.ambassadorsdk.ConversionParameters;

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
        Utilities.class,
        Log.class
})
public class ConversionDBHelperTest {

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                Utilities.class,
                Log.class
        );

        PowerMockito.spy(ConversionDBHelper.class);
    }

    @Test
    public void onCreateTest() {
        // ARRANGE
        Context context = Mockito.mock(Context.class);
        ConversionDBHelper conversionDBHelper = Mockito.spy(new ConversionDBHelper(context));
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        Mockito.doNothing().when(db).execSQL(Mockito.anyString());

        // ACT
        conversionDBHelper.onCreate(db);

        // ASSERT
        Mockito.verify(db).execSQL(Mockito.eq(ConversionSQLStrings.SQL_CREATE_ENTRIES));
    }

    @Test
    public void onUpgradeTest() {
        // ARRANGE
        Context context = Mockito.mock(Context.class);
        ConversionDBHelper conversionDBHelper = Mockito.spy(new ConversionDBHelper(context));
        Mockito.doNothing().when(conversionDBHelper).onCreate(Mockito.any(SQLiteDatabase.class));
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        Mockito.doNothing().when(db).execSQL(Mockito.anyString());

        // ACT
        conversionDBHelper.onUpgrade(db, 0, 1);

        // ASSERT
        Mockito.verify(conversionDBHelper).onCreate(Mockito.eq(db));
        Mockito.verify(db).execSQL(Mockito.eq(ConversionSQLStrings.SQL_DELETE_ENTRIES));
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
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CAMPAIGN, conversionParameters.campaign);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL, conversionParameters.email);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_FIRST_NAME, conversionParameters.firstName);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_LAST_NAME, conversionParameters.lastName);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EMAIL_NEW_AMBASSADOR, conversionParameters.emailNewAmbassador);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_UID, conversionParameters.uid);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM1, conversionParameters.custom1);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM2, conversionParameters.custom2);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_CUSTOM3, conversionParameters.custom3);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_AUTO_CREATE, conversionParameters.autoCreate);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_REVENUE, conversionParameters.revenue);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_DEACTIVATE_NEW_AMBASSADOR, conversionParameters.deactivateNewAmbassador);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_TRANSACTION_UID, conversionParameters.transactionUid);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_ADD_TO_GROUP_ID, conversionParameters.addToGroupId);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA1, conversionParameters.eventData1);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA2, conversionParameters.eventData2);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_EVENT_DATA3, conversionParameters.eventData3);
        Mockito.verify(contentValues).put(ConversionSQLStrings.ConversionSQLEntry.MBSY_IS_APPROVED, conversionParameters.isApproved);
    }

    @Test
    public void createConversionParameterWithCursorTest() throws Exception {
        // ARRANGE
        Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(cursor.getInt(Mockito.anyInt())).thenReturn(-1);
        Mockito.when(cursor.getString(Mockito.anyInt())).thenReturn("-1");

        // ACT
        ConversionParameters conversionParameters = ConversionDBHelper.createConversionParameterWithCursor(cursor);

        // ASSERT
        Assert.assertEquals(conversionParameters.campaign, -1);
        Assert.assertEquals(conversionParameters.email, "-1");
        Assert.assertEquals(conversionParameters.firstName, "-1");
        Assert.assertEquals(conversionParameters.lastName, "-1");
        Assert.assertEquals(conversionParameters.emailNewAmbassador, -1);
        Assert.assertEquals(conversionParameters.uid, "-1");
        Assert.assertEquals(conversionParameters.custom1, "-1");
        Assert.assertEquals(conversionParameters.custom2, "-1");
        Assert.assertEquals(conversionParameters.custom3, "-1");
        Assert.assertEquals(conversionParameters.autoCreate, -1);
        Assert.assertEquals(conversionParameters.revenue, -1);
        Assert.assertEquals(conversionParameters.deactivateNewAmbassador, -1);
        Assert.assertEquals(conversionParameters.transactionUid, "-1");
        Assert.assertEquals(conversionParameters.addToGroupId, "-1");
        Assert.assertEquals(conversionParameters.eventData1, "-1");
        Assert.assertEquals(conversionParameters.eventData2, "-1");
        Assert.assertEquals(conversionParameters.eventData3, "-1");
        Assert.assertEquals(conversionParameters.isApproved, -1);
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
