package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.R;

import java.util.ArrayList;

/**
 * Created by JakeDunahee on 7/22/15.
 */
class SocialGridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SocialGridModel> models;
    LayoutInflater inflater;
    private ShapeDrawable rectShapeDrawable;

    public SocialGridAdapter(Context context, ArrayList<SocialGridModel> models) {
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.models = models;

        //create drawable to use as border around non-filled grid cells
        RectShape rect = new RectShape();
        rectShapeDrawable = new ShapeDrawable(rect);
        Paint paint = rectShapeDrawable.getPaint();
        paint.setColor(context.getResources().getColor(R.color.ultraLightGray));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Utilities.getPixelSizeForDimension(R.dimen.grid_cell_outline_width));
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
        backgroundView.setBackgroundColor(model.getBackgroundColor());

        if (model.willDrawBorder()) {
            backgroundView.setBackground(rectShapeDrawable);
        }

        return convertView;
    }

}
