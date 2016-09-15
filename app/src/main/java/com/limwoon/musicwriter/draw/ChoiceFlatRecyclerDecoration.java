package com.limwoon.musicwriter.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ejdej on 2016-08-05.
 */
public class ChoiceFlatRecyclerDecoration extends RecyclerView.ItemDecoration {

    Paint paint;

    public ChoiceFlatRecyclerDecoration(){
        paint=new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLUE);
    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top = parent.getPaddingTop();
        int bottom = top-50;

        c.drawLine(left, top, right, bottom, paint);
    }
}
