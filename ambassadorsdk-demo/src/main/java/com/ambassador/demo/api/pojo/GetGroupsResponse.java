package com.ambassador.demo.api.pojo;

public class GetGroupsResponse {

    public GroupResponse[] results;

    public static class GroupResponse {

        public String group_name;
        public String group_id;

    }

}
