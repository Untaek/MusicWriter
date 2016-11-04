package com.limwoon.musicwriter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.http.FaceBookUserData;
import com.limwoon.musicwriter.http.FacebookCheckSignIn;
import com.limwoon.musicwriter.http.LoadUserPicBitmapFromURLAsync;
import com.limwoon.musicwriter.http.account.LoginAsync;
import com.limwoon.musicwriter.image.UserPicture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    EditText editTextId;
    EditText editTextPw;
    CheckBox checkBoxAutoLogin;

    String id;
    String pw;

    Activity activity;
    Toolbar toolbar;

    CallbackManager callbackManager;

    UserPicture userPicture;

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

        userPicture = new UserPicture(this);

        // 로그인 버튼 클릭
        findViewById(R.id.confirm_log_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = editTextId.getText().toString();
                pw = editTextPw.getText().toString();
                boolean autoLogin;
                autoLogin=true;

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
            public void onClick(View view) {
                Intent intent = new Intent(activity, SignInActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 찾기
        findViewById(R.id.textBtn_find_pw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FindPwActivity.class);
                startActivity(intent);
            }
        });

       // 페이스북 로그인
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginButton facebookLoginBtn = (LoginButton) findViewById(R.id.facebook_login);
        facebookLoginBtn.setReadPermissions(Arrays.asList("public_profile", "email"));

        facebookLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                            String token = loginResult.getAccessToken().getToken();
                            long id = object.getLong("id");
                            new FacebookCheckSignIn().execute(id);
                            String lastName = object.getString("last_name");
                            String firstName = object.getString("first_name");
                            String email = object.getString("email");
                            String pictureURL = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            PUBLIC_APP_DATA.setIsLogin(true);
                            PUBLIC_APP_DATA.setUserID(id);
                            PUBLIC_APP_DATA.setUserToken(token);
                            PUBLIC_APP_DATA.setUserData(object.toString());
                            PUBLIC_APP_DATA.setUserStrID(firstName + lastName);
                            PUBLIC_APP_DATA.setUserEmail(email);
                            PUBLIC_APP_DATA.setPictureURL(pictureURL);
                            PUBLIC_APP_DATA.setUserFacebookPicUrl(pictureURL);
                            PUBLIC_APP_DATA.setImageName(String.valueOf(id));
                            PUBLIC_APP_DATA.setIsFacebook(true);

                            SharedPreferences sp = getSharedPreferences("al", MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("ft", token);
                            edit.apply();
                            new FaceBookUserData(LoginActivity.this).getUserDataFromDB(id);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, picture.width(200).height(200), gender");
                parameters.putString("picture", "type=large");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("facebook", "onCancel: facebook");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("facebook", "onError: facebook");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }
}
