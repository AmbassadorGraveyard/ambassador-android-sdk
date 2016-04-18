package com.ambassador.demo.dialogs;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ambassador.demo.R;
import com.ambassador.demo.api.Requests;
import com.ambassador.demo.api.pojo.GetCampaignsResponse;
import com.ambassador.demo.data.User;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CampaignChooserDialog extends BaseListChooser<GetCampaignsResponse.CampaignResponse, BaseListChooser.BaseChooserAdapter> {

    protected CampaignChooserAdapter adapter;
    protected SerializableCampaign campaign;

    public CampaignChooserDialog(Context context) {
        super(context, "Choose a Campaign");
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetCampaignsResponse.CampaignResponse response = getAdapter().getItem(position);
                campaign = new SerializableCampaign(response.name, response.uid);
                dismiss();
            }
        });
    }

    @Override
    protected BaseChooserAdapter getAdapter() {
        if (adapter == null) {
            adapter = new CampaignChooserAdapter(getOwnerActivity());
        }

        return adapter;
    }

    @Override
    public String getResult() {
        return campaign.getJson();
    }

    protected class CampaignChooserAdapter extends BaseChooserAdapter {

        public CampaignChooserAdapter(Context context) {
            super(context, new ArrayList<GetCampaignsResponse.CampaignResponse>());
            Requests.get().getCampaigns(User.get().getUniversalToken(), new Callback<GetCampaignsResponse>() {
                @Override
                public void success(GetCampaignsResponse getCampaignsResponse, Response response) {
                    GetCampaignsResponse.CampaignResponse[] results = getCampaignsResponse.results;
                    for (GetCampaignsResponse.CampaignResponse result : results) {
                        data.add(result);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(getClass().getSimpleName(), error.toString());
                }
            });
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);

            GetCampaignsResponse.CampaignResponse item = getItem(position);

            TextView tv1 = (TextView) convertView.findViewById(R.id.tvChooserName);
            TextView tv2 = (TextView) convertView.findViewById(R.id.tvChooserSubName);

            tv1.setText(item.name);
            tv2.setText("Campaign ID: " + item.uid);

            return convertView;
        }

    }

    public class SerializableCampaign extends SerializablePojo {

        public String name;
        public int id;

        public SerializableCampaign(String name, int id) {
            this.name = name;
            this.id = id;
        }

    }

}
