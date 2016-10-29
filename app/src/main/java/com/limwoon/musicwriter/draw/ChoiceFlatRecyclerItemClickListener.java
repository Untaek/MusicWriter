package com.limwoon.musicwriter.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.NoteStore;

/**
 * Created by ejdej on 2016-08-05.
 */
public class ChoiceFlatRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    int position;
    ChoiceFlatRecyclerAdapter adapter;
    Context context;
    NoteStore noteStore;
    LinearLayout linearLayout;
    public int selectedStringNum;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    CheckBox checkBox6;

    public int getPosition(){
        return position;
    }

    public ChoiceFlatRecyclerItemClickListener(Context context, NoteStore noteStore, int selectedStringNum, LinearLayout linearLayout){
        this.context = context;
        this.noteStore = noteStore;
        this.selectedStringNum = selectedStringNum;
        this.linearLayout = linearLayout;
        checkBox1 = (CheckBox) linearLayout.findViewById(R.id.checkbox_1st_string);
        checkBox2 = (CheckBox) linearLayout.findViewById(R.id.checkbox_2nd_string);
        checkBox3 = (CheckBox) linearLayout.findViewById(R.id.checkbox_3rd_string);
        checkBox4 = (CheckBox) linearLayout.findViewById(R.id.checkbox_4th_string);
        checkBox5 = (CheckBox) linearLayout.findViewById(R.id.checkbox_5th_string);
        checkBox6 = (CheckBox) linearLayout.findViewById(R.id.checkbox_6th_string);
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
        adapter = (ChoiceFlatRecyclerAdapter) rv.getAdapter();
        if(view!=null && gestureDetector.onTouchEvent(e)){
           position = rv.getChildLayoutPosition(rv.findChildViewUnder(e.getX(), e.getY()));
            adapter.list.get(position).setSelected(true);
            adapter.list.get(position).setFlat(position);

            noteStore.cacheNote(selectedStringNum, position);
            Toast.makeText(context ,position+ "번 플랫 선택", Toast.LENGTH_SHORT).show();


            if(selectedStringNum == 0)
                checkBox1.setChecked(true);
            else if(selectedStringNum ==1)
                checkBox2.setChecked(true);
            else if(selectedStringNum ==2)
                checkBox3.setChecked(true);
            else if(selectedStringNum ==3)
                checkBox4.setChecked(true);
            else if(selectedStringNum ==4)
                checkBox5.setChecked(true);
            else if(selectedStringNum ==5)
                checkBox6.setChecked(true);

        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d("onTouchEvent -->", "onTouchEvent");
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        Log.d("onRequestDisallow -->", "onRequestDisallow");
    }


}
