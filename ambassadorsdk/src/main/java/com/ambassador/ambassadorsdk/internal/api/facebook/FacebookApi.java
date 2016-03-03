package com.ambassador.ambassadorsdk.internal.api.facebook;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import java.util.List;

import javax.inject.Inject;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.Permission;
import facebook4j.auth.AccessToken;

/**
 * Handles the Facebook API using Facebook4j.
 */
public class FacebookApi {

    /** Auth singleton object to retreive Facebook token */
    @Inject protected Auth auth;

    /**
     * Default constructor. Injects dependencies.
     */
    public FacebookApi() {
        AmbSingleton.inject(this);
    }

    /**
     * Creates a Facebook object to use, and populates its configuration such as appId and appSecret.
     * @return a new Facebook object.
     */
    @NonNull
    protected Facebook getFacebook() {
        Facebook facebook = new FacebookFactory().getInstance();
        String appId = new StringResource(R.string.facebook_app_id).getValue();
        String appSecret = new StringResource(R.string.facebook_app_secret).getValue();
        facebook.setOAuthAppId(appId, appSecret);
        facebook.setOAuthPermissions("user_birthday,publish_actions");
        if (auth.getFacebookToken() != null) facebook.setOAuthAccessToken(new AccessToken(auth.getFacebookToken()));
        return facebook;
    }

    /**
     * Returns a url usable for OAuth authentication.
     * @param requestCompletion calls back with url.
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
     * Trades a verifier code for an access token with the Facebook backend.
     * @param code verifier code returned from OAuth login.
     * @param requestCompletion callback to return access token string value.
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
     * Verifies the accessToken for validity and correct permissions.
     * @param requestCompletion
     */
    public void verifyAccessToken(final RequestManager.RequestCompletion requestCompletion) {
        final Facebook facebook = getFacebook();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Permission> permissions = facebook.getPermissions();
                    for (Permission permission : permissions) {
                        if (permission.getName().equals("publish_actions")) {
                            requestCompletion.onSuccess("success");
                            return;
                        }
                    }
                    requestCompletion.onSuccess("failed");
                } catch (Exception e) {
                    requestCompletion.onFailure(null);
                }
            }
        }).start();
    }

    /**
     * Posts to Facebook on behalf of the user.
     * @param message the string to be posted in the status update.
     * @param requestCompletion callback for request completion.
     */
    public void postToFacebook(final String message, final RequestManager.RequestCompletion requestCompletion) {
        final Facebook facebook = getFacebook();
        if (auth.getFacebookToken() == null) {
            requestCompletion.onFailure(null);
            return;
        }
        facebook.setOAuthAccessToken(new AccessToken(auth.getFacebookToken()));
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
