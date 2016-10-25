package com.limwoon.musicwriter.http.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.limwoon.musicwriter.FindPwActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-09-28.
 */

public class FindPwAsync extends AsyncTask<String, Void, Integer> {
    String id;
    String email;

    HttpURLConnection urlConnection;
    InputStream is;
    OutputStream os;

    Context context;

    ProgressDialog dialog;
    public FindPwAsync(Context context){
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("이메일을 보내는 중입니다..");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... str) {
        id=str[0];
        email=str[1];
        String message = "id="+id + "&email="+email;
        int result = -1;

        try {
            URL url = new URL("http://115.71.236.157/findpw.php");
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
    protected void onProgressUpdate(Void... values) {

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer result) {
        dialog.dismiss();
        if(result == 10){
            Toast.makeText(context, "입력한 아이디와 이메일 맞지 않음", Toast.LENGTH_SHORT).show();
        }

        switch (result){
            case 1:
                Toast.makeText(context, "성공", Toast.LENGTH_SHORT).show();
                break;
            case 10:
                Toast.makeText(context, "입력한 아이디와 이메일 맞지 않음", Toast.LENGTH_SHORT).show();
                break;
            case 20:
                Toast.makeText(context, "이메일 전송 실패", Toast.LENGTH_SHORT).show();
                break;
            case 30:
                Toast.makeText(context, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "알수 없는 오류" + result, Toast.LENGTH_SHORT).show();
                break;
        }
        super.onPostExecute(result);
    }
}
