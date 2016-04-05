package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.internal.views.CircleImageView;
import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.activities.CustomizationActivity;
import com.ambassador.demoapp.data.Integration;
import com.ambassador.demoapp.data.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public final class ReferFragment extends Fragment {

    @Bind(R.id.ivAddRaf) protected CircleImageView ivAddRaf;
    @Bind(R.id.tvNoRafs) protected TextView tvNoRafs;
    @Bind(R.id.lvRafs) protected ListView lvRafs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refer, container, false);
        ButterKnife.bind(this, view);

        ivAddRaf.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.add_raf));
        ivAddRaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomizationActivity.class);
                startActivity(intent);
            }
        });

        final RafAdapter adapter = new RafAdapter();
        lvRafs.setAdapter(adapter);

        if (adapter.getCount() > 0) {
            tvNoRafs.setVisibility(View.GONE);
            ivAddRaf.setVisibility(View.GONE);
        }

//        lvRafs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RafAdapter.RafItem item = adapter.getItem(position);
//                String path = item.getOptionsPath();
//                Demo.get().presentRAF(getActivity(), path);
//            }
//        });
//        lvRafs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                RafAdapter.RafItem rafItem = adapter.getItem(position);
//                RAFOptions rafOptions = null;
//                try {
//                    rafOptions = RAFOptionsFactory.decodeResources(getActivity().getAssets().open(rafItem.getOptionsPath()), getActivity());
//                } catch (Exception e) {
//                    Toast.makeText(getActivity(), "Didn't work!", Toast.LENGTH_SHORT).show();
//                }
//                if (rafOptions != null) {
//                    StringBuilder readmeBuilder = new StringBuilder();
//                    readmeBuilder.append("AmbassadorSDK 1.1.4\n");
//                    readmeBuilder.append("Add the items from the assets folder to your applications local assets folder.\n");
//                    readmeBuilder.append("Use the following code snippet to present this refer a friend integration:\n");
//                    readmeBuilder.append("AmbassadorSDK.presentRAF(context, campaignId, \"raf.xml\");\n");
//
//                    String filename = new CustomizationPackage(getActivity())
//                            .add("raf.xml", rafOptions)
//                            .add("README.txt", readmeBuilder.toString(), CustomizationPackage.Directory.FILES)
//                            .zip();
//                    File file = new File(getContext().getFilesDir(), filename);
//                    Uri uri = FileProvider.getUriForFile(getContext(), "com.ambassador.fileprovider", file);
//                    final Intent intent = ShareCompat.IntentBuilder.from(getActivity())
//                            .setType("*/*")
//                            .setStream(uri)
//                            .setChooserTitle("Share Integration Assets")
//                            .createChooserIntent()
//                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
//                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//                    getActivity().startActivity(intent);
//                }
//                return true;
//            }
//        });
//
        setListViewHeightBasedOnChildren(lvRafs);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        closeSoftKeyboard();
    }

    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
            ivShare.setColorFilter(Color.parseColor("#232f3b"));

            return convertView;
        }

    }

}
