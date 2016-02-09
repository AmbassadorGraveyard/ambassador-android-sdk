package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

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
public class AuthTest {

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
        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.linkedInToken = "linkedInToken";
        auth.twitterToken = "twitterToken";
        auth.twitterSecret = "twitterSecret";

        // ACT
        auth.save();

        // ASSERT
        Mockito.verify(context).getSharedPreferences(Mockito.eq("auth"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(editor).putString(Mockito.eq("auth"), Mockito.eq("{\"universalId\":\"universalId\",\"universalToken\":\"universalToken\",\"linkedInToken\":\"linkedInToken\",\"twitterToken\":\"twitterToken\",\"twitterSecret\":\"twitterSecret\"}"));
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveWithNullContextDoesNotSave() {
        // ARRANGE
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(null);

        Auth auth = new Auth();
        auth.universalId = "universalId";
        auth.universalToken = "universalToken";
        auth.linkedInToken = "linkedInToken";
        auth.twitterToken = "twitterToken";
        auth.twitterSecret = "twitterSecret";

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
        auth.linkedInToken = "linkedInToken";
        auth.twitterToken = "twitterToken";
        auth.twitterSecret = "twitterSecret";

        // ACT
        auth.clear();

        // ASSERT
        Assert.assertNull(auth.getUniversalId());
        Assert.assertNull(auth.getUniversalToken());
        Assert.assertNull(auth.getLinkedInToken());
        Assert.assertNull(auth.getTwitterToken());
        Assert.assertNull(auth.getTwitterSecret());
    }

    @Test
    public void settersDoSaveOnInvocatio() {
        // ARRANGE
        Auth auth = Mockito.spy(new Auth());
        Mockito.doNothing().when(auth).save();

        // ACT
        auth.setUniversalId("universalId");
        auth.setUniversalToken("universalToken");
        auth.setLinkedInToken("linkedInToken");
        auth.setTwitterToken("twitterToken");
        auth.setTwitterSecret("twitterSecret");

        // ASSERT
        Mockito.verify(auth, Mockito.times(5)).save();
    }

}
