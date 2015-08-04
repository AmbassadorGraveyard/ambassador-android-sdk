package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class ContactListAdapter extends BaseAdapter {
    public ArrayList<ContactObject> contactObjects;
    public LayoutInflater inflater;
    public Boolean shouldShowPhoneNumbers;
    public ArrayList<ContactObject> selectedContacts;

    public ContactListAdapter(Context context, ArrayList<ContactObject> contactObjects, Boolean showPhoneNumbers) {
        this.contactObjects = contactObjects;
        this.shouldShowPhoneNumbers = showPhoneNumbers;
        selectedContacts = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactObjects.size();
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

        ContactObject currentObject = contactObjects.get(position);
        tvName.setText(currentObject.name);

        if (shouldShowPhoneNumbers) {
            tvPhoneOrEmail.setText(currentObject.type + " - " + currentObject.phoneNumber);
        } else {
            tvPhoneOrEmail.setText(currentObject.emailAddress);
        }

        if (selectedContacts.contains(contactObjects.get(position))) {
            ivCheckMark.setX(convertView.getWidth() - ivCheckMark.getWidth() - 15);
        } else {
            ivCheckMark.setX(convertView.getWidth());
        }

        return convertView;
    }
}
