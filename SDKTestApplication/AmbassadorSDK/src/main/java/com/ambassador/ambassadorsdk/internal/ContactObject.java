package com.ambassador.ambassadorsdk.internal;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public class ContactObject implements Comparable<ContactObject> {

    private String name;
    private String phoneNumber;
    private String emailAddress;
    private String type;
    private String pictureUri;
    private Bitmap pictureBitmap;
    private String thumbnailUri;
    private Bitmap thumbnailBitmap;

    private ContactObject() {}

    public String getName() {
        return name;
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

    public String getPictureUri() {
        return pictureUri;
    }

    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }

    public void setPictureBitmap(Bitmap bitmap) {
        this.pictureBitmap = bitmap;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public Bitmap getThumbnailBitmap() {
        return thumbnailBitmap;
    }

    public void setThumbnailBitmap(Bitmap bitmap) {
        this.thumbnailBitmap = bitmap;
    }

    @Override
    public int compareTo(@NonNull ContactObject another) {
        return name.compareTo(another.getName());
    }

    public ContactObject copy() {
        return new ContactObject.Builder()
                .setName(name)
                .setThumbnailUri(thumbnailUri)
                .setPictureUri(pictureUri)
                .setPhoneNumber(phoneNumber)
                .setEmailAddress(emailAddress)
                .setType(type)
                .build();
    }

    public static final class Builder {

        private String name;
        private String pictureUri;
        private String thumbnailUri;
        private String phoneNumber;
        private String emailAddress;
        private String type;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPictureUri(String pictureUri) {
            this.pictureUri = pictureUri;
            return this;
        }

        public Builder setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public ContactObject build() {
            ContactObject tmp = new ContactObject();
            tmp.name = this.name;
            tmp.pictureUri = this.pictureUri;
            tmp.thumbnailUri = this.thumbnailUri;
            tmp.phoneNumber = this.phoneNumber;
            tmp.emailAddress = this.emailAddress;
            tmp.type = this.type;
            return tmp;
        }

    }

}
