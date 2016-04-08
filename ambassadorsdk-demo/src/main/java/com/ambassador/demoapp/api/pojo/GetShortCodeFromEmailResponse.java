package com.ambassador.demoapp.api.pojo;

public class GetShortCodeFromEmailResponse {

    public int count;
    public Result[] results;

    public static class Result {

        public String short_code;

    }

}
