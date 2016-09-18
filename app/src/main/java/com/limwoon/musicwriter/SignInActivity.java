package com.limwoon.musicwriter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.limwoon.musicwriter.http.SignInAsync;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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

                if(!id.equals("") && !pw1.equals("") && !pw2.equals("") && !email.equals("")) {
                    if(pw1.equals(pw2)){
                        correctPw = pw1;
                        Bundle bundle = new Bundle();
                        bundle.putString("id", id);
                        bundle.putString("pw", correctPw);
                        bundle.putString("email", email);
                        SignInAsync task = new SignInAsync(getBaseContext());
                        task.execute(bundle);
                    }
                    else {
                        Toast.makeText(getBaseContext(), "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getBaseContext(), "빈 칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
