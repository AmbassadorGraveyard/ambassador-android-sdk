package com.ambassador.ambassadorsdk;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by dylan on 11/05/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, ContentValues.class})
public class ConversionDBHelperTest {

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(com.ambassador.ambassadorsdk.ConversionDBHelperTest conversionDBHelperTest);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        PowerMockito.mockStatic(ConversionDBHelper.class);

        TestComponent component = DaggerConversionDBHelperTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);
    }

    @Test
    public void createValuesFromConversionTest() throws Exception {

    }

    @Test
    public void createConversionParameterWithCursorTest() {
        // ARRANGE
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.getInt(anyInt())).thenReturn(0);
        when(mockCursor.getString(anyInt())).thenReturn("");

        // ACT
        ConversionDBHelper.createConversionParameterWithCursor(mockCursor);

        // ASSERT
        verify(mockCursor, times(18)).getColumnIndex(anyString());
    }

    @Test
    public void deleteRowTest() {
        // ARRANGE
        SQLiteDatabase mockDb = mock(SQLiteDatabase.class);
        when(mockDb.delete(anyString(), anyString(), (String[]) any())).thenReturn(0);
        String[] selectionArgs1 = { String.valueOf(5) };
        String[] selectionArgs2 = { String.valueOf(10) };

        PowerMockito.mockStatic(Log.class);

        // ACT
        ConversionDBHelper.deleteRow(mockDb, 5);

        // ASSERT
        verify(mockDb).delete(anyString(), anyString(), eq(selectionArgs1));

        // ACT
        ConversionDBHelper.deleteRow(mockDb, 10);

        // ASSERT
        verify(mockDb).delete(anyString(), anyString(), eq(selectionArgs2));
    }

}
