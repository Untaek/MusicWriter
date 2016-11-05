package com.limwoon.musicwriter;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.draw.NoteBitmapMaker;
import com.limwoon.musicwriter.user.UserCheck;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PUBLIC_APP_DATA.setIsLoaded(true);
        UserCheck userCheck = new UserCheck(this);
        userCheck.checkIsLogin();

        NativeClass.createEngine();
        NativeClass.createBefferQueueAudioPlayer();
        NativeClass.createBufferFromAsset(getAssets(), "");

        NoteBitmapMaker noteBitmapMaker = new NoteBitmapMaker(this);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainNavActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
