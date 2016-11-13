package com.limwoon.musicwriter.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limwoon.musicwriter.MusicViewActivity;
import com.limwoon.musicwriter.MusicWriteActivity;
import com.limwoon.musicwriter.NativeClass;
import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.SQLite.DefineSQL;
import com.limwoon.musicwriter.SQLite.SheetDbHelper;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.sounds.Sounds;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-08-18.
 */
public class SheetRecyListAdapter extends RecyclerView.Adapter<SheetRecyListAdapter.CustomHolder> {

    ArrayList<SheetData> sheetList;
    Context context;
    ProgressDialog dialog;

    SheetDbHelper sheetDbHelper;
    Cursor cursor;
    SQLiteDatabase db;

    boolean isPlay=false;
    Thread playThread;

    private void showProgress(){
        Thread thread = new Thread(new Runnable() {
            Handler handler = new Handler();
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new ProgressDialog(context);
                        dialog.setMessage("불러오는 중 입니다");
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                });
            }
        });
        thread.start();
    }

    private void closeProgress(){
        if(dialog!=null){
            dialog.cancel();
        }
    }

    public SheetRecyListAdapter(ArrayList<SheetData> sheetList, Context context){
        this.sheetList=sheetList;
        this.context=context;
    }

    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_sheet_recy_my, parent, false);

        return new CustomHolder(cardView);
    }

    @Override
    public void onBindViewHolder(CustomHolder holder, int position) {
        holder.titleTextView.setText(sheetList.get(position).getTitle());
        myOnClickListener(holder.card_view_linear_first, position);
        myOnClickListener(holder.btnModify, position);
        myOnClickListener(holder.btnDelete, position);
        myOnClickListener(holder.btnPlay, position);
    }

    @Override
    public int getItemCount() {
        return sheetList.size();
    }

    private void myOnClickListener(View view, final int pos){
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sheetDbHelper  = new SheetDbHelper(context);
                db = sheetDbHelper.getReadableDatabase();

                String[] columns = {
                        DefineSQL._ID,
                        DefineSQL.COLUMN_NAME_TITLE,
                        DefineSQL.COLUMN_NAME_AUTHOR,
                        DefineSQL.COLUMN_NAME_BEATS,
                        DefineSQL.COLUMN_NAME_NOTE,
                        DefineSQL.COLUMN_NAME_TEMPO
                };

                cursor = db.query(
                        DefineSQL.TABLE_NAME,
                        columns,
                        DefineSQL._ID+"="+sheetList.get(pos).getId(),
                        null,
                        null,
                        null,
                        null,
                        null
                );

                cursor.moveToFirst();

                switch (view.getId()){
                    case R.id.card_view_linear_first:
                        //showProgress();
                        int beat = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_BEATS));
                        String musicData = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE));
                        int ID = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_TITLE));
                        String author = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_AUTHOR));
                        int tempo = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_TEMPO));
                        Intent intent = new Intent(context, MusicViewActivity.class);
                        intent.putExtra("id", ID);
                        intent.putExtra("beats", beat);
                        intent.putExtra("musicData", musicData);
                        intent.putExtra("title", title);
                        intent.putExtra("author", author);
                        intent.putExtra("tempo", tempo);
                        //closeProgress();
                        context.startActivity(intent);
                        break;
                    case R.id.btn_modify:
                        int beats = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_BEATS));
                        String musicDatas = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE));
                        int IDs = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID));
                        String title2 = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_TITLE));
                        String author2 = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_AUTHOR));
                        int tempo2 = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_TEMPO));
                        Intent intentMod = new Intent(context, MusicWriteActivity.class);
                        intentMod.putExtra("isEdit", true);
                        intentMod.putExtra("id", sheetList.get(pos).getId());
                        intentMod.putExtra("beatIndex", beats);
                        intentMod.putExtra("title", title2);
                        intentMod.putExtra("author", author2);
                        intentMod.putExtra("tempo", tempo2);
                        context.startActivity(intentMod);

                        break;
                    case R.id.btn_delete:
                        AlertDialog dialog = new AlertDialog.Builder(context).
                                setTitle("정말로 삭제하시겠습니까").
                                setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SheetDbHelper dbHelper = new SheetDbHelper(context);
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        db.delete(DefineSQL.TABLE_NAME, DefineSQL._ID+"="+sheetList.get(pos).getId(),null);
                                        sheetList.remove(pos);
                                        notifyItemRemoved(pos);

                                        SharedPreferences sharedPreferences = context.getSharedPreferences("TempNoteData", context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.apply();
                                    }
                                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        }).show();
                        break;
                    case R.id.btn_play:

                        if(!isPlay){
                            String data = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE));
                            NoteParser noteParser = new NoteParser(data);
                            final ArrayList<NoteData> notes = new ArrayList<>();
                            for(int i=0; i<noteParser.getNoteLength(); i++) {
                                notes.add(noteParser.getNoteAt(i));
                            }
                            final ImageView play = (ImageView) view;

                            playThread = new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    play.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            play.setImageResource(R.drawable.ic_pause_black_24dp);
                                        }
                                    });
                                    isPlay=true;
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    for(int i=0; i<notes.size(); i++) {
                                        if (notes.get(i).node) continue;
                                        for (int j = 0; j < 6; j++) {
                                            if (notes.get(i).tone[j] != -1) {
                                                NativeClass.setPlayingBufferQueue(j, notes.get(i).tone[j]);
                                            }
                                        }
                                        try {
                                            Thread.sleep(Sounds.getDuration(notes.get(i).duration, sheetList.get(pos).getTempo()));
                                            NativeClass.setStopBufferQueue();

                                        } catch (InterruptedException e) { // 정지버튼 클릭
                                            play.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                                                }
                                            });
                                            isPlay=false;
                                            NativeClass.setStopBufferQueue();
                                            break;
                                        }
                                    }
                                    play.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                                        }
                                    });
                                    isPlay=false;
                                }
                            });
                            playThread.start();
                        }
                        else {
                            playThread.interrupt();
                        }
                        break;

                }
            }
        });
    }


    public static final class CustomHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        ImageView btnPlay;
        ImageView btnModify;
        ImageView btnDelete;
        LinearLayout card_view_linear_first;

        public CustomHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.testTextView);
            btnPlay= (ImageView) itemView.findViewById(R.id.btn_play);
            btnModify= (ImageView) itemView.findViewById(R.id.btn_modify);
            btnDelete= (ImageView) itemView.findViewById(R.id.btn_delete);
            card_view_linear_first = (LinearLayout) itemView.findViewById(R.id.card_view_linear_first);
        }
    }
}
