package com.limwoon.musicwriter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class UserInfActivity extends AppCompatActivity {

    ImageView imageView_userPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inf);

        imageView_userPic = (ImageView) findViewById(R.id.imageView_user_picture_inf);

        //imageView_userPic.set
    }
}
