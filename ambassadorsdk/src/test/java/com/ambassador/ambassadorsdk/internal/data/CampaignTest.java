package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

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
})
public class CampaignTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class
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
        Campaign campaign = new Campaign();
        campaign.id = "260";
        campaign.url = "url";
        campaign.shortCode = "shortCode";
        campaign.emailSubject = "emailSubject";
        campaign.shareMessage = "shareMessage";
        campaign.referredByShortCode = "shortCode";

        // ACT
        campaign.save();

        // ASSERT
        Mockito.verify(context).getSharedPreferences(Mockito.eq("campaign"), Mockito.eq(Context.MODE_PRIVATE));
        Mockito.verify(editor).putString(Mockito.eq("260"), Mockito.eq("{\"id\":\"260\",\"url\":\"url\",\"shortCode\":\"shortCode\",\"shareMessage\":\"shareMessage\",\"emailSubject\":\"emailSubject\",\"referredByShortCode\":\"shortCode\",\"convertedOnInstall\":false,\"isActive\":false}"));
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveWithNullContextDoesNotSave() {
        // ARRANGE
        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(null);

        Campaign campaign = new Campaign();
        campaign.id = "260";
        campaign.url = "url";
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
    public void clearDoesClearAllFields() {
        // ARRANGE
        Campaign campaign = new Campaign();
        campaign.id = "260";
        campaign.url = "url";
        campaign.shortCode = "shortCode";
        campaign.emailSubject = "emailSubject";
        campaign.shareMessage = "shareMessage";
        campaign.referredByShortCode = "shortCode";

        // ACT
        campaign.clear();

        // ASSERT
        Assert.assertNull(campaign.getId());
        Assert.assertNull(campaign.getUrl());
        Assert.assertNull(campaign.getShortCode());
        Assert.assertNull(campaign.getEmailSubject());
        Assert.assertNull(campaign.getShareMessage());
        Assert.assertNull(campaign.getReferredByShortCode());
    }

    @Test
    public void settersDoSaveOnInvocation() {
        // ARRANGE
        Campaign campaign = Mockito.spy(new Campaign());
        Mockito.doNothing().when(campaign).save();

        // ACT
        campaign.setId("260");
        campaign.setUrl("url");
        campaign.setShortCode("shortCode");
        campaign.setEmailSubject("emailSubject");
        campaign.setShareMessage("shareMessage");
        campaign.setReferredByShortCode("shortCode");

        // ASSERT
        Mockito.verify(campaign, Mockito.times(6)).save();
    }

}
