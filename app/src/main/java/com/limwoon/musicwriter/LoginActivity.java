package com.limwoon.musicwriter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.http.LoginAsync;

public class LoginActivity extends AppCompatActivity {

    EditText editTextId;
    EditText editTextPw;
    CheckBox checkBoxAutoLogin;

    String id;
    String pw;

    Activity activity;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity=this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextId = (EditText) findViewById(R.id.login_id);
        editTextPw = (EditText) findViewById(R.id.login_pw);
        checkBoxAutoLogin = (CheckBox) findViewById(R.id.auto_login);

        // 로그인 버튼 클릭
        findViewById(R.id.confirm_log_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = editTextId.getText().toString();
                pw = editTextPw.getText().toString();
                boolean autoLogin = checkBoxAutoLogin.isChecked();

                LoginAsync loginAsync = new LoginAsync(activity);
                if(!id.equals("") && !pw.equals("")){
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("pw", pw);
                    bundle.putBoolean("al", autoLogin);
                    loginAsync.execute(bundle);
                }
                else{
                    Toast.makeText(getBaseContext(), "빈 칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 회원가입 버튼 클릭
        findViewById(R.id.textBtn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SignInActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }
}
