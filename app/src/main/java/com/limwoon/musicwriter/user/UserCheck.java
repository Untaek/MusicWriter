package com.limwoon.musicwriter.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.http.FaceBookUserData;
import com.limwoon.musicwriter.http.FacebookCheckSignIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by 운택 on 2016-10-03.
 */

public class UserCheck {
    Context context;

    public UserCheck(Context context){
        this.context=context;
    }

    public boolean checkIsLogin(){
        String token;
        String token_f;
        SharedPreferences sp = context.getSharedPreferences("al", Context.MODE_PRIVATE);
        token = sp.getString("jwt", null);
        token_f = sp.getString("ft", null);

        if(token != null){
            try {
                String jwtClaim = token.split("\\.")[1];
                String decodedJwtClaim = new String(Base64.decode(jwtClaim, Base64.NO_WRAP), "UTF-8");
                JSONObject decodedJwtClaimJSON = new JSONObject(decodedJwtClaim);
                long userID = decodedJwtClaimJSON.getLong("userID");
                String userStrID = decodedJwtClaimJSON.getString("userStrID");
                String userEmail = decodedJwtClaimJSON.getString("userEmail");
                String userPicUrl = decodedJwtClaimJSON.getString("userPic_url");
                int enablePush = decodedJwtClaimJSON.getInt("push");

                PUBLIC_APP_DATA.setUserToken(token);
                PUBLIC_APP_DATA.setUserData(decodedJwtClaim);
                PUBLIC_APP_DATA.setUserID(userID);
                PUBLIC_APP_DATA.setUserStrID(userStrID);
                PUBLIC_APP_DATA.setUserEmail(userEmail);
                PUBLIC_APP_DATA.setPictureURL(userPicUrl);
                PUBLIC_APP_DATA.setIsLogin(true);
                PUBLIC_APP_DATA.setIsFacebook(false);
                PUBLIC_APP_DATA.setImageName(String.valueOf(userID));
                PUBLIC_APP_DATA.setEnablePush(enablePush);

                return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(token_f != null){
            final AccessToken accessToken = AccessToken.getCurrentAccessToken();
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                String token = accessToken.getToken();
                                long id = object.getLong("id");
                                new FacebookCheckSignIn().execute(id);
                                String lastName = object.getString("last_name");
                                String firstName = object.getString("first_name");
                                String email = object.getString("email");
                                String pictureURL = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                PUBLIC_APP_DATA.setIsLogin(true);
                                PUBLIC_APP_DATA.setUserID(id);
                                PUBLIC_APP_DATA.setUserToken(token);
                                PUBLIC_APP_DATA.setUserData(object.toString());
                                PUBLIC_APP_DATA.setUserStrID(firstName + lastName);
                                PUBLIC_APP_DATA.setUserEmail(email);
                                PUBLIC_APP_DATA.setPictureURL(pictureURL);
                                PUBLIC_APP_DATA.setImageName(String.valueOf(id));
                                PUBLIC_APP_DATA.setIsFacebook(true);
                                PUBLIC_APP_DATA.setUserFacebookPicUrl(pictureURL);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, picture.width(200).height(200)");
            request.setParameters(parameters);
            request.executeAsync();
            return true;
        }

        return false;
    }
    public boolean saveTokenToLocal(String tokenName, String token){
        if(!tokenName.equals("jwt") || !tokenName.equals("ft")){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences("al", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(tokenName, token);
        editor.apply();

        return true;
    }
    public boolean loadFacebookUserDataFromDB(){

        return false;
    }
    public boolean loadLocalToken(){

        return false;
    }
    public boolean loadDBToken(){

        return false;
    }
    public boolean loadDBFacebookToken(){

        return false;
    }

    public boolean logout(){
        PUBLIC_APP_DATA.setUserID(0);
        PUBLIC_APP_DATA.setUserToken(null);
        PUBLIC_APP_DATA.setUserPicBitmap(null);
        PUBLIC_APP_DATA.setUserStrID(null);
        PUBLIC_APP_DATA.setPictureURL(null);
        PUBLIC_APP_DATA.setIsLogin(false);
        PUBLIC_APP_DATA.setUserData(null);
        PUBLIC_APP_DATA.setFbToken(null);
        PUBLIC_APP_DATA.setUserEmail(null);
        PUBLIC_APP_DATA.setIsFacebook(false);
        PUBLIC_APP_DATA.setImageName(null);
        LoginManager.getInstance().logOut();

        SharedPreferences sp = context.getSharedPreferences("al", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();

        return true;
    }

}
