package com.limwoon.musicwriter.http.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ejdej on 2016-10-02.
 */

public class UpdatePictureDBAsync_FacebookUser extends AsyncTask<String, Void, Integer> {
    @Override
    protected Integer doInBackground(String... imageUrls) {
        try {
            String imageUrl = Base64.encodeToString(imageUrls[0].getBytes(), Base64.NO_WRAP);
            String message = "id="+ PUBLIC_APP_DATA.getUserID() + "&imageurl="+imageUrl;

            URL url = new URL("http://115.71.236.157/changeuserfacebookpic.php");
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
/*
            while(true){
                String line = reader.readLine();
                Log.d("result ff", "doInBackground: "+ line);
                if(line==null) break;
            }
*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
