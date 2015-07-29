package com.example.ambassador.ambassadorsdk;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
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

        AmbassadorSingleton.getInstance().ambActivity = this;

        fbDialog = new ShareDialog(this);
//        rafParams = (RAFParameters) getIntent().getSerializableExtra("test");

        // UI Components
        tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (EditText) findViewById(R.id.etShortURL);
        gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);

        rafParams = new RAFParameters(); // Temp RAFPARAMS while in just framework
        setCustomizedText(rafParams);

        AmbassadorSingleton.getInstance().rafParameters = rafParams;

        btnCopyPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyShortURLToClipboard();
            }
        });

        SocialGridAdapter gridAdapter = new SocialGridAdapter(this, gridTitles, gridDrawables);
        gvSocialGrid.setAdapter(gridAdapter);

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
                        System.out.println("Email Share");
                        break;
                    case 4:
                        System.out.println("SMS Share");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //endregion


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

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
//        SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
//        linkedInToken = prefs.getString("linkedInToken", null);

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



