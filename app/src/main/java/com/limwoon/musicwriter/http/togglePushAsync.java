package com.limwoon.musicwriter.http;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by 운택 on 2016-10-15.
 */

public class TogglePushAsync extends AsyncTask<Integer, Void, Integer> {
    @Override
    protected Integer doInBackground(Integer... state) {
        String message = "userID="+ PUBLIC_APP_DATA.getUserID() + "&state=" + state[0];

        try {
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"togglepush.php");
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

            while(true){
                String line = reader.readLine();
                Log.d("push_toggle", "doInBackground: "+line);
                if(line==null) break;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
