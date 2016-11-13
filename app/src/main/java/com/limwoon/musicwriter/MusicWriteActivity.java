package com.limwoon.musicwriter;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.SQLite.DefineSQL;
import com.limwoon.musicwriter.SQLite.SheetDbHelper;
import com.limwoon.musicwriter.data.ChoiceFlatData;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteStore;
import com.limwoon.musicwriter.draw.BaseSheet;
import com.limwoon.musicwriter.draw.ChoiceFlatRecyclerAdapter;
import com.limwoon.musicwriter.draw.ChoiceFlatRecyclerDecoration;
import com.limwoon.musicwriter.draw.ChoiceFlatRecyclerItemClickListener;
import com.limwoon.musicwriter.draw.NoteRecyclerAdapter;
import com.limwoon.musicwriter.draw.NoteRecyclerClickListener;
import com.limwoon.musicwriter.draw.NoteRecyclerViewDecoration;
import com.limwoon.musicwriter.draw.NoteBitmapMaker;
import com.limwoon.musicwriter.draw.SheetAppender;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.list.SheetRecyDivider;
import com.limwoon.musicwriter.sounds.Sounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MusicWriteActivity extends AppCompatActivity {

    public static int beatIndex;

    // DB
    long id = -1;
    Cursor cursor;
    String title;
    String author;
    int tempo;

    ArrayList<NoteData> noteList;
    NoteStore noteStore;
    BaseSheet musicBaseSheet;
    SheetAppender sheetAppender;
    LinearLayout sheetWrapView;
    RecyclerView noteRecyclerView;
    NoteRecyclerAdapter noteAdapter;
    NoteRecyclerViewDecoration noteRecyclerViewDecoration;
    LinearLayoutManager noteLinearLayoutManager;
    NoteRecyclerClickListener noteRecyclerClickListener;
    HorizontalScrollView horizontalScrollView;

    TextView textViewString;
    CheckBox checkBox1st;
    CheckBox checkBox2nd;
    CheckBox checkBox3rd;
    CheckBox checkBox4th;
    CheckBox checkBox5th;
    CheckBox checkBox6th;
    LinearLayout string_6_container;
    LinearLayout string_5_container;
    LinearLayout string_4_container;
    LinearLayout string_3_container;
    LinearLayout string_2_container;
    LinearLayout string_1_container;
    int selectedStringNum = 0;

    SeekBar seekBarSelectBeat;
    Button btnInsertRest;
    int duration = 2; // 1 : 16분음표, 2 : 8분음표, 4: 4분음표, 8 : 2분음표, 16 : 온음표
    NoteBitmapMaker noteBitmapMaker;
    ImageView imageViewExamNote;
    ImageView imageViewExamRest;

    Button btnInsertNote;
    RecyclerView choiceFlatRecyclerView;
    ChoiceFlatRecyclerAdapter choiceFlatRecyclerAdapter;
    LinearLayoutManager choiceFlatLayoutManager;
    ChoiceFlatRecyclerDecoration choiceFlatRecyclerDecoration;
    ArrayList<ChoiceFlatData> flatList;
    ChoiceFlatRecyclerItemClickListener choiceFlatRecyclerItemClickListener;

    int i;
    ImageView btnPlayMusic;
    SeekBar musicSeekBar;
    boolean isPlay = false;
    Thread playThread;
    int musicProgress = 0;

    private void setFlatSelector(int num) {
        if (noteStore.getTempData().tone[num] != -1)
            flatList.get(noteStore.getTempData().tone[num]).setSelected(true);

        for (int i = 0; i < 6; i++) {
            if (i != num && noteStore.getTempData().tone[i] != -1 && noteStore.getTempData().tone[i] != noteStore.getTempData().tone[num])
                flatList.get(noteStore.getTempData().tone[i]).setSelected(false);
        }
        choiceFlatRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_write);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getIntExtra("beatIndex", -1) != -1) {
            beatIndex = getIntent().getIntExtra("beatIndex", -1);
            tempo = getIntent().getIntExtra("tempo", -1);
        }
        id = getIntent().getLongExtra("id", -1);
        title = getIntent().getStringExtra("title");
        author = getIntent().getStringExtra("author");

        //노트 리스트와 NoteStore 연결(리스트 조작 클래스)
        noteList = new ArrayList<>();
        noteAdapter = new NoteRecyclerAdapter(this, noteList);
        noteStore = new NoteStore(noteList, noteAdapter, beatIndex);

        // BaseSheet 생성후 LinearLayout 에 붙임
        musicBaseSheet = new BaseSheet(this, beatIndex, tempo);
        sheetWrapView = (LinearLayout) findViewById(R.id.music_sheet_wrapper);
        sheetWrapView.addView(musicBaseSheet);

        // NoteDrawer 생성 (음표를 리스트에 넣기 위함)
        noteRecyclerView = (RecyclerView) findViewById(R.id.note_recyclerview);
        noteRecyclerViewDecoration = new NoteRecyclerViewDecoration();
        noteLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        noteRecyclerClickListener = new NoteRecyclerClickListener(this, noteAdapter);
        noteRecyclerView.addItemDecoration(noteRecyclerViewDecoration);
        noteRecyclerView.setLayoutManager(noteLinearLayoutManager);
        noteRecyclerView.setAdapter(noteAdapter);
        noteRecyclerView.addOnItemTouchListener(noteRecyclerClickListener);

        noteRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        if (id != -1) {
            NoteParser noteParser;
            SheetDbHelper dbHelper = new SheetDbHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] columns = {
                    DefineSQL._ID,
                    DefineSQL.COLUMN_NAME_TITLE,
                    DefineSQL.COLUMN_NAME_AUTHOR,
                    DefineSQL.COLUMN_NAME_BEATS,
                    DefineSQL.COLUMN_NAME_NOTE,
            };

            cursor = db.query(
                    DefineSQL.TABLE_NAME,
                    columns,
                    DefineSQL._ID + "=" + id, null, null, null, null
            );
            cursor.moveToFirst();
            noteParser = new NoteParser(cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE)));

            beatIndex = noteParser.getBeats();

            // 리스트에 하나씩 넣으면서 악보 그리기
            for (int i = 0; i < noteParser.getNoteLength(); i++) {
                noteStore.setTempData(noteParser.getNoteAt(i));
                if(!noteStore.getTempData().node){
                    noteStore.saveNote(i);
                    noteStore.cursor++;
                }
            }
            noteStore.setTempData(new NoteData());
            for (int j = 0; j < noteList.size() / 4; j++) {
                sheetAppender = new SheetAppender(getApplicationContext());
                sheetWrapView.addView(sheetAppender);
            }
        }
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        // 음악 재생
        btnPlayMusic = (ImageView) findViewById(R.id.btnMusicPlay);
        musicSeekBar = (SeekBar) findViewById(R.id.music_seek_bar);

        musicSeekBar.setMax(noteList.size() - 1);

        final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        };

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlay) {
                    musicSeekBar.setAlpha(0.7f);
                    musicSeekBar.setOnTouchListener(onTouchListener);
                    btnPlayMusic.setImageResource(R.drawable.ic_pause_black_36dp);
                    isPlay = true;
                    if (musicProgress == noteList.size() - 1) musicProgress = 0;
                    playThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (i = musicProgress; i < noteList.size(); i++) {
                                musicProgress = i;
                                horizontalScrollView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        horizontalScrollView.smoothScrollTo(150 * i - 150, 0);
                                    }
                                });
                                noteRecyclerView.getChildAt(i).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            noteRecyclerView.getChildAt(i - 2).setBackgroundColor(Color.alpha(0));
                                        } catch (NullPointerException e) {
                                            Log.d("jumpChild", "yes");
                                        }
                                        try {
                                            noteRecyclerView.getChildAt(i - 1).setBackgroundColor(Color.alpha(0));
                                        } catch (NullPointerException e) {
                                            Log.d("jumpChild", "yes");
                                        }
                                        try {
                                            noteRecyclerView.getChildAt(i).setBackgroundColor(Color.GREEN);
                                        } catch (NullPointerException e) {
                                            Log.d("Song end", "end");
                                        }
                                    }
                                });
                                if (noteList.get(i).node) continue;

                                for (int j = 0; j < 6; j++) {
                                    if (noteList.get(i).tone[j] != -1) {
                                        NativeClass.setPlayingBufferQueue(j, noteList.get(i).tone[j]);
                                    }
                                }
                                try {
                                    Thread.sleep(Sounds.getDuration(noteList.get(i).duration, tempo));
                                    NativeClass.setStopBufferQueue();
                                } catch (InterruptedException e) { // 정지버튼 클릭
                                    NativeClass.setStopBufferQueue();
                                    noteRecyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noteRecyclerView.getChildAt(musicProgress).setBackgroundColor(Color.alpha(0));
                                        }
                                    });
                                    break;
                                }
                                if (i == noteList.size() - 1) {
                                    if (i > 1)
                                        noteRecyclerView.getChildAt(i - 1).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    noteRecyclerView.getChildAt(i - 1).setBackgroundColor(Color.alpha(0));
                                                } catch (NullPointerException e) {
                                                    Log.d("nullpointer", "" + i);
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
                                if (playThread.isInterrupted()) break;
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
                                    btnPlayMusic.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                                }
                            });
                        }
                    });
                    playThread.start();
                } else {
                    musicSeekBar.setAlpha(1);
                    musicSeekBar.setOnTouchListener(null);
                    isPlay = false;
                    btnPlayMusic.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                    if (playThread != null) {
                        playThread.interrupt();
                    }
                }
            }
        });

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                if (b) {
                    horizontalScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            horizontalScrollView.smoothScrollTo(150 * i - 150, 0);
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


        /////////////////////////////////////////////////
        // 여기서부터는 악보에 추가시키는 버튼 및 기능 //
        /////////////////////////////////////////////////

        // 음표 쉼표 넣는 버튼
        btnInsertNote = (Button) findViewById(R.id.insert_note_btn);
        btnInsertRest = (Button) findViewById(R.id.btnInsertRest);

        seekBarSelectBeat = (SeekBar) findViewById(R.id.seekBar_select_beat);
        imageViewExamNote = (ImageView) findViewById(R.id.imageview_exam_note);
        imageViewExamRest = (ImageView) findViewById(R.id.imageview_exam_rest);

        checkBox1st = (CheckBox) findViewById(R.id.checkbox_1st_string);
        checkBox2nd = (CheckBox) findViewById(R.id.checkbox_2nd_string);
        checkBox3rd = (CheckBox) findViewById(R.id.checkbox_3rd_string);
        checkBox4th = (CheckBox) findViewById(R.id.checkbox_4th_string);
        checkBox5th = (CheckBox) findViewById(R.id.checkbox_5th_string);
        checkBox6th = (CheckBox) findViewById(R.id.checkbox_6th_string);

        string_1_container = (LinearLayout) findViewById(R.id.checkbox_1st_string_container);
        string_2_container = (LinearLayout) findViewById(R.id.checkbox_2nd_string_container);
        string_3_container = (LinearLayout) findViewById(R.id.checkbox_3rd_string_container);
        string_4_container = (LinearLayout) findViewById(R.id.checkbox_4th_string_container);
        string_5_container = (LinearLayout) findViewById(R.id.checkbox_5th_string_container);
        string_6_container = (LinearLayout) findViewById(R.id.checkbox_6th_string_container);

        //플랫 선택하는 부분
        textViewString = (TextView) findViewById(R.id.textview_choice_string);
        choiceFlatRecyclerView = (RecyclerView) findViewById(R.id.recycler_choice_flat);
        flatList = new ArrayList<>();
        choiceFlatRecyclerAdapter = new ChoiceFlatRecyclerAdapter(getApplicationContext(), flatList, choiceFlatRecyclerView);
        choiceFlatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        choiceFlatRecyclerView.setAdapter(choiceFlatRecyclerAdapter);
        choiceFlatRecyclerView.setLayoutManager(choiceFlatLayoutManager);
        choiceFlatRecyclerDecoration = new ChoiceFlatRecyclerDecoration();
        choiceFlatRecyclerView.addItemDecoration(choiceFlatRecyclerDecoration);
        choiceFlatRecyclerItemClickListener = new ChoiceFlatRecyclerItemClickListener(getApplicationContext(), noteStore, selectedStringNum, (LinearLayout) findViewById(R.id.checkbox_wrapper));
        choiceFlatRecyclerView.addOnItemTouchListener(choiceFlatRecyclerItemClickListener);
        choiceFlatRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));


        for (int i = 0; i <= 20; i++) {
            flatList.add(new ChoiceFlatData(i));
        }

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.getId() == R.id.checkbox_1st_string) {
                    if (b) {
                    } else {
                        noteStore.cacheDelNote(0);
                    }
                }
                if (compoundButton.getId() == R.id.checkbox_2nd_string) {
                    if (b) {
                    } else {
                        noteStore.cacheDelNote(1);
                    }
                }
                if (compoundButton.getId() == R.id.checkbox_3rd_string) {
                    if (b) {
                    } else {
                        noteStore.cacheDelNote(2);
                    }
                }
                if (compoundButton.getId() == R.id.checkbox_4th_string) {
                    if (b) {
                    } else {
                        noteStore.cacheDelNote(3);
                    }
                }
                if (compoundButton.getId() == R.id.checkbox_5th_string) {
                    if (b) {
                    } else {
                        noteStore.cacheDelNote(4);
                    }
                }
                if (compoundButton.getId() == R.id.checkbox_6th_string) {
                    if (b) {
                    } else {
                        noteStore.cacheDelNote(5);
                    }
                }

            }
        };

    View.OnClickListener string_containerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkbox_1st_string_container:
                    textViewString.setText("1번 줄");

                    choiceFlatRecyclerItemClickListener.selectedStringNum = 0;
                    setFlatSelector(0);
                    break;
                case R.id.checkbox_2nd_string_container:
                    textViewString.setText("2번 줄");

                    choiceFlatRecyclerItemClickListener.selectedStringNum = 1;
                    setFlatSelector(1);
                    break;
                case R.id.checkbox_3rd_string_container:
                    textViewString.setText("3번 줄");

                    choiceFlatRecyclerItemClickListener.selectedStringNum = 2;
                    setFlatSelector(2);
                    break;
                case R.id.checkbox_4th_string_container:
                    textViewString.setText("4번 줄");

                    choiceFlatRecyclerItemClickListener.selectedStringNum = 3;
                    setFlatSelector(3);
                    break;
                case R.id.checkbox_5th_string_container:
                    textViewString.setText("5번 줄");

                    choiceFlatRecyclerItemClickListener.selectedStringNum = 4;
                    setFlatSelector(4);
                    break;
                case R.id.checkbox_6th_string_container:
                    textViewString.setText("6번 줄");

                    choiceFlatRecyclerItemClickListener.selectedStringNum = 5;
                    setFlatSelector(5);
            }
        }
    };

        string_1_container.setOnClickListener(string_containerClick);
        string_2_container.setOnClickListener(string_containerClick);
        string_3_container.setOnClickListener(string_containerClick);
        string_4_container.setOnClickListener(string_containerClick);
        string_5_container.setOnClickListener(string_containerClick);
        string_6_container.setOnClickListener(string_containerClick);

        checkBox1st.setOnCheckedChangeListener(checkedChangeListener);
        checkBox2nd.setOnCheckedChangeListener(checkedChangeListener);
        checkBox3rd.setOnCheckedChangeListener(checkedChangeListener);
        checkBox4th.setOnCheckedChangeListener(checkedChangeListener);
        checkBox5th.setOnCheckedChangeListener(checkedChangeListener);
        checkBox6th.setOnCheckedChangeListener(checkedChangeListener);

        noteBitmapMaker = new NoteBitmapMaker(getApplicationContext());

        /// 노트 삽입 리스너
        btnInsertNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noteStore.isHasCache()) {
                    noteStore.cacheDuration(duration);
                    noteStore.saveNote(noteStore.cursor);
                    noteStore.cursor++;

                    int backSize = sheetWrapView.getMeasuredWidth();
                    if (noteList.size() > 1 && backSize - noteList.size()*150 <=600) {
                        sheetAppender = new SheetAppender(getApplicationContext());
                        sheetWrapView.addView(sheetAppender);
                    }

                    checkBox1st.setChecked(false);
                    checkBox2nd.setChecked(false);
                    checkBox3rd.setChecked(false);
                    checkBox4th.setChecked(false);
                    checkBox5th.setChecked(false);
                    checkBox6th.setChecked(false);

                    musicSeekBar.setMax(noteList.size()-1);
                    for(int i=0; i<flatList.size(); i++)
                        flatList.get(i).setSelected(false);
                    choiceFlatRecyclerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "넣을 음을 선택 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnInsertRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteStore.cacheIsRest(true);
                noteStore.cacheDuration(duration);
                noteStore.saveNote(noteStore.cursor);
                noteStore.cursor++;

                int backSize = sheetWrapView.getMeasuredWidth();
                if (noteList.size() > 1 && backSize - noteList.size()*150 <=600) {
                    sheetAppender = new SheetAppender(getApplicationContext());
                    sheetWrapView.addView(sheetAppender);
                }
                checkBox1st.setChecked(false);
                checkBox2nd.setChecked(false);
                checkBox3rd.setChecked(false);
                checkBox4th.setChecked(false);
                checkBox5th.setChecked(false);
                checkBox6th.setChecked(false);

                musicSeekBar.setMax(noteList.size()-1);
            }
        });

        imageViewExamNote.setImageBitmap(NoteBitmapMaker.getBitmap_preview(duration));
        imageViewExamRest.setImageBitmap(NoteBitmapMaker.getBitmap_preview(duration + 5));

        seekBarSelectBeat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // 0 : 16분음표, 1 : 8분음표, 2: 4분음표, 3 : 2분음표, 4 : 온음표
                duration = i;

                imageViewExamNote.setImageBitmap(NoteBitmapMaker.getBitmap_preview(duration));
                imageViewExamRest.setImageBitmap(NoteBitmapMaker.getBitmap_preview(duration + 5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    long clickTime = 0;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        long systemTime = System.currentTimeMillis();
        Toast toast = Toast.makeText(this,"뒤로 가려면 한번 더 누르세요", Toast.LENGTH_SHORT);

        if(clickTime < systemTime){
            clickTime = systemTime+1500;
            toast.show();
        }else if(clickTime >= systemTime){
            toast.cancel();
            finish();
            if(getIntent().getBooleanExtra("isEdit", false)){
                overridePendingTransition(R.anim.activity_slide_right, R.anim.activity_slide_right_gone);
            }
        }
    }

    // DB 저장하기 --------------------------------------
    SheetDbHelper sheetDbHelper = new SheetDbHelper(this);
    int currentId;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            currentId = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID));
            Log.d("DefineSQL_ID", ""+currentId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (item.getItemId() == R.id.action_save_new) {
            View editVIew = getLayoutInflater().inflate(R.layout.dialog_save_new, null);
            final EditText editText = (EditText) editVIew.findViewById(R.id.editText_title);
            editText.requestFocus();
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("제목을 입력해주세요")
                    .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int index) {
                            String mTitle = editText.getText().toString();

                            JSONObject jsonObject;
                            JSONArray jsonArray = new JSONArray();
                            JSONObject toneJsonObj;
                            try {
                                for (int i = 0; i < noteList.size(); i++) {
                                    jsonObject = new JSONObject();
                                    toneJsonObj = new JSONObject();
                                    jsonObject.put("index", i);
                                    for (int j = 0; j < 6; j++) {
                                        toneJsonObj.put("tone" + j, noteList.get(i).tone[j]);
                                    }
                                    jsonObject.put("tone", toneJsonObj);
                                    jsonObject.put("duration", noteList.get(i).duration);
                                    jsonObject.put("isRest", noteList.get(i).rest);
                                    jsonObject.put("isNode", noteList.get(i).node);
                                    jsonArray.put(jsonObject);
                                }
                                jsonObject = new JSONObject();
                                jsonObject.put("beats", beatIndex);
                                jsonObject.put("author", "나");
                                jsonObject.put("title", mTitle);
                                jsonObject.put("note", jsonArray);

                                SQLiteDatabase db = sheetDbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(DefineSQL.COLUMN_NAME_TITLE, mTitle);
                                values.put(DefineSQL.COLUMN_NAME_AUTHOR, "나");
                                values.put(DefineSQL.COLUMN_NAME_BEATS, beatIndex);
                                values.put(DefineSQL.COLUMN_NAME_NOTE, jsonObject.toString());
                                values.put(DefineSQL.COLUMN_NAME_TEMPO, tempo);

                                title = mTitle;
                                author = "나";

                                db.insert(DefineSQL.TABLE_NAME, null, values);

                                db = sheetDbHelper.getReadableDatabase();

                                String[] cols = {DefineSQL._ID};

                                Cursor cursor = db.query(
                                        DefineSQL.TABLE_NAME,
                                        cols,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                );

                                cursor.moveToLast();
                                currentId = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID));
                                Log.d("currentid", ""+currentId);

                                /*
                                SharedPreferences sharedPreferences = getSharedPreferences("TempNoteData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("noteData", jsonArray.toString());
                                editor.apply();
*/
                                Toast.makeText(getApplicationContext(), "새로 저장합니다", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("취소", null)
                    .setView(editVIew)
                    .create();

            dialog.show();

        } else if (item.getItemId() == R.id.action_save_again) {
            Toast.makeText(this, "기존 악보에 저장합니다", Toast.LENGTH_SHORT).show();
            JSONObject jsonObject;
            JSONArray jsonArray = new JSONArray();
            JSONObject toneJsonObj;
            try {
                for (int i = 0; i < noteList.size(); i++) {
                    jsonObject = new JSONObject();
                    toneJsonObj = new JSONObject();
                    jsonObject.put("index", i);
                    for (int j = 0; j < 6; j++) {
                        toneJsonObj.put("tone" + j, noteList.get(i).tone[j]);
                    }
                    jsonObject.put("tone", toneJsonObj);
                    jsonObject.put("duration", noteList.get(i).duration);
                    jsonObject.put("isRest", noteList.get(i).rest);
                    jsonObject.put("isNode", noteList.get(i).node);
                    jsonArray.put(jsonObject);
                }
                jsonObject = new JSONObject();
                jsonObject.put("beats", beatIndex);
                jsonObject.put("author", author);
                jsonObject.put("title", title);
                jsonObject.put("note", jsonArray);
                Log.d("JSONDATAobj->", "" + jsonObject.toString());

                SQLiteDatabase db = sheetDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DefineSQL.COLUMN_NAME_TITLE, title);
                values.put(DefineSQL.COLUMN_NAME_AUTHOR, author);
                values.put(DefineSQL.COLUMN_NAME_BEATS, beatIndex);
                values.put(DefineSQL.COLUMN_NAME_NOTE, jsonObject.toString());
                values.put(DefineSQL.COLUMN_NAME_TEMPO, tempo);

                db.update(DefineSQL.TABLE_NAME, values,DefineSQL._ID+"="+currentId, null);

                SharedPreferences sharedPreferences = getSharedPreferences("TempNoteData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("noteData", jsonObject.toString());
                editor.apply();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else if(item.getItemId() == android.R.id.home){
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mysic_write_save, menu);
        return true;
    }

}
