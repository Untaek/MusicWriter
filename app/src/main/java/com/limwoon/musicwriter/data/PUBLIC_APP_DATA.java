package com.limwoon.musicwriter.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.limwoon.musicwriter.NativeClass;
import com.limwoon.musicwriter.draw.NoteRestExam;
import com.limwoon.musicwriter.http.FaceBookUserData;
import com.limwoon.musicwriter.sounds.Sounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

/**
 * Created by ejdej on 2016-09-21.
 */
public class PUBLIC_APP_DATA extends Application {

    static private String userToken;
    static private String userData;
    static private long userID;
    static private String userStrID;
    static private String userEmail;
    static private boolean isLogin;
    static private AccessToken fbToken;
    static private String pictureURL;
    static private String imageName;
    static private Bitmap userPicBitmap;
    static private boolean isFacebook = false;
    public static String serverUrl = "http://115.71.236.157/";
    static private String userFacebookPicUrl;

    static public AssetManager assetManager;

    public static String getUserFacebookPicUrl() {
        return userFacebookPicUrl;
    }

    public static void setUserFacebookPicUrl(String userFacebookPicUrl) {
        PUBLIC_APP_DATA.userFacebookPicUrl = userFacebookPicUrl;
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static void setServerUrl(String serverUrl) {
        PUBLIC_APP_DATA.serverUrl = serverUrl;
    }

    public static boolean isFacebook() {
        return isFacebook;
    }

    public static void setIsFacebook(boolean isFacebook) {
        PUBLIC_APP_DATA.isFacebook = isFacebook;
    }

    public static Bitmap getUserPicBitmap() {
        return userPicBitmap;
    }

    public static void setUserPicBitmap(Bitmap userPicBitmap) {
        PUBLIC_APP_DATA.userPicBitmap = userPicBitmap;
    }

    public static String getImageName() {
        return imageName;
    }

    public static void setImageName(String imageName){
        PUBLIC_APP_DATA.imageName = "user_" + imageName + "_pic.jpg";
    }

    public static String getPictureURL() {
        return pictureURL;
    }

    public static void setPictureURL(String pictureURL) {
        PUBLIC_APP_DATA.pictureURL = pictureURL;
    }

    public static void logout(){
        userToken = null;
        userData = null;
        userID = 0;
        userStrID = null;
        userEmail = null;
        pictureURL = null;
        isLogin = false;
    }

    public static AccessToken getFbToken() {
        return fbToken;
    }

    public static void setFbToken(AccessToken fbToken) {
        PUBLIC_APP_DATA.fbToken = fbToken;
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

    public static long getUserID() {
        return userID;
    }

    public static void setUserID(long userID) {
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
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Sounds sounds = new Sounds();
        sounds.loadSound(this);
        NativeClass nativeClass = new NativeClass();
        assetManager = getAssets();
        NoteRestExam noteRestExam = new NoteRestExam(getApplicationContext());
    }
}
