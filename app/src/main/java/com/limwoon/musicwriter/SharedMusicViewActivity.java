package com.limwoon.musicwriter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.data.CommentData;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.data.NoteStore;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.draw.BaseSheet;
import com.limwoon.musicwriter.draw.NoteRecyclerAdapter;
import com.limwoon.musicwriter.draw.SheetAppender;
import com.limwoon.musicwriter.http.CheckFavorite;
import com.limwoon.musicwriter.http.CheckLike;
import com.limwoon.musicwriter.http.DeleteSharedSheetAsync;
import com.limwoon.musicwriter.http.DisFavoriteSheetAsync;
import com.limwoon.musicwriter.http.DisLikeSheetAsync;
import com.limwoon.musicwriter.http.FavoriteSheetAsync;
import com.limwoon.musicwriter.http.LikeSheetAsync;
import com.limwoon.musicwriter.http.LoadComments;
import com.limwoon.musicwriter.http.LoadSharedSheetList;
import com.limwoon.musicwriter.http.SendNotiAsync;
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
    SeekBar seekBar_music;
    HorizontalScrollView horizontalScrollView_sheet;
    Button button_play;
    Thread playThread;
    boolean isPlay = false;
    int musicProgress;
    int playPos;

    ArrayList<CommentData> commentList = new ArrayList<>();
    RecyclerView commentRecyclerView;
    CommentRecyclerAdapter commentRecyclerAdapter;
    LinearLayoutManager commentLinearLayoutManager;
    NestedScrollView nestedScrollView;

    SheetData data;
    static public boolean userLikeState;
    static public boolean userFavoriteState;
    Button button_like;
    Button button_favorite;
    TextView textView_commentCount;
    Button button_writeComment;
    EditText editText_writeComment;
    ProgressBar progressBar_loadingComment;
    static public boolean commentLoading = false;

    Intent resultIntent = new Intent();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shared_music_view_menu, menu);
        if(PUBLIC_APP_DATA.getUserID()!= data.getUploadUserID()){
            menu.getItem(0).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                setResult(1, resultIntent);
                finish();
                break;
            case R.id.menu_shared_sheet_delete :
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                final ProgressDialog progressDialog = new ProgressDialog(SharedMusicViewActivity.this);progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.show();
                                new DeleteSharedSheetAsync()
                                        .setDeleteSharedSheetCallback(new DeleteSharedSheetAsync.DeleteSharedSheetCallback() {
                                            @Override
                                            public void onResult(int result) {
                                                progressDialog.dismiss();
                                                resultIntent.putExtra("deleted", true);
                                                setResult(1, resultIntent);
                                                finish();
                                            }
                                        })
                                        .execute(data.getId(), PUBLIC_APP_DATA.getUserID());
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create();
                dialog.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_music_view);
        data = (SheetData) getIntent().getSerializableExtra("data");
        resultIntent.putExtra("pos", getIntent().getIntExtra("pos", -1));
        resultIntent.putExtra("id", data.getId());

        NoteParser noteParser = new NoteParser(data.getNote());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_like = (Button) findViewById(R.id.button_like);
        button_favorite = (Button) findViewById(R.id.button_favorite);
        button_writeComment = (Button) findViewById(R.id.button_write_comment);
        editText_writeComment = (EditText) findViewById(R.id.editText_comment);

        if(!PUBLIC_APP_DATA.isLogin()){
            button_like.setEnabled(false);
            button_favorite.setEnabled(false);
            button_writeComment.setEnabled(false);
            editText_writeComment.setEnabled(false);
            editText_writeComment.setText("로그인이 필요합니다");
        }else{
            new CheckLike(findViewById(R.id.button_like), userLikeState).execute(data.getId());
            new CheckFavorite(findViewById(R.id.button_favorite)).execute(data.getId());
        }

        textView_commentCount = (TextView) findViewById(R.id.textView_comment_header);
        commentRecyclerView = (RecyclerView) findViewById(R.id.recycler_comment);
        progressBar_loadingComment = (ProgressBar) findViewById(R.id.progress_bar_loading_comment);
        commentRecyclerView.setNestedScrollingEnabled(false);

        commentRecyclerAdapter = new CommentRecyclerAdapter(this, commentList);
        commentRecyclerView.setAdapter(commentRecyclerAdapter);
        commentLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        commentRecyclerView.setLayoutManager(commentLinearLayoutManager);
        new LoadComments(commentList, commentRecyclerAdapter, textView_commentCount, this).execute(data.getId(), (long)0);

        seekBar_music = (SeekBar) findViewById(R.id.music_seek_bar);
        horizontalScrollView_sheet = (HorizontalScrollView) findViewById(R.id.horizontalScrollView_sheet);
        button_play = (Button) findViewById(R.id.button_play);


        TextView textView_title = (TextView) findViewById(R.id.textView_title);
        TextView textView_author = (TextView) findViewById(R.id.textView_author);
        TextView textView_description = (TextView) findViewById(R.id.textView_description);

        textView_title.setText("제목: "+data.getTitle());
        textView_author.setText("작곡자: "+data.getAuthor());
        textView_description.setText("");


        seekBar_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                if (b) {
                    horizontalScrollView_sheet.post(new Runnable() {
                        @Override
                        public void run() {
                            horizontalScrollView_sheet.smoothScrollTo(150 * i - 150, 0);
                        }
                    });
                    musicProgress = i;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlay) {
                    seekBar_music.setAlpha(0.7f);
                    //seekBar_music.setOnTouchListener(onTouchListener);
                   // button_play.setImageResource(R.drawable.ic_pause_black_48dp);
                    isPlay = true;
                    if (musicProgress == noteList.size() - 1) musicProgress = 0;
                    playThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (playPos = musicProgress; playPos < noteList.size(); playPos++) {
                                musicProgress = playPos;
                                horizontalScrollView_sheet.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        horizontalScrollView_sheet.smoothScrollTo(150 * playPos - 150, 0);
                                    }
                                });
                                sheetRecyView.getChildAt(playPos).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            sheetRecyView.getChildAt(playPos - 2).setBackgroundColor(Color.alpha(0));
                                        } catch (NullPointerException e) {
                                            Log.d("jumpChild", "yes");
                                        }
                                        try {
                                            sheetRecyView.getChildAt(playPos - 1).setBackgroundColor(Color.alpha(0));
                                        } catch (NullPointerException e) {
                                            Log.d("jumpChild", "yes");
                                        }
                                        try {
                                            sheetRecyView.getChildAt(playPos).setBackgroundColor(Color.GREEN);
                                        } catch (NullPointerException e) {
                                            Log.d("Song end", "end");
                                        }
                                    }
                                });
                                if (noteList.get(playPos).node) continue;
                                for (int j = 0; j < 6; j++) {
                                    if (noteList.get(playPos).tone[j] != -1) {
                                        NativeClass.setPlayingBufferQueue(j, noteList.get(playPos).tone[j]);
                                    }
                                }
                                try {
                                    Thread.sleep(Sounds.getDuration(noteList.get(playPos).duration, data.getTempo())); //////////////////////////
                                    NativeClass.setStopBufferQueue();
                                } catch (InterruptedException e) { // 정지버튼 클릭
                                    NativeClass.setStopBufferQueue();
                                    sheetRecyView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            sheetRecyView.getChildAt(musicProgress).setBackgroundColor(Color.alpha(0));
                                        }
                                    });
                                    break;
                                }
                                if (playPos == noteList.size() - 1) {
                                    sheetRecyView.getChildAt(playPos - 1).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sheetRecyView.getChildAt(playPos - 1).setBackgroundColor(Color.alpha(0));
                                            } catch (NullPointerException e) {
                                                Log.d("nullpointer", "" + playPos);
                                            }
                                        }
                                    });
                                }
                                seekBar_music.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        seekBar_music.setProgress(playPos);
                                    }
                                });
                                if (playThread.isInterrupted()) break;
                            }
                            seekBar_music.setOnTouchListener(null);
                            isPlay = false;
                            seekBar_music.post(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar_music.setAlpha(1);
                                }
                            });
                            button_play.post(new Runnable() {
                                @Override
                                public void run() {
                                    //button_play.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                }
                            });
                        }
                    });
                    playThread.start();
                } else {
                    seekBar_music.setAlpha(1);
                    seekBar_music.setOnTouchListener(null);
                    isPlay = false;
                    //button_play.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    if (playThread != null) {
                        playThread.interrupt();
                    }
                }
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
                        resultIntent.putExtra("like", data.getLikes()+1);
                    }
                    else{
                        new DisLikeSheetAsync().execute(data.getId());
                        Toast.makeText(SharedMusicViewActivity.this, "추천을 취소했습니다", Toast.LENGTH_SHORT).show();
                        button_like.setText("추천");
                        userLikeState=true;
                        resultIntent.putExtra("like", data.getLikes()-1);
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
                        resultIntent.putExtra("favorite", userFavoriteState);
                    }
                    else{
                        new DisFavoriteSheetAsync().execute(data.getId());
                        Toast.makeText(SharedMusicViewActivity.this, "즐겨찾기를 취소했습니다", Toast.LENGTH_SHORT).show();
                        button_favorite.setText("즐겨찾기");
                        userFavoriteState=true;
                        resultIntent.putExtra("favorite", userFavoriteState);
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
                    if(!editText_writeComment.getText().toString().equals("")){
                        Bundle bundle = new Bundle();
                        bundle.putString("comment", editText_writeComment.getText().toString());
                        bundle.putLong("sheetID", data.getId());
                        bundle.putLong("userID", PUBLIC_APP_DATA.getUserID());
                        resultIntent.putExtra("comment", commentList.size()+1);

                        new WriteCommentAsync(commentList, commentRecyclerAdapter, textView_commentCount).execute(bundle);
                        if(data.getUploadUserID() != PUBLIC_APP_DATA.getUserID()){
                            new SendNotiAsync().execute(data.getUploadUserID(), data.getId());
                        }
                        editText_writeComment.setText("");
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(editText_writeComment.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                }
            }
        });

        editText_writeComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
                    appbarLayout.setExpanded(false);
                }
            }
        });
        editText_writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
                appbarLayout.setExpanded(false);
            }
        });

        commentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItem = commentLinearLayoutManager.findLastVisibleItemPosition();
                int itemCount = commentLinearLayoutManager.getItemCount();
                if(lastItem >= itemCount-2 && dy>0 && !commentLoading && commentList.size()%7==0){
                    commentLoading=true;
                    new LoadComments(commentList, commentRecyclerAdapter, null, getApplicationContext()).execute(data.getId(), (long)itemCount/7);
                }
                Log.d("comm", "onScrolled: "+itemCount);
            }
        });

        nestedScrollView = (NestedScrollView) findViewById(R.id.content_container);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int lastItem = commentLinearLayoutManager.findLastVisibleItemPosition();
                int itemCount = commentLinearLayoutManager.getItemCount();
                if(lastItem >= itemCount-2 && (commentRecyclerView.getHeight()-v.getMaxScrollAmount())-oldScrollY<0 && !commentLoading && commentList.size()%7==0){
                    commentLoading=true;
                    new LoadComments(commentList, commentRecyclerAdapter, null, getApplicationContext()).setCommentStateCallback(new LoadComments.CommentStateCallback() {
                        @Override
                        public void loadCompleted(int num) {
                            if(num==7)
                                progressBar_loadingComment.setVisibility(View.INVISIBLE);
                            else
                                progressBar_loadingComment.setVisibility(View.GONE);
                        }
                    }).execute(data.getId(), (long)itemCount/7);
                    progressBar_loadingComment.setVisibility(View.VISIBLE);
                }

            }
        });

        noteList = new ArrayList<>();
        sheetRecyView = (RecyclerView) findViewById(R.id.viewSheetRecycler);
        sheetRecyAdapter = new NoteRecyclerAdapter(this, noteList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        sheetRecyView.setAdapter(sheetRecyAdapter);
        sheetRecyView.setLayoutManager(linearLayoutManager);

        sheetBaseLinear = (LinearLayout) findViewById(R.id.sheetBaseLinear);
        baseSheet = new BaseSheet(this, noteParser.getBeats(), data.getTempo());
        sheetBaseLinear.addView(baseSheet);
        NoteStore noteStore = new NoteStore(noteList, sheetRecyAdapter, noteParser.getBeats());
        seekBar_music.setMax(noteParser.getNoteLength()-1);

        for (int i = 0; i < noteParser.getNoteLength(); i++) {
            noteStore.setTempData(noteParser.getNoteAt(i));
            if(!noteStore.getTempData().node)
                noteStore.saveNote(i);
        }

        if(noteList.get(noteList.size()-1).node){
            noteList.remove(noteList.size()-1);
        }

        for(int j=0; j<noteList.size()/4; j++){
            sheetAppender = new SheetAppender(getApplicationContext());
            sheetBaseLinear.addView(sheetAppender);
        }

        if(sheetBaseLinear.getChildCount()!=1){
            sheetBaseLinear.removeViewAt(sheetBaseLinear.getChildCount()-1);
            int sheetWidth = sheetBaseLinear.getChildCount()*600;
            int noteWidth = linearLayoutManager.getItemCount()*150;
            sheetAppender = new SheetAppender(getApplicationContext(), true, noteWidth-sheetWidth+200);
            sheetBaseLinear.addView(sheetAppender);
        }else{
            sheetAppender = new SheetAppender(getApplicationContext(), true, 100);
            sheetBaseLinear.addView(sheetAppender);
        }
    }
}
