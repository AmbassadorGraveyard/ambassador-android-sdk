package com.example.ambassador.ambassadorsdk;


/**
 * Created by JakeDunahee on 7/31/15.
 */
class ContactObject {
    public String name;
    public String phoneNumber;
    public String emailAddress;
    public String type;

    public ContactObject() {
        name = "No name available";
        phoneNumber = "No number available";
        emailAddress = "Email not available";
        type = "Not available";
    }

    public ContactObject(String name, String type, String phoneNumber) {
        this.name = name;
        this.type = type;
        this.phoneNumber = phoneNumber;
    }

    public ContactObject(String name, String emailAddress) {
        this.name = name;
        this.emailAddress = emailAddress;
    }
}
