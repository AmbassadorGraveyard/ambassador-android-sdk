package com.ambassador.ambassadorsdk.internal.api.facebook;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import javax.inject.Inject;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;

/**
 *
 */
public class FacebookApi {

    /** Auth singleton object to retreive Facebook token */
    @Inject protected Auth auth;

    /**
     *
     */
    public FacebookApi() {
        AmbSingleton.inject(this);
    }

    /**
     *
     * @return
     */
    @NonNull
    protected Facebook getFacebook() {
        Facebook facebook = new FacebookFactory().getInstance();
        String appId = new StringResource(R.string.facebook_app_id).getValue();
        String appSecret = new StringResource(R.string.facebook_app_secret).getValue();
        facebook.setOAuthAppId(appId, appSecret);
        facebook.setOAuthPermissions("publish_actions");
        if (auth.getFacebookToken() != null) facebook.setOAuthAccessToken(new AccessToken(auth.getFacebookToken()));
        return facebook;
    }

    /**
     *
     * @param requestCompletion
     */
    public void getAuthUrl(final RequestManager.RequestCompletion requestCompletion) {
        final Facebook facebook = getFacebook();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = facebook.getOAuthAuthorizationURL("https://api.getenvoy.co/auth/facebook/auth");
                if (url == null) {
                    requestCompletion.onFailure(null);
                } else {
                    requestCompletion.onSuccess(url);
                }
            }
        }).start();
    }

    /**
     *
     * @param code
     * @param requestCompletion
     */
    public void getAccessToken(final String code, final RequestManager.RequestCompletion requestCompletion) {
        final Facebook facebook = getFacebook();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AccessToken accessToken = facebook.getOAuthAccessToken(code, "https://api.getenvoy.co/auth/facebook/auth");
                    requestCompletion.onSuccess(accessToken.getToken());
                } catch (Exception e) {
                    requestCompletion.onFailure(null);
                }
            }
        }).start();
    }

    /**
     *
     * @param message
     * @param requestCompletion
     */
    public void postToFacebook(final String message, final RequestManager.RequestCompletion requestCompletion) {
        final Facebook facebook = getFacebook();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    facebook.postStatusMessage(message);
                } catch (Exception e) {
                    requestCompletion.onFailure(null);
                }
            }
        }).start();
    }

}
