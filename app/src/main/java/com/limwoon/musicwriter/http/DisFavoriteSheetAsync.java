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

/**
 * Created by ejdej on 2016-10-10.
 */

public class DisFavoriteSheetAsync extends AsyncTask<Long, Void, Void> {

    @Override
    protected Void doInBackground(Long... sheetIds) {

        try {
            String message = "id="+ PUBLIC_APP_DATA.getUserID()+ "&sheetid="+sheetIds[0];
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"disfavoritemusic.php");
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
                Log.d("likess", "doInBackground: "+ line);
                if(line==null) break;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
