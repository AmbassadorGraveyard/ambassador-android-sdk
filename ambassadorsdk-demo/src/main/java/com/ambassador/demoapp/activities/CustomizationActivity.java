package com.ambassador.demoapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.views.CircleImageView;
import com.ambassador.ambassadorsdk.internal.views.CrossfadedTextView;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.data.Integration;
import com.ambassador.demoapp.dialogs.CampaignChooserDialog;
import com.ambassador.demoapp.views.ColorInputView;
import com.ambassador.demoapp.views.InputView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    protected Campaign selectedCampaign;

    protected boolean hasPhoto = false;

    @Bind(R.id.ivProductPhoto) protected CircleImageView ivProductPhoto;
    @Bind(R.id.tvProductPhotoInfo) protected TextView tvProductPhotoInfo;
    @Bind(R.id.inputIntegrationName) protected InputView inputIntegrationName;
    @Bind(R.id.inputHeaderText) protected InputView inputHeaderText;
    @Bind(R.id.inputTextField1) protected InputView inputTextField1;
    @Bind(R.id.inputTextField2) protected InputView inputTextField2;
    @Bind(R.id.rvCampaignChooser) protected RelativeLayout rvCampaignChooser;
    @Bind(R.id.tvSelectedCampaign) protected TextView tvSelectedCampaign;
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
                final CampaignChooserDialog campaignChooserDialog = new CampaignChooserDialog(CustomizationActivity.this);
                campaignChooserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (campaignChooserDialog.getSelectedCampaign() == null) return;
                        JsonObject json = new JsonParser().parse(campaignChooserDialog.getSelectedCampaign()).getAsJsonObject();
                        String name = json.get("name").getAsString();
                        int id = json.get("id").getAsInt();
                        Campaign campaign = new Campaign();
                        campaign.setId(id);
                        campaign.setName(name);
                        new DataHandler().setCampaign(campaign);
                    }
                });
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
        Integration integration = new DataHandler().getIntegration();
        AmbassadorSDK.identify("jake@getambassador.com");
        AmbassadorSDK.presentRAF(this, integration.getCampaignId() + "", integration.getRafOptions());
        //finish();
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
                        boolean didSave = saveImage(bitmap, IMAGE_SAVE_FILENAME);
                        if (didSave) {
                            tvProductPhotoInfo.setTextColor(Color.parseColor("#4197d0"));
                            tvProductPhotoInfo.setText("Remove Product Photo");
                            hasPhoto = true;
                            tvProductPhotoInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ivProductPhoto.setImageDrawable(ContextCompat.getDrawable(CustomizationActivity.this, R.drawable.add_photo));
                                    tvProductPhotoInfo.setTextColor(Color.BLACK);
                                    tvProductPhotoInfo.setText("Upload Product Photo");
                                    tvProductPhotoInfo.setOnClickListener(null);
                                    hasPhoto = false;
                                }
                            });
                        }
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

    protected boolean saveImage(Bitmap bitmap, String filename) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(filename, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            Log.v("Ambassador-Demo", "Image saved with filename: " + filename);
        } catch (Exception e) {
            Log.e("Ambassador-Demo", e.toString());
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e("Ambassador-Demo", e.toString());
                    return false;
                }
            }
        }
        return true;
    }

    protected static class ChannelAdapter extends BaseAdapter {

        protected Activity activity;
        protected List<Channel> channelItems;

        public ChannelAdapter(Activity activity) {
            this.activity = activity;

            this.channelItems = new ArrayList<>();
            channelItems.add(new Channel("Facebook"));
            channelItems.add(new Channel("Twitter"));
            channelItems.add(new Channel("LinkedIn"));
            channelItems.add(new Channel("Email"));
            channelItems.add(new Channel("SMS"));
        }

        @Override
        public int getCount() {
            return channelItems.size();
        }

        @Override
        public Channel getItem(int position) {
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

            final Channel channel = getItem(position);

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
            Channel movedItem = getItem(from);
            channelItems.remove(from);
            if (from > to) from--;
            channelItems.add(to, movedItem);
            notifyDataSetChanged();
        }

    }

    protected class DataHandler {

        public void setIntegration(@NonNull Integration integration) {
            setIntegrationName(integration.getName());
            Campaign campaign = new Campaign();
            campaign.setName(integration.getCampaignName());
            campaign.setId(integration.getCampaignId());
            setCampaign(campaign);
            RAFOptions rafOptions = integration.getRafOptions();
        }

        @NonNull
        public Integration getIntegration() {
            Integration integration = new Integration();
            integration.setName(getIntegrationName());
            integration.setCampaignId(selectedCampaign.getId());
            integration.setCreatedAtDate(System.currentTimeMillis());
            RAFOptions rafOptions = new RAFOptions.Builder()
                    .setLogo(hasPhoto ? IMAGE_SAVE_FILENAME : null)
                    .setLogoPosition("1")
                    .setToolbarTitle(getHeaderText())
                    .setHomeToolbarColor(getHeaderColor())
                    .setContactsToolbarColor(getHeaderColor())
                    .setTitleText(getTextField1())
                    .setHomeWelcomeTitleColor(getTextField1Color())
                    .setDescriptionText(getTextField2())
                    .setHomeWelcomeDescriptionColor(getTextField2Color())
                    .setChannels(getChannels())
                    .setContactsSendButtonColor(getButtonColor())
                    .build();
            integration.setRafOptions(rafOptions);
            return integration;
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
            selectedCampaign = campaign;
            if (campaign.equals(Campaign.NONE)) {
                tvSelectedCampaign.setText("Select a Campaign");
                tvSelectedCampaign.setTextColor(Color.parseColor("#e6e6e6"));
            } else {
                tvSelectedCampaign.setText(campaign.getName());
                tvSelectedCampaign.setTextColor(Color.parseColor("#253244"));
            }
        }

        @NonNull
        public String getHeaderText() {
            return inputHeaderText.getText().toString();
        }

        public void setHeaderText(@NonNull String text) {
            inputHeaderText.setText(text);
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
            List<Channel> neededChannels = Arrays.asList(Channel.DEFAULTS);
            List<Channel> items = new ArrayList<>();
            for (String paramChannel : paramChannels) {
                Channel channelItem = Channel.get(paramChannel);
                if (channelItem != null) {
                    channelItem.enabled = true;
                    items.add(channelItem);
                    neededChannels.remove(channelItem);
                }
            }

            for (Channel neededChannel : neededChannels) {
                neededChannel.enabled = false;
                items.add(neededChannel);
            }

            channelAdapter.channelItems = items;
            channelAdapter.notifyDataSetChanged();
        }

        @NonNull
        public String[] getChannels() {
            List<Channel> items = channelAdapter.channelItems;
            List<String> processedItems = new ArrayList<>();
            for (Channel channelItem : items) {
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    protected static class Channel {

        public static final Channel FACEBOOK = new Channel("Facebook");
        public static final Channel TWITTER = new Channel("Twitter");
        public static final Channel LINKEDIN = new Channel("LinkedIn");
        public static final Channel EMAIL = new Channel("Email");
        public static final Channel SMS = new Channel("SMS");

        public static final Channel[] DEFAULTS = new Channel[]{ FACEBOOK, TWITTER, LINKEDIN, EMAIL, SMS };

        protected String name;
        protected boolean enabled = true;

        public Channel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public static Channel get(String name) {
            switch (name.toLowerCase()) {
                case "facebook":
                    return FACEBOOK;
                case "twitter":
                    return TWITTER;
                case "linkedin":
                    return LINKEDIN;
                case "email":
                    return EMAIL;
                case "sms":
                    return SMS;
                default:
                    return null;
            }
        }

    }

}
