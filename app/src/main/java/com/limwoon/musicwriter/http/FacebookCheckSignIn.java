package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
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

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-10-01.
 */

public class FacebookCheckSignIn extends AsyncTask<Long, Void, Integer> {
    @Override
    protected Integer doInBackground(Long... ids) {
        long id = ids[0];
        String message = "id="+ id;
        int result = -1;
        try {
            URL url = new URL("http://115.71.236.157/checkfacebookuser.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = connection.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isReader);

            while(true){
                String line = reader.readLine();
                Log.d(TAG, "doInBackground: "+ line);
                if(line==null) break;
            }


            result = 1;

            String picUrl = reader.readLine();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
