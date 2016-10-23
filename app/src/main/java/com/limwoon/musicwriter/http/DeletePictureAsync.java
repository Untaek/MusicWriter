package com.limwoon.musicwriter.http;

import android.os.AsyncTask;

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
 * Created by 운택 on 2016-10-23.
 */

public class DeletePictureAsync extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... params) {
        int result = 10;
        try {
            String message = "userid="+PUBLIC_APP_DATA.getUserID();
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"deletepicture.php");
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
            result = Integer.parseInt(reader.readLine());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
