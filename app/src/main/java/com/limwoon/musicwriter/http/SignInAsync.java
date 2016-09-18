package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 운택 on 2016-09-15.
 */
public class SignInAsync extends AsyncTask<Bundle, Void, Void> {

    String id;
    String pw;
    String email;

    URL url = null;
    HttpURLConnection httpURLConn = null;
    InputStream is = null;
    OutputStream os = null;

    Object result = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Bundle... bundles) {
        try {
            Bundle bundle = bundles[0];
            id = bundle.getString("id");
            pw = bundle.getString("pw");
            email = bundle.getString("email");

            url = new URL("http://115.71.236.157/exam.php");
            httpURLConn = (HttpURLConnection) url.openConnection();
            httpURLConn.setRequestMethod("POST");
            httpURLConn.setDoInput(true);
            httpURLConn.setDoOutput(true);
            httpURLConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String message = "id="+ id + "&pw=" + pw + "&email=" + email;
            //is = httpURLConn.getInputStream();
            os = httpURLConn.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpURLConn.getInputStream();

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            while (true){
                String line = reader.readLine();
                if(line == null) break;
                Log.d("response", line);
            }

            is.close();
            result = builder.toString();

        }catch (MalformedURLException e){

        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpURLConn.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("httpURLCONN", ""+result);
    }
}
