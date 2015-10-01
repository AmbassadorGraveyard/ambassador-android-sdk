package com.example.ambassador.ambassadorsdk;

/**
 * Created by coreyfields on 9/16/15.
 */
public interface AmbassadorApplicationComponent {
    void inject(AmbassadorActivity ambassadorActivity);
    void inject(TweetDialog tweetDialog);
    void inject(TwitterLoginActivity twitterLoginActivity);
    void inject(LinkedInDialog linkedInDialog);
    void inject(LinkedInLoginActivity linkedInLoginActivity);
    void inject(ContactSelectorActivity contactSelectorActivity);
    void inject(BulkShareHelper bulkShareHelper);
    void inject(ConversionUtility conversionUtility);
}
