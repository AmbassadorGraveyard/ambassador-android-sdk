package com.example.ambassador.ambassadorsdk;

/**
 * Created by coreyfields on 9/16/15.
 */
public interface AmbassadorActivityComponent {
    void inject(AmbassadorActivity ambassadorActivity);
    void inject(TweetDialog tweetDialog);
    void inject(LinkedInDialog linkedInDialog);
}
