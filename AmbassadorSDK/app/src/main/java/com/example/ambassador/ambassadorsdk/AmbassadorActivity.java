package com.example.ambassador.ambassadorsdk;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


public class AmbassadorActivity extends AppCompatActivity {
    private TextView tvWelcomeTitle, tvWelcomeDesc;
    private ShareDialog fbDialog;
    private ImageButton btnCopyPaste;
    private CustomEditText etShortUrl;
    private GridView gvSocialGrid;
    private RAFParameters rafParams;
    private ProgressDialog pd;
    private Timer networkTimer;
    private int timerSeconds;
    private final String[] gridTitles = new String[]{"Facebook", "Twitter", "LinkedIn", "Email", "SMS"};
    private final Integer[] gridDrawables = new Integer[]{R.mipmap.facebook_icon, R.mipmap.twitter_icon, R.mipmap.linkedin_icon,
                                                            R.mipmap.email_icon, R.mipmap.sms_icon};

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador);

        initFacebook(this);

        AmbassadorSingleton.getInstance().context = getApplicationContext();

        rafParams = (RAFParameters) getIntent().getSerializableExtra("test");
        AmbassadorSingleton.getInstance().rafParameters = rafParams;

        // UI Components
        tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (CustomEditText) findViewById(R.id.etShortURL);
        gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);


        getCorrectUrl();
        setCustomizedText(rafParams);
        AmbassadorSingleton.getInstance().rafParameters = rafParams;

        setUpToolbar();

        setCustomizedText(rafParams);

        etShortUrl.setEditTextTint(Color.DKGRAY);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("pusherData"));

        // Sets up social grid
        SocialGridAdapter gridAdapter = new SocialGridAdapter(this, gridTitles, gridDrawables);
        gvSocialGrid.setAdapter(gridAdapter);

        btnCopyPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyShortURLToClipboard();
            }
        });

        // Listener for gridView item clicks
        gvSocialGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        shareWithFacebook();
                        break;
                    case 1:
                        shareWithTwitter();
                        break;
                    case 2:
                        shareWithLinkedIn();
                        break;
                    case 3:
                        goToContactsPage(false);
                        break;
                    case 4:
                        goToContactsPage(true);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    // Handler that handles UI updates for the timer
    final android.os.Handler timerHandler = new android.os.Handler();
    private void showNetworkError() {
        timerHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "There seems to be a network error.  Pleas try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        // Executed when Pusher data is recieved, used to update the shortURL editText if loading screen is present
        @Override
        public void onReceive(Context context, Intent intent) {
            getCorrectUrl();
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
    //endregion


    //region HELPER FUNCTIONS
    public void copyShortURLToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simpleText", etShortUrl.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void setCustomizedText(RAFParameters params) {
        tvWelcomeTitle.setText(params.welcomeTitle);
        tvWelcomeDesc.setText(params.welcomeDescription);
    }

    void goToContactsPage(Boolean showPhoneNumbers) {
        Intent contactIntent = new Intent(this, ContactSelectorActivity.class);
        contactIntent.putExtra("showPhoneNumbers", showPhoneNumbers);
        startActivity(contactIntent);
    }


    // Gets URL from Pusher
    public void getCorrectUrl() {
        // First checks to see if Pusher info has already been saved to SharedPreferencs
        if (AmbassadorSingleton.getInstance().getPusherInfo() != null) {
            // If loading screen is showing, then we should hide it
            if (pd != null) {
                pd.hide();
                networkTimer.cancel();
            }

            // Next we check if the shortURL Edittext is empty or if has been set
            if (etShortUrl.getText().toString().isEmpty()) {
                try {
                    // We get a JSON object from the Pusher Info string saved to SharedPreferences
                    JSONObject pusherData = new JSONObject(AmbassadorSingleton.getInstance().getPusherInfo());
                    JSONArray urlArray = pusherData.getJSONArray("urls");

                    // Iterates throught all the urls in the Pusher object until we find one will a matching campaign ID
                    for (int i = 0; i < urlArray.length(); i++) {
                        JSONObject urlObj = urlArray.getJSONObject(i);
                        int campID = urlObj.getInt("campaign_uid");
                        if (campID == Integer.parseInt(AmbassadorSingleton.getInstance().getCampaignID())) {
                            etShortUrl.setText(urlObj.getString("url"));
                            AmbassadorSingleton.getInstance().saveURL(urlObj.getString("url"));
                            AmbassadorSingleton.getInstance().rafParameters.shareMessage =
                                    rafParams.shareMessage + " " + urlObj.getString("url");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // If the pusher object hasn't been saved to SharedPreferences, we show a loading screen until it has been
            // Should only happen on first launch
            pd = new ProgressDialog(this);
            pd.setMessage("Loading");
            pd.setOwnerActivity(this);
            pd.setCancelable(false);
            pd.show();

            networkTimer = new Timer();
            networkTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    showNetworkError();
                }
            }, 30000);
        }
    }

    void setUpToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_bar);
        toolbar.setBackgroundColor(Color.LTGRAY);
        toolbar.setTitleTextColor(Color.DKGRAY);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AmbassadorSingleton.getInstance().rafParameters.toolbarTitle);
        }
    }
    //endregion


    //region SOCAIL MEDIA CALLS - FACEBOOK, TWITTER, LINKEDIN
    public void initFacebook(AmbassadorActivity ambassadorActivity) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbDialog = new ShareDialog(ambassadorActivity);
    }

    public void shareWithFacebook() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(rafParams.shareMessage)
                .setContentUrl(Uri.parse(AmbassadorSingleton.getInstance().getURL()))
                .build();

        fbDialog.show(content);
    }

    public void shareWithTwitter() {
        // Presents twitter login screen if user has not logged in yet
        if (AmbassadorSingleton.getInstance().getTwitterAccessToken() != null) {
            TweetDialog tweetDialog = new TweetDialog(this);
            tweetDialog.setOwnerActivity(this);
            tweetDialog.show();
        } else {
            Intent i = new Intent(this, TwitterLoginActivity.class);
            startActivity(i);
        }
    }

    public void shareWithLinkedIn() {
        // Presents login screen if user hasn't signed in yet
        if (AmbassadorSingleton.getInstance().getLinkedInToken() == null) {
            Intent intent = new Intent(this, LinkedInLoginActivity.class);
            startActivity(intent);
        } else {
            LinkedInPostDialog dialog = new LinkedInPostDialog(this);
            dialog.setOwnerActivity(this);
            dialog.show();
        }
    }
    //endregion
}