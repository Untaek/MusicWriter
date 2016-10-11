package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.util.Log;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.list.SharedListPagerFragment;
import com.limwoon.musicwriter.list.SharedSheetRecyclerAdapter;

import org.json.JSONArray;
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
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-10-07.
 */

public class LoadSharedSheetList extends AsyncTask<Integer, Void, Integer> {

    ArrayList<SheetData> list;
    SharedSheetRecyclerAdapter adapter;

    public LoadSharedSheetList(ArrayList<SheetData> list, SharedSheetRecyclerAdapter adapter){
        this.list = list;
        this.adapter = adapter;
    }

    @Override
    protected Integer doInBackground(Integer... pages) {
        int page = pages[0];

        try {
            String message = "page="+page;
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"loadsharedsheetlist.php");
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
            /*
            while(true){
                String line = reader.readLine();
                Log.d(TAG, "doInBackground: "+ line);
                if(line==null) break;
            }*/
           String json = reader.readLine();

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
                list.add(sheetData);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        SharedListPagerFragment.listLoading = false;
        adapter.notifyDataSetChanged();
    }
}
