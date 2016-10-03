package com.limwoon.musicwriter.http;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.image.UserPicture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by 운택 on 2016-09-24.
 */

public class FaceBookUserData {

    Context context;

    public FaceBookUserData(Context context){
        this.context = context;
    }

    public void getUserDataFromDB(long id){
        new getDataAsync().execute(id);
    }

    private class getDataAsync extends AsyncTask<Long, Void, String>{
        @Override
        protected String doInBackground(Long... ids) {
            long id = ids[0];
            String result = null;
            try {
                String message = "id="+id;
                URL url = new URL(PUBLIC_APP_DATA.serverUrl + "getfacebookuserdata.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = connection.getOutputStream();
                os.write(message.getBytes());
                os.flush();
                os.close();

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                result = reader.readLine();

                /*
                while(true){
                    String line = reader.readLine();
                    Log.d("resultzz", "doInBackground: "+line);
                    if(line==null) break;
                }*/

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("ssssssssss", "onPostExecute: "+ s);
            if(s.equals("10")) return;
            try {
                JSONObject json = new JSONObject(s);
                String userPic_url= json.getString("userPic_url");
                PUBLIC_APP_DATA.setPictureURL(userPic_url);
                PUBLIC_APP_DATA.setImageName(String.valueOf(PUBLIC_APP_DATA.getUserID()));

                Log.d("imagename", "onPostExecute: "+ PUBLIC_APP_DATA.getImageName());
                Log.d("imageurl", "onPostExecute: "+ PUBLIC_APP_DATA.getPictureURL());

                Bitmap userPic = new LoadUserPicBitmapFromURLAsync().execute(userPic_url).get();
                new UserPicture(context).cachingImage(userPic);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            super.onPostExecute(s);
        }
    }



}
