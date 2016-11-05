package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.list.SharedSheetRecyclerAdapter;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by ejdej on 2016-10-12.
 */

public class LoadFavoriteListAsync extends AsyncTask<Bundle, Void, Integer> {

    ArrayList<SheetData> list;
    SharedSheetRecyclerAdapter adapter;
    ListStateListener mListStateListener;

    public LoadFavoriteListAsync(ArrayList<SheetData> list, SharedSheetRecyclerAdapter adapter) {
        this.list = list;
        this.adapter = adapter;
    }

    public LoadFavoriteListAsync setListStateListener(ListStateListener listener){
        this.mListStateListener=listener;
        return this;
    }

    @Override
    protected Integer doInBackground(Bundle... bundles) {
        long userId = PUBLIC_APP_DATA.getUserID();
        String message = "id="+userId;

        try {
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"loadfavorites.php");
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
            JSONObject jsonResponse = new JSONObject(json);
            Log.d(TAG, "doInBackground: "+ jsonResponse);

            String today = jsonResponse.getString("today");
            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            Date date_today =  dateFormat.parse(today);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String author = jsonObject.getString("author");
                String note = jsonObject.getString("note");
                String uploadTime = jsonObject.getString("uploadtime");
                long uploadUserID = jsonObject.getLong("uploadUserID");
                long comments = jsonObject.getLong("comments");
                long likes = jsonObject.getLong("likes");
                long id = jsonObject.getLong("sheetID");
                int tempo = jsonObject.getInt("tempo");
                Log.d(TAG, "doInBackground: "+title);

                note = note.substring(1, note.length()-1);

                Date date_comment = dateFormat.parse(uploadTime);
                Log.d("date_today", "doInBackground: "+ date_today.getTime());
                Log.d("date_today", "doInBackground: "+ date_today);
                Log.d("date_comment", "doInBackground: "+date_comment.getTime());
                Log.d("date_comment", "doInBackground: "+date_comment);
                long todayMill = date_today.getTime();
                long uploadMill = date_comment.getTime();
                long time = (todayMill - uploadMill)/1000;
                String timeStr = null;
                if(time < 60){
                    timeStr = "방금 전";
                }else if(time <60*60){
                    timeStr = time/60 + "분 전";
                }else if(time < 60*60*24){
                    timeStr = time/(60*60) + "시간 전";
                }else if(time < 60*60*24*30){
                    timeStr = time/(60*60*24) + "일 전";
                }

                SheetData sheetData = new SheetData();
                sheetData.setId(id);
                sheetData.setTitle(title);
                sheetData.setAuthor(author);
                sheetData.setNote(note);
                sheetData.setUploadTime(timeStr);
                //sheetData.setUploadUserStrID(uploadUserID);
                sheetData.setUploadUserID(uploadUserID);
                sheetData.setComments(comments);
                sheetData.setLikes(likes);
                sheetData.setTempo(tempo);
                sheetData.setIsFavorite(true);
                list.add(sheetData);
            }

        return null;
    } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        adapter.notifyDataSetChanged();
        if(mListStateListener!=null){
            mListStateListener.onLoaded();
        }
    }
}
