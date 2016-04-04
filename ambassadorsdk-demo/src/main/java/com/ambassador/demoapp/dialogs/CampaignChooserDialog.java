package com.ambassador.demoapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.demoapp.R;
import com.ambassador.demoapp.api.Requests;
import com.ambassador.demoapp.api.pojo.GetCampaignsResponse;
import com.ambassador.demoapp.data.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CampaignChooserDialog extends Dialog {

    @Bind(R.id.lvCampaignChooser) protected ListView lvCampaignChooser;

    protected List<Campaign> campaigns;
    protected Campaign selectedCampaign;

    protected CampaignAdapter adapter;

    public CampaignChooserDialog(Context context) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        campaigns = new ArrayList<>();
        populateCampaigns();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_campaign_chooser);
        ButterKnife.bind(this);
        adapter = new CampaignAdapter(getContext(), campaigns);
        lvCampaignChooser.setAdapter(adapter);
        lvCampaignChooser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Campaign campaign = adapter.getItem(position);
                selectedCampaign = campaign;
                dismiss();
            }
        });
    }

    protected void populateCampaigns() {
        Requests.get().getCampaigns(User.get().getUniversalToken(), new Callback<GetCampaignsResponse>() {
            @Override
            public void success(GetCampaignsResponse getCampaignsResponse, Response response) {
                for (GetCampaignsResponse.CampaignResponse campaignResponse : getCampaignsResponse.results) {
                    campaigns.add(new Campaign(campaignResponse.uid, campaignResponse.name));
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getContext(), "An error occurred while getting campaigns!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    protected static class Campaign {

        protected int id;
        protected String name;

        public Campaign(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    protected static class CampaignAdapter extends BaseAdapter {

        protected Context context;
        protected List<Campaign> campaigns;

        public CampaignAdapter(Context context, List<Campaign> campaigns) {
            this.context = context;
            this.campaigns = campaigns;
        }

        @Override
        public int getCount() {
            return campaigns.size();
        }

        @Override
        public Campaign getItem(int position) {
            return campaigns.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.view_campaign_item, parent, false);
            }

            Campaign campaign = getItem(position);

            TextView tvChannelName = (TextView) convertView.findViewById(R.id.tvCampaignName);
            tvChannelName.setText(campaign.getName());

            TextView tvCampaignId = (TextView) convertView.findViewById(R.id.tvCampaignId);
            tvCampaignId.setText("Campaign ID: " + campaign.getId());

            return convertView;
        }

    }

    public String getSelectedCampaign() {
        return selectedCampaign == null ? null : new Gson().toJson(selectedCampaign);
    }

}
