package com.ambassador.ambassadorsdk.internal.injection;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.activities.LinkedInLoginActivity;
import com.ambassador.ambassadorsdk.internal.activities.TwitterLoginActivity;
import com.ambassador.ambassadorsdk.internal.adapters.ContactListAdapter;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.dialogs.AskNameDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.SocialShareDialog;
import com.ambassador.ambassadorsdk.internal.notifications.InstanceIdListener;

public interface AmbassadorApplicationComponent {

    void inject(AmbassadorActivity ambassadorActivity);
    void inject(SocialShareDialog socialShareDialog);
    void inject(LinkedInLoginActivity linkedInLoginActivity);
    void inject(TwitterLoginActivity twitterLoginActivity);
    void inject(ContactSelectorActivity contactSelectorActivity);
    void inject(ContactListAdapter contactListAdapter);
    void inject(BulkShareHelper bulkShareHelper);
    void inject(ConversionUtility conversionUtility);
    void inject(RequestManager requestManager);
    void inject(AmbassadorSDK ambassadorSDK);
    void inject(AskNameDialog askNameDialog);
    void inject(IdentifyAugurSDK identify);
    void inject(PusherSDK pusherSDK);
    void inject(InstallReceiver installReceiver);
    void inject(InstanceIdListener instanceIdListener);

}