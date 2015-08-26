package com.example.ambassador.ambassadorsdk;

/**
 * Created by JakeDunahee on 8/21/15.
 */

// Exception thrown for incorrect conversoinParameters
public class ConversionParametersException extends Exception {
    public ConversionParametersException() {
        super("Conversion parameters must have set values for 'mbsy_revenue," +
                "'mbsy_campaign', and 'mbsy_email.");
    }
}
