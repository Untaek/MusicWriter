package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-10-07.
 */

public class LoadSharedSheetList extends AsyncTask<Integer, Void, Integer> {

    ArrayList<SheetData> list;
    SharedSheetRecyclerAdapter adapter;
    String query = "'";
    TextView textView_result;
    ListStateListener mListStateListener;

    public LoadSharedSheetList(ArrayList<SheetData> list, SharedSheetRecyclerAdapter adapter, String query, TextView result) {
        this.list = list;
        this.adapter = adapter;
        this.query = query;
        this.textView_result = result;
    }

    public LoadSharedSheetList(ArrayList<SheetData> list, SharedSheetRecyclerAdapter adapter){
        this.list = list;
        this.adapter = adapter;
    }

    public LoadSharedSheetList setListStateListener(ListStateListener listener){
        this.mListStateListener=listener;
        return this;
    }

    @Override
    protected Integer doInBackground(Integer... pages) {
        int page = pages[0];
        // 페이징

        int sort = pages[1];
        // 0: 최신, 1: 좋아요

        int fav = pages[2];
        // 0: 기본, 1: 내 악보

        long userID = 0;
        if(fav==1){
            userID=PUBLIC_APP_DATA.getUserID();
        }
        // 게시된 악보 불러오기를 위함

        try {
            String message = "page="+page+"&sort="+sort+"&userID="+userID+"&query="+query+"&me="+PUBLIC_APP_DATA.getUserID();
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

            String json = reader.readLine();

            JSONObject jsonResponse = new JSONObject(json);
            Log.d(TAG, "doInBackground: "+ jsonResponse);

            String today = jsonResponse.getString("today");
            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            JSONArray favoriteJSON=null;
            if(PUBLIC_APP_DATA.getUserID()!=0){
                String favoriteList = jsonResponse.getJSONObject("mydata").getString("favorite_music");
                favoriteJSON = new JSONArray(favoriteList);
            }

            Log.d(TAG, "doInBackground fav : "+favoriteJSON);

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
                sheetData.setUploadUserID(uploadUserID);
                sheetData.setComments(comments);
                sheetData.setLikes(likes);
                sheetData.setTempo(tempo);

                if(favoriteJSON!=null){
                    for(int j=0; j<favoriteJSON.length(); j++){
                        if(favoriteJSON.getLong(j) == id){
                            sheetData.setIsFavorite(true);
                        }
                    }
                }

                list.add(sheetData);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        SharedListPagerFragment.listLoading = false;
        adapter.notifyDataSetChanged();
        if(mListStateListener!=null){
            mListStateListener.onLoaded();
        }

        if(textView_result!=null){
            if(list.size()>0) {
                textView_result.setText("검색 결과");
            }
            else{
                textView_result.setText("검색된 결과가 없습니다");
            }
        }
    }
}
