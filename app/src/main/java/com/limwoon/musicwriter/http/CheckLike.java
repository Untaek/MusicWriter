package com.limwoon.musicwriter.http;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.limwoon.musicwriter.SharedMusicViewActivity;
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
 * Created by ejdej on 2016-10-10.
 */

public class CheckLike extends AsyncTask<Long, Void, Integer> {

    View button;
    boolean state;

    public CheckLike(View button, boolean state){
        this.button=button;
        this.state=state;
    }

    @Override
    protected Integer doInBackground(Long... sheetIDs) {
        long userID = PUBLIC_APP_DATA.getUserID();
        long sheetID = sheetIDs[0];
        String message = "userID="+userID + "&sheetID="+sheetID;
        int result = 0;

        try {
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"checklikestate.php");
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
            Log.d("checklike", "doInBackground: "+result);

/*            while(true){
                String line = reader.readLine();
                Log.d("checklike", "doInBackground: "+line);
                if(line==null) break;
            }
*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        if(result==2){
            ((Button)button).setText("추천했습니다");
            SharedMusicViewActivity.userLikeState=false;
        }else if(result==1){
            ((Button)button).setText("추천");
            SharedMusicViewActivity.userLikeState=true;
        }
    }
}
