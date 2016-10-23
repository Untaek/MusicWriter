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
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by ejdej on 2016-10-12.
 */

public class LoadFavoriteListAsync extends AsyncTask<Bundle, Void, Integer> {

    ArrayList<SheetData> list;
    SharedSheetRecyclerAdapter adapter;

    public LoadFavoriteListAsync(ArrayList<SheetData> list, SharedSheetRecyclerAdapter adapter) {
        this.list = list;
        this.adapter = adapter;
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
            Log.d("favjson", "doInBackground: " + json);

            JSONArray jsonArray = new JSONArray(json);
            Log.d(TAG, "doInBackground: " + jsonArray);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String author = jsonObject.getString("author");
                String note = jsonObject.getString("note");
                String uploadTime = jsonObject.getString("uploadtime");
                String uploadUserID = jsonObject.getString("uploadUserID");
                long comments = jsonObject.getLong("comments");
                long likes = jsonObject.getLong("likes");
                long id = jsonObject.getLong("sheetID");
                int tempo = jsonObject.getInt("tempo");
                Log.d(TAG, "doInBackground: "+title);

                note = note.substring(1, note.length()-1);

                SheetData sheetData = new SheetData();
                sheetData.setId(id);
                sheetData.setTitle(title);
                sheetData.setAuthor(author);
                sheetData.setNote(note);
                sheetData.setUploadTime(uploadTime);
                sheetData.setUploadUserStrID(uploadUserID);
                sheetData.setComments(comments);
                sheetData.setLikes(likes);
                sheetData.setTempo(tempo);
                list.add(sheetData);
            }
/*
            while(true){
                String line = reader.readLine();
                Log.d("favorites", "doInBackground: "+ line);
                if(line==null) break;
            }
*/
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
        }

        return  null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        adapter.notifyDataSetChanged();
    }
}
