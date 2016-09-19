package com.limwoon.musicwriter.http;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 운택 on 2016-09-18.
 */
public class LoginAsync extends AsyncTask<Bundle, Void, Integer> {

    Context context;

    HttpURLConnection httpConn;
    InputStream is;
    OutputStream os;
    BufferedReader bufferedReader;

    String id;
    String pw;

    public LoginAsync(Context context){
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Bundle... bundles) {
        int result = 0;

        try {
            Bundle bundle = bundles[0];
            id = bundle.getString("id");
            pw = bundle.getString("pw");
            String message = "id="+ id + "&pw=" + pw;

            URL url = new URL("http://115.71.236.157/login.php");
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            os = httpConn.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpConn.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            result = Integer.parseInt(bufferedReader.readLine());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpConn.disconnect();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result==1){
            Toast.makeText(context, "로그인 완료", Toast.LENGTH_SHORT).show();
            ((Activity)context).finish();
        }else if(result==10){
            Toast.makeText(context, "아이디나 비밀번호가 틀립니다", Toast.LENGTH_SHORT).show();
        }

        super.onPostExecute(result);
    }
}
