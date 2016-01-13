package com.ambassador.ambassadorsdk;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.HashMap;

public class TestUtils {

    private static HashMap<Integer, String> overrides = new HashMap<>();

    public static void mockString(int id, String value) {
        overrides.put(id, value);
    }

    public static void mockStrings() {
        mockStrings(Mockito.mock(Context.class));
    }

    public static void mockStrings(Context context) {
        PowerMockito.mockStatic(AmbassadorSingleton.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);
        PowerMockito.doReturn("string").when(context).getString(Mockito.anyInt());
        for (int key : overrides.keySet()) {
            PowerMockito.doReturn(overrides.get(key)).when(context).getString(key);
        }
    }

}
