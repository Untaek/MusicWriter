package com.limwoon.musicwriter.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.limwoon.musicwriter.MainNavActivity;
import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.SharedMusicViewActivity;
import com.limwoon.musicwriter.SharedSheetActivity;
import com.limwoon.musicwriter.SplashActivity;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-10-15.
 */

public class fcm extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();
        Log.d(TAG, "onMessageReceived: " + data);
        noti(data);
        super.onMessageReceived(message);
    }

    public void noti(Map<String, String> data) {

        try {
            String message = "sheetID=" + data.get("sheetID");
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl() + "loadsharedsheet.php");
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

            String json = reader.readLine();

            JSONObject jsonObject = new JSONObject(json);

            if(jsonObject.getInt("push")==0) return;

            String title = jsonObject.getString("title");
            String author = jsonObject.getString("author");
            String note = jsonObject.getString("note");
            String uploadTime = jsonObject.getString("uploadtime");
            long uploadUserID = jsonObject.getLong("uploadUserID");
            long comments = jsonObject.getLong("comments");
            long likes = jsonObject.getLong("likes");
            long id = jsonObject.getLong("sheetID");
            int tempo = jsonObject.getInt("tempo");

            note = note.substring(1, note.length() - 1);

            SheetData sheetData = new SheetData();
            sheetData.setId(id);
            sheetData.setTitle(title);
            sheetData.setAuthor(author);
            sheetData.setNote(note);
            sheetData.setUploadTime(uploadTime);
            sheetData.setUploadUserID(uploadUserID);
            sheetData.setComments(comments);
            sheetData.setLikes(likes);
            sheetData.setTempo(tempo);

            Intent resultIntent = new Intent(this, SharedMusicViewActivity.class);
            resultIntent.putExtra("data", sheetData);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainNavActivity.class);
            stackBuilder.addNextIntent(new Intent(getApplicationContext(), MainNavActivity.class));
            stackBuilder.addNextIntent(new Intent(getApplicationContext(), SharedSheetActivity.class));
            stackBuilder.addNextIntent(resultIntent);
            if(!PUBLIC_APP_DATA.isLoaded()){
                stackBuilder.addNextIntent(new Intent(getApplicationContext(), SplashActivity.class));
            }
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification noti = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(data.get("title"))
                    .setContentText(data.get("text"))
                    .setSmallIcon(R.drawable.logo_soft)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();


            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, noti);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
