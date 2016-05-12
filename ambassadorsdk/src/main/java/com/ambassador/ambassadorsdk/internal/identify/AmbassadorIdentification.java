package com.ambassador.ambassadorsdk.internal.identify;

public class AmbassadorIdentification {

    private OnChangeListener onChangeListener;

    protected String email;
    protected String firstName;
    protected String lastName;
    protected String company;
    protected String phone;
    protected String street;
    protected String city;
    protected String state;
    protected String postalCode;
    protected String country;
    protected String customLabel1;
    protected String customLabel2;
    protected String customLabel3;

    public String getEmail() {
        return email;
    }

    public AmbassadorIdentification setEmail(String email) {
        this.email = email;
        notifyChange();
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public AmbassadorIdentification setFirstName(String firstName) {
        this.firstName = firstName;
        notifyChange();
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public AmbassadorIdentification setLastName(String lastName) {
        this.lastName = lastName;
        notifyChange();
        return this;
    }

    public String getCompany() {
        return company;
    }

    public AmbassadorIdentification setCompany(String company) {
        this.company = company;
        notifyChange();
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public AmbassadorIdentification setPhone(String phone) {
        this.phone = phone;
        notifyChange();
        return this;
    }

    public String getStreet() {
        return street;
    }

    public AmbassadorIdentification setStreet(String street) {
        this.street = street;
        notifyChange();
        return this;
    }

    public String getCity() {
        return city;
    }

    public AmbassadorIdentification setCity(String city) {
        this.city = city;
        notifyChange();
        return this;
    }

    public String getState() {
        return state;
    }

    public AmbassadorIdentification setState(String state) {
        this.state = state;
        notifyChange();
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public AmbassadorIdentification setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        notifyChange();
        return this;
    }

    public String getCountry() {
        return country;
    }

    public AmbassadorIdentification setCountry(String country) {
        this.country = country;
        notifyChange();
        return this;
    }

    public String getCustomLabel1() {
        return customLabel1;
    }

    public AmbassadorIdentification setCustomLabel1(String customLabel1) {
        this.customLabel1 = customLabel1;
        notifyChange();
        return this;
    }

    public String getCustomLabel2() {
        return customLabel2;
    }

    public AmbassadorIdentification setCustomLabel2(String customLabel2) {
        this.customLabel2 = customLabel2;
        notifyChange();
        return this;
    }

    public String getCustomLabel3() {
        return customLabel3;
    }

    public AmbassadorIdentification setCustomLabel3(String customLabel3) {
        this.customLabel3 = customLabel3;
        notifyChange();
        return this;
    }

    protected void notifyChange() {
        if (this.onChangeListener != null) {
            this.onChangeListener.change();
        }
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public interface OnChangeListener {
        void change();
    }

}
