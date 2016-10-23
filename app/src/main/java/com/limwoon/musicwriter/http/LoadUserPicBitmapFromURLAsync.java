package com.limwoon.musicwriter.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 운택 on 2016-10-03.
 */

public class LoadUserPicBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... params) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            if(connection!=null)connection.disconnect();
        }
        return bitmap;
    }
}
