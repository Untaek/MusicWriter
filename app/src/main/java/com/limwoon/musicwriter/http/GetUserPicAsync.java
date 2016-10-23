package com.limwoon.musicwriter.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.image.UserPicture;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 운택 on 2016-10-01.
 */

public class GetUserPicAsync extends AsyncTask<String, Void, Integer> {
    Context context;
    ImageView imageView;
    ProgressBar progressBar;

    UserPicture userPicture;

    public GetUserPicAsync(Context context, ImageView imageView, ProgressBar progressBar){
        this.context=context;
        this.imageView=imageView;
        this.progressBar=progressBar;
        this.userPicture = new UserPicture(context);
    }

    public GetUserPicAsync(Context context){
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Integer doInBackground(String... params) {    // 이미지 파일 주소를 인자로 받음 ex)http://xx.xx.xx/user_00_pic.jpg
        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            String FILENAME = PUBLIC_APP_DATA.getImageName();
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
        return 1;
    }


    @Override
    protected void onPostExecute(Integer result) {
        Bitmap bit = new UserPicture(context).getUserPicBitmapFromCache(PUBLIC_APP_DATA.getImageName());
        if(imageView!=null)
            imageView.setImageBitmap(bit);
        if(progressBar!=null)
            progressBar.setVisibility(View.INVISIBLE);
        super.onPostExecute(result);
    }
}
