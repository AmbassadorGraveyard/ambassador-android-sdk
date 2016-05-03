package com.ambassador.app.activities.main.integration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.views.CircleImageView;
import com.ambassador.app.Demo;
import com.ambassador.app.R;
import com.ambassador.app.activities.PresenterManager;
import com.ambassador.app.activities.customization.CustomizationActivity;
import com.ambassador.app.activities.main.MainActivity;
import com.ambassador.app.data.Integration;
import com.ambassador.app.data.User;
import com.ambassador.app.exports.Export;
import com.ambassador.app.exports.IntegrationExport;
import com.ambassador.app.utils.Share;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public final class IntegrationFragment extends Fragment implements IntegrationView, MainActivity.TabFragment {

    protected IntegrationPresenter integrationPresenter;

    protected static final int LISTVIEW_EXTRA_PADDING = 110;

    protected boolean editing = false;

    protected RafAdapter adapter;

    @Bind(R.id.ivAddRaf) protected CircleImageView ivAddRaf;
    @Bind(R.id.tvNoRafs) protected TextView tvNoRafs;
    @Bind(R.id.lvRafs) protected ListView lvRafs;
    @Bind(R.id.fabAdd) protected FloatingActionButton fabAdd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            integrationPresenter = new IntegrationPresenter();
        } else {
            integrationPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_integration, container, false);
        ButterKnife.bind(this, view);

        ivAddRaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrationPresenter.onAddClicked();
            }
        });

        adapter = new RafAdapter();
        lvRafs.setAdapter(adapter);


        if (adapter.getCount() > 0) {
            tvNoRafs.setVisibility(View.GONE);
            ivAddRaf.setVisibility(View.GONE);
            lvRafs.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);

            int height = LISTVIEW_EXTRA_PADDING + adapter.getCount() * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 78, getActivity().getResources().getDisplayMetrics());
            lvRafs.getLayoutParams().height = height;
        }

        lvRafs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integration item = adapter.getItem(position);
                AmbassadorSDK.presentRAF(getActivity(), item.getCampaignId()+"", item.getRafOptions());
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomizationActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        integrationPresenter.bindView(this);
        closeSoftKeyboard();
        adapter = new RafAdapter();
        lvRafs.setAdapter(adapter);
        if (adapter.getCount() > 0) {
            tvNoRafs.setVisibility(View.GONE);
            ivAddRaf.setVisibility(View.GONE);
            lvRafs.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);

            int height = LISTVIEW_EXTRA_PADDING + adapter.getCount() * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 78, getActivity().getResources().getDisplayMetrics());
            lvRafs.getLayoutParams().height = height;
        } else {
            tvNoRafs.setVisibility(View.VISIBLE);
            ivAddRaf.setVisibility(View.VISIBLE);
            lvRafs.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
        }

        ((MainActivity) getActivity()).notifyIntegrationSetInvalidated();
    }

    @Override
    public void onPause() {
        super.onPause();
        integrationPresenter.unbindView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(integrationPresenter, outState);
    }

    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    private final class RafAdapter extends BaseAdapter {

        private List<Integration> items;

        public RafAdapter() {
            items = new ArrayList<>();
            SharedPreferences preferences = Demo.get().getSharedPreferences("integrations", Context.MODE_PRIVATE);
            String integrationsArrayString = preferences.getString(User.get().getUniversalId(), "[]");
            JsonArray integrationsArray = new JsonParser().parse(integrationsArrayString).getAsJsonArray();
            for (int i = 0; i < integrationsArray.size(); i++) {
                Integration integration = new Gson().fromJson(integrationsArray.get(i).getAsString(), Integration.class);
                items.add(integration);
            }

            Collections.sort(items);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Integration getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_raf_item, parent, false);
            }

            Integration item = getItem(position);

            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            tvTitle.setText(item.getName());

            TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            Date date = new Date(item.getCreatedAtDate());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            String time = simpleDateFormat.format(date);
            tvDescription.setText(String.format("Created %s", time));

            ImageView ivShare = (ImageView) convertView.findViewById(R.id.ivShare);
            if (editing) {
                ivShare.setImageResource(R.drawable.ic_mode_edit);
            } else {
                ivShare.setImageResource(R.drawable.ic_share_white);
            }
            ivShare.setColorFilter(Color.parseColor("#232f3b"));

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editing) {
                        edit(position);
                    } else {
                        share(position);
                    }
                }
            });

            ImageView ivDelete = (ImageView) convertView.findViewById(R.id.ivDeleteRaf);
            if (editing) {
                ivDelete.setVisibility(View.VISIBLE);
            } else {
                ivDelete.setVisibility(View.GONE);
            }
            ivDelete.setColorFilter(Color.parseColor("#e34d41"));

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editing) {
                        delete(position);
                    }
                }
            });

            return convertView;
        }

    }

    @Override
    public void onActionClicked() {
        editing = !editing;
        refreshEditingState();
    }

    @Override
    public Drawable getActionDrawable() {
        return ContextCompat.getDrawable(getActivity(), editing ? R.drawable.done_icon : R.drawable.edit_icon);
    }

    @Override
    public boolean getActionVisibility() {
        return adapter.getCount() > 0;
    }

    protected void refreshEditingState() {
        adapter.notifyDataSetChanged();
    }

    protected void share(int item) {
        Integration integration = adapter.getItem(item);
        RAFOptions rafOptions = integration.getRafOptions();
        if (rafOptions != null) {

            Export<Integration> export = new IntegrationExport();
            export.setModel(integration);
            String filename = export.zip(getActivity());
            new Share(filename).withSubject("Ambassador RAF Integration Instructions").withBody(export.getReadme()).execute(getActivity());
        }
    }

    protected void edit(int item) {
        Integration integration = adapter.getItem(item);
        Intent intent = new Intent(getActivity(), CustomizationActivity.class);
        intent.putExtra("editing", true);
        intent.putExtra("integration", integration.getCreatedAtDate());
        startActivity(intent);
    }

    protected void delete(final int item) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to delete this integration?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integration integration = adapter.getItem(item);
                        integration.delete();
                        adapter.items.remove(item);
                        adapter.notifyDataSetChanged();
                        if (adapter.items.size() == 0) {
                            editing = false;
                            onResume();
                        } else {
                            int height = LISTVIEW_EXTRA_PADDING + adapter.getCount() * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 78, getActivity().getResources().getDisplayMetrics());
                            lvRafs.getLayoutParams().height = height;
                        }
                    }
                }).setNegativeButton("Cancel", null)
                .show();
    }

}
