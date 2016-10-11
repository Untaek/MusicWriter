package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.limwoon.musicwriter.data.CommentData;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
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
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by ejdej on 2016-10-10.
 */

public class LoadComments extends AsyncTask<Long, Void, Integer> {
    ArrayList<CommentData> commentList;
    CommentRecyclerAdapter commentRecyclerAdapter;
    TextView textView_count;

    public LoadComments(ArrayList<CommentData> commentList, CommentRecyclerAdapter commentRecyclerAdapter, @Nullable TextView textView_count) {
        this.commentList = commentList;
        this.commentRecyclerAdapter = commentRecyclerAdapter;
        this.textView_count = textView_count;
    }

    @Override
    protected Integer doInBackground(Long... sheetIdAndPage) {
        long sheetIDa = sheetIdAndPage[0];
        long page = 0;

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

            JSONArray jsonArray = jsonObject.getJSONArray("comments");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                long commentID = object.getLong("commentID");
                long userID = object.getLong("userID");
                long sheetID = object.getLong("sheetID");
                String uploadTime = object.getString("uploadTime");
                String commentText = object.getString("commentText");

                CommentData commentData = new CommentData();
                commentData.setCommentID(commentID);
                commentData.setUserID(userID);
                commentData.setSheetID(sheetID);
                commentData.setUploadTime(uploadTime);
                commentData.setComment(commentText);

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
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        commentRecyclerAdapter.notifyDataSetChanged();
        if(textView_count!=null){
            textView_count.setText("댓글 ("+commentList.size()+")");
        }
        super.onPostExecute(integer);
    }
}
