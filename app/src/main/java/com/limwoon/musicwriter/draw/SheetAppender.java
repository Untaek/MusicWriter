package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by ejdej on 2016-08-10.
 */
public class SheetAppender extends BaseSheet {

    private boolean isEnd = false;
    private int length;

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

    public SheetAppender(Context context, boolean isEnd, int length){
        super(context);
        startX=0;
        this.isEnd=isEnd;
        if (isEnd){
            endX=length;
            this.length=length;
        }

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        if(isEnd){
            baseLinePaint.setStrokeWidth(20);
            canvas.drawLine(endX-10,
                    startY+rowDistance,
                    endX-10,
                    startY+baseLineLength,
                    baseLinePaint
            );
            canvas.drawLine(endX-32,
                    startY+rowDistance,
                    endX-32,
                    startY+baseLineLength,
                    linePaint
            );
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(600, baseLineLength+rowDistance*2+startY);
        if(isEnd){
            setMeasuredDimension(endX+100, baseLineLength+rowDistance*2+startY);
        }
    }
}
