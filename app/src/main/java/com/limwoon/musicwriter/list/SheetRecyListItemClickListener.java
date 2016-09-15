package com.limwoon.musicwriter.list;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.limwoon.musicwriter.MusicViewActivity;
import com.limwoon.musicwriter.MusicWriteActivity;
import com.limwoon.musicwriter.SQLite.DefineSQL;

/**
 * Created by ejdej on 2016-08-19.
 */
public class SheetRecyListItemClickListener implements RecyclerView.OnItemTouchListener {
    Context context;
    SQLiteDatabase db;
    Cursor cursor;

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public SheetRecyListItemClickListener(Context context, SQLiteDatabase db, Cursor cursor){
        this.context = context;
        this.db = db;
        this.cursor = cursor;
    }

    GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
            Log.d("gesture->", "onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            Log.d("gesture->", "onSingleTabUp");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            Log.d("gesture->", "LongPress"+motionEvent.toString());
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    });

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View view = rv.findChildViewUnder(e.getX(),e.getY());

        if(view!=null && gestureDetector.onTouchEvent(e)){
            int pos = rv.getChildAdapterPosition(view);
            Log.d("gesturePos->", ""+pos);

            cursor.moveToPosition(pos);
            Log.d("cursorGetVal->", ""+cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE)));
            int beat = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_BEATS));
            String musicData = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE));
            int ID = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID));
            Intent intent = new Intent(context, MusicViewActivity.class);
            intent.putExtra("id", ID);
            intent.putExtra("beats", beat);
            intent.putExtra("musicData", musicData);
            context.startActivity(intent);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
