package com.ambassador.ambassadorsdk.internal.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represents and holds information about a contact in the user's contact list.
 */
public final class Contact implements Comparable<Contact> {

    protected String name;
    protected String phoneNumber;
    protected String emailAddress;
    protected String type;
    protected String pictureUri;
    protected Bitmap pictureBitmap;
    protected String thumbnailUri;
    protected Bitmap thumbnailBitmap;

    protected Contact() {}

    @NonNull
    public String getName() {
        return name != null ? name : "Unknown";
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : "Unknown";
    }

    @NonNull
    public String getEmailAddress() {
        return emailAddress != null ? emailAddress : "Unknown";
    }

    @NonNull
    public String getType() {
        return type != null ? type : "Other";
    }

    @Nullable
    public String getPictureUri() {
        return pictureUri;
    }

    @Nullable
    public String getThumbnailUri() {
        return thumbnailUri;
    }

    @Nullable
    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }

    @Nullable
    public Bitmap getThumbnailBitmap() {
        return thumbnailBitmap;
    }

    public void setPictureBitmap(@Nullable Bitmap bitmap) {
        this.pictureBitmap = bitmap;
    }

    public void setThumbnailBitmap(@Nullable Bitmap bitmap) {
        this.thumbnailBitmap = bitmap;
    }

    @Override
    public int compareTo(@NonNull Contact another) {
        return name.compareTo(another.getName());
    }

    @NonNull
    public Contact copy() {
        return new Contact.Builder()
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
        private String phoneNumber;
        private String emailAddress;
        private String type;
        private String pictureUri;
        private String thumbnailUri;

        public Builder() {}

        @NonNull
        public Builder setName(@Nullable String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder setPhoneNumber(@NonNull String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        @NonNull
        public Builder setEmailAddress(@Nullable String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        @NonNull
        public Builder setType(@Nullable String type) {
            this.type = type;
            return this;
        }

        @NonNull
        public Builder setPictureUri(@Nullable String pictureUri) {
            this.pictureUri = pictureUri;
            return this;
        }

        @NonNull
        public Builder setThumbnailUri(@Nullable String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
            return this;
        }

        @NonNull
        public Contact build() {
            Contact tmp = new Contact();
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
