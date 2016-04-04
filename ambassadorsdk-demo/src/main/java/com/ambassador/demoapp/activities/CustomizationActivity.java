package com.ambassador.demoapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.views.CircleImageView;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.data.Integration;
import com.ambassador.demoapp.dialogs.CampaignChooserDialog;
import com.ambassador.demoapp.views.ColorInputView;
import com.ambassador.demoapp.views.InputView;
import com.mobeta.android.dslv.DragSortListView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizationActivity extends AppCompatActivity {

    protected static final int PHOTO_CHOOSER_INTENT = 313;

    protected static final String IMAGE_SAVE_FILENAME = "image.png";

    protected ChannelAdapter channelAdapter;

    @Bind(R.id.ivProductPhoto) protected CircleImageView ivProductPhoto;
    @Bind(R.id.inputIntegrationName) protected InputView inputIntegrationName;
    @Bind(R.id.inputTextField1) protected InputView inputTextField1;
    @Bind(R.id.inputTextField2) protected InputView inputTextField2;
    @Bind(R.id.rvCampaignChooser) protected RelativeLayout rvCampaignChooser;
    @Bind(R.id.lvChannels) protected DragSortListView lvChannels;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customizer, menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.save_icon));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
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
                        saveImage(bitmap, IMAGE_SAVE_FILENAME);
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
        channelAdapter = new ChannelAdapter(this);
        lvChannels.setAdapter(channelAdapter);
        lvChannels.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                channelAdapter.drop(from, to);
            }
        });
    }

    protected void saveImage(Bitmap bitmap, String filename) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(filename, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            Log.v("Ambassador-Demo", "Image saved with filename: " + filename);
        } catch (Exception e) {
            Log.e("Ambassador-Demo", e.toString());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e("Ambassador-Demo", e.toString());
                }
            }
        }
    }

    protected void importIntegration(Integration integration) {
        DataHandler dataHandler = new DataHandler();
        dataHandler.setIntegration(integration);
    }

    protected Integration exportIntegration() {
        DataHandler dataHandler = new DataHandler();


        return dataHandler.getIntegration();
    }

    protected static class ChannelAdapter extends BaseAdapter {

        protected Activity activity;
        protected List<ChannelItem> channelItems;

        public ChannelAdapter(Activity activity) {
            this.activity = activity;

            this.channelItems = new ArrayList<>();
            channelItems.add(new ChannelItem("Facebook"));
            channelItems.add(new ChannelItem("Twitter"));
            channelItems.add(new ChannelItem("LinkedIn"));
            channelItems.add(new ChannelItem("Email"));
            channelItems.add(new ChannelItem("SMS"));
        }

        @Override
        public int getCount() {
            return channelItems.size();
        }

        @Override
        public ChannelItem getItem(int position) {
            return channelItems.get(position);
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

            final ChannelItem channel = getItem(position);

            TextView tvChannelName = (TextView) convertView.findViewById(R.id.tvChannelName);
            tvChannelName.setText(channel.getName());

            SwitchCompat swChannel = (SwitchCompat) convertView.findViewById(R.id.swChannel);
            swChannel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    channel.enabled = isChecked;
                }
            });
            swChannel.setChecked(channel.isEnabled());
            return convertView;
        }

        public void drop(int from, int to) {
            ChannelItem movedItem = getItem(from);
            channelItems.remove(from);
            if (from > to) from--;
            channelItems.add(to, movedItem);
            notifyDataSetChanged();
        }

        protected static class ChannelItem {

            protected String name;
            protected boolean enabled = true;

            public ChannelItem(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public boolean isEnabled() {
                return enabled;
            }

        }

    }

    protected class DataHandler {

        public void setIntegration(@NonNull Integration integration) {
            
        }

        @NonNull
        public Integration getIntegration() {
            return new Integration();
        }

        @NonNull
        public String getIntegrationName() {
            return inputIntegrationName.getText().toString();
        }

        public void setIntegrationName(@NonNull String integrationName) {
            inputIntegrationName.setText(integrationName);
        }

        @NonNull
        public Campaign getCampaign() {
            return null;
        }

        public void setCampaign(@NonNull Campaign campaign) {

        }

        @ColorInt
        public int getHeaderColor() {
            return civHeader.getColor();
        }

        public void setHeaderColor(@ColorInt int color) {
            civHeader.setColor(color);
        }

        @NonNull
        public String getTextField1() {
            return inputTextField1.getText().toString();
        }

        public void setTextField1(@NonNull String text) {
            inputTextField1.setText(text);
        }

        @ColorInt
        public int getTextField1Color() {
            return civTextField1.getColor();
        }

        public void setTextField1Color(@ColorInt int color) {
            civTextField1.setColor(color);
        }

        @NonNull
        public String getTextField2() {
            return inputTextField2.getText().toString();
        }

        public void setTextField2(@NonNull String text) {
            inputTextField2.setText(text);
        }

        @ColorInt
        public int getTextField2Color() {
            return civTextField2.getColor();
        }

        public void setTextField2Color(@ColorInt int color) {
            civTextField2.setColor(color);
        }

        public void setChannels(@NonNull String[] channels) {
            String[] uppercaseChannels = new String[channels.length];
            for (int i = 0; i < channels.length; i++) {
                uppercaseChannels[i] = channels[i].toUpperCase();
            }
            List<String> paramChannels = Arrays.asList(uppercaseChannels);
            List<String> neededChannels = Arrays.asList("FACEBOOK", "TWITTER", "LINKEDIN", "EMAIL", "SMS");
            List<ChannelAdapter.ChannelItem> items = new ArrayList<>();
            for (String paramChannel : paramChannels) {
                ChannelAdapter.ChannelItem channelItem = new ChannelAdapter.ChannelItem(paramChannel);
                channelItem.enabled = true;
                items.add(channelItem);
                neededChannels.remove(paramChannel);
            }

            for (String neededChannel : neededChannels) {
                ChannelAdapter.ChannelItem channelItem = new ChannelAdapter.ChannelItem(neededChannel);
                channelItem.enabled = false;
                items.add(channelItem);
            }

            channelAdapter.channelItems = items;
            channelAdapter.notifyDataSetChanged();
        }

        @NonNull
        public String[] getChannels() {
            List<ChannelAdapter.ChannelItem> items = channelAdapter.channelItems;
            List<String> processedItems = new ArrayList<>();
            for (ChannelAdapter.ChannelItem channelItem : items) {
                if (channelItem.isEnabled()) {
                    processedItems.add(channelItem.getName().toUpperCase());
                }
            }

            return processedItems.toArray(new String[processedItems.size()]);
        }

        @ColorInt
        public int getButtonColor() {
            return civButtons.getColor();
        }

        public void setButtonColor(@ColorInt int color) {
            civButtons.setColor(color);
        }

    }

    protected static class Campaign {

        protected static final Campaign NONE = new Campaign();

        protected int id;
        protected String name;

    }

}
