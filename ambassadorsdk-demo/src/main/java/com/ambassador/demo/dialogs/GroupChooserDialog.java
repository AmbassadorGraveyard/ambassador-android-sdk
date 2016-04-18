package com.ambassador.demo.dialogs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.demo.R;
import com.ambassador.demo.api.Requests;
import com.ambassador.demo.api.pojo.GetGroupsResponse;
import com.ambassador.demo.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupChooserDialog extends BaseListChooser<GetGroupsResponse.GroupResponse, BaseListChooser.BaseChooserAdapter> {

    protected GroupChooserAdapter adapter;

    public GroupChooserDialog(Context context) {
        super(context, "Choose Groups");
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetGroupsResponse.GroupResponse groupResponse = getAdapter().getItem(position);
                getAdapter().check(position);
                getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    protected GroupChooserAdapter getAdapter() {
        if (adapter == null) {
            adapter = new GroupChooserAdapter(getContext());
        }
        return adapter;
    }

    @Override
    public String getResult() {
        return null;
    }

    protected class GroupChooserAdapter extends BaseChooserAdapter {

        protected Map<GetGroupsResponse.GroupResponse, Boolean> checks;

        public GroupChooserAdapter(Context context) {
            super(context, new ArrayList<GetGroupsResponse.GroupResponse>());
            Requests.get().getGroups(User.get().getUniversalToken(), new Callback<GetGroupsResponse>() {
                @Override
                public void success(GetGroupsResponse getGroupsResponse, Response response) {
                    GetGroupsResponse.GroupResponse[] results = getGroupsResponse.results;
                    for (GetGroupsResponse.GroupResponse result : results) {
                        data.add(result);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(getClass().getSimpleName(), error.toString());
                }
            });

            checks = new HashMap<>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);

            GetGroupsResponse.GroupResponse item = getItem(position);

            TextView tv1 = (TextView) convertView.findViewById(R.id.tvChooserName);
            TextView tv2 = (TextView) convertView.findViewById(R.id.tvChooserSubName);
            ImageView ivCheckMark = (ImageView) convertView.findViewById(R.id.ivCheckMark);

            tv1.setText(item.group_name);
            tv2.setText("Group ID: " + item.group_id);

            ivCheckMark.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));

            if (checks.keySet().contains(item) && checks.get(item)) {
                ivCheckMark.setVisibility(View.VISIBLE);
            } else {
                ivCheckMark.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        protected void check(int position) {
            GetGroupsResponse.GroupResponse item = getAdapter().getItem(position);
            checks.put(item, true);
        }

    }

}
