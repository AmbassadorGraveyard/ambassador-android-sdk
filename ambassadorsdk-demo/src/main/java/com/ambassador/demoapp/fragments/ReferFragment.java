package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.activities.CustomizationActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ReferFragment extends Fragment {

    @Bind(R.id.ivAddRaf) protected CircleImageView ivAddRaf;
    //@Bind(R.id.lvRafs) protected ListView lvRafs;

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

//        final RafAdapter adapter = new RafAdapter();
//        lvRafs.setAdapter(adapter);
//        lvRafs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RafAdapter.RafItem item = adapter.getItem(position);
//                String path = item.getOptionsPath();
//                Demo.get().presentRAF(getActivity(), path);
//            }
//        });

//        setListViewHeightBasedOnChildren(lvRafs);

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

        private RafItem[] items;

        public RafAdapter() {
            items = new RafItem[6];
            items[0] = new RafItem("Shoes RAF", "description", "raf_shoes.xml")
                    .setContentDescription("shoeRaf");
            items[1] = new RafItem("Shirt RAF", "description", "raf_shirt.xml")
                    .setContentDescription("shirtRaf");
            items[2] = new RafItem("Ambassador RAF", "description", "raf_ambassador.xml")
                    .setContentDescription("ambassadorRaf");

            items[3] = new RafItem("Shoes RAF", "description", "raf_shoes.xml")
                    .setContentDescription("shoeRaf");
            items[4] = new RafItem("Shirt RAF", "description", "raf_shirt.xml")
                    .setContentDescription("shirtRaf");
            items[5] = new RafItem("Ambassador RAF", "description", "raf_ambassador.xml")
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
