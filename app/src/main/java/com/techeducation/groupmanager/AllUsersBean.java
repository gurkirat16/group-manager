package com.techeducation.groupmanager;

import java.io.Serializable;

/**
 * Created by mitaly on 5/3/17.
 */

public class AllUsersBean implements Serializable{
    int user_id;
    String username;

    public AllUsersBean(int user_id, String username) {
        this.user_id = user_id;
        this.username = username;
    }

    public int getuserid() {
        return user_id;
    }

    public void setuserid(int user_id) {
        this.user_id = user_id;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "AllUsersBean{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                '}';
    }
}
