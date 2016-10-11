package com.limwoon.musicwriter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.data.CommentData;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.draw.BaseSheet;
import com.limwoon.musicwriter.draw.NoteRecyclerAdapter;
import com.limwoon.musicwriter.draw.SheetAppender;
import com.limwoon.musicwriter.http.CheckFavorite;
import com.limwoon.musicwriter.http.CheckLike;
import com.limwoon.musicwriter.http.DisFavoriteSheetAsync;
import com.limwoon.musicwriter.http.DisLikeSheetAsync;
import com.limwoon.musicwriter.http.FavoriteSheetAsync;
import com.limwoon.musicwriter.http.LikeSheetAsync;
import com.limwoon.musicwriter.http.LoadComments;
import com.limwoon.musicwriter.http.WriteCommentAsync;
import com.limwoon.musicwriter.list.CommentRecyclerAdapter;
import com.limwoon.musicwriter.sounds.Sounds;

import java.util.ArrayList;

public class SharedMusicViewActivity extends AppCompatActivity {

    ArrayList<NoteData> noteList = new ArrayList<>();
    RecyclerView sheetRecyView;
    NoteRecyclerAdapter sheetRecyAdapter;
    LinearLayoutManager linearLayoutManager;
    LinearLayout sheetBaseLinear;
    BaseSheet baseSheet;
    SheetAppender sheetAppender;

    ArrayList<CommentData> commentList = new ArrayList<>();
    RecyclerView commentRecyclerView;
    CommentRecyclerAdapter commentRecyclerAdapter;
    LinearLayoutManager commentLinearLayoutManager;

    SheetData data;
    static public boolean userLikeState;
    static public boolean userFavoriteState;
    Button button_like;
    Button button_favorite;
    TextView textView_commentCount;
    Button button_writeComment;
    EditText editText_writeComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_music_view);

        data = (SheetData) getIntent().getSerializableExtra("data");
        NoteParser noteParser = new NoteParser(data.getNote());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("zdz");
        setSupportActionBar(toolbar);

        button_like = (Button) findViewById(R.id.button_like);
        button_favorite = (Button) findViewById(R.id.button_favorite);
        button_writeComment = (Button) findViewById(R.id.button_write_comment);
        editText_writeComment = (EditText) findViewById(R.id.editText_comment);

        new CheckLike(findViewById(R.id.button_like), userLikeState).execute(data.getId());
        new CheckFavorite(findViewById(R.id.button_favorite)).execute(data.getId());


        textView_commentCount = (TextView) findViewById(R.id.textView_comment_header);
        commentRecyclerView = (RecyclerView) findViewById(R.id.recycler_comment);
        commentRecyclerAdapter = new CommentRecyclerAdapter(getApplicationContext(), commentList);
        commentRecyclerView.setAdapter(commentRecyclerAdapter);
        commentLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        commentRecyclerView.setLayoutManager(commentLinearLayoutManager);
        new LoadComments(commentList, commentRecyclerAdapter, textView_commentCount).execute(data.getId());


        TextView textView_title = (TextView) findViewById(R.id.textView_title);
        TextView textView_author = (TextView) findViewById(R.id.textView_author);
        TextView textView_description = (TextView) findViewById(R.id.textView_description);

        textView_title.setText("제목: "+data.getTitle());
        textView_author.setText("작곡자: "+data.getAuthor());
        textView_description.setText("");

        findViewById(R.id.button_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread playThread = new Thread(new Runnable() {
                    @Override
                    public void run(){
                        NativeClass.createEngine();
                        NativeClass.createAssetAudioPlayer(PUBLIC_APP_DATA.assetManager, "");

                        for(int i=0; i<noteList.size(); i++) {
                            if (noteList.get(i).node) continue;
                            for (int j = 0; j < 6; j++) {
                                if (noteList.get(i).tone[j] != -1) {
                                    NativeClass.setPlayingAssetAudioPlayer(j, noteList.get(i).tone[j]);
                                }
                            }
                            try {
                                Thread.sleep(Sounds.getDuration(noteList.get(i).duration));
                                NativeClass.setStopAssetAudioPlayer(0);
                                if(i == noteList.size()-1) NativeClass.releaseAll();
                            } catch (InterruptedException e) { // 정지버튼 클릭
                                NativeClass.setStopAssetAudioPlayer(0);
                                break;
                            }
                        }
                    }
                });
                playThread.start();
            }
        });

        button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PUBLIC_APP_DATA.isLogin()){
                    if(userLikeState){
                        new LikeSheetAsync().execute(data.getId());
                        Toast.makeText(SharedMusicViewActivity.this, "추천 했습니다", Toast.LENGTH_SHORT).show();
                        button_like.setText("추천했습니다");
                        userLikeState=false;
                    }
                    else{
                        new DisLikeSheetAsync().execute(data.getId());
                        Toast.makeText(SharedMusicViewActivity.this, "추천을 취소했습니다", Toast.LENGTH_SHORT).show();
                        button_like.setText("추천");
                        userLikeState=true;
                    }

                }else{
                    Toast.makeText(SharedMusicViewActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PUBLIC_APP_DATA.isLogin()){
                    if(userFavoriteState){
                        new FavoriteSheetAsync().execute(data.getId());
                        Toast.makeText(SharedMusicViewActivity.this, "즐겨찾기에 추가 했습니다", Toast.LENGTH_SHORT).show();
                        button_favorite.setText("즐겨찾기 되었습니다");
                        userFavoriteState=false;
                    }
                    else{
                        new DisFavoriteSheetAsync().execute(data.getId());
                        Toast.makeText(SharedMusicViewActivity.this, "즐겨찾기를 취소했습니다", Toast.LENGTH_SHORT).show();
                        button_favorite.setText("즐겨찾기");
                        userFavoriteState=true;
                    }
                }else{
                    Toast.makeText(SharedMusicViewActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PUBLIC_APP_DATA.isLogin()){
                    Bundle bundle = new Bundle();
                    bundle.putString("comment", editText_writeComment.getText().toString());
                    bundle.putLong("sheetID", data.getId());
                    bundle.putLong("userID", PUBLIC_APP_DATA.getUserID());

                    new WriteCommentAsync().execute(bundle);
                    editText_writeComment.setText("");
                }
            }
        });

        noteList = new ArrayList<>();
        sheetRecyView = (RecyclerView) findViewById(R.id.viewSheetRecycler);
        sheetRecyAdapter = new NoteRecyclerAdapter(this, noteList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        sheetRecyView.setAdapter(sheetRecyAdapter);
        sheetRecyView.setLayoutManager(linearLayoutManager);
        sheetRecyView.setPadding(260,0,0,0);

        sheetBaseLinear = (LinearLayout) findViewById(R.id.sheetBaseLinear);
        baseSheet = new BaseSheet(this, noteParser.getBeats());
        sheetBaseLinear.addView(baseSheet);

        for(int i=0; i<noteParser.getNoteLength(); i++) {
            noteList.add(noteParser.getNoteAt(i));
            sheetRecyAdapter.notifyItemChanged(i);
        }
        for(int j=0; j<noteList.size()/4; j++){
            sheetAppender = new SheetAppender(getApplicationContext());
            sheetBaseLinear.addView(sheetAppender);
        }
    }
}
