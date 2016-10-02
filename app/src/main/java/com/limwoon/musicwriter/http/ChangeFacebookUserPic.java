package com.limwoon.musicwriter.http;

import android.os.AsyncTask;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ejdej on 2016-10-02.
 */

public class ChangeFacebookUserPic extends AsyncTask<String, Void, Integer> {
    @Override
    protected Integer doInBackground(String... strings) {
        try {
            String message = "id="+ PUBLIC_APP_DATA.getUserID() + "&image";

            URL url = new URL("http://115.71.236.157/changeruerfacebookpic.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
