package com.ambassador.ambassadorsdk.internal;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.R;
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
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by JakeDunahee on 7/22/15.
 */
public class AmbassadorActivity extends AppCompatActivity {

    CustomEditText etShortUrl;
    private ProgressDialog pd;
    private LockableScrollView scrollView;
    private LinearLayout llMainLayout;
    private Timer networkTimer;
    private CallbackManager callbackManager;
    private final android.os.Handler timerHandler = new android.os.Handler();
    private ArrayList<SocialGridModel> gridModels;

    final private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "There seems to be an issue while attempting to load. Please try again.", Toast.LENGTH_SHORT).show();
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

    @Inject
    ShareDialog fbDialog;

    @Inject
    RequestManager requestManager;

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Inject
    PusherSDK pusher;

    // ACTIVITY OVERRIDE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AmbassadorSingleton.isValid()) {
            finish();
            return;
        }

        /** Apparently the content view has to be inflated like this for the ViewTreeObserver to work */
        final View view = LayoutInflater.from(this).inflate(R.layout.activity_ambassador, null, false);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getViewTreeObserver().isAlive()) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                int parentHeight = scrollView.getHeight();
                int childHeight = llMainLayout.getHeight();

                if (childHeight - parentHeight > 0 && childHeight - parentHeight < Utilities.getPixelSizeForDimension(R.dimen.ambassador_activity_scroll_lock_buffer)) {
                    scrollView.lock();
                }
            }
        });

        Utilities.setStatusBar(getWindow(), getResources().getColor(R.color.homeToolBar));

        setContentView(view);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig("***REMOVED***","***REMOVED***");
        Fabric.with(this, new TwitterCore(twitterAuthConfig));

        //tell Dagger to inject dependencies
        AmbassadorSingleton.getInstanceAmbModule().setContext(this);
        AmbassadorSingleton.getInstanceComponent().inject(this);

        ambassadorConfig.setRafParameters(
                getResources().getString(R.string.RAFdefaultShareMessage),
                getResources().getString(R.string.RAFtitleText),
                getResources().getString(R.string.RAFdescriptionText),
                getResources().getString(R.string.RAFtoolbarTitle));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("pusherData"));

        // UI Components
        scrollView = (LockableScrollView) findViewById(R.id.scrollView);
        llMainLayout = (LinearLayout) findViewById(R.id.llMainLayout);
        StaticGridView gvSocialGrid = (StaticGridView) findViewById(R.id.gvSocialGrid);
        ImageButton btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);
        TextView tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        TextView tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (CustomEditText) findViewById(R.id.etShortURL);

        tvWelcomeTitle.setText(ambassadorConfig.getRafParameters().titleText);
        tvWelcomeDesc.setText(ambassadorConfig.getRafParameters().descriptionText);
        _setUpToolbar(ambassadorConfig.getRafParameters().toolbarTitle);

        loadCustomImages();

        btnCopyPaste.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyShortURLToClipboard(etShortUrl.getText().toString(), getApplicationContext());
            }
        });

        btnCopyPaste.setColorFilter(getResources().getColor(R.color.ultraLightGray));

        // Sets up social gridView
        _instantiateGridModelsIntoArray();
        final SocialGridAdapter gridAdapter = new SocialGridAdapter(this, gridModels);
        gvSocialGrid.setAdapter(gridAdapter);
        gvSocialGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SocialGridModel model = gridAdapter.getItem(position);
                model.click();
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
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
            pusher.subscribePusher(new PusherSDK.PusherSubscribeCallback() {
                @Override
                public void pusherSubscribed() {
                   requestManager.identifyRequest();
                }

                @Override
                public void pusherFailed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
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
        pusher.createPusher(new PusherSDK.PusherSubscribeCallback() {
            @Override
            public void pusherSubscribed() {
                requestManager.identifyRequest();
            }

            @Override
            public void pusherFailed() {
                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    // END ACTIVITY OVERRIDE METHODS


    // ONCLICK METHODS
    String copyShortURLToClipboard(String copyText, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simpleText", copyText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();

        return clip.toString();
    }

    void loadCustomImages() {
        //first check if an image exists
        int drawableId = getResources().getIdentifier("raf_logo", "drawable", getPackageName());
        if (drawableId == 0) return;

        int pos;
        try {
            pos = Integer.parseInt(getString(R.string.RAFLogoPosition));
        }
        catch (NumberFormatException e) {
            pos = 0;
        }

        if (pos >= 1 && pos <= 5) {
            ImageView logo = new ImageView(this);
            logo.setId(drawableId);
            logo.setImageDrawable(ContextCompat.getDrawable(this, drawableId));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Utilities.getPixelSizeForDimension(R.dimen.raf_logo_height));
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = Utilities.getPixelSizeForDimension(R.dimen.raf_logo_top_margin);
            logo.setLayoutParams(params);
            llMainLayout.addView(logo, pos-1);
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
        fbDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (result.getPostId() != null) {
                    Toast.makeText(getApplicationContext(), "Posted successfully!", Toast.LENGTH_SHORT).show();
                    requestManager.bulkShareTrack(BulkShareHelper.SocialServiceTrackType.FACEBOOK);
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "Unable to post.  Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        fbDialog.show(content);
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
                    Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(TwitterException e) {

                }
            });
        }
    }

    private void requestReauthTwitter() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("We need you to re-authenticate Twitter. Continue?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareWithTwitter();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
    }

    void shareWithLinkedIn() {
        // Presents login screen if user hasn't signed in yet
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
                .setMessage("We need you to re-authenticate LinkedIn. Continue?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareWithLinkedIn();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
    }

    // END ONCLICK METHODS


    // UI SETTER METHODS
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

    private void _setUpToolbar(String toolbarTitle) {
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle(toolbarTitle); }

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar == null) return;

        final Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(getResources().getColor(R.color.homeToolBarArrow), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(arrow);

        toolbar.setBackgroundColor(getResources().getColor(R.color.homeToolBar));
        toolbar.setTitleTextColor(getResources().getColor(R.color.homeToolBarText));
    }
    // END UI SETTER METHODS

    /**
     * Instantiates a model object for each social grid item and binds a passthrough
     * onclick method that calls the existing method handler, eg. shareWithFacebook();
     */
    private void _instantiateGridModelsIntoArray() {
        SocialGridModel.Builder modelFacebook = new SocialGridModel.Builder()
                .setName("FACEBOOK")
                .setIconDrawable(R.drawable.facebook_icon)
                .setBackgroundColor(getResources().getColor(R.color.facebook_blue))
                .setOnClickListener(new SocialGridModel.OnClickListener() {
                    @Override
                    public void onClick() {
                        shareWithFacebook();
                    }
                });

        SocialGridModel.Builder modelTwitter = new SocialGridModel.Builder()
                .setName("TWITTER")
                .setIconDrawable(R.drawable.twitter_icon)
                .setBackgroundColor(getResources().getColor(R.color.twitter_blue))
                .setOnClickListener(new SocialGridModel.OnClickListener() {
                    @Override
                    public void onClick() {
                        shareWithTwitter();
                    }
                });

        SocialGridModel.Builder modelLinkedIn = new SocialGridModel.Builder()
                .setName("LINKEDIN")
                .setIconDrawable(R.drawable.linkedin_icon)
                .setBackgroundColor(getResources().getColor(R.color.linkedin_blue))
                .setOnClickListener(new SocialGridModel.OnClickListener() {
                    @Override
                    public void onClick() {
                        shareWithLinkedIn();
                    }
                });

        SocialGridModel.Builder modelEmail = new SocialGridModel.Builder()
                .setName("EMAIL")
                .setIconDrawable(R.drawable.email_icon)
                .setBackgroundColor(getResources().getColor(android.R.color.white))
                .setDrawBorder(true)
                .setOnClickListener(new SocialGridModel.OnClickListener() {
                    @Override
                    public void onClick() {
                        goToContactsPage(true);
                    }
                });

        SocialGridModel.Builder modelSms = new SocialGridModel.Builder()
                .setName("SMS")
                .setIconDrawable(R.drawable.sms_icon)
                .setBackgroundColor(getResources().getColor(android.R.color.white))
                .setDrawBorder(true)
                .setOnClickListener(new SocialGridModel.OnClickListener() {
                    @Override
                    public void onClick() {

                    }
                });

        HashMap<String, SocialGridModel.Builder> map = new HashMap<>();
        map.put("facebook", modelFacebook);
        map.put("twitter", modelTwitter);
        map.put("linkedin", modelLinkedIn);
        map.put("email", modelEmail);
        map.put("sms", modelSms);

        ArrayList<SocialGridModel> tmpGridModels = new ArrayList<>();

        String[] order = getResources().getStringArray(R.array.channels);
        for (int i = 0; i < order.length; i++) {
            String channel = order[i].toLowerCase();
            if (map.containsKey(channel)) {
                SocialGridModel.Builder modelBuilder = map.get(channel);
                modelBuilder.setWeight(i);
                SocialGridModel model = modelBuilder.build();
                if (!tmpGridModels.contains(model)) {
                    tmpGridModels.add(model);
                }
            }
        }

        _handleDisablingAndSorting(tmpGridModels);
    }

    private void _handleDisablingAndSorting(ArrayList<SocialGridModel> tmpGridModels) {
        gridModels = new ArrayList<>();

        for (SocialGridModel model : tmpGridModels) {
            gridModels.add(model);
        }

        Collections.sort(gridModels);
    }
}