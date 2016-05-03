package com.ambassador.app.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.app.R;
import com.ambassador.app.api.Requests;
import com.ambassador.app.api.pojo.GetGroupsResponse;
import com.ambassador.app.data.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupChooserDialog extends BaseListChooser<GetGroupsResponse.GroupResponse, BaseListChooser.BaseChooserAdapter> {

    protected GroupChooserAdapter adapter;
    protected boolean wasCanceled;
    protected List<String> startupGroups;

    public GroupChooserDialog(Context context) {
        super(context, "Choose Groups");
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetGroupsResponse.GroupResponse groupResponse = getAdapter().getItem(position);
                boolean added = getAdapter().check(position);
                ImageView ivCheckMark = (ImageView) view.findViewById(R.id.ivCheckMark);
                if (added) {
                    ivCheckMark.animate().translationX(0).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
                } else {
                    ivCheckMark.animate().translationX(100).setDuration(250).setInterpolator(new AccelerateInterpolator()).start();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasCanceled = true;
                dismiss();
            }
        });
        findViewById(R.id.tvSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasCanceled = false;
                dismiss();
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
        if (wasCanceled) return null;
        if (getAdapter().getChecked().length == 0) return "";
        String[] selected = getAdapter().getChecked();
        Arrays.sort(selected);
        String out = "";
        for (int i = 0; i < selected.length - 1; i++) {
            out += selected[i] + ", ";
        }
        out += selected[selected.length - 1];
        return out;
    }

    public void setSelected(String groups) {
        this.startupGroups = Arrays.asList(groups.split(","));
    }

    protected class GroupChooserAdapter extends BaseChooserAdapter {

        protected List<GetGroupsResponse.GroupResponse> checks;

        public GroupChooserAdapter(Context context) {
            super(context, new ArrayList<GetGroupsResponse.GroupResponse>());
            Requests.get().getGroups(User.get().getUniversalToken(), new Callback<GetGroupsResponse>() {
                @Override
                public void success(GetGroupsResponse getGroupsResponse, Response response) {
                    findViewById(R.id.rlChooserButtons).setVisibility(View.VISIBLE);
                    GetGroupsResponse.GroupResponse[] results = getGroupsResponse.results;
                    for (GetGroupsResponse.GroupResponse result : results) {
                        data.add(result);
                        if (startupGroups != null && startupGroups.contains(result.group_id)) {
                            checks.add(result);
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(getClass().getSimpleName(), error.toString());
                }
            });

            checks = new ArrayList<>();
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
            ivCheckMark.setColorFilter(Color.parseColor("#28446B"));

            if (checks.contains(item)) {
                ivCheckMark.setTranslationX(0);
            } else {
                ivCheckMark.setTranslationX(100);
            }

            return convertView;
        }

        protected boolean check(int position) {
            GetGroupsResponse.GroupResponse item = getAdapter().getItem(position);
            if (checks.contains(item)) {
                checks.remove(item);
                return false;
            } else {
                checks.add(item);
                return true;
            }
        }

        public String[] getChecked() {
            String[] out = new String[checks.size()];
            for (int i = 0; i < checks.size(); i++) {
                out[i] = checks.get(i).group_id;
            }
            return out;
        }

    }

}
