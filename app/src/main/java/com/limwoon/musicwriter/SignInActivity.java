package com.limwoon.musicwriter;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.limwoon.musicwriter.http.account.SignInAsync;

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    String id= null;
    String correctPw = null;
    String pw1= null;
    String pw2= null;
    String email= null;

    EditText editTextId;
    EditText editTextPw1;
    EditText editTextPw2;
    EditText editTextEmail;

    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        activity = this;

        editTextId = (EditText) findViewById(R.id.sign_in_id);
        editTextPw1 = (EditText) findViewById(R.id.sign_in_pw);
        editTextPw2 = (EditText) findViewById(R.id.sign_in_pw_2);
        editTextEmail = (EditText) findViewById(R.id.sign_in_email);
        findViewById(R.id.confirm_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                id = editTextId.getText().toString();
                pw1 = editTextPw1.getText().toString();
                pw2 = editTextPw2.getText().toString();
                email = editTextEmail.getText().toString();

                Pattern idPattern = Pattern.compile("[a-zA-Z0-9_]{4,16}");
                Pattern pwPattern = Pattern.compile("(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{12,}");
                Pattern emailPattern = Pattern.compile("[0-9a-zA-Z]([\\-.\\w]*[0-9a-zA-Z\\-_+])*@([0-9a-zA-Z][\\-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9}");

                if(!id.equals("") && !pw1.equals("") && !pw2.equals("") && !email.equals("")) {
                    if(!idPattern.matcher(id).matches()){
                        Toast.makeText(getBaseContext(), "아이디는 영문, 숫자를 포함해 최소 4글자 최대 16글자만 가능합니다", Toast.LENGTH_SHORT).show();
                    }else if(!pwPattern.matcher(pw1).matches()){
                        Toast.makeText(getBaseContext(), "비밀번호는 영문, 숫자, 특수문자를 포함해 최소 12글자 이상이어야 합니다", Toast.LENGTH_SHORT).show();
                    }else if(!emailPattern.matcher(email).matches()){
                        Toast.makeText(getBaseContext(), "알맞은 이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                    }else{
                        if(pw1.equals(pw2)){
                            correctPw = pw1;
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("pw", correctPw);
                            bundle.putString("email", email);
                            SignInAsync task = new SignInAsync(activity);
                            task.execute(bundle);
                        }
                        else {
                            Toast.makeText(getBaseContext(), "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getBaseContext(), "빈 칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
