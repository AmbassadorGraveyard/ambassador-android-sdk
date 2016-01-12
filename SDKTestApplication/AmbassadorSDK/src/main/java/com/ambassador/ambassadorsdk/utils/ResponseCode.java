package com.ambassador.ambassadorsdk.utils;

public final class ResponseCode {

    private int responseCode;

    private ResponseCode() {}

    public ResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isSuccessful() {
        return (this.responseCode >= 200 && this.responseCode < 300);
    }

}
