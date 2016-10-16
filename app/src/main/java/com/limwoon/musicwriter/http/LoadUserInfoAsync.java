package com.limwoon.musicwriter.http;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.limwoon.musicwriter.R;
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

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-10-15.
 */

public class LoadUserInfoAsync extends AsyncTask<Void, Void, String[]> {

    Activity activity;

    public LoadUserInfoAsync(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        long userID = PUBLIC_APP_DATA.getUserID();
        String[] result = new String[3];

        String message = "userID="+ userID;
        Log.d(TAG, "doInBackground: "+ userID);
        try {
            URL url = new URL("http://115.71.236.157/loaduserinfo.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = connection.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isReader);
            String json = reader.readLine();
            Log.d(TAG, "doInBackground: "+json);

            JSONObject jsonObject = new JSONObject(json);
            result[0] = String.valueOf(jsonObject.getLong("sheets"));
            result[1] = String.valueOf(jsonObject.getLong("likes"));
            result[2] = String.valueOf(jsonObject.getInt("push"));

            /*
            while(true){
                String line = reader.readLine();
                Log.d(TAG, "doInBackground: "+ line);
                if(line==null) break;
            }
            */

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(final String[] result) {
        super.onPostExecute(result);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView sheets = (TextView) activity.findViewById(R.id.textView_info_sheets);
                sheets.setText(result[0]);
                TextView likes = (TextView) activity.findViewById(R.id.textView_info_likes);
                likes.setText(result[1]);
                Switch enablePush = (Switch) activity.findViewById(R.id.switch_push);
                if(result[2]=="1"){
                    enablePush.setChecked(true);
                }else{
                    enablePush.setChecked(false);
                }
            }
        });

    }
}
