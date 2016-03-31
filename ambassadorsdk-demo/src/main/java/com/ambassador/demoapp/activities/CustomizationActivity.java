package com.ambassador.demoapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.internal.views.CircleImageView;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.dialogs.CampaignChooserDialog;
import com.ambassador.demoapp.views.ColorInputView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizationActivity extends AppCompatActivity {

    protected static final int PHOTO_CHOOSER_INTENT = 313;

    @Bind(R.id.ivProductPhoto) protected CircleImageView ivProductPhoto;

    @Bind(R.id.rvCampaignChooser) protected RelativeLayout rvCampaignChooser;

    @Bind(R.id.lvChannels) protected ListView lvChannels;

    @Bind(R.id.civHeader) protected ColorInputView civHeader;
    @Bind(R.id.civTextField1) protected ColorInputView civTextField1;
    @Bind(R.id.civTextField2) protected ColorInputView civTextField2;
    @Bind(R.id.civButtons) protected ColorInputView civButtons;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization);
        ButterKnife.bind(this);
        setUpActionBar();

        ivProductPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_photo));
        ivProductPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PHOTO_CHOOSER_INTENT);
            }
        });

        rvCampaignChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CampaignChooserDialog campaignChooserDialog = new CampaignChooserDialog(CustomizationActivity.this);
                campaignChooserDialog.show();
            }
        });

        setUpChannelList();

        civHeader.setActivity(this);
        civTextField1.setActivity(this);
        civTextField2.setActivity(this);
        civButtons.setActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_CHOOSER_INTENT:
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        ivProductPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e(CustomizationActivity.class.getSimpleName(), e.toString());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionBarColor)));
        setTitle(Html.fromHtml("<small>Edit Refer a Friend View</small>"));
    }

    protected void setUpChannelList() {
        lvChannels.setAdapter(new ChannelAdapter(this));
    }

    protected static class ChannelAdapter extends BaseAdapter {

        protected Activity activity;
        protected ChannelItem[] channelItems;

        public ChannelAdapter(Activity activity) {
            this.activity = activity;

            this.channelItems = new ChannelItem[5];
            channelItems[0] = new ChannelItem("Facebook");
            channelItems[1] = new ChannelItem("Twitter");
            channelItems[2] = new ChannelItem("LinkedIn");
            channelItems[3] = new ChannelItem("Email");
            channelItems[4] = new ChannelItem("SMS");
        }

        @Override
        public int getCount() {
            return channelItems.length;
        }

        @Override
        public ChannelItem getItem(int position) {
            return channelItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.view_channel_item, parent, false);
            }

            ChannelItem channel = getItem(position);

            TextView tvChannelName = (TextView) convertView.findViewById(R.id.tvChannelName);
            tvChannelName.setText(channel.getName());

            return convertView;
        }

        protected static class ChannelItem {

            protected String name;

            public ChannelItem(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

        }

    }

}
