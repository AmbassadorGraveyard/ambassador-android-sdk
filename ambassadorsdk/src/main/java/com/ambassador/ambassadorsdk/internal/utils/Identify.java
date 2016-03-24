package com.ambassador.ambassadorsdk.internal.utils;

import java.util.regex.Pattern;

/**
 * Utility class that takes an email address and performs validations on it.
 */
public class Identify {

    /** Regex to match nearly all possibilities of rfc2822. */
    protected static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );

    /** The stored email address passed in the constructor to use in all methods. */
    protected String emailAddress;

    @SuppressWarnings("unused")
    protected Identify() {}

    /**
     * Only available constructor. Instantiates and stores the email.
     * @param emailAddress the email address to use throughout class.
     */
    public Identify(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Compares stored email to rfc2822 standard to validate if email address.
     * @return true if valid address, false if not.
     */
    public boolean isValidEmail() {
        return emailAddress != null && EMAIL_REGEX.matcher(emailAddress).matches();
    }

}
