package com.ambassador.ambassadorsdk;

/**
 * Created by JakeDunahee on 7/31/15.
 */
class ContactObject implements Comparable<ContactObject> {

    private String name;
    private String pictureUri;
    private String phoneNumber;
    private String emailAddress;
    private String type;

    public ContactObject() {
        name = "No name available";
        phoneNumber = "No number available";
        emailAddress = "Email not available";
        type = "Not available";
    }

    public ContactObject(String name, String pictureUri, String emailAddress) {
        this.name = name;
        this.pictureUri = pictureUri;
        this.emailAddress = emailAddress;
    }

    public ContactObject(String name, String pictureUri, String type, String phoneNumber) {
        this.name = name;
        this.pictureUri = pictureUri;
        this.type = type;
        this.phoneNumber = phoneNumber;
    }

    public ContactObject(String name, String pictureUri, String emailAddress, String type, String phoneNumber) {
        this.name = name;
        this.pictureUri = pictureUri;
        this.emailAddress = emailAddress;
        this.type = type;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

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

    @Override
    public int compareTo(ContactObject another) {
        return name.compareTo(another.getName());
    }

    public ContactObject clone() {
        return new ContactObject(name, pictureUri, emailAddress, type, phoneNumber);
    }

}
