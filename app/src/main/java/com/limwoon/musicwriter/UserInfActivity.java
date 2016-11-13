package com.limwoon.musicwriter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.http.LoadUserInfoAsync;
import com.limwoon.musicwriter.http.LoadUserPicBitmapFromURLAsync;
import com.limwoon.musicwriter.http.TogglePushAsync;
import com.limwoon.musicwriter.image.UserPicture;
import com.limwoon.musicwriter.http.account.ChangePwAsync;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class UserInfActivity extends AppCompatActivity {

    ImageView imageView_userPic;
    boolean isMatch = true;
    boolean isFill = true;
    UserPicture userPicture;
    TextView textView_userStrID;
    TextView textView_userEmail;
    Switch switch_push;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("사용자 정보");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView_userPic = (ImageView) findViewById(R.id.imageView_user_picture_inf);
        userPicture = new UserPicture(this);

        textView_userStrID = (TextView) findViewById(R.id.textView_info_name);
        textView_userStrID.setText(PUBLIC_APP_DATA.getUserStrID());
        textView_userEmail = (TextView) findViewById(R.id.textView_info_email);
        textView_userEmail.setText(PUBLIC_APP_DATA.getUserEmail());


        Button button_changepw = (Button) findViewById(R.id.button_change_pw);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_change_pic);

        new LoadUserInfoAsync(this).execute();

        if(!PUBLIC_APP_DATA.isFacebook())
            button_changepw.setVisibility(View.VISIBLE);
        else
            button_changepw.setVisibility(View.GONE);

        Bitmap userPicBitmap = userPicture.getUserPicBitmapFromCache(PUBLIC_APP_DATA.getImageName());
        if(userPicBitmap != null){
            imageView_userPic.setImageBitmap(userPicBitmap);
        }
        else{
            Drawable drawable = getResources().getDrawable(R.drawable.ic_account_box_light_24dp);
            Bitmap b = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(b);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        imageView_userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.choice_select_pic, null);
                Button button_gall = (Button) view.findViewById(R.id.choice_from_gallery);
                Button button_camera = (Button) view.findViewById(R.id.choice_from_camera);
                Button button_from_facebook = (Button) view.findViewById(R.id.choice_from_facebook);
                Button button_clear_picture = (Button) view.findViewById(R.id.choice_clear_picture);

                if(PUBLIC_APP_DATA.isFacebook()) button_from_facebook.setVisibility(View.VISIBLE);
                else button_from_facebook.setVisibility(View.GONE);

                button_gall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, 100);
                    }
                });
                button_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 100);
                    }
                });
                button_from_facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Bitmap bit = new LoadUserPicBitmapFromURLAsync().execute(PUBLIC_APP_DATA.getUserFacebookPicUrl()).get();
                            userPicture.changeUserPicture(1, bit); // 1 : 페이스북 이미지 가져와서 바꾸기
                            imageView_userPic.setImageBitmap(bit);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
                button_clear_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView_userPic.setImageResource(R.drawable.ic_account_circle_white_48dp);
                        userPicture.unCachingImage(PUBLIC_APP_DATA.getImageName());
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfActivity.this);
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        button_changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialView = getLayoutInflater().inflate(R.layout.dialog_change_pw, null);
                final EditText editText_pw1 = (EditText) dialView.findViewById(R.id.dialog_editText_pw1);
                final EditText editText_pw2 = (EditText) dialView.findViewById(R.id.dialog_editText_pw2);
                final EditText editText_pwc = (EditText) dialView.findViewById(R.id.dialog_editText_pw_now);


                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfActivity.this);
                builder.setTitle("비밀번호 바꾸기")
                        .setView(dialView)
                        .setPositiveButton("바꾸기", null)
                        .setNegativeButton("취소", null);

                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog1) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String pw1 = editText_pw1.getText().toString();
                                String pw2 = editText_pw2.getText().toString();
                                String cpw = editText_pwc.getText().toString();
                                if(!pw1.equals("") && !pw2.equals("") && !cpw.equals("")){
                                    if(pw1.equals(pw2)){
                                        isMatch = true;
                                        String pws[] = {pw1, cpw};
                                        new ChangePwAsync(UserInfActivity.this).execute(pws);
                                    }
                                    else{
                                        isMatch = false;
                                    }
                                    isFill = true;
                                }
                                else{
                                    isFill = false;
                                }
                                if(isMatch && isFill) dialog.dismiss();
                                else if(!isMatch) Toast.makeText(UserInfActivity.this, "바꿀 비밀번호 두개가 서로 다릅니다", Toast.LENGTH_SHORT).show();
                                else if(!isFill) Toast.makeText(UserInfActivity.this, "빈 칸을 모두 입력하세요", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        switch_push = (Switch) findViewById(R.id.switch_push);
        switch_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new TogglePushAsync().execute(1);
                }else{
                    new TogglePushAsync().execute(0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                Uri uri = data.getData();
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(true)
                        .setRequestedSize(240, 240)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bit = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    userPicture.changeUserPicture(0, bit);
                    imageView_userPic.setImageBitmap(bit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
