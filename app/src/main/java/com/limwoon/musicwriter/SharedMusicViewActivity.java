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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.draw.BaseSheet;
import com.limwoon.musicwriter.draw.NoteRecyclerAdapter;
import com.limwoon.musicwriter.draw.SheetAppender;
import com.limwoon.musicwriter.http.LikeSheetAsync;
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

    SheetData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_music_view);

        data = (SheetData) getIntent().getSerializableExtra("data");
        NoteParser noteParser = new NoteParser(data.getNote());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("zdz");
        setSupportActionBar(toolbar);

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

        findViewById(R.id.button_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PUBLIC_APP_DATA.isLogin()){
                    new LikeSheetAsync().execute(data.getId());
                    Toast.makeText(SharedMusicViewActivity.this, "추천 했습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SharedMusicViewActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.button_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PUBLIC_APP_DATA.isLogin()){
                    Toast.makeText(SharedMusicViewActivity.this, "즐겨찾기에 추가 했습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SharedMusicViewActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
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
