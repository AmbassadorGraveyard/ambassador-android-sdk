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
public class CampaignTest {

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
    public void save_withNonNullContext_doesSaveSerialized() {
        // ARRANGE
        Campaign campaign = new Campaign();
        campaign.id = "260";
        campaign.shortCode = "shortCode";
        campaign.emailSubject = "emailSubject";
        campaign.shareMessage = "shareMessage";
        campaign.referredByShortCode = "shortCode";

        // ACT
        campaign.save();

        // ASSERT
        Mockito.verify(context).getSharedPreferences(Mockito.eq("campaign"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(editor).putString(Mockito.eq("260"), Mockito.eq("{\"id\":\"260\",\"shortCode\":\"shortCode\",\"shareMessage\":\"shareMessage\",\"emailSubject\":\"emailSubject\",\"referredByShortCode\":\"shortCode\"}"));
        Mockito.verify(editor).apply();
    }

    @Test
    public void save_withNullContext_doesNotSave() {
        // ARRANGE
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(null);

        Campaign campaign = new Campaign();
        campaign.id = "260";
        campaign.shortCode = "shortCode";
        campaign.emailSubject = "emailSubject";
        campaign.shareMessage = "shareMessage";
        campaign.referredByShortCode = "shortCode";


        // ACT
        campaign.save();

        // ASSERT
        Mockito.verify(context, Mockito.never()).getSharedPreferences(Mockito.eq("campaign"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(sharedPreferences, Mockito.never()).edit();
        Mockito.verify(editor, Mockito.never()).putString(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(editor, Mockito.never()).apply();
    }

    @Test
    public void clear_doesClearAllFields() {
        // ARRANGE
        Campaign campaign = new Campaign();
        campaign.id = "260";
        campaign.shortCode = "shortCode";
        campaign.emailSubject = "emailSubject";
        campaign.shareMessage = "shareMessage";
        campaign.referredByShortCode = "shortCode";

        // ACT
        campaign.clear();

        // ASSERT
        Assert.assertNull(campaign.getId());
        Assert.assertNull(campaign.getShortCode());
        Assert.assertNull(campaign.getEmailSubject());
        Assert.assertNull(campaign.getShareMessage());
        Assert.assertNull(campaign.getReferredByShortCode());
    }

    @Test
    public void setters_doSaveOnInvocation() {
        // ARRANGE
        Campaign campaign = Mockito.spy(new Campaign());
        Mockito.doNothing().when(campaign).save();

        // ACT
        campaign.setId("260");
        campaign.setShortCode("shortCode");
        campaign.setEmailSubject("emailSubject");
        campaign.setShareMessage("shareMessage");
        campaign.setReferredByShortCode("shortCode");

        // ASSERT
        Mockito.verify(campaign, Mockito.times(5)).save();
    }

}
