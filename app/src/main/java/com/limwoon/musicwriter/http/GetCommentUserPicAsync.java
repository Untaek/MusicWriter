package com.limwoon.musicwriter.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.limwoon.musicwriter.data.CommentData;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.list.CommentRecyclerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 운택 on 2016-10-22.
 */

public class GetCommentUserPicAsync extends AsyncTask<String, Void, Bitmap> {

    Context context;
    ArrayList<CommentData> list;
    int position;
    CommentRecyclerAdapter adapter;

    public GetCommentUserPicAsync(Context context, ArrayList<CommentData> list, int position, CommentRecyclerAdapter adapter) {
        this.context = context;
        this.list = list;
        this.position = position;
        this.adapter = adapter;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        HttpURLConnection connection = null;
        Bitmap bitmap;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(input);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            String FILENAME = "user_"+list.get(position).getUserID()+"_pic.jpg";
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(imageBytes);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            if(connection!=null)connection.disconnect();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        list.get(position).setUserPicture(bitmap);
        adapter.notifyDataSetChanged();
        super.onPostExecute(bitmap);
    }
}
