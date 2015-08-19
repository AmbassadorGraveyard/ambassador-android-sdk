package com.example.ambassador.ambassadorsdk;

import android.app.ActionBar;
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

import org.w3c.dom.Text;


public class AmbassadorActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public String linkedInToken;

    private TextView tvWelcomeTitle, tvWelcomeDesc;
    private ShareDialog fbDialog;
    private ImageButton btnCopyPaste;
    private CustomEditText etShortUrl;
    private GridView gvSocialGrid;
    private RAFParameters rafParams;
    private final String[] gridTitles = new String[]{"Facebook", "Twitter", "LinkedIn", "Email", "SMS"};
    private final Integer[] gridDrawables = new Integer[]{R.mipmap.facebook_icon, R.mipmap.twitter_icon, R.mipmap.linkedin_icon,
                                                            R.mipmap.email_icon, R.mipmap.sms_icon};

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador);

        initFacebook(this);//

        AmbassadorSingleton.getInstance().context = getApplicationContext();
        rafParams = (RAFParameters) getIntent().getSerializableExtra("test");
        AmbassadorSingleton.getInstance().rafParameters = rafParams;

        // UI Components
        tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (CustomEditText) findViewById(R.id.etShortURL);
        gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);

        setUpToolbar();

        setCustomizedText(rafParams);

        etShortUrl.setEditTextTint(Color.DKGRAY);

        etShortUrl.setText("mbsy.co/test_shouldhavegotten");

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("augurID"));

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TEMPORARY - Makes toast upon successfully receiving Augur ID
            Toast.makeText(getApplicationContext(), "AugurID = " + AmbassadorSingleton.getInstance().getIdentifyObject(), Toast.LENGTH_LONG).show();
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
                .setContentUrl(Uri.parse(etShortUrl.getText().toString()))
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



