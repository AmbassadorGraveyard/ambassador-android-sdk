package com.ambassador.demo.api.pojo;

public class LoginRequest {

    protected String email;
    protected String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
