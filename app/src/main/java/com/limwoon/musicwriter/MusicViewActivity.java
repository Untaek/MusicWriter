package com.limwoon.musicwriter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.limwoon.musicwriter.SQLite.DefineSQL;
import com.limwoon.musicwriter.SQLite.SheetDbHelper;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.draw.BaseSheet;
import com.limwoon.musicwriter.draw.NoteRecyclerAdapter;
import com.limwoon.musicwriter.draw.NoteRestExam;
import com.limwoon.musicwriter.draw.SheetAppender;
import com.limwoon.musicwriter.http.ShareSheetAsync;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.sounds.Sounds;

import java.util.ArrayList;

public class MusicViewActivity extends AppCompatActivity {

    Intent intentData;
    int id;
    int beats;
    String title;
    String author;
    String data;

    RecyclerView sheetRecyView;
    NoteRecyclerAdapter sheetRecyAdapter;
    LinearLayoutManager linearLayoutManager;
    BaseSheet baseSheet;
    SheetAppender sheetAppender;
    ArrayList<NoteData> noteList;
    LinearLayout sheetBaseLinear;
    HorizontalScrollView horizontalScrollView;

    NoteParser noteParser;

    int i;
    ImageView btnPlayMusic;
    SeekBar musicSeekBar;
    boolean isPlay = false;
    Thread playThread;
    int musicProgress =0;

    NativeClass nativeClass;

    Button button_shareMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NoteRestExam noteRestExam = new NoteRestExam(this);

        // 디비에서 정보 가져오기, 기본적인 정보들 변수에 넣기
        intentData = getIntent();
        data = intentData.getStringExtra("musicData");
        noteParser = new NoteParser(data);
        beats = noteParser.getBeats();
        title = noteParser.getTitle();
        author = noteParser.getAuthor();
        id = intentData.getIntExtra("id",-1);

        //악보 뷰 만들기
        noteList = new ArrayList<>();
        sheetRecyView = (RecyclerView) findViewById(R.id.viewSheetRecycler);
        sheetRecyAdapter = new NoteRecyclerAdapter(this, noteList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        sheetRecyView.setAdapter(sheetRecyAdapter);
        sheetRecyView.setLayoutManager(linearLayoutManager);
        sheetRecyView.setPadding(260,0,0,0);

        sheetBaseLinear = (LinearLayout) findViewById(R.id.sheetBaseLinear);
        baseSheet = new BaseSheet(this, beats);
        sheetBaseLinear.addView(baseSheet);

        // 리스트에 하나씩 넣으면서 악보 그리기
        for(int i=0; i<noteParser.getNoteLength(); i++) {
            noteList.add(noteParser.getNoteAt(i));
            sheetRecyAdapter.notifyItemChanged(i);
        }
        for(int j=0; j<noteList.size()/4; j++){
            sheetAppender = new SheetAppender(getApplicationContext());
            sheetBaseLinear.addView(sheetAppender);
        }

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        TextView textViewAuthor = (TextView) findViewById(R.id.textViewAuthor);
        btnPlayMusic = (ImageView) findViewById(R.id.music_play_btn);
        musicSeekBar = (SeekBar) findViewById(R.id.music_seek_bar);

        musicSeekBar.setMax(noteParser.getNoteLength()-1);

        nativeClass = new NativeClass();
        nativeClass.createEngine();
        nativeClass.createAssetAudioPlayer(getAssets(), "");

        final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        };

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlay){
                    musicSeekBar.setAlpha(0.7f);
                    musicSeekBar.setOnTouchListener(onTouchListener);
                    btnPlayMusic.setImageResource(R.drawable.ic_pause_black_48dp);
                    isPlay = true;
                    if(musicProgress==noteList.size()-1) musicProgress=0;
                    playThread = new Thread(new Runnable() {
                        @Override
                        public void run(){
                            for(i=musicProgress; i<noteList.size(); i++){
                                musicProgress=i;
                                horizontalScrollView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        horizontalScrollView.smoothScrollTo(150*i-150,0);
                                    }
                                });
                                sheetRecyView.getChildAt(i).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            sheetRecyView.getChildAt(i-2).setBackgroundColor(Color.alpha(0));
                                        }catch (NullPointerException e){
                                            Log.d("jumpChild","yes");
                                        }
                                        try {
                                            sheetRecyView.getChildAt(i-1).setBackgroundColor(Color.alpha(0));
                                        }catch (NullPointerException e){
                                            Log.d("jumpChild","yes");
                                        }
                                        try {
                                            sheetRecyView.getChildAt(i).setBackgroundColor(Color.GREEN);
                                        }catch (NullPointerException e){
                                            Log.d("Song end","end");
                                        }
                                    }
                                });
                                if(noteList.get(i).node) continue;
                                for(int j=0; j<6; j++){
                                    if(noteList.get(i).tone[j]!=-1){
                                        nativeClass.setPlayingAssetAudioPlayer(j, noteList.get(i).tone[j]);
                                    }
                                }
                                try {
                                    Thread.sleep(Sounds.getDuration(noteList.get(i).duration));
                                    nativeClass.setStopAssetAudioPlayer(0);
                                } catch (InterruptedException e) { // 정지버튼 클릭
                                    nativeClass.setStopAssetAudioPlayer(0);
                                    sheetRecyView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            sheetRecyView.getChildAt(musicProgress).setBackgroundColor(Color.alpha(0));
                                        }
                                    });
                                    break;
                                }
                                if(i==noteList.size()-1){
                                    sheetRecyView.getChildAt(i-1).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sheetRecyView.getChildAt(i-1).setBackgroundColor(Color.alpha(0));
                                            }catch (NullPointerException e){
                                                Log.d("nullpointer", ""+i);

                                            }
                                        }
                                    });
                                }
                                musicSeekBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        musicSeekBar.setProgress(i);
                                    }
                                });
                                if(playThread.isInterrupted()) break;
                            }
                            musicSeekBar.setOnTouchListener(null);
                            isPlay = false;
                            musicSeekBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    musicSeekBar.setAlpha(1);
                                }
                            });
                            btnPlayMusic.post(new Runnable() {
                                @Override
                                public void run() {
                                    btnPlayMusic.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                }
                            });

                        }
                    });
                    playThread.start();
                }else{
                    musicSeekBar.setAlpha(1);
                    musicSeekBar.setOnTouchListener(null);
                    isPlay = false;
                    btnPlayMusic.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    if(playThread!=null){
                        playThread.interrupt();
                    }
                }
            }
        });

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                if(b){
                    horizontalScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            horizontalScrollView.smoothScrollTo(150*i-150,0);
                        }
                    });
                    musicProgress = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textViewTitle.setText("제목 : "+title);
        textViewAuthor.setText("작곡자 : "+author);

        button_shareMusic = (Button) findViewById(R.id.button_share_music);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MusicViewActivity.this);
        builder.setTitle("이 곡을 게시 하시겠습니까?").setMessage("z");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("author", PUBLIC_APP_DATA.getUserStrID());
                bundle.putString("note", data);

                new ShareSheetAsync(getApplicationContext()).execute(bundle);
            }
        }).setNegativeButton("아니요", null);

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MusicViewActivity.this);
        builder1.setTitle("오류").setMessage("로그인이 필요합니다");
        builder1.setPositiveButton("로그인하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }).setNegativeButton("닫기", null);
        button_shareMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PUBLIC_APP_DATA.isLogin()){
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        nativeClass.releaseAll();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(playThread!=null)
        playThread.interrupt();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("TempNoteData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String noteData = sharedPreferences.getString("noteData", null);
        if(noteData!=null){
            noteList.clear();
            noteParser.setData2(noteData);
            sheetRecyAdapter.index=0;
            for(int i=0; i<noteParser.getNoteLength(); i++) {
                noteList.add(noteParser.getNoteAt(i));
                sheetRecyAdapter.notifyItemChanged(i);
                if (noteList.size() > 1 && (noteList.size()) % 4 == 0) {
                    sheetAppender = new SheetAppender(getApplicationContext());
                    sheetBaseLinear.addView(sheetAppender);
                }
            }
            editor.clear();
            editor.apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_edit){
            Log.d("id", ""+id);

            Intent intent = new Intent(getApplicationContext(),MusicWriteActivity.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("id", id);
            intent.putExtra("beatIndex", beats);
            intent.putExtra("title", title);
            intent.putExtra("author", author);
            Log.d("beats", ""+beats);
            startActivity(intent);

            overridePendingTransition(R.anim.activity_slide_left, R.anim.activity_slide_left_gone);

        }else if(item.getItemId()==R.id.action_delete){
            AlertDialog dialog = new AlertDialog.Builder(this).
            setTitle("정말로 삭제하시겠습니까").
            setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SheetDbHelper dbHelper = new SheetDbHelper(getApplicationContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DefineSQL.TABLE_NAME, DefineSQL._ID+"="+id,null);

                    SharedPreferences sharedPreferences = getSharedPreferences("TempNoteData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    finish();
                }
            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            }).show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mysic_view_modify, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
