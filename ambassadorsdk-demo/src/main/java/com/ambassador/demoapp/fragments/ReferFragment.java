package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.factories.RAFOptionsFactory;
import com.ambassador.demoapp.CustomizationPackage;
import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ReferFragment extends Fragment {

    @Bind(R.id.etCampaignId)    protected EditText etCampaignId;
    @Bind(R.id.lvRafs)          protected ListView lvRafs;

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

        etCampaignId.setText(Demo.get().getCampaignId());
        etCampaignId.addTextChangedListener(etCampaignIdTextWatcher);

        final RafAdapter adapter = new RafAdapter();
        lvRafs.setAdapter(adapter);
        lvRafs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RafAdapter.RafItem item = adapter.getItem(position);
                String path = item.getOptionsPath();
                Demo.get().presentRAF(getActivity(), path);
            }
        });

        lvRafs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                RafAdapter.RafItem rafItem = adapter.getItem(position);
                RAFOptions rafOptions = null;
                try {
                    rafOptions = RAFOptionsFactory.decodeResources(getActivity().getAssets().open(rafItem.getOptionsPath()), getActivity());
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Didn't work!", Toast.LENGTH_SHORT).show();
                }
                if (rafOptions != null) {
                    String filename = new CustomizationPackage(getActivity())
                            .add("raf.xml", rafOptions)
                            .add("README.txt", "AmbassadorSDK 1.1.4\nTest")
                            .zip();
                    File file = new File(getContext().getFilesDir(), filename);
                    Uri uri = FileProvider.getUriForFile(getContext(), "com.ambassador.fileprovider", file);
                    final Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                            .setType("*/*")
                            .setStream(uri)
                            .setChooserTitle("Share Integration Assets")
                            .createChooserIntent()
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    getActivity().startActivity(intent);
                }
                return true;
            }
        });

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

    protected TextWatcher etCampaignIdTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = etCampaignId.getText().toString();
            Demo.get().setCampaignId(text);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    private final class RafAdapter extends BaseAdapter {

        private RafItem[] items;

        public RafAdapter() {
            items = new RafItem[3];
            items[0] = new RafItem("Shoes RAF", "description", "raf_shoes.xml")
                    .setContentDescription("shoeRaf");
            items[1] = new RafItem("Shirt RAF", "description", "raf_shirt.xml")
                    .setContentDescription("shirtRaf");
            items[2] = new RafItem("Ambassador RAF", "description", "raf_ambassador.xml")
                    .setContentDescription("ambassadorRaf");
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public RafItem getItem(int position) {
            return items[position];
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

            RafItem item = getItem(position);

            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            tvTitle.setText(item.getTitle());

            TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            tvDescription.setText(item.getDescription());

            ImageView ivArrow = (ImageView) convertView.findViewById(R.id.ivArrow);
            ivArrow.setColorFilter(Color.BLACK);

            convertView.setContentDescription(item.getContentDescription());

            return convertView;
        }

        private final class RafItem {

            private String title;
            private String description;
            private String optionsPath;
            private String contentDescription;

            public RafItem(String title, String description, String optionsPath) {
                this.title = title;
                this.description = description;
                this.optionsPath = optionsPath;
            }

            public String getTitle() {
                return title;
            }

            public String getDescription() {
                return description;
            }

            public String getOptionsPath() {
                return optionsPath;
            }

            public RafItem setContentDescription(String contentDescription) {
                this.contentDescription = contentDescription;
                return this;
            }

            public String getContentDescription() {
                return contentDescription;
            }

        }

    }

}
