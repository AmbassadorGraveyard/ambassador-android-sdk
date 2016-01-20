package com.ambassador.ambassadorsdk.internal.utils;

public final class ResponseCode {

    private int responseCode;

    @SuppressWarnings("unused")
    private ResponseCode() {}

    public ResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isSuccessful() {
        return (this.responseCode >= 200 && this.responseCode < 300);
    }

}
