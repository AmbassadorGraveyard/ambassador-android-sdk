package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Collections2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.Filter;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class ContactListAdapter extends BaseAdapter  {
    public ArrayList<ContactObject> contactObjects, filteredContactList, selectedContacts;
    public LayoutInflater inflater;
    public Boolean shouldShowPhoneNumbers, isFiltering;

    public ContactListAdapter(Context context, ArrayList<ContactObject> contactObjects, Boolean showPhoneNumbers) {
        this.contactObjects = contactObjects;
        this.shouldShowPhoneNumbers = showPhoneNumbers;
        selectedContacts = new ArrayList<>();
        filteredContactList = (ArrayList<ContactObject>)contactObjects.clone();
        isFiltering = false;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_contacts, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvPhoneOrEmail = (TextView) convertView.findViewById(R.id.tvNumberOrEmail);
        ImageView ivCheckMark = (ImageView)convertView.findViewById(R.id.ivCheckMark);

        ContactObject currentObject = filteredContactList.get(position);

        tvName.setText(currentObject.name);

        if (shouldShowPhoneNumbers) {
            tvPhoneOrEmail.setText(currentObject.type + " - " + currentObject.phoneNumber);
        } else {
            tvPhoneOrEmail.setText(currentObject.emailAddress);
        }

        if (selectedContacts.contains(filteredContactList.get(position))) {
            ivCheckMark.setX(convertView.getWidth() - ivCheckMark.getWidth() - 15);
        } else {
            ivCheckMark.setX(convertView.getWidth());
        }

        return convertView;
    }

    public void filterList(String filterString) {
        if (filterString != null || filterString != "") {
            filteredContactList.clear();
            for (int i = 0; i < contactObjects.size(); i++) {
                ContactObject object = contactObjects.get(i);
                if (object.name.toLowerCase().contains(filterString.toLowerCase())) {
                    filteredContactList.add(object);
                }
            }

            notifyDataSetChanged();
        } else {
            clearFilter();
        }
    }

    public void clearFilter() {
        filteredContactList.clear();
        filteredContactList = (ArrayList<ContactObject>)contactObjects.clone();
        notifyDataSetChanged();
    }
}
