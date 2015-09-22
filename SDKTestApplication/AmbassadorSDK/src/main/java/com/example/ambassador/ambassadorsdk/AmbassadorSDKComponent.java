package com.example.ambassador.ambassadorsdk;

/**
 * Created by coreyfields on 9/16/15.
 */
public interface AmbassadorSDKComponent {
    void inject(TweetDialog tweetDialog);
    void inject(LinkedInDialog linkedInDialog);
}
