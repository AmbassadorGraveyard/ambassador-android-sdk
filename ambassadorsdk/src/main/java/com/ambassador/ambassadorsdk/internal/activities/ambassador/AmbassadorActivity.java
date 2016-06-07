package com.ambassador.ambassadorsdk.internal.activities.ambassador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.activities.contacts.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.activities.oauth.SocialOAuthActivity;
import com.ambassador.ambassadorsdk.internal.adapters.SocialGridAdapter;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.dialogs.AskEmailDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.SocialShareDialog;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.models.ShareMethod;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.ambassadorsdk.internal.views.LockableScrollView;
import com.ambassador.ambassadorsdk.internal.views.ShakableEditText;
import com.ambassador.ambassadorsdk.internal.views.StaticGridView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Activity that handles sharing options and copying the share URL.
 */
public final class AmbassadorActivity extends AppCompatActivity {

    @Nullable
    @Bind(B.id.action_bar)      protected Toolbar               toolbar;

    @Bind(B.id.svParent)        protected LockableScrollView    svParent;
    @Bind(B.id.llParent)        protected LinearLayout          llParent;
    @Bind(B.id.tvWelcomeTitle)  protected TextView              tvWelcomeTitle;
    @Bind(B.id.tvWelcomeDesc)   protected TextView              tvWelcomeDesc;
    @Bind(B.id.flShortUrl)      protected FrameLayout           flShortUrl;
    @Bind(B.id.etShortURL)      protected ShakableEditText      etShortUrl;
    @Bind(B.id.btnCopy)         protected ImageButton           btnCopy;
    @Bind(B.id.gvSocialGrid)    protected StaticGridView        gvSocialGrid;

    @Inject protected RequestManager        requestManager;
    @Inject protected Auth                  auth;
    @Inject protected User                  user;
    @Inject protected Campaign              campaign;
    @Inject protected PusherManager         pusherManager;
    @Inject protected Device                device;

    protected RAFOptions            raf;
    protected ProgressDialog        progressDialog;
    protected Timer                 networkTimer;
    protected ShareManager          currentManager;
    protected FacebookManager       facebookManager;
    protected TwitterManager        twitterManager;
    protected LinkedInManager       linkedInManager;
    protected EmailManager          emailManager;
    protected SmsManager            smsManager;
    protected boolean retried;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador);

        // Injection
        AmbSingleton.init(this);
        AmbSingleton.inject(this);
        ButterFork.bind(this);
        raf = RAFOptions.get();

        // Requirement checks
        finishIfSingletonInvalid();
        if (isFinishing()) return;

        setUpData();
        setUpShareManagers();
        setUpLockingScrollView();
        setUpOptions();
        setUpToolbar();
        setUpSocialGridView();
        setUpCustomImages();
        setUpCopy();

        if (AmbIdentify.getRunningInstance() != null) {
            setUpLoader();
            AmbIdentify.getRunningInstance().setCompletionListener(new AmbIdentify.CompletionListener() {
                @Override
                public void complete() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            identifyWithStoredInfo();
                        }
                    });
                }

                @Override
                public void noSDK() {
                    showNoSDKAccessError();
                }

                @Override
                public void networkError() {
                    showNetworkError();
                }
            });
        } else if (user.getUserId() != null) {
            identifyWithStoredInfo();
        } else {
            identifyWithDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) progressDialog.dismiss();
        if (networkTimer != null) {
            networkTimer.cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentManager != null) {
            currentManager.onActivityResult(requestCode, resultCode, data);
        }

        currentManager = null;
    }

    protected void finishIfSingletonInvalid() {
        if (!AmbSingleton.isValid()) {
            finish();
        }
    }

    protected void setUpData() {
        user.refresh();
        user.setFacebookAccessToken(null);
        user.setTwitterAccessToken(null);
        campaign.refresh();
    }

    protected void setUpShareManagers() {
        facebookManager = new FacebookManager();
        twitterManager = new TwitterManager();
        linkedInManager = new LinkedInManager();
        emailManager = new EmailManager();
        smsManager = new SmsManager();
    }

    protected void setUpLockingScrollView() {
        final View view = findViewById(android.R.id.content);
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
    }

    protected void setUpOptions() {
        llParent.setBackgroundColor(raf.getHomeBackgroundColor());

        tvWelcomeTitle.setText(raf.getTitleText());
        tvWelcomeTitle.setTextColor(raf.getHomeWelcomeTitleColor());
        tvWelcomeTitle.setTextSize(raf.getHomeWelcomeTitleSize());
        tvWelcomeTitle.setTypeface(raf.getHomeWelcomeTitleFont());

        tvWelcomeDesc.setText(raf.getDescriptionText());
        tvWelcomeDesc.setTextColor(raf.getHomeWelcomeDescriptionColor());
        tvWelcomeDesc.setTextSize(raf.getHomeWelcomeDescriptionSize());
        tvWelcomeDesc.setTypeface(raf.getHomeWelcomeDescriptionFont());

        flShortUrl.setBackgroundColor(raf.getHomeShareTextBar());

        etShortUrl.setBackgroundColor(raf.getHomeShareTextBar());
        etShortUrl.setTextColor(raf.getHomeShareTextColor());
        etShortUrl.setTextSize(raf.getHomeShareTextSize());
        etShortUrl.setTypeface(raf.getHomeShareTextFont());

        btnCopy.setColorFilter(getResources().getColor(R.color.ultraLightGray));
    }

    protected void setUpToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(raf.getToolbarTitle());
        }

        if (toolbar == null) return;

        final Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(raf.getHomeToolbarArrowColor(), PorterDuff.Mode.SRC_ATOP);

        toolbar.setNavigationIcon(arrow);
        toolbar.setBackgroundColor(raf.getHomeToolbarColor());
        toolbar.setTitleTextColor(raf.getHomeToolbarTextColor());

        Utilities.setStatusBar(getWindow(), raf.getHomeToolbarColor());
    }

    protected void setUpSocialGridView() {
        final SocialGridAdapter gridAdapter = new SocialGridAdapter(this, getShareMethods());
        gvSocialGrid.setAdapter(gridAdapter);
        gvSocialGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareMethod model = gridAdapter.getItem(position);
                model.click();
            }
        });
    }

    protected void setUpCustomImages() {
        String drawablePath = raf.getLogo();
        int drawableId = raf.getLogoResId();
        if (drawablePath == null && drawableId == -555) return;

        try {
            int pos;
            pos = Integer.parseInt(raf.getLogoPosition());

            Drawable drawable;
            try {
                drawable = Drawable.createFromStream(getAssets().open(drawablePath), null);
            } catch (Exception e) {
                try {
                    drawable = Drawable.createFromStream(openFileInput(drawablePath), null);
                } catch (Exception e2) {
                    drawable = getResources().getDrawable(drawableId);
                }
            }

            if (drawable == null) return;

            if (pos >= 1 && pos <= 5) {
                ImageView logo = new ImageView(this);
                logo.setScaleType(ImageView.ScaleType.CENTER_CROP);

                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                float ratio = (float) width / (float) height;

                logo.setImageDrawable(drawable);

                int heightToSet = Utilities.getPixelSizeForDimension(R.dimen.raf_logo_height);
                int widthToSet = (int) (heightToSet * ratio);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthToSet, heightToSet);

                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.topMargin = Utilities.getPixelSizeForDimension(R.dimen.raf_logo_top_margin);
                logo.setLayoutParams(params);
                llParent.addView(logo, pos-1);
            }
        } catch (Exception e) {
            Log.e("AmbassadorSDK", e.toString());
        }
    }

    protected void setUpCopy() {
        btnCopy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyShortUrlToClipboard(etShortUrl.getText().toString(), getApplicationContext());
            }
        });
    }

    protected void setUpLoader() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(new StringResource(R.string.loading).getValue());
        progressDialog.setOwnerActivity(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        progressDialog.show();

        networkTimer = new Timer();
        networkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                showNetworkError();
            }
        }, 15000);
    }

    protected void setUpPusher() {
        pusherManager.refreshListeners();
        pusherManager.addPusherListener(new PusherListenerAdapter() {
            @Override
            public void subscribed() {
                super.subscribed();
                requestManager.identifyRequest(pusherManager, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {
                        // All is swell, do nothing.
                    }

                    @Override
                    public void onFailure(Object failureResponse) {
                        AmbassadorSDK.identify(null);
                        showNetworkError();
                    }
                });
            }

            @Override
            public void subscriptionFailed() {
                super.subscriptionFailed();
                showNoSDKAccessError();
            }

            @Override
            public void connectionFailed() {
                super.connectionFailed();
                showNetworkError();
            }
        });

        pusherManager.startNewChannel();
        pusherManager.subscribeChannelToAmbassador();
    }

    private void copyShortUrlToClipboard(@NonNull String copyText, @NonNull Context context) {
        device.copyToClipboard(copyText);
        Toast.makeText(context, new StringResource(R.string.copied_to_clipboard).getValue(), Toast.LENGTH_SHORT).show();
    }

    private void requestReauthFacebook() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(new StringResource(R.string.facebook_reauthenticate_message).getValue())
                .setPositiveButton(new StringResource(R.string.ok).getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        facebookManager.onShareRequested();
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

    private void requestReauthTwitter() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(new StringResource(R.string.twitter_reauthenticate_message).getValue())
                .setPositiveButton(new StringResource(R.string.ok).getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        twitterManager.onShareRequested();
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

    private void requestReauthLinkedIn() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(new StringResource(R.string.linked_in_reauthenticate_message).getValue())
                .setPositiveButton(new StringResource(R.string.ok).getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkedInManager.onShareRequested();
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

    protected void identifyWithStoredInfo() {
        if (user.getIdentifyData() != null) {
            JsonObject data = new JsonParser().parse(user.getIdentifyData()).getAsJsonObject();
            JsonArray urls = data.getAsJsonObject("body").getAsJsonArray("urls");

            boolean found = tryAndSetURL(urls, raf.getDefaultShareMessage());
            if (!found) {
                if (!retried && AmbassadorSDK.identify(user.getUserId())) {
                    retried = true;
                    setUpLoader();
                    AmbIdentify.getRunningInstance().setCompletionListener(new AmbIdentify.CompletionListener() {
                        @Override
                        public void complete() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    identifyWithStoredInfo();
                                }
                            });
                        }

                        @Override
                        public void noSDK() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showNoSDKAccessError();
                                }
                            });
                        }

                        @Override
                        public void networkError() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showNetworkError();
                                }
                            });
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Creates an error string matching front-end message if we have the user's email, otherwise use 'You'
                            String errorString = user.getAmbassadorIdentification().getEmail() != null ?
                                    String.format("%s is not authorized to access this campaign.", user.getAmbassadorIdentification().getEmail()) :
                                    "You are not authorized to access this campaign";

                            Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            // If there is a campaign returned, but the active flag is false
            } else if (campaign != null && !campaign.isActive()) {
                showCampaignNotActiveError();
                return;
            }
        } else {
            setUpLoader();
            setUpPusher();
        }
    }

    protected void identifyWithDialog() {
        final AskEmailDialog askEmailDialog = new AskEmailDialog(this);
        askEmailDialog.setOnEmailReceivedListener(new AskEmailDialog.OnEmailReceivedListener() {
            @Override
            public void onEmailReceived(String email) {
                AmbIdentify.identifyType = "raf";
                if (AmbassadorSDK.identify(email)) {
                    askEmailDialog.dismiss();
                    setUpLoader();
                    AmbIdentify.getRunningInstance().setCompletionListener(new AmbIdentify.CompletionListener() {
                        @Override
                        public void complete() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    identifyWithStoredInfo();
                                }
                            });
                        }

                        @Override
                        public void noSDK() {
                            showNoSDKAccessError();
                        }

                        @Override
                        public void networkError() {
                            showNetworkError();
                        }
                    });
                } else {
                    Toast.makeText(AmbassadorActivity.this, new StringResource(R.string.invalid_email).getValue(), Toast.LENGTH_SHORT).show();
                    askEmailDialog.shake();
                }
            }

            @Override
            public void onCanceled() {
                askEmailDialog.dismiss();
                finish();
            }
        });
        askEmailDialog.show();
    }

    protected boolean tryAndSetURL(JsonArray urlArray, String initialShareMessage) {
        boolean campaignFound = false;
        for (int i = 0; i < urlArray.size(); i++) {
            JsonObject urlObj = urlArray.get(i).getAsJsonObject();
            int campID = urlObj.get("campaign_uid").getAsInt();
            int myUID = Integer.parseInt(campaign.getId());
            if (campID == myUID) {
                etShortUrl.setText(urlObj.get("url").getAsString());
                campaign.setUrl(urlObj.get("url").getAsString());
                campaign.setShortCode(urlObj.get("short_code").getAsString());
                campaign.setEmailSubject(urlObj.get("subject").getAsString());
                campaign.setIsActive(urlObj.get("is_active").getAsBoolean());
                campaignFound = true;


                //check for weird multiple URL issue seen occasionally
                if (!initialShareMessage.contains(urlObj.get("url").getAsString())) {
                    raf.setDefaultShareMessage(initialShareMessage + " " + urlObj.get("url").getAsString());
                }
            }
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
            networkTimer.cancel();
        }

        return campaignFound;
    }

    private void showNetworkError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AmbIdentify.getRunningInstance() != null) {
                    AmbIdentify.getRunningInstance().cancel();
                }
                Toast.makeText(getApplicationContext(), new StringResource(R.string.loading_failure).getValue(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    protected void showNoSDKAccessError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (networkTimer != null) networkTimer.cancel();
                new AlertDialog.Builder(AmbassadorActivity.this)
                        .setMessage("You currently don't have access to the SDK. If you have any questions please contact support.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    protected void showCampaignNotActiveError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (networkTimer != null) networkTimer.cancel();
                new AlertDialog.Builder(AmbassadorActivity.this)
                        .setMessage("This campaign is no longer active.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    @NonNull
    protected List<ShareMethod> getShareMethods() {
        HashMap<String, ShareMethod.Builder> shareMethodMap = getDefaultShareMethodBuilderMap();
        ArrayList<ShareMethod> shareMethods = new ArrayList<>();
        String[] order = raf.getChannels();

        for (int i = 0; i < order.length; i++) {
            String channel = order[i].toLowerCase();
            if (shareMethodMap.containsKey(channel)) {
                ShareMethod shareMethod = shareMethodMap.get(channel)
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

    @NonNull
    protected HashMap<String, ShareMethod.Builder> getDefaultShareMethodBuilderMap() {
        ShareMethod.Builder modelFacebook = new ShareMethod.Builder()
                .setName("FACEBOOK")
                .setIconDrawable(R.drawable.facebook_icon)
                .setBackgroundColor(getResources().getColor(R.color.facebook_blue))
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        launchShareMethod(facebookManager);
                    }
                });

        ShareMethod.Builder modelTwitter = new ShareMethod.Builder()
                .setName("TWITTER")
                .setIconDrawable(R.drawable.twitter_icon)
                .setBackgroundColor(getResources().getColor(R.color.twitter_blue))
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        launchShareMethod(twitterManager);
                    }
                });

        ShareMethod.Builder modelLinkedIn = new ShareMethod.Builder()
                .setName("LINKEDIN")
                .setIconDrawable(R.drawable.linkedin_icon)
                .setBackgroundColor(getResources().getColor(R.color.linkedin_blue))
                .setShareAction(new ShareMethod.ShareAction() {
                    @Override
                    public void share() {
                        launchShareMethod(linkedInManager);
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
                        launchShareMethod(emailManager);
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
                        launchShareMethod(smsManager);
                    }
                });

        HashMap<String, ShareMethod.Builder> map = new HashMap<>();
        map.put("facebook", modelFacebook);
        map.put("twitter", modelTwitter);
        map.put("linkedin", modelLinkedIn);
        map.put("email", modelEmail);
        map.put("sms", modelSms);

        return map;
    }

    private void launchShareMethod(@NonNull ShareManager shareManager) {
        this.currentManager = shareManager;
        shareManager.onShareRequested();
    }

    protected interface ShareManager {
        void onShareRequested();
        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    }

    protected class FacebookManager implements ShareManager {


        protected FacebookManager() {

        }

        @Override
        public void onShareRequested() {
            if (user.getFacebookAccessToken() != null) {
                SocialShareDialog facebookDialog = new SocialShareDialog(AmbassadorActivity.this);
                facebookDialog.setSocialNetwork(SocialShareDialog.SocialNetwork.FACEBOOK);
                facebookDialog.setOwnerActivity(AmbassadorActivity.this);
                facebookDialog.setSocialDialogEventListener(new SocialShareDialog.ShareDialogEventListener() {
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
                        user.setFacebookAccessToken(null);
                        requestReauthFacebook();
                    }
                });
                facebookDialog.show();
            } else {
                Intent intent = new Intent(AmbassadorActivity.this, SocialOAuthActivity.class);
                intent.putExtra("socialNetwork", "facebook");
                startActivityForResult(intent, 5555);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (user.getFacebookAccessToken() != null) {
                onShareRequested();
            }
        }

    }

    protected class TwitterManager implements ShareManager {


        protected TwitterManager() {

        }

        @Override
        public void onShareRequested() {
            if (user.getTwitterAccessToken() != null) {
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
                        user.setTwitterAccessToken(null);
                        requestReauthTwitter();
                    }
                });
                tweetDialog.show();
            } else {
                Intent intent = new Intent(AmbassadorActivity.this, SocialOAuthActivity.class);
                intent.putExtra("socialNetwork", "twitter");
                startActivityForResult(intent, 5555);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (user.getTwitterAccessToken() != null) {
                onShareRequested();
            }
        }

    }
    protected class LinkedInManager implements ShareManager {

        @Override
        public void onShareRequested() {
            if (user.getLinkedInAccessToken() != null) {
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
                        user.setLinkedInAccessToken(null);
                        requestReauthLinkedIn();
                    }
                });
                linkedInDialog.show();
            } else {
                Intent intent = new Intent(AmbassadorActivity.this, SocialOAuthActivity.class);
                intent.putExtra("socialNetwork", "linkedin");
                startActivityForResult(intent, 123);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (user.getLinkedInAccessToken() != null) {
                onShareRequested();
            }
        }

    }

    protected class EmailManager implements ShareManager {

        @Override
        public void onShareRequested() {
            Intent contactIntent = new Intent(AmbassadorActivity.this, ContactSelectorActivity.class);
            contactIntent.putExtra("showPhoneNumbers", false);
            startActivityForResult(contactIntent, 123);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        }

    }

    protected class SmsManager implements ShareManager {

        @Override
        public void onShareRequested() {
            Intent contactIntent = new Intent(AmbassadorActivity.this, ContactSelectorActivity.class);
            contactIntent.putExtra("showPhoneNumbers", true);
            startActivityForResult(contactIntent, 123);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        }

    }

}