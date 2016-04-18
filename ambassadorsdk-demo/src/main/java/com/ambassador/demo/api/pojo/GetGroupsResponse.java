package com.ambassador.demo.api.pojo;

public class GetGroupsResponse {

    public GroupResponse[] results;

    public static class GroupResponse {

        public String uid;
        public String group_name;

    }

}
