package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;

/**
 * Created by JakeDunahee on 7/22/15.
 */
class SocialGridAdapter extends BaseAdapter {
    private Context context;
    private String[] nameArray;
    private Integer[] drawablesArray;
    LayoutInflater inflater;

    public SocialGridAdapter(Context context, String[] gridNames, Integer[] gridImages) {
        this.context = context;
        this.nameArray = gridNames;
        this.drawablesArray = gridImages;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return drawablesArray.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_social_cell, null);
        }

        ImageView gridImage = (ImageView) convertView.findViewById(R.id.ivGridImage);
        TextView gridTitle = (TextView) convertView.findViewById(R.id.tvGridTitle);
        RelativeLayout backgroundView = (RelativeLayout) convertView.findViewById(R.id.rlBackground);

        gridImage.setImageResource(drawablesArray[position]);
        gridTitle.setText(nameArray[position]);
        backgroundView.getBackground().setColorFilter(getCorrectBackgroundColor(position), PorterDuff.Mode.SRC_ATOP);

        return convertView;
    }

    public int getCorrectBackgroundColor(int position) {
        switch (position) {
            case 0:
                return Color.parseColor("#3b5998");
            case 1:
                return Color.parseColor("#55acee");
            case 2:
                return Color.parseColor("#0077b5");
            default:
                return Color.WHITE;
        }
    }
}
