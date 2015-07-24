package com.example.ambassador.ambassadorsdk;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.w3c.dom.Text;


public class AmbassadorActivity extends ActionBarActivity {
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

        FacebookSdk.sdkInitialize(getApplicationContext());

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
                        System.out.println("Twitter Share");
                        break;
                    case 2:
                        System.out.println("Linkedin Share");
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
    public void shareWithFacebook() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(rafParams.shareMessage)
                .setContentUrl(Uri.parse(etShortUrl.getText().toString()))
                .build();

        fbDialog.show(content);
    }
    //endregion
}



