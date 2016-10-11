package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.os.Bundle;
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
 * Created by ejdej on 2016-10-11.
 */

public class WriteCommentAsync extends AsyncTask<Bundle, Void, Integer> {
    @Override
    protected Integer doInBackground(Bundle... bundles) {
        Bundle bundle = bundles[0];
        String comment = bundle.getString("comment");
        long userID = bundle.getLong("userID");
        long sheetID = bundle.getLong("sheetID");
        int result=11;

        String message = "comment=" + comment + "&userID=" + userID + "&sheetID=" + sheetID;
        try {
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl() + "writecomment.php");
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

            while (true) {
                String line = reader.readLine();
                Log.d("comment write", "doInBackground: " + line);
                if (line == null) break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
