package com.limwoon.musicwriter.http;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 운택 on 2016-10-06.
 */

public class ShareSheetAsync extends AsyncTask<Bundle, Void, Integer> {

    private long id;
    private String title;
    private String author;
    private String note;
    private int tempo;

    Context context;
    
    public ShareSheetAsync(Context context){
        this.context=context;
    }
    
    @Override
    protected Integer doInBackground(Bundle... bundles) {
        id = PUBLIC_APP_DATA.getUserID();
        title = bundles[0].getString("title");
        author = bundles[0].getString("author");
        note = bundles[0].getString("note");
        tempo = bundles[0].getInt("tempo");

        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("title", title);
            json.put("author", author);
            json.put("note", note);
            json.put("tempo", tempo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON", "doInBackground: "+json);

        try {
            String message = "data="+json.toString();
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"sharesheet.php");
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
                Log.d("ECHO", "doInBackground: " + line);
                if(line==null) break;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        Toast.makeText(context, "곡이 게시 되었습니다", Toast.LENGTH_SHORT).show();
        
        super.onPostExecute(integer);
    }
}
