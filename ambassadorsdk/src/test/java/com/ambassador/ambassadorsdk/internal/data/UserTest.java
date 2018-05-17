package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.identify.AmbassadorIdentification;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbSingleton.class,
        Log.class,
})
public class UserTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                Log.class
        );

        context = Mockito.mock(Context.class);
        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(context);

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

        user.userId = "jake";
        user.ambassadorIdentification = new AmbassadorIdentification();
        user.ambassadorIdentification.setEmail("jake@getambassador.com");
        user.facebookAccessToken = "facebookAccessToken";
        user.twitterAccessToken = "twitterAccessToken";
        user.linkedInAccessToken = "linkedInAccessToken";

        // ACT
        user.save();

        // ASSERT
        Mockito.verify(context).getSharedPreferences(Mockito.eq("user"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(editor).putString(Mockito.eq("jake@getambassador.com"), Mockito.eq("{\"userId\":\"jake\",\"ambassadorIdentification\":{\"email\":\"jake@getambassador.com\"},\"gcmToken\":\"gcmToken\",\"facebookAccessToken\":\"facebookAccessToken\",\"twitterAccessToken\":\"twitterAccessToken\",\"linkedInAccessToken\":\"linkedInAccessToken\"}"));
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveWithNullContextDoesNotSave() {
        // ARRANGE
        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(null);

        User user = new User();
        user.userId = "jake@getambassador.com";
        user.ambassadorIdentification = new AmbassadorIdentification();

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
        user.userId = "jake@getambassador.com";
        user.ambassadorIdentification = new AmbassadorIdentification();

        // ACT
        user.clear();

        // ASSERT
        Assert.assertNull(user.getUserId());
    }

    @Test
    public void settersDoSaveOnInvocation() {
        // ARRANGE
        User user = Mockito.spy(new User());
        Mockito.doNothing().when(user).save();

        // ACT
        user.setUserId("jake@getambassador.com");
        user.setAmbassadorIdentification(new AmbassadorIdentification());
        user.setWebDeviceId("web");
        user.setFacebookAccessToken("facebook");
        user.setTwitterAccessToken("twitter");
        user.setLinkedInAccessToken("linkedin");
        user.setIdentifyData("identifyData");

        // ASSERT
        Mockito.verify(user, Mockito.times(9)).save();
    }

}
