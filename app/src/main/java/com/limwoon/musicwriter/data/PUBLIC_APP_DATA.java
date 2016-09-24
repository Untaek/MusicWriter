package com.limwoon.musicwriter.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Base64;

import com.limwoon.musicwriter.sounds.Sounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ejdej on 2016-09-21.
 */
public class PUBLIC_APP_DATA extends Application {

    static private String userToken;
    static private String userData;
    static private int userID;
    static private String userStrID;
    static private String userEmail;
    static private boolean isLogin;

    public static void logout(){
        userToken = null;
        userData = null;
        userID = 0;
        userStrID = null;
        userEmail = null;
        isLogin = false;
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        PUBLIC_APP_DATA.isLogin = isLogin;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        PUBLIC_APP_DATA.userEmail = userEmail;
    }

    public static String getUserStrID() {
        return userStrID;
    }

    public static void setUserStrID(String userStrID) {
        PUBLIC_APP_DATA.userStrID = userStrID;
    }

    public static String getUserData() {
        return userData;
    }

    public static void setUserData(String userData) {
        PUBLIC_APP_DATA.userData = userData;
    }

    public static int getUserID() {
        return userID;
    }

    public static void setUserID(int userID) {
        PUBLIC_APP_DATA.userID = userID;
    }

    public static String getUserToken() {
        return userToken;
    }

    public static void setUserToken(String userToken) {
        PUBLIC_APP_DATA.userToken = userToken;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences al = getSharedPreferences("al", MODE_PRIVATE);
        String jwt = al.getString("jwt", null);

        if(jwt != null){
            try {
                String jwtClaim = jwt.split("\\.")[1];
                String decodedJwtClaim = new String(Base64.decode(jwtClaim, Base64.NO_WRAP), "UTF-8");
                JSONObject decodedJwtClaimJSON = new JSONObject(decodedJwtClaim);
                int userID = decodedJwtClaimJSON.getInt("userID");
                String userStrID = decodedJwtClaimJSON.getString("userStrID");
                String userEmail = decodedJwtClaimJSON.getString("userEmail");

                PUBLIC_APP_DATA.setUserToken(jwt);
                PUBLIC_APP_DATA.setUserData(decodedJwtClaim);
                PUBLIC_APP_DATA.setUserID(userID);
                PUBLIC_APP_DATA.setUserStrID(userStrID);
                PUBLIC_APP_DATA.setUserEmail(userEmail);
                PUBLIC_APP_DATA.setIsLogin(true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Sounds sounds = new Sounds();
        sounds.loadSound(this);
    }
}
