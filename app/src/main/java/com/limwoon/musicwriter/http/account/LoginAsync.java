package com.limwoon.musicwriter.http.account;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.http.GetUserPicAsync;

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

import static android.content.ContentValues.TAG;

/**
 * Created by 운택 on 2016-09-18.
 */
public class LoginAsync extends AsyncTask<Bundle, Void, Integer> {

    Context context;

    HttpURLConnection httpConn;
    InputStream is;
    OutputStream os;
    BufferedReader bufferedReader;

    String id;
    String pw;
    boolean autoLogin;

    public LoginAsync(Context context){
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Bundle... bundles) {
        int result = 0;
        String data = null;

        try {
            Bundle bundle = bundles[0];
            id = bundle.getString("id");
            pw = bundle.getString("pw");
            autoLogin = bundle.getBoolean("al");
            String message = "id="+ id + "&pw=" + pw;

            URL url = new URL("http://115.71.236.157/login.php");
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            os = httpConn.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.flush();
            os.close();

            is = httpConn.getInputStream();

            //// 로그인 판별  ////
            bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            data = bufferedReader.readLine();
            JSONObject jsonData = new JSONObject(data);
            result = jsonData.getInt("result");

            String jwt = jsonData.getString("jwt");
            String jwtClaim = jwt.split("\\.")[1];
            String decodedJwtClaim = new String(Base64.decode(jwtClaim, Base64.NO_WRAP), "UTF-8");

            JSONObject decodedJwtClaimJSON = new JSONObject(decodedJwtClaim);
            long userID = decodedJwtClaimJSON.getLong("userID");
            String userStrID = decodedJwtClaimJSON.getString("userStrID");
            String userEmail = decodedJwtClaimJSON.getString("userEmail");
            String userPic_url = decodedJwtClaimJSON.getString("userPic_url");

            PUBLIC_APP_DATA.setUserToken(jwt);
            PUBLIC_APP_DATA.setUserData(decodedJwtClaim);
            PUBLIC_APP_DATA.setUserID(userID);
            PUBLIC_APP_DATA.setUserStrID(userStrID);
            PUBLIC_APP_DATA.setUserEmail(userEmail);
            PUBLIC_APP_DATA.setPictureURL(userPic_url);
            PUBLIC_APP_DATA.setImageName(String.valueOf(userID));
            PUBLIC_APP_DATA.setIsLogin(true);
            PUBLIC_APP_DATA.setIsFacebook(false);

            SharedPreferences autoLoginPref = context.getSharedPreferences("al", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = autoLoginPref.edit();
            editor.putString("jwt", jwt);
            editor.apply();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
           result=Integer.parseInt(data);
        } finally {
            httpConn.disconnect();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result==1){
            Toast.makeText(context, "로그인 완료", Toast.LENGTH_SHORT).show();
            new GetUserPicAsync(context).execute(PUBLIC_APP_DATA.getPictureURL());
            ((Activity)context).finish();
        }else if(result==10){
            Toast.makeText(context, "아이디나 비밀번호가 틀립니다", Toast.LENGTH_SHORT).show();
        }

        super.onPostExecute(result);
    }
}
