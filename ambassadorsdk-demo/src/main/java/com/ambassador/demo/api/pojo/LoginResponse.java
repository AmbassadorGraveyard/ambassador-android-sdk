package com.ambassador.demo.api.pojo;

public class LoginResponse {

    public String token;
    public Company company;

    public static class Company {

        public int uid;
        public String email;
        public String first_name;
        public String last_name;
        public String avatar_url;
        public String universal_id;
        public String universal_token;
        public String sdk_token;

    }

}
