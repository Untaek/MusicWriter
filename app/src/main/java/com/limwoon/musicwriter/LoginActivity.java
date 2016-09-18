package com.limwoon.musicwriter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.http.LoginAsync;

public class LoginActivity extends AppCompatActivity {

    EditText editTextId;
    EditText editTextPw;

    String id;
    String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextId = (EditText) findViewById(R.id.login_id);
        editTextPw = (EditText) findViewById(R.id.login_pw);

        // 로그인 버튼 클릭
        findViewById(R.id.confirm_log_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = editTextId.getText().toString();
                pw = editTextPw.getText().toString();

                LoginAsync loginAsync = new LoginAsync(getBaseContext());
                if(!id.equals("") && !pw.equals("")){
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("pw", pw);
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
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });


    }
}
