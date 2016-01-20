package com.ambassador.ambassadorsdk.internal.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.PusherChannel;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.SocialGridAdapter;
import com.ambassador.ambassadorsdk.internal.SocialShareDialog;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.models.ShareMethod;
import com.ambassador.ambassadorsdk.internal.views.LockableScrollView;
import com.ambassador.ambassadorsdk.internal.views.ShakableEditText;
import com.ambassador.ambassadorsdk.internal.views.StaticGridView;
import com.ambassador.ambassadorsdk.internal.utils.StringResource;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.pusher.client.connection.ConnectionState;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterfork.Bind;
import io.fabric.sdk.android.Fabric;

/**
 * Activity that handles sharing options and copying the share URL.
 */
public final class AmbassadorActivity extends AppCompatActivity {

    // region Fields

    // region Constants
    int constants;
    // endregion

    // region Views
    @Bind(B.id.action_bar)      protected Toolbar               toolbar;
    @Bind(B.id.svParent)        protected LockableScrollView    svParent;
    @Bind(B.id.llParent)        protected LinearLayout          llParent;
    @Bind(B.id.tvWelcomeTitle)  protected TextView              tvWelcomeTitle;
    @Bind(B.id.tvWelcomeDesc)   protected TextView              tvWelcomeDesc;
    @Bind(B.id.flShortUrl)      protected FrameLayout           flShortUrl;
    @Bind(B.id.etShortURL)      protected ShakableEditText      etShortUrl;
    @Bind(B.id.btnCopy)         protected ImageButton           btnCopy;
    @Bind(B.id.gvSocialGrid)    protected StaticGridView        gvSocialGrid;
    // endregion

    // region Dependencies
    @Inject protected RequestManager        requestManager;
    @Inject protected AmbassadorConfig      ambassadorConfig;
    @Inject protected PusherSDK             pusherSDK;
    // endregion

    // region Local members
    protected RAFOptions raf = RAFOptions.get();
    // endregion

    // endregion

    // region Methods

    // region Activity overrides
    int activityOverrides;
    // endregion

    // region Requirement checks
    int requirementChecks;
    // endregion

    // region Setup
    int setup;
    // endregion

    // region OnClickListeners
    int onclicks;
    // endregion

    // region UI helpers
    int uihelpers;
    // endregion

    // endregion

    private ProgressDialog pd;
    private Timer networkTimer;
    private CallbackManager callbackManager;
    private final Handler timerHandler = new Handler();

    final private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), new StringResource(R.string.loading_failure).getValue(), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    final private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        // Executed when PusherSDK data is received, used to update the shortURL editText if loading screen is present
        @Override
        public void onReceive(Context context, Intent intent) {
            tryAndSetURL(ambassadorConfig.getPusherInfo(), ambassadorConfig.getRafParameters().defaultShareMessage);
        }
    };

    /**
     * It's really stupid that this has to be here.  Facebook and Twitter want to use OnActivityResult
     * but they don't use or handle the requestCode's.  So we have to keep track of who to pass the callback
     * to.
     */
    private enum LaunchedSocial {
        FACEBOOK, TWITTER
    };

    LaunchedSocial launchedSocial = null;

    TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Apparently the content view has to be inflated like this for the ViewTreeObserver to work */
        final View view = LayoutInflater.from(this).inflate(R.layout.activity_ambassador, null, false);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getViewTreeObserver().isAlive()) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                int parentHeight = svParent.getHeight();
                int childHeight = llParent.getHeight();

                if (childHeight - parentHeight > 0 && childHeight - parentHeight < Utilities.getPixelSizeForDimension(R.dimen.ambassador_activity_scroll_lock_buffer)) {
                    svParent.lock();
                }
            }
        });

        Utilities.setStatusBar(getWindow(), raf.getHomeToolbarColor());

        setContentView(view);

        if (!AmbassadorSingleton.isValid()) {
            finish();
            return;
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        String twitterConsumerKey = new StringResource(R.string.twitter_consumer_key).getValue();
        String twitterConsumerSecret = new StringResource(R.string.twitter_consumer_secret).getValue();
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(twitterConsumerKey, twitterConsumerSecret);
        Fabric.with(this, new TwitterCore(twitterAuthConfig));

        //tell Dagger to inject dependencies
        AmbassadorSingleton.getInstanceAmbModule().setContext(this);
        AmbassadorSingleton.getInstanceComponent().inject(this);

        ambassadorConfig.setRafParameters(
                raf.getDefaultShareMessage(),
                raf.getTitleText(),
                raf.getDescriptionText(),
                raf.getToolbarTitle()
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("pusherData"));

        tvWelcomeTitle.setText(ambassadorConfig.getRafParameters().titleText);
        tvWelcomeDesc.setText(ambassadorConfig.getRafParameters().descriptionText);
        setUpToolbar();

        try {
            loadCustomImages();
        } catch (Exception e) {

        }
        setTheme();

        btnCopy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyShortURLToClipboard(etShortUrl.getText().toString(), getApplicationContext());
            }
        });

        btnCopy.setColorFilter(getResources().getColor(R.color.ultraLightGray));

        // Sets up social gridView
        final SocialGridAdapter gridAdapter = new SocialGridAdapter(this, getShareMethods());
        gvSocialGrid.setAdapter(gridAdapter);
        gvSocialGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareMethod model = gridAdapter.getItem(position);
                model.click();
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage(new StringResource(R.string.loading).getValue());
        pd.setOwnerActivity(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        pd.show();

        networkTimer = new Timer();
        networkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                _showNetworkError();
            }
        }, 30000);

        ambassadorConfig.nullifyTwitterIfInvalid(null);
        ambassadorConfig.nullifyLinkedInIfInvalid(null);

        //if we have a channel and it's not expired and connected, call API Identify
        if (PusherChannel.getSessionId() != null && !PusherChannel.isExpired() && PusherChannel.getConnectionState() == ConnectionState.CONNECTED) {
            requestManager.identifyRequest();
            return;
        }

        //if we have a channel and it's not expired but it's not currently connected, subscribe to the existing channel
        if (PusherChannel.getSessionId() != null && !PusherChannel.isExpired() && PusherChannel.getConnectionState() != ConnectionState.CONNECTED) {
            pusherSDK.subscribePusher(new PusherSDK.PusherSubscribeCallback() {
                @Override
                public void pusherSubscribed() {
                   requestManager.identifyRequest();
                }

                @Override
                public void pusherFailed() {
                    Toast.makeText(getApplicationContext(), new StringResource(R.string.connection_failure).getValue(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            return;
        }

        //otherwise, resubscribe to pusher and then call API Identify
        PusherChannel.setSessionId(null);
        PusherChannel.setChannelName(null);
        PusherChannel.setExpiresAt(null);
        PusherChannel.setRequestId(-1);
        pusherSDK.createPusher(new PusherSDK.PusherSubscribeCallback() {
            @Override
            public void pusherSubscribed() {
                requestManager.identifyRequest();
            }

            @Override
            public void pusherFailed() {
                Toast.makeText(getApplicationContext(), new StringResource(R.string.connection_failure).getValue(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setTheme() {
        llParent.setBackgroundColor(raf.getHomeBackgroundColor());

        tvWelcomeTitle.setTextColor(raf.getHomeWelcomeTitleColor());
        tvWelcomeTitle.setTextSize(raf.getHomeWelcomeTitleSize());
        tvWelcomeTitle.setTypeface(raf.getHomeWelcomeTitleFont());

        tvWelcomeDesc.setTextColor(raf.getHomeWelcomeDescriptionColor());
        tvWelcomeDesc.setTextSize(raf.getHomeWelcomeDescriptionSize());
        tvWelcomeDesc.setTypeface(raf.getHomeWelcomeDescriptionFont());

        flShortUrl.setBackgroundColor(raf.getHomeShareTextBar());

        etShortUrl.setBackgroundColor(raf.getHomeShareTextBar());
        etShortUrl.setTextColor(raf.getHomeShareTextColor());
        etShortUrl.setTextSize(raf.getHomeShareTextSize());
        etShortUrl.setTypeface(raf.getHomeShareTextFont());
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if (pd != null) pd.dismiss();
        if (networkTimer != null) { networkTimer.cancel(); }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish(); // Dismisses activity if back button pressed
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (launchedSocial) {
            case FACEBOOK:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case TWITTER:
                twitterAuthClient.onActivityResult(requestCode, resultCode, data);
                break;

        }

        launchedSocial = null;
    }

    String copyShortURLToClipboard(String copyText, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simpleText", copyText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, new StringResource(R.string.copied_to_clipboard).getValue(), Toast.LENGTH_SHORT).show();

        return clip.toString();
    }

    void loadCustomImages() throws Exception {
        //first check if an image exists

        String drawablePath = raf.getLogo();
        if (drawablePath == null) return;

        int pos;
        try {
            pos = Integer.parseInt(raf.getLogoPosition());
        }
        catch (NumberFormatException e) {
            pos = 0;
        }

        if (pos >= 1 && pos <= 5) {
            ImageView logo = new ImageView(this);
            logo.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Drawable drawable = Drawable.createFromStream(getAssets().open(drawablePath), null);
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            float ratio = (float) width / (float) height;

            logo.setImageDrawable(Drawable.createFromStream(getAssets().open(drawablePath), null));

            int heightToSet = Utilities.getPixelSizeForDimension(R.dimen.raf_logo_height);
            int widthToSet = (int) (heightToSet * ratio);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthToSet, heightToSet);

            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = Utilities.getPixelSizeForDimension(R.dimen.raf_logo_top_margin);
            logo.setLayoutParams(params);
            llParent.addView(logo, pos-1);
        }
    }

    void goToContactsPage(Boolean showPhoneNumbers) {
        Intent contactIntent = new Intent(this, ContactSelectorActivity.class);
        contactIntent.putExtra("showPhoneNumbers", showPhoneNumbers);
        startActivity(contactIntent);
    }

    void shareWithFacebook() {
        launchedSocial = LaunchedSocial.FACEBOOK;
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(ambassadorConfig.getRafParameters().defaultShareMessage)
                .setContentUrl(Uri.parse(ambassadorConfig.getURL()))
                .build();
        callbackManager = CallbackManager.Factory.create();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (result.getPostId() != null) {
                    Toast.makeText(getApplicationContext(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
                    requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.FACEBOOK);
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
            }
        });
        shareDialog.show(content);
    }

    void shareWithTwitter() {
        // Presents twitter login screen if user has not logged in yet
        if (ambassadorConfig.getTwitterAccessToken() != null) {
            SocialShareDialog tweetDialog = new SocialShareDialog(AmbassadorActivity.this);
            tweetDialog.setSocialNetwork(SocialShareDialog.SocialNetwork.TWITTER);
            tweetDialog.setOwnerActivity(AmbassadorActivity.this);
            tweetDialog.setSocialDialogEventListener(new SocialShareDialog.ShareDialogEventListener() {
                @Override
                public void postSuccess() {

                }

                @Override
                public void postFailed() {

                }

                @Override
                public void postCancelled() {

                }

                @Override
                public void needAuth() {
                    ambassadorConfig.setTwitterAccessTokenSecret(null);
                    ambassadorConfig.setTwitterAccessToken(null);
                    TwitterCore.getInstance().getSessionManager().clearActiveSession();
                    requestReauthTwitter();
                }
            });
            tweetDialog.show();
        } else {
            launchedSocial = LaunchedSocial.TWITTER;
            twitterAuthClient = new TwitterAuthClient();
            twitterAuthClient.authorize(AmbassadorActivity.this, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    ambassadorConfig.setTwitterAccessToken(result.data.getAuthToken().token);
                    ambassadorConfig.setTwitterAccessToken(result.data.getAuthToken().secret);
                    Toast.makeText(getApplicationContext(), new StringResource(R.string.login_success).getValue(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(TwitterException e) {

                }
            });
        }
    }

    private void requestReauthTwitter() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(new StringResource(R.string.twitter_reauthenticate_message).getValue())
                .setPositiveButton(new StringResource(R.string.ok).getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareWithTwitter();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(new StringResource(R.string.cancel).getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
    }

    void shareWithLinkedIn() {
        if (ambassadorConfig.getLinkedInToken() != null) {
            SocialShareDialog linkedInDialog = new SocialShareDialog(AmbassadorActivity.this);
            linkedInDialog.setSocialNetwork(SocialShareDialog.SocialNetwork.LINKEDIN);
            linkedInDialog.setOwnerActivity(AmbassadorActivity.this);
            linkedInDialog.setSocialDialogEventListener(new SocialShareDialog.ShareDialogEventListener() {
                @Override
                public void postSuccess() {

                }

                @Override
                public void postFailed() {

                }

                @Override
                public void postCancelled() {

                }

                @Override
                public void needAuth() {
                    ambassadorConfig.setLinkedInToken(null);
                    requestReauthLinkedIn();
                }
            });
            linkedInDialog.show();
        } else {
            Intent intent = new Intent(AmbassadorActivity.this, LinkedInLoginActivity.class);
            startActivity(intent);
        }
    }

    private void requestReauthLinkedIn() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(new StringResource(R.string.linked_in_reauthenticate_message).getValue())
                .setPositiveButton(new StringResource(R.string.ok).getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareWithLinkedIn();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(new StringResource(R.string.cancel).getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
    }

    void tryAndSetURL(String pusherString, String initialShareMessage) {
        // Functionality: Gets URL from PusherSDK
        // First checks to see if PusherSDK info has already been saved to SharedPreferencs
        if (pd != null) {
            pd.dismiss();
            networkTimer.cancel();
        }

        try {
            // We get a JSON object from the PusherSDK Info string saved to SharedPreferences
            JSONObject pusherData = new JSONObject(pusherString);
            JSONArray urlArray = pusherData.getJSONArray("urls");

            // Iterates throught all the urls in the PusherSDK object until we find one will a matching campaign ID
            for (int i = 0; i < urlArray.length(); i++) {
                JSONObject urlObj = urlArray.getJSONObject(i);
                int campID = urlObj.getInt("campaign_uid");
                int myUID = Integer.parseInt(ambassadorConfig.getCampaignID());
                if (campID == myUID) {
                    etShortUrl.setText(urlObj.getString("url"));
                    ambassadorConfig.setURL(urlObj.getString("url"));
                    ambassadorConfig.setReferrerShortCode(urlObj.getString("short_code"));
                    ambassadorConfig.setEmailSubject(urlObj.getString("subject"));

                    //check for weird multiple URL issue seen occasionally
                    if (!initialShareMessage.contains(urlObj.getString("url"))) {
                        ambassadorConfig.setRafDefaultMessage(initialShareMessage + " " + urlObj.getString("url"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _showNetworkError() {
        // Functionality: After 30 seconds of waiting for data from PusherSDK/Augur, shows toast to user
        // stating that there is a network error and then dismisses the RAF activity
        timerHandler.post(myRunnable);
    }

    private void setUpToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(ambassadorConfig.getRafParameters().toolbarTitle);
        }

        if (toolbar == null) return;

        final Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(raf.getHomeToolbarArrowColor(), PorterDuff.Mode.SRC_ATOP);

        toolbar.setNavigationIcon(arrow);
        toolbar.setBackgroundColor(raf.getHomeToolbarColor());
        toolbar.setTitleTextColor(raf.getHomeToolbarTextColor());
    }

    @NonNull
    private List<ShareMethod> getShareMethods() {
        ShareMethod.Builder modelFacebook = new ShareMethod.Builder()
                .setName("FACEBOOK")
                .setIconDrawable(R.drawable.facebook_icon)
                .setBackgroundColor(getResources().getColor(R.color.facebook_blue))
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        shareWithFacebook();
                    }
                });

        ShareMethod.Builder modelTwitter = new ShareMethod.Builder()
                .setName("TWITTER")
                .setIconDrawable(R.drawable.twitter_icon)
                .setBackgroundColor(getResources().getColor(R.color.twitter_blue))
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        shareWithTwitter();
                    }
                });

        ShareMethod.Builder modelLinkedIn = new ShareMethod.Builder()
                .setName("LINKEDIN")
                .setIconDrawable(R.drawable.linkedin_icon)
                .setBackgroundColor(getResources().getColor(R.color.linkedin_blue))
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        shareWithLinkedIn();
                    }
                });

        ShareMethod.Builder modelEmail = new ShareMethod.Builder()
                .setName("EMAIL")
                .setIconDrawable(R.drawable.email_icon)
                .setBackgroundColor(getResources().getColor(android.R.color.white))
                .setDrawBorder(true)
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        goToContactsPage(false);
                    }
                });

        ShareMethod.Builder modelSms = new ShareMethod.Builder()
                .setName("SMS")
                .setIconDrawable(R.drawable.sms_icon)
                .setBackgroundColor(getResources().getColor(android.R.color.white))
                .setDrawBorder(true)
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        goToContactsPage(true);
                    }
                });

        HashMap<String, ShareMethod.Builder> map = new HashMap<>();
        map.put("facebook", modelFacebook);
        map.put("twitter", modelTwitter);
        map.put("linkedin", modelLinkedIn);
        map.put("email", modelEmail);
        map.put("sms", modelSms);

        ArrayList<ShareMethod> shareMethods = new ArrayList<>();

        String[] order = raf.getChannels();
        for (int i = 0; i < order.length; i++) {
            String channel = order[i].toLowerCase();
            if (map.containsKey(channel)) {
                ShareMethod shareMethod = map.get(channel)
                        .setWeight(i)
                        .build();

                if (!shareMethods.contains(shareMethod)) {
                    shareMethods.add(shareMethod);
                }
            }
        }

        Collections.sort(shareMethods);
        return shareMethods;
    }

}