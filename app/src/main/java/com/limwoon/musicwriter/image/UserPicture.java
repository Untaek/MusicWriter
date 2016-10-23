package com.limwoon.musicwriter.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.http.DeletePictureAsync;
import com.limwoon.musicwriter.http.account.UpdatePictureDBAsync_FacebookUser;
import com.limwoon.musicwriter.http.UpdateToken;
import com.limwoon.musicwriter.http.UploadUserPicAsync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-10-03.
 */

public class UserPicture {

    long userID;
    String userPic_url;
    Context context;
    boolean notFound = false;

    public UserPicture(Context context){
        this.context=context;
        this.userID= PUBLIC_APP_DATA.getUserID();
        this.userPic_url=PUBLIC_APP_DATA.getPictureURL();
    }

    public boolean isNotFound() {
        return notFound;
    }

    public interface OnPictureReadyListener{
        void onLoaded();
    }

    private OnPictureReadyListener mListener;

    public void setOnPictureReadyListener(OnPictureReadyListener onPictureReadyListener){
        this.mListener=onPictureReadyListener;
    }

    public Bitmap getUserPicBitmapFromCache(String imageName){
        Bitmap b = null;
        try {
            FileInputStream fis = context.openFileInput(imageName);
            byte[] imageBytes = new byte[fis.available()];
            while (fis.read(imageBytes) != -1){}
            fis.close();
            b = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
            Log.d("TAG", "onCreate: Found "+imageName);
            notFound=false;
            return b;
        } catch (FileNotFoundException e) {
            Log.d("TAG", "onCreate: notFound " + imageName);
            notFound=true;
            b = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_account_circle_black_48dp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }

    public boolean changeUserPicture(int where, Bitmap bitmap) { // 0 서버 1 페이스북
        Bitmap pictureBitmap = bitmap;

        if(where == 0){ // 갤러리와 카메라로 바꾸기
            userPic_url = PUBLIC_APP_DATA.serverUrl+"user_pic/"+PUBLIC_APP_DATA.getImageName();
            if(PUBLIC_APP_DATA.isFacebook()){
                new UpdatePictureDBAsync_FacebookUser().execute(userPic_url); // db 수정
                Log.d("userPic_url", "changeUserPicture: "+PUBLIC_APP_DATA.getPictureURL()  );
            }else{
               //문제있음. 이미지 업로드 단계에서 db에 입력됨 new UpdatePictureDBAsync().execute();  // db 수정
                new UpdateToken(context).execute(PUBLIC_APP_DATA.getUserStrID()); // 토큰 업데이트
            }
            new UploadUserPicAsync(context).execute(pictureBitmap);  // 이미지 업로드

        }else if(where == 1){ // 페이스북 사진 가져오기
            new UpdatePictureDBAsync_FacebookUser().execute(PUBLIC_APP_DATA.getUserFacebookPicUrl()); // db 수정
        }
        cachingImage(pictureBitmap); // 이미지 캐싱
        return false;
    }

    public void cachingImage(Bitmap picture){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        Bitmap imageBitmap = picture;
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
        byte[] imageBytes = byteStream.toByteArray();
        String FILENAME = PUBLIC_APP_DATA.getImageName();
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(imageBytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mListener!= null)
             mListener.onLoaded();
    }

    public void unCachingImage(String imageName){

        File file = new File(context.getFilesDir().getPath()+"/"+imageName);
        Log.d(TAG, "unCachingImage: "+file.exists());
        if(file.exists()){
            file.delete();
            new DeletePictureAsync().execute();
        }
    }
}
