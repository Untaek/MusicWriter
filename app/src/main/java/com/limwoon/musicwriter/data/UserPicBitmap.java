package com.limwoon.musicwriter.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.limwoon.musicwriter.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-10-01.
 */

public class UserPicBitmap{
    private Context context;
    private ImageView imageView;

    public UserPicBitmap(Context context){
        this.context=context;
    }
    public UserPicBitmap(Context context, ImageView imageView){
        this.context=context;
        this.imageView=imageView;
    }
    public Bitmap getUserPicBitmap(){
        Bitmap b = null;
        try {
            FileInputStream fis = context.openFileInput(PUBLIC_APP_DATA.getImageName());
            Log.d(TAG, "getUserPicBitmap: "+ PUBLIC_APP_DATA.getImageName());
            byte[] imageBytes = new byte[fis.available()];
            while (fis.read(imageBytes) != -1){}
            fis.close();
            b = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("TAG", "onCreate: notFound");
            b = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_account_circle_white_48dp);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "onCreate: IOEXCEP");
        }
        return b;
    }

}
