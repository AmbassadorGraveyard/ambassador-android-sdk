package com.example.ambassador.ambassadorsdk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by JakeDunahee on 7/31/15.
 */
class ContactListAdapter extends BaseAdapter  {
    public ArrayList<ContactObject> selectedContacts;
    private ArrayList<ContactObject> contactObjects, filteredContactList;
    private Boolean shouldShowPhoneNumbers;
    private final Activity context;
    private final int checkmarkPxXPos;

    public ContactListAdapter(Activity context, ArrayList<ContactObject> contactObjects, Boolean showPhoneNumbers) {
        this.context = context;
        this.contactObjects = contactObjects;
        this.shouldShowPhoneNumbers = showPhoneNumbers;
        selectedContacts = new ArrayList<>();
        filteredContactList = new ArrayList<>(contactObjects);

        checkmarkPxXPos = Utilities.getPixelSizeForDimension(R.dimen.contact_select_checkmark_x);
    }

    static class ViewHolder {
        protected TextView tvName;
        protected TextView tvPhoneOrEmail;
        protected ImageView ivCheckMark;
    }

    @Override
    public int getCount() {
        return filteredContactList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_contacts, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.tvPhoneOrEmail = (TextView)convertView.findViewById(R.id.tvNumberOrEmail);
            viewHolder.ivCheckMark = (ImageView)convertView.findViewById(R.id.ivCheckMark);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ContactObject currentObject = filteredContactList.get(position);
        viewHolder.tvName.setText(currentObject.name);

        if (shouldShowPhoneNumbers) {
            viewHolder.tvPhoneOrEmail.setText(currentObject.type + " - " + currentObject.phoneNumber);
        } else {
            viewHolder.tvPhoneOrEmail.setText(currentObject.emailAddress);
        }

        // Checks whether the view should be selected or not and correctly positions the checkmark image
        if (selectedContacts.contains(filteredContactList.get(position))) {
            viewHolder.ivCheckMark.setX(convertView.getWidth() - viewHolder.ivCheckMark.getWidth() - checkmarkPxXPos);
        } else {
            viewHolder.ivCheckMark.setX(convertView.getWidth());
        }

        return convertView;
    }

    public void filterList(String filterString) {
        // Functionality: Filters the arrayLists based on search parameters in contactSelector activity
        if (filterString != null && !filterString.equals("")) {
            filteredContactList.clear();
            for (int i = 0; i < contactObjects.size(); i++) {
                ContactObject object = contactObjects.get(i);
                if (object.name.toLowerCase().contains(filterString.toLowerCase())) { filteredContactList.add(object); }
            }

            notifyDataSetChanged();
        } else {
            clearFilter();
        }
    }

    private void clearFilter() {
        filteredContactList.clear();
        filteredContactList = new ArrayList<>(contactObjects);
        notifyDataSetChanged();
    }

    public void updateArrays(int position, View view) {
        // Functionality: Adds and removes contacts to and from the selectedArray and animates checkmark image
        final ImageView imageView = (ImageView) view.findViewById(R.id.ivCheckMark);
        if (selectedContacts.contains(filteredContactList.get(position))) {
            selectedContacts.remove(filteredContactList.get(position));
            imageView.animate().setDuration(100).x(view.getWidth()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationCancel(animation);
                    imageView.setVisibility(View.GONE);
                }
            }).start();
        } else {
            selectedContacts.add(filteredContactList.get(position));
            imageView.setVisibility(View.VISIBLE);
            imageView.animate().setDuration(300).setInterpolator(new OvershootInterpolator())
                    .x(view.getWidth() - imageView.getWidth() - checkmarkPxXPos).setListener(null).start();
        }
    }
}
