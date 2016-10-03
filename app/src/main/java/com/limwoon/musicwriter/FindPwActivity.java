package com.limwoon.musicwriter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.limwoon.musicwriter.http.account.FindPwAsync;

public class FindPwActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_pw);

        final EditText editText_id = (EditText) findViewById(R.id.editText_findid);
        final EditText editText_email = (EditText) findViewById(R.id.editText_findid_email);
        editText_id.requestFocus();

        findViewById(R.id.button_findid_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = editText_id.getText().toString();
                String email = editText_email.getText().toString();
                String s[] = {id, email};
                if(id.equals("") || email.equals("")){
                    Toast.makeText(getBaseContext(), "빈 칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    FindPwAsync findPwAsync = new FindPwAsync(getBaseContext());
                    findPwAsync.execute(s);
                }
            }
        });
    }
}
