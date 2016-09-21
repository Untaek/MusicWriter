package com.limwoon.musicwriter.data;

import android.app.Application;

/**
 * Created by ejdej on 2016-09-21.
 */
public class PUBLIC_APP_DATA extends Application {

    static private String userToken;

    public static String getUserToken() {
        return userToken;
    }

    public static void setUserToken(String userToken) {
        PUBLIC_APP_DATA.userToken = userToken;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
