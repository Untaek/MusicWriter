package com.limwoon.musicwriter;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by ejdej on 2016-09-02.
 */
public class testActivity extends AppCompatActivity {
    NativeClass nativeClass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        Button button = (Button) findViewById(R.id.soundbtn);

        final AssetManager assetManager = getAssets();

        nativeClass = new NativeClass();
        nativeClass.createEngine();
        nativeClass.createAssetAudioPlayer(assetManager, "");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeClass.setPlayingAssetAudioPlayer(4, 0);
                nativeClass.setPlayingAssetAudioPlayer(1, 0);
                nativeClass.setPlayingAssetAudioPlayer(2, 0);
                nativeClass.setPlayingAssetAudioPlayer(3, 0);
                nativeClass.setPlayingAssetAudioPlayer(0, 0);
                try {
                    Thread.sleep(1000);
                    //
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        nativeClass.releaseAll();

        super.onDestroy();
    }
}
