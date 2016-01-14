package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;

import java.util.ArrayList;

/**
 * Created by JakeDunahee on 7/22/15.
 */
class SocialGridAdapter extends BaseAdapter {

    private RAFOptions raf = RAFOptions.get();

    private Context context;
    private ArrayList<SocialGridModel> models;
    LayoutInflater inflater;
    private float cornerRadius;

    public SocialGridAdapter(Context context, ArrayList<SocialGridModel> models) {
        this.context = context;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.models = models;
        this.cornerRadius = raf.getSocialOptionCornerRadius();
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public SocialGridModel getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_social_cell, parent, false);
        }

        SocialGridModel model = getItem(position);
        ImageView gridImage = (ImageView) convertView.findViewById(R.id.ivGridImage);
        TextView gridTitle = (TextView) convertView.findViewById(R.id.tvGridTitle);
        RelativeLayout backgroundView = (RelativeLayout) convertView.findViewById(R.id.rlBackground);

        gridImage.setImageResource(model.getIconDrawable());
        gridTitle.setText(model.getName());

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(model.getBackgroundColor());
        if (cornerRadius < 0) {
            backgroundDrawable.setCornerRadius(context.getResources().getDimension(R.dimen.social_option_size));
        } else {
            backgroundDrawable.setCornerRadius(cornerRadius);
        }

        if (model.willDrawBorder()) {
            backgroundDrawable.setStroke(3, context.getResources().getColor(R.color.ultraLightGray));
        }

        backgroundView.setBackground(backgroundDrawable);

        return convertView;
    }

}
