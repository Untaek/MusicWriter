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
 * Created by 운택 on 2016-10-22.
 */

public class DeleteCommentAsync extends AsyncTask<Long, Void, Integer> {

    public interface DeleteCommentCallback{
        void onResult(int result);
    }

    private DeleteCommentCallback callback;

    public DeleteCommentAsync setDeleteCommentCallback(DeleteCommentCallback callback){
        this.callback = callback;
        return this;
    }

    @Override
    protected Integer doInBackground(Long... params) {
        int result = 10;
        try {
            String message = "commentid="+params[0];
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"deletecomment.php");
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
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result==1){
            callback.onResult(result);
        }
        super.onPostExecute(result);
    }
}
