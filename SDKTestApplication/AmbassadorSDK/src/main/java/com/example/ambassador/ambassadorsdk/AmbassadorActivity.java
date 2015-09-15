package com.example.ambassador.ambassadorsdk;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JakeDunahee on 7/22/15.
 */
public class AmbassadorActivity extends AppCompatActivity {
    private TextView tvWelcomeTitle, tvWelcomeDesc;
    private CustomEditText etShortUrl;
    private ServiceSelectorPreferences rafParams;
    private ProgressDialog pd;
    private Timer networkTimer;
    private AmbassadorActivity ambassadorActivity;
    private final android.os.Handler timerHandler = new android.os.Handler();
    private final String[] gridTitles = new String[]{"FACEBOOK", "TWITTER", "LINKEDIN", "EMAIL", "SMS"};
    private final Integer[] gridDrawables = new Integer[]{R.drawable.facebook_icon, R.drawable.twitter_icon, R.drawable.linkedin_icon,
            R.drawable.email_icon, R.drawable.sms_icon};

    final private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "There seems to be an issue while attempting to load.  Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    final private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        // Executed when Pusher data is received, used to update the shortURL editText if loading screen is present
        @Override
        public void onReceive(Context context, Intent intent) {
            _tryAndSetURL();
        }
    };


    // ACTIVITY OVERRIDE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador);

        ambassadorActivity = this;
        rafParams = (ServiceSelectorPreferences) getIntent().getSerializableExtra("test");
        AmbassadorSingleton.getInstance().rafParameters = rafParams;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("pusherData"));

        // UI Components
        GridView gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        ImageButton btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);
        tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (CustomEditText) findViewById(R.id.etShortURL);

        _setCustomizedText(rafParams);
        _tryAndSetURL();

        btnCopyPaste.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _copyShortURLToClipboard();
            }
        });

        btnCopyPaste.setColorFilter(getResources().getColor(R.color.ultraLightGray));

        // Sets up social gridView
        SocialGridAdapter gridAdapter = new SocialGridAdapter(this, gridTitles, gridDrawables);
        gvSocialGrid.setAdapter(gridAdapter);
        gvSocialGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _respondToGridViewClick(position);
            }
        });
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if (networkTimer != null) { networkTimer.cancel(); }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish(); // Dismisses activity if back button pressed
        return super.onOptionsItemSelected(item);
    }
    // END ACTIVITY OVERRIDE METHODS


    // ONCLICK METHODS
    private void _copyShortURLToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simpleText", etShortUrl.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void _respondToGridViewClick(int position) {
        // Functionality: Handles items being clicked in the gridview
        switch (position) {
            case 0:
                _shareWithFacebook();
                break;
            case 1:
                _shareWithTwitter();
                break;
            case 2:
                _shareWithLinkedIn();
                break;
            case 3:
                _goToContactsPage(false);
                break;
            case 4:
                _goToContactsPage(true);
                break;
            default:
                break;
        }
    }

    private void _goToContactsPage(Boolean showPhoneNumbers) {
        Intent contactIntent = new Intent(this, ContactSelectorActivity.class);
        contactIntent.putExtra("showPhoneNumbers", showPhoneNumbers);
        startActivity(contactIntent);
    }

    private void _shareWithFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(rafParams.defaultShareMessage)
                .setContentUrl(Uri.parse(AmbassadorSingleton.getInstance().getURL()))
                .build();

        ShareDialog fbDialog = new ShareDialog(ambassadorActivity);
        fbDialog.show(content);
    }

    private void _shareWithTwitter() {
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

    private void _shareWithLinkedIn() {
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
    // END ONCLICK METHODS


    // UI SETTER METHODS
    private void _tryAndSetURL() {
        // Functionality: Gets URL from Pusher
        // First checks to see if Pusher info has already been saved to SharedPreferencs
        if (AmbassadorSingleton.getInstance().getPusherInfo() != null) {
            _setUrlText();
        } else {
            _showLoader();
        }
    }

    private void _showLoader() {
        // If the pusher object hasn't been saved to SharedPreferences, we show a loading screen until it has been
        // Should only happen on first launch
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
    }

    private void _showNetworkError() {
        // Funtionality: After 30 seconds of waiting for data from Pusher/Augur, shows toast to user
        // stating that there is a network error and then dismisses the RAF activity
        timerHandler.post(myRunnable);
    }

    private void _setUrlText() {
        // If loading screen is showing, then we should hide it and cancel the network timer
        if (pd != null) {
            pd.hide();
            networkTimer.cancel();
        }

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
                    AmbassadorSingleton.getInstance().saveShortCode(urlObj.getString("short_code"));
                    AmbassadorSingleton.getInstance().saveEmailSubject(urlObj.getString("subject"));
                    AmbassadorSingleton.getInstance().rafParameters.defaultShareMessage =
                            rafParams.defaultShareMessage + " " + urlObj.getString("url");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _setCustomizedText(ServiceSelectorPreferences params) {
        tvWelcomeTitle.setText(params.titleText);
        tvWelcomeDesc.setText(params.descriptionText);
        _setUpToolbar(params.toolbarTitle);
    }

    private void _setUpToolbar(String toolbarTitle) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setBackgroundColor(getResources().getColor(R.color.ambassador_blue));
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }

        if (getSupportActionBar() != null) { getSupportActionBar().setTitle(toolbarTitle); }
    }
    // END UI SETTER METHODS
}