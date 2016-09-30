package com.limwoon.musicwriter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.http.ChangePwAsync;
import com.limwoon.musicwriter.http.ChangeUserPic;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UserInfActivity extends AppCompatActivity {

    ImageView imageView_userPic;
    boolean isMatch = true;
    boolean isFill = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inf);

        imageView_userPic = (ImageView) findViewById(R.id.imageView_user_picture_inf);

        findViewById(R.id.button_change_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

                startActivityForResult(intent, 100);
            }
        });

        findViewById(R.id.button_change_pw).setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                Uri uri = data.getData();
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(true)
                        .setRequestedSize(160, 160)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bit = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    imageView_userPic.setImageBitmap(bit);
                    new ChangeUserPic(UserInfActivity.this).execute(bit);

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
