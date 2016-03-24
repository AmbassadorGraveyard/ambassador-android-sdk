package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbSingleton.class,
        RequestManager.class,
})
public class AuthTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class
        );

        context = Mockito.mock(Context.class);
        Mockito.when(AmbSingleton.getContext()).thenReturn(context);

        sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);

        editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.when(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor);
    }

    @Test
    public void saveWithNonNullContextDoesSaveSerialized() {
        // ARRANGE
        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.envoyId = "envoyId";
        auth.envoySecret = "envoySecret";

        // ACT
        auth.save();

        // ASSERT
        Mockito.verify(context).getSharedPreferences(Mockito.eq("auth"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(editor).putString(Mockito.eq("auth"), Mockito.eq("{\"universalId\":\"universalId\",\"universalToken\":\"universalToken\",\"envoyId\":\"envoyId\",\"envoySecret\":\"envoySecret\"}"));
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveWithNullContextDoesNotSave() {
        // ARRANGE
        Mockito.when(AmbSingleton.getContext()).thenReturn(null);

        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.envoyId = "envoyId";
        auth.envoySecret = "envoySecret";

        // ACT
        auth.save();

        // ASSERT
        Mockito.verify(context, Mockito.never()).getSharedPreferences(Mockito.eq("auth"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(sharedPreferences, Mockito.never()).edit();
        Mockito.verify(editor, Mockito.never()).putString(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(editor, Mockito.never()).apply();
    }

    @Test
    public void clearDoesClearAllFields() {
        // ARRANGE
        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.envoyId = "envoyId";
        auth.envoySecret = "envoySecret";


        // ACT
        auth.clear();

        // ASSERT
        Assert.assertNull(auth.getUniversalId());
        Assert.assertNull(auth.getUniversalToken());
        Assert.assertNull(auth.getEnvoyId());
        Assert.assertNull(auth.getEnvoySecret());
    }

    @Test
    public void settersDoSaveOnInvocation() {
        // ARRANGE
        Auth auth = Mockito.spy(new Auth());
        Mockito.doNothing().when(auth).save();

        // ACT
        auth.setUniversalId("universalId");
        auth.setUniversalToken("universalToken");
        auth.setEnvoyId("envoyId");
        auth.setEnvoySecret("envoySecret");

        // ASSERT
        Mockito.verify(auth, Mockito.times(4)).save();
    }

}
