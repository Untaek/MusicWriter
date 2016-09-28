package com.limwoon.musicwriter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FindPwActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_pw);

        findViewById(R.id.editText_findid).requestFocus();
    }
}
