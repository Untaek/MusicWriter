package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.util.Log;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-11-05.
 */

public class RefreshListItem extends AsyncTask<Long, Void, SheetData> {
    ArrayList<SheetData> list;
    ListStateListener mListStateListener;
    int index;

    public RefreshListItem(ArrayList<SheetData> list, int index) {
        this.list = list;
        this.index = index;
    }

    public RefreshListItem setOnListStateListener(ListStateListener listener){
        this.mListStateListener=listener;
        return this;
    }

    @Override
    protected SheetData doInBackground(Long... params) {

        String message = "sheetid="+ params[0] + "&userid=" + params[1];
        SheetData sheetData = null;
        try {
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"refreshlistitem.php");
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
            Log.d("refresh", "doInBackground: "+params[1]);
            Log.d("refresh", "doInBackground: "+json);
            JSONObject jsonResponse = new JSONObject(json);

            JSONArray favoriteJSON=null;
            if(PUBLIC_APP_DATA.getUserID()!=0){
                String favoriteList = jsonResponse.getJSONObject("favorite").getString("favorite_music");
                favoriteJSON = new JSONArray(favoriteList);
            }

            Log.d("refresh", "doInBackground: "+favoriteJSON);

            JSONObject jsonObject = jsonResponse.getJSONObject("list");
            String title = jsonObject.getString("title");
            String author = jsonObject.getString("author");
            String note = jsonObject.getString("note");
            String uploadTime = jsonObject.getString("uploadtime");
            long uploadUserID = jsonObject.getLong("uploadUserID");
            long comments = jsonObject.getLong("comments");
            long likes = jsonObject.getLong("likes");
            long id = jsonObject.getLong("sheetID");
            int tempo = jsonObject.getInt("tempo");

            sheetData = list.get(index);
            sheetData.setComments(comments);
            sheetData.setLikes(likes);

            if(favoriteJSON!=null){
                sheetData.setIsFavorite(false);
                for(int j=0; j<favoriteJSON.length(); j++){
                    if(favoriteJSON.getLong(j) == id){
                        sheetData.setIsFavorite(true);
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sheetData;
    }

    @Override
    protected void onPostExecute(SheetData data) {
        if(mListStateListener!=null){
            mListStateListener.onLoaded(data);
        }
        super.onPostExecute(data);
    }
}
