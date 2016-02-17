package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.google.gson.JsonObject;

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
        AmbassadorSingleton.class,
})
public class UserTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class
        );

        context = Mockito.mock(Context.class);
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);

        sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);

        editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.when(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor);
    }

    @Test
    public void saveWithNonNullContextDoesSaveSerialized() {
        // ARRANGE
        User user = new User();
        user.email = "jake@getambassador.com";
        user.firstName = "jake";
        user.lastName = "dunahee";
        user.gcmToken = "gcmToken";
        user.augurData = null;

        // ACT
        user.save();

        // ASSERT
        Mockito.verify(context).getSharedPreferences(Mockito.eq("user"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(editor).putString(Mockito.eq("jake@getambassador.com"), Mockito.eq("{\"firstName\":\"jake\",\"lastName\":\"dunahee\",\"email\":\"jake@getambassador.com\",\"gcmToken\":\"gcmToken\"}"));
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveWithNullContextDoesNotSave() {
        // ARRANGE
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(null);

        User user = new User();
        user.email = "jake@getambassador.com";
        user.firstName = "jake";
        user.lastName = "dunahee";
        user.gcmToken = "gcmToken";
        user.augurData = null;

        // ACT
        user.save();

        // ASSERT
        Mockito.verify(context, Mockito.never()).getSharedPreferences(Mockito.eq("user"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(sharedPreferences, Mockito.never()).edit();
        Mockito.verify(editor, Mockito.never()).putString(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(editor, Mockito.never()).apply();
    }

    @Test
    public void clearDoesClearAllFields() {
        // ARRANGE
        User user = new User();
        user.email = "jake@getambassador.com";
        user.firstName = "jake";
        user.lastName = "dunahee";
        user.gcmToken = "gcmToken";
        user.augurData = new JsonObject();

        // ACT
        user.clear();

        // ASSERT
        Assert.assertNull(user.getEmail());
        Assert.assertNull(user.getFirstName());
        Assert.assertNull(user.getLastName());
        Assert.assertNull(user.getGcmToken());
        Assert.assertNull(user.getAugurData());
    }

    @Test
    public void settersDoSaveOnInvocation() {
        // ARRANGE
        User user = Mockito.spy(new User());
        Mockito.doNothing().when(user).save();

        // ACT
        user.setEmail("jake@getambassador.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setGcmToken("gcmToken");
        user.setAugurData(new JsonObject());

        // ASSERT
        Mockito.verify(user, Mockito.times(5)).save();
    }

}