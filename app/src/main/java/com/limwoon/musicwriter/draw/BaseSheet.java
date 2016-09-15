package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.limwoon.musicwriter.MusicWriteActivity;
import com.limwoon.musicwriter.R;

/**
 * Created by ejdej on 2016-08-01.
 */


// 악보 그려주는 클래스


public class BaseSheet extends View {
    public BaseSheet(Context context, int beat) {
        super(context);
        this.beat=beat;
    }
    public BaseSheet(Context context){
        super(context);
    }

    Resources res;
    int beat=-1;

    // 높은 음자리표 비트맵과 크기지정
    Bitmap clef;
    int clefWidth = 120;
    int clefHeight =(int) (clefWidth*2.37837837);

    //
    Paint linePaint;
    Paint baseLinePaint;
    int rowDistance = 44;
    int startX = 50;
    int endX = 900;
    int startY = 200;

    int sheetDistance = (rowDistance*5) + 150;
    int baseLineLength =  sheetDistance + rowDistance*6;

    int startTABX = startX+50;
    int startTABY = sheetDistance + startY + rowDistance*2 + 24;

    void init(){
        if(beat==-1){
            beat = MusicWriteActivity.beatIndex;
        }

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(5);
        linePaint.setTextSize(114);

        baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseLinePaint.setStyle(Paint.Style.FILL);
        baseLinePaint.setStrokeWidth(16);
        baseLinePaint.setTextSize(70);

        res = getResources();
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 16;
        if(res != null)
            clef = BitmapFactory.decodeResource(res, R.drawable.trebleclef, opt);
            clef = Bitmap.createScaledBitmap(clef, clefWidth, clefHeight, false);
    }
    void draw5Line(Canvas canvas){
        int i=1;
        while (true){
            canvas.drawLine(
                    startX,
                    startY+rowDistance*i,
                    endX,
                    startY+rowDistance*i,
                    linePaint
            );
            i++;
            if(i>5){
                break;
            }
        }
    }

    void drawTabLine(Canvas canvas){
        int i=1;
        while (true){
            canvas.drawLine(
                    startX,
                    startY + sheetDistance + rowDistance*i,
                    endX,
                    startY + sheetDistance + rowDistance*i,
                    linePaint
            );
            i++;
            if(i>6){
                break;
            }
        }
    }

    void drawBaseLine(Canvas canvas){
        canvas.drawLine(startX,
                startY+rowDistance,
                startX,
                startY+baseLineLength,
                baseLinePaint
        );
    }

    void drawTAB(Canvas canvas){
        canvas.drawText("T", startTABX, startTABY, baseLinePaint);
        canvas.drawText("A", startTABX, startTABY+60, baseLinePaint);
        canvas.drawText("B", startTABX, startTABY+120, baseLinePaint);
    }

    void drawClef(Canvas canvas){
        canvas.drawBitmap(clef, 70, startY, null);
    }

    void drawBeat(Canvas canvas){
        String child = beat+2+"";
        String parent = 4+"";

        canvas.drawText(child, 180, startY+130, linePaint);
        canvas.drawText(parent, 180, startY+220, linePaint);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        if(getClass()==BaseSheet.class){
            drawBaseLine(canvas);
            drawTAB(canvas);
            drawClef(canvas);
            drawBeat(canvas);
        }
        draw5Line(canvas);
        drawTabLine(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(endX, baseLineLength+rowDistance*2+startY);
    }
}
