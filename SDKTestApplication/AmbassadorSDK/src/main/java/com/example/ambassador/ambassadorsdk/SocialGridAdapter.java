package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.graphics.Color;
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

/**
 * Created by JakeDunahee on 7/22/15.
 */
class SocialGridAdapter extends BaseAdapter {
    private Context context;
    private String[] nameArray;
    private Integer[] drawablesArray;
    LayoutInflater inflater;
    private ShapeDrawable rectShapeDrawable;

    public SocialGridAdapter(Context context, String[] gridNames, Integer[] gridImages) {
        this.context = context;
        this.nameArray = gridNames;
        this.drawablesArray = gridImages;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
            convertView = inflater.inflate(R.layout.custom_social_cell, parent, false);
        }

        ImageView gridImage = (ImageView) convertView.findViewById(R.id.ivGridImage);
        TextView gridTitle = (TextView) convertView.findViewById(R.id.tvGridTitle);
        RelativeLayout backgroundView = (RelativeLayout) convertView.findViewById(R.id.rlBackground);

        gridImage.setImageResource(drawablesArray[position]);
        gridTitle.setText(nameArray[position]);
        backgroundView.setBackgroundColor(_getCorrectBackgroundColor(position));
        if (position == 3 || position == 4) {
            backgroundView.setBackground(rectShapeDrawable);
        }

        return convertView;
    }

    private int _getCorrectBackgroundColor(int position) {
        switch (position) {
            case 0:
                return context.getResources().getColor(R.color.facebook_blue);
            case 1:
                return context.getResources().getColor(R.color.twitter_blue);
            case 2:
                return context.getResources().getColor(R.color.linkedin_blue);
            default:
                return Color.WHITE;
        }
    }
}
