package com.ambassador.ambassadorsdk.internal;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.activities.LinkedInLoginActivity;

/**
 * Created by coreyfields on 9/16/15.
 */
public interface AmbassadorApplicationComponent {
    void inject(AmbassadorActivity ambassadorActivity);
    void inject(SocialShareDialog socialShareDialog);
    void inject(LinkedInLoginActivity linkedInLoginActivity);
    void inject(ContactSelectorActivity contactSelectorActivity);
    void inject(BulkShareHelper bulkShareHelper);
    void inject(ConversionUtility conversionUtility);
    void inject(RequestManager requestManager);
    void inject(AmbassadorSDK ambassadorSDK);
    void inject(ContactNameDialog contactNameDialog);
    void inject(IdentifyAugurSDK identify);
    void inject(PusherSDK pusherSDK);
    void inject(InstallReceiver installReceiver);
}