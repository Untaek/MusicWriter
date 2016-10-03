package com.limwoon.musicwriter.http.account;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * Created by 운택 on 2016-09-15.
 */
public class SignInAsync extends AsyncTask<Bundle, Void, Integer> {

    Context context;

    String id;
    String pw;
    String email;

    URL url = null;
    HttpURLConnection httpURLConn = null;
    InputStream is = null;
    OutputStream os = null;

    String result = null;

    public SignInAsync(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Bundle... bundles) {
        try {
            Bundle bundle = bundles[0];
            id = bundle.getString("id");
            pw = bundle.getString("pw");
            email = bundle.getString("email");

            url = new URL("http://115.71.236.157/signin.php");
            httpURLConn = (HttpURLConnection) url.openConnection();
            httpURLConn.setRequestMethod("POST");
            httpURLConn.setDoInput(true);
            httpURLConn.setDoOutput(true);
            httpURLConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String message = "id="+ id + "&pw=" + pw + "&email=" + email;
            os = httpURLConn.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpURLConn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            while (true){
                String line = reader.readLine();
                if(line == null) break;
                Log.d("response", line);
                result = line;
            }
            is.close();

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpURLConn.disconnect();
        }

        return Integer.parseInt(result);
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result==1){
            Toast.makeText(context, "가입이 완료", Toast.LENGTH_SHORT).show();
            ((Activity)context).finish();
        }else if(result==10){
            Toast.makeText(context, "아이디가 이미 존재합니다", Toast.LENGTH_SHORT).show();
        }else if(result==11){
            Toast.makeText(context, "이메일이 이미 존재합니다", Toast.LENGTH_SHORT).show();
        }

        super.onPostExecute(result);
    }
}
