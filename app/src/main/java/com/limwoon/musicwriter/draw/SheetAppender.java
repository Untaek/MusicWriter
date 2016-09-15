package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by ejdej on 2016-08-10.
 */
public class SheetAppender extends BaseSheet {

    private boolean isEnd = false;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public SheetAppender(Context context) {
        super(context);
        startX=0;
        endX=600;

    }

    public SheetAppender(Context context, boolean isEnd){
        super(context);
        startX=0;
        endX=600;
        if (isEnd)
            endX=0;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(600, baseLineLength+rowDistance*2+startY);
    }
}
