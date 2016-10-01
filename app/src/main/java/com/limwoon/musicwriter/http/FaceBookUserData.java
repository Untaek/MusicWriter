package com.limwoon.musicwriter.http;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 운택 on 2016-09-24.
 */

public class FaceBookUserData {

    Context context;

    public FaceBookUserData(Context context){
        this.context = context;
    }


    public void setUserData(final AccessToken token){
        GraphRequest request = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("jsonobject", object+"");
                        Log.d("response", response+"");
                        try {
                            String lastName = object.getString("last_name");
                            String firstName = object.getString("first_name");
                            String email = object.getString("email");
                            String picture_url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            PUBLIC_APP_DATA.setIsLogin(true);
                            PUBLIC_APP_DATA.setFbToken(token);
                            PUBLIC_APP_DATA.setUserData(object.toString());
                            PUBLIC_APP_DATA.setUserStrID(firstName + lastName);
                            PUBLIC_APP_DATA.setUserEmail(email);
                            PUBLIC_APP_DATA.setPictureURL(picture_url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

}
