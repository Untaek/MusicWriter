package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ejdej on 2016-08-25.
 */
public class NoteRecyclerClickListener implements RecyclerView.OnItemTouchListener {

    Context context;
    NoteRecyclerAdapter adapter;
    public int clickedPosition=-1;

    public NoteRecyclerClickListener(Context context, NoteRecyclerAdapter adapter){
        this.context=context;
        this.adapter=adapter;
    }

    GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    });
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View view = rv.findChildViewUnder(e.getX(), e.getY());
        int pos = rv.getChildAdapterPosition(view);
        if (view != null && gestureDetector.onTouchEvent(e)) {
            Log.d("Touched!!", "" + rv.getChildAdapterPosition(view));
            Log.d("adapterINdex",""+adapter.index);
            adapter.index=pos;
            clickedPosition = pos;
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
