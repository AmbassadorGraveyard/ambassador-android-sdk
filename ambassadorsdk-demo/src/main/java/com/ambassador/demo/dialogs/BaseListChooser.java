package com.ambassador.demo.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ambassador.demo.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseListChooser<D, A extends BaseListChooser.BaseChooserAdapter> extends Dialog {

    protected List<D> data;
    protected A adapter;

    @Bind(R.id.tvChooserTitle) protected TextView tvChooserTitle;
    @Bind(R.id.lvChooser) protected ListView lvChooser;

    private String title;
    private AdapterView.OnItemClickListener onItemClickListener;

    public BaseListChooser(Context context, String title) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        data = new ArrayList<>();
        setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_chooser);
        ButterKnife.bind(this);

        setTitle(title);
        setOnItemClickListener(onItemClickListener);

        lvChooser.setAdapter(getAdapter());
    }

    protected abstract A getAdapter();
    public abstract String getResult();

    protected void setTitle(String title) {
        this.title = title != null ? title : "Chooser Dialog";
        if (tvChooserTitle != null) {
            tvChooserTitle.setText(this.title);
        }
    }

    protected void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            this.onItemClickListener = onItemClickListener;
            if (lvChooser != null) {
                lvChooser.setOnItemClickListener(onItemClickListener);
            }
        }
    }

    protected abstract class BaseChooserAdapter extends BaseAdapter {

        protected Context context;
        protected List<D> data;

        public BaseChooserAdapter(Context context, List<D> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public D getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.view_chooser_item, parent, false);
            }

            return convertView;
        }

    }

    protected abstract class SerializablePojo {

        public String getJson() {
            return new Gson().toJson(this);
        }

    }

}
