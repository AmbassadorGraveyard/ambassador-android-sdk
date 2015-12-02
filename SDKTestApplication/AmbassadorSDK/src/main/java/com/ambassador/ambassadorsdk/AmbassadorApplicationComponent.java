package com.ambassador.ambassadorsdk;

/**
 * Created by coreyfields on 9/16/15.
 */
interface AmbassadorApplicationComponent {
    void inject(AmbassadorActivity ambassadorActivity);
    void inject(TweetDialog tweetDialog);
    void inject(TwitterLoginActivity twitterLoginActivity);
    void inject(LinkedInDialog linkedInDialog);
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
