package com.limwoon.musicwriter.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-09-30.
 */

public class ChangeUserPic extends AsyncTask<Bitmap, Void, Integer> {

    InputStream is;
    DataOutputStream dataos;
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    String imageUrl;

    Context context;

    public ChangeUserPic(Context context){
        this.context=context;
    }

    @Override
    protected Integer doInBackground(Bitmap... bitmaps) {
        Bitmap imageBitmap = bitmaps[0];
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
        byte[] imageBytes = byteStream.toByteArray();

        String imageName = "user_" + PUBLIC_APP_DATA.getUserID() + "_pic.jpg";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int result=-1;
        try {
            URL phpUrl = new URL("http://115.71.236.157/changeuserpic.php");
            HttpURLConnection connection = (HttpURLConnection) phpUrl.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            dataos = new DataOutputStream(connection.getOutputStream());
            dataos.writeBytes(twoHyphens + boundary + lineEnd);
            dataos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                    + imageName + "\"" + lineEnd);
            dataos.writeBytes(lineEnd);
            dataos.write(imageBytes, 0, imageBytes.length);
            dataos.writeBytes(lineEnd);
            dataos.writeBytes(twoHyphens + boundary + lineEnd);
            dataos.flush();
            dataos.close();

            is = connection.getInputStream();

            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String resultJson = bufferedReader.readLine();
            JSONObject json = new JSONObject(resultJson);
            result = json.getInt("result");
            imageUrl = json.getString("url");

            String FILENAME = imageName;
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(imageBytes);
            fos.close();

            /*
            while(true){
                String line = bufferedReader.readLine();
                Log.d(TAG, "doInBackground: "+ line);
                if(line==null) break;
            }
            */
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result){
            case 1:
                Toast.makeText(context, "대표 이미지를 변경하였습니다", Toast.LENGTH_SHORT).show();
                if(!PUBLIC_APP_DATA.isFacebook()) {
                    PUBLIC_APP_DATA.setPictureURL(imageUrl);
                    new UpdateToken(context).execute(PUBLIC_APP_DATA.getUserStrID());
                }
                else{

                }
                break;
            case 10:
                Toast.makeText(context, "이미지 변경을 실패했습니다", Toast.LENGTH_SHORT).show();
                break;
        }
        super.onPostExecute(result);
    }
}
