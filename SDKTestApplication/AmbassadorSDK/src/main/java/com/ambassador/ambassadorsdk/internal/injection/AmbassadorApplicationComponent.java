package com.ambassador.ambassadorsdk.internal.injection;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.Pusher2;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.activities.LinkedInLoginActivity;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.dialogs.AskNameDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.SocialShareDialog;

public interface AmbassadorApplicationComponent {

    void inject(AmbassadorActivity ambassadorActivity);
    void inject(SocialShareDialog socialShareDialog);
    void inject(LinkedInLoginActivity linkedInLoginActivity);
    void inject(ContactSelectorActivity contactSelectorActivity);
    void inject(BulkShareHelper bulkShareHelper);
    void inject(ConversionUtility conversionUtility);
    void inject(RequestManager requestManager);
    void inject(AmbassadorSDK ambassadorSDK);
    void inject(AskNameDialog askNameDialog);
    void inject(IdentifyAugurSDK identify);
    void inject(PusherSDK pusherSDK);
    void inject(Pusher2 pusher2);
    void inject(Pusher2.Channel channel);
    void inject(InstallReceiver installReceiver);

}