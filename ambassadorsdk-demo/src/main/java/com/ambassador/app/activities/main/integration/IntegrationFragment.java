package com.ambassador.app.activities.main.integration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.ambassador.ambassadorsdk.internal.views.CircleImageView;
import com.ambassador.app.R;
import com.ambassador.app.activities.PresenterManager;
import com.ambassador.app.activities.customization.CustomizationActivity;
import com.ambassador.app.activities.main.MainActivity;
import com.ambassador.app.data.Integration;
import com.ambassador.app.utils.Share;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public final class IntegrationFragment extends Fragment implements IntegrationView, MainActivity.TabFragment {

    protected IntegrationPresenter integrationPresenter;

    @Bind(R.id.ivAddRaf) protected CircleImageView ivAddRaf;
    @Bind(R.id.tvNoRafs) protected TextView tvNoRafs;
    @Bind(R.id.lvRafs) protected ListView lvRafs;
    @Bind(R.id.fabAdd) protected FloatingActionButton fabAdd;

    protected IntegrationAdapter integrationAdapter;
    protected AmbassadorSDK AmbassadorSDK;
    protected boolean editing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AmbassadorSDK = new AmbassadorSDK(getActivity());

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

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrationPresenter.onAddClicked();
            }
        });

        lvRafs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                integrationPresenter.onIntegrationClicked(position);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        integrationPresenter.bindView(this);
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

    @Override
    public void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    @Override
    public void showEmptyListContent() {
        tvNoRafs.setVisibility(View.VISIBLE);
        ivAddRaf.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyListContent() {
        tvNoRafs.setVisibility(View.GONE);
        ivAddRaf.setVisibility(View.GONE);
    }

    @Override
    public void showPopulatedListContent() {
        lvRafs.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePopulatedListContent() {
        lvRafs.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);
    }

    @Override
    public void setListContent(List<Integration> content) {
        if (integrationAdapter == null) {
            integrationAdapter = new IntegrationAdapter();
            lvRafs.setAdapter(integrationAdapter);
        }

        integrationAdapter.data = content;
        integrationAdapter.notifyDataSetChanged();

        lvRafs.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85 * content.size(), getResources().getDisplayMetrics());

        ((MainActivity) getActivity()).notifyIntegrationSetInvalidated();

        if (content.size() == 0) editing = false;
    }

    @Override
    public void createIntegration() {
        Intent intent = new Intent(getActivity(), CustomizationActivity.class);
        startActivity(intent);
    }

    @Override
    public void toggleEditing() {
        editing = !editing;
        integrationPresenter.updateView();
    }

    @Override
    public void present(Integration integration) {
        AmbassadorSDK.presentRAF(getActivity(), String.valueOf(integration.getCampaignId()), integration.getRafOptions());
    }

    @Override
    public void edit(Integration integration) {
        Intent intent = new Intent(getActivity(), CustomizationActivity.class);
        intent.putExtra("editing", true);
        intent.putExtra("integration", integration.getCreatedAtDate());
        startActivity(intent);
    }

    @Override
    public void askToDelete(final Integration integration) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to delete this integration?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        integrationPresenter.onDeleteConfirmed(integration);
                    }
                }).setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void share(Share share) {
        share.execute(getActivity());
    }

    @Override
    public void onActionClicked() {
        integrationPresenter.onActionClicked();
    }

    @Override
    public boolean getActionVisibility() {
        return integrationAdapter != null && integrationAdapter.getCount() > 0 ;
    }

    @DrawableRes
    @Override
    public int getActionDrawable() {
        return editing ? R.drawable.done_icon : R.drawable.edit_icon;
    }

    @Override
    public String getTitle() {
        return "Refer a Friend";
    }

    protected final class IntegrationAdapter extends BaseAdapter {

        private List<Integration> data;

        @Bind(R.id.tvTitle) protected TextView tvTitle;
        @Bind(R.id.tvDescription) protected TextView tvDescription;
        @Bind(R.id.ivShare) protected ImageView ivShare;
        @Bind(R.id.ivDeleteRaf) protected ImageView ivDelete;

        public IntegrationAdapter() {
            data = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Integration getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_integration_item, parent, false);
            }

            ButterKnife.bind(this, convertView);

            ivShare.setColorFilter(Color.parseColor("#232f3b"));
            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editing) {
                        integrationPresenter.onEditClicked(position);
                    } else {
                        integrationPresenter.onShareClicked(position);
                    }
                }
            });

            ivDelete.setColorFilter(Color.parseColor("#e34d41"));
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    integrationPresenter.onDeleteClicked(position);
                }
            });

            Integration item = getItem(position);

            tvTitle.setText(item.getName());
            tvDescription.setText(String.format("Created %s", new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(item.getCreatedAtDate()))));
            ivShare.setImageResource(editing ? R.drawable.ic_mode_edit : R.drawable.ic_share_white);
            ivDelete.setVisibility(editing ? View.VISIBLE : View.GONE);

            return convertView;
        }

    }

}
