package com.limwoon.musicwriter.http.account;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by 운택 on 2016-09-30.
 */

public class ChangePwAsync extends AsyncTask<String, Void, Integer> {
    String pw;
    String currentPw;

    HttpURLConnection urlConnection;
    InputStream is;
    OutputStream os;

    Context context;
            
    public ChangePwAsync(Context context){
        this.context=context;
    }
    
    @Override
    protected Integer doInBackground(String... str) {
        pw=str[0];
        currentPw=str[1];

        Log.d(TAG, "doInBackground: "+PUBLIC_APP_DATA.getUserID());
        String message = "id="+ PUBLIC_APP_DATA.getUserStrID() + "&pw="+pw + "&cpw="+currentPw;
        int result = -1;

        try {
            URL url = new URL("http://115.71.236.157/changepw.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            os = urlConnection.getOutputStream();
            os.write(message.getBytes());
            os.close();

            is = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);

            result = Integer.parseInt(bufferedReader.readLine());

/*
            while(true){
                String line = bufferedReader.readLine();
                Log.d(TAG, "doInBackground: "+ line);
                if(line==null) break;
            }
*/

            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch(result){
            case 1:
                Toast.makeText(context, "비밀번호 변경 성공", Toast.LENGTH_SHORT).show();
                break;
            case 10:
                Toast.makeText(context, "현재 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                break;
            case 11:
                Toast.makeText(context, "정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "알 수 없는 오류", Toast.LENGTH_SHORT).show();
                break;
        }
        
        super.onPostExecute(result);
    }
}
