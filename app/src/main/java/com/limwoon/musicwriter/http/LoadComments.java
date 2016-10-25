package com.limwoon.musicwriter.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.limwoon.musicwriter.SharedMusicViewActivity;
import com.limwoon.musicwriter.data.CommentData;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.image.UserPicture;
import com.limwoon.musicwriter.list.CommentRecyclerAdapter;

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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
 * Created by ejdej on 2016-10-10.
 */

public class LoadComments extends AsyncTask<Long, Void, Integer> {
    ArrayList<CommentData> commentList;
    CommentRecyclerAdapter commentRecyclerAdapter;
    TextView textView_count;
    Context context;
    String count;
    CommentStateCallback callback;

    public interface CommentStateCallback{
        void loadCompleted(int num);
    }

    public LoadComments setCommentStateCallback(CommentStateCallback callback){
        this.callback = callback;
        return this;
    }

    public LoadComments(ArrayList<CommentData> commentList, CommentRecyclerAdapter commentRecyclerAdapter, @Nullable TextView textView_count, Context context) {
        this.commentList = commentList;
        this.commentRecyclerAdapter = commentRecyclerAdapter;
        this.textView_count = textView_count;
        this.context = context;
    }
    long sheetIDa;
    long page;
    JSONArray jsonArray;

    @Override
    protected Integer doInBackground(Long... sheetIdAndPage) {
        sheetIDa = sheetIdAndPage[0];
        page = sheetIdAndPage[1];

        String message = "sheetID="+sheetIDa+"&page="+page;
        try {
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl()+"loadcomments.php");
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
            count = jsonObject.getString("count");
            String today = jsonObject.getString("today");
            jsonArray = jsonObject.getJSONArray("comments");
            Log.d(TAG, "doInBackground: "+ jsonArray);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            Date date_today =  dateFormat.parse(today);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                long commentID = object.getLong("commentID");
                long userID = object.getLong("userID");
                long sheetID = object.getLong("sheetID");
                String userStrID = object.getString("userStrID");
                String uploadTime = object.getString("uploadTime");
                String commentText = object.getString("commentText");
                String userPicUrl = object.getString("userPic_url");

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

                Log.d("date_diff", "doInBackground: "+time);

                CommentData commentData = new CommentData();
                commentData.setCommentID(commentID);
                commentData.setUserID(userID);
                commentData.setSheetID(sheetID);
                commentData.setUploadTime(timeStr);
                commentData.setComment(commentText);
                commentData.setUserStrID(userStrID);
                commentData.setUserPicUrl(userPicUrl);

                commentList.add(commentData);
            }

            Log.d(TAG, "doInBackground: " + jsonObject);
            /*
            while(true){
                String line = reader.readLine();
                Log.d("comments", "doInBackground: "+ line);
                if(line==null) break;
            }*/

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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {

        SharedMusicViewActivity.commentLoading = false;
        if(textView_count!=null){
            textView_count.setText("댓글 ("+count+")");
        }

        UserPicture userPicture = new UserPicture(context);
        for(int i=(int)page*7; i<commentList.size(); i++){
            Bitmap pic = userPicture.getUserPicBitmapFromCache("user_" + commentList.get(i).getUserID() + "_pic.jpg");
            if(!userPicture.isNotFound() || commentList.get(i).getUserPicUrl().equals("0")){
                commentList.get(i).setUserPicture(pic);
            }
            else{
                new GetCommentUserPicAsync(context, commentList, i, commentRecyclerAdapter).execute(commentList.get(i).getUserPicUrl());
            }
        }

        commentRecyclerAdapter.notifyDataSetChanged();
        if(this.callback!=null){
            callback.loadCompleted(jsonArray.length());
        }
        super.onPostExecute(integer);
    }
}
