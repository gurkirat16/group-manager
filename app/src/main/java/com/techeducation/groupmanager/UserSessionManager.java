package com.techeducation.groupmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by mitaly on 22/3/17.
 */

public class UserSessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    private static final String SHARED_PREF_NAME = "loginSharedPref";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ACCESS = "access";
    public static final String KEY_USER_ID = "user_id";

    public UserSessionManager(Context context){
        this.context=context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }

    public void createLoginSession(String email,String password,int access,int user_id){
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_PASSWORD,password);
        editor.putInt(KEY_ACCESS,access);
        editor.putInt(KEY_USER_ID,user_id);
        editor.commit();

        StartActivity.access = access;
        StartActivity.user_id = user_id;
    }


    public HashMap<String, String> getUserDetails(){

        //Use hashmap to store user credentials
        HashMap user = new HashMap();

        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));

        user.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD, null));

        user.put(KEY_ACCESS, sharedPreferences.getInt(KEY_ACCESS,0));

        user.put(KEY_USER_ID, sharedPreferences.getInt(KEY_USER_ID,0));

        // return user
        return user;
    }

    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        context.startActivity(i);
    }




}
