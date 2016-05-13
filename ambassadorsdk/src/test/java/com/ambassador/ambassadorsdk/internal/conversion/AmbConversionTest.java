package com.ambassador.ambassadorsdk.internal.conversion;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.google.gson.JsonParser;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbSingleton.class
})
public class AmbConversionTest {

    protected Context context;

    @Before
    public void setup() {
        PowerMockito.mockStatic(
                AmbSingleton.class
        );

        context = Mockito.mock(Context.class);
        PowerMockito.when(AmbSingleton.getContext()).thenReturn(context);
    }

    @Test
    public void testsSaveDoesAddToNonEmptyArray() {
        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn("[{\"cats\":\"cats\"}]");
        final SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = (String) invocation.getArguments()[1];
                Assert.assertEquals(2, new JsonParser().parse(value).getAsJsonArray().size());
                return editor;
            }
        }).when(editor).putString(Mockito.anyString(), Mockito.anyString());

        AmbConversion ambConversion = AmbConversion.get(new ConversionParameters(), false, Mockito.mock(ConversionStatusListener.class));
        ambConversion.save();
    }

    @Test
    public void testsSaveDoesAddToEmptyArray() {
        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn("[]");
        final SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = (String) invocation.getArguments()[1];
                Assert.assertEquals(1, new JsonParser().parse(value).getAsJsonArray().size());
                return editor;
            }
        }).when(editor).putString(Mockito.anyString(), Mockito.anyString());

        AmbConversion ambConversion = AmbConversion.get(new ConversionParameters(), false, Mockito.mock(ConversionStatusListener.class));
        ambConversion.save();
    }

}
