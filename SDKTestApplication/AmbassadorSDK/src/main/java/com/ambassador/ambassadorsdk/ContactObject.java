package com.ambassador.ambassadorsdk;

import android.graphics.Bitmap;

/**
 * Created by JakeDunahee on 7/31/15.
 */
class ContactObject implements Comparable<ContactObject> {

    private String name;
    private String pictureUri;
    private String thumbnailUri;
    private String phoneNumber;
    private String emailAddress;
    private String type;
    private Bitmap thumbBmp;
    private Bitmap picBmp;

    public ContactObject() {
        name = "No name available";
        phoneNumber = "No number available";
        emailAddress = "Email not available";
        type = "Not available";
    }

    public ContactObject(String name, String thumbnailUri, String pictureUri, String emailAddress) {
        this.name = name;
        this.thumbnailUri = thumbnailUri;
        this.pictureUri = pictureUri;
        this.emailAddress = emailAddress;
    }

    public ContactObject(String name, String thumbnailUri, String pictureUri, String type, String phoneNumber) {
        this.name = name;
        this.thumbnailUri = thumbnailUri;
        this.pictureUri = pictureUri;
        this.type = type;
        this.phoneNumber = phoneNumber;
    }

    public ContactObject(String name, String thumbnailUri, String pictureUri, String emailAddress, String type, String phoneNumber) {
        this.name = name;
        this.thumbnailUri = thumbnailUri;
        this.pictureUri = pictureUri;
        this.emailAddress = emailAddress;
        this.type = type;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getThumbnailUri() { return thumbnailUri; }

    public String getPictureUri() {
        return pictureUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getType() {
        return type;
    }

    public Bitmap getThumbBmp() {
        return thumbBmp;
    }

    public Bitmap getPicBmp() {
        return picBmp;
    }

    public void setThumbBmp(Bitmap bitmap) {
        this.thumbBmp = bitmap;
    }

    public void setPicBmp(Bitmap bitmap) {
        this.picBmp = bitmap;
    }

    @Override
    public int compareTo(ContactObject another) {
        return name.compareTo(another.getName());
    }

    public ContactObject clone() {
        return new ContactObject(name, pictureUri, emailAddress, type, phoneNumber);
    }

}
