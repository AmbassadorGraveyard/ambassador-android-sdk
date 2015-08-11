package com.example.ambassador.ambassadorsdk;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;
import org.w3c.dom.Text;


public class AmbassadorActivity extends ActionBarActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public String linkedInToken;

    private TextView tvWelcomeTitle, tvWelcomeDesc;
    private ShareDialog fbDialog;
    private ImageButton btnCopyPaste;
    private EditText etShortUrl;
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
        final AmbassadorActivity activity = this;
        initSocialMedias(activity);

        AmbassadorSingleton.getInstance().context = getApplicationContext();

        fbDialog = new ShareDialog(this);
        rafParams = (RAFParameters) getIntent().getSerializableExtra("test");

        // UI Components
        tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (EditText) findViewById(R.id.etShortURL);
        gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);

        setCustomizedText(rafParams);

        AmbassadorSingleton.getInstance().rafParameters = rafParams;

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
                        presentLinkedInLoginIfNeeded();
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

    public void setShortURL() {
        etShortUrl.setText("mbsy.co/test_shouldhavegotten");
    }
    //endregion


    //region SOCAIL MEDIA CALL - FACEBOOK, TWITTER, LINKEDIN
    public void initSocialMedias(AmbassadorActivity ambassadorActivity) {
        // TWITTER initialization
        TwitterAuthConfig authConfig = new TwitterAuthConfig(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        // FACEBOOK initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public void shareWithFacebook() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(rafParams.shareMessage)
                .setContentUrl(Uri.parse(etShortUrl.getText().toString()))
                .build();

        fbDialog.show(content);
    }

    public void shareWithTwitter() {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("Check out this awesome company! " + etShortUrl.getText().toString());

        builder.show();
    }

    public void presentLinkedInLoginIfNeeded() {
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



