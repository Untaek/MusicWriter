package com.limwoon.musicwriter.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by ejdej on 2016-08-13.
 */
public class NoteRecyclerViewDecoration extends RecyclerView.ItemDecoration {
    RecyclerView recyclerView;
    Paint paint = new Paint();
    int childWidth=0;
    int childHeight;

    int topBottomPadding = 30;

    public NoteRecyclerViewDecoration(){
        paint.setStrokeWidth(10);
        paint.setColor(Color.YELLOW);
    }

    public int setChildWidth(int pos){
        childWidth = pos* 200;
        return childWidth;
    }

    public int setChildHeight(int pos){
        return 0;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        childWidth=parent.getChildAt(state.getItemCount()-1).getMeasuredWidth();

        c.drawRect(34+childWidth*(state.getItemCount()-1)+parent.getPaddingLeft(),
                topBottomPadding,
                134+childWidth*(state.getItemCount()-1)+parent.getPaddingLeft(),
                parent.getMeasuredHeight()-topBottomPadding,
                paint);
    }
}
