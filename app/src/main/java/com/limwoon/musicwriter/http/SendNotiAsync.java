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
 * Created by 운택 on 2016-10-15.
 */

public class SendNotiAsync extends AsyncTask<Long, Void, String> {
    @Override
    protected String doInBackground(Long... id) {
        try {
            long userID = id[0];
            long sheetID = id[1];

            Log.d("sheetID", "doInBackground: "+sheetID);
            String message = "userID="+userID + "&sheetID=" + sheetID;
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"fcmmessaging.php");
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
                Log.d("send_noti", "doInBackground: "+line);
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
