package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.limwoon.musicwriter.data.NoteData;

/**
 * Created by ejdej on 2016-08-03.
 */
public class Note extends View {
    public Note(Context context, NoteData data) {
        super(context);
        this.context = context;
        this.noteData = data;
    }

    Paint paint;
    Paint linePaint;
    RectF rect;

    NoteData noteData;
    int[] note;
    boolean isRest;
    int duration;
    int noteWeight;
    Context context;
    Resources res;

    int x = 70;
    int y = 160;

    float rectX;
    float rectY;

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        linePaint = new Paint();
        linePaint.setStrokeWidth(7);
        rect = new RectF(40, 345 ,100, 385);

        res = context.getResources();
    }

    private void noteStartOdd(int index, int step){
        noteWeight=((note[index]/2)*22);
        noteWeight+=(22*step);
    }

    private void noteStartEven(int index, int step){
        noteWeight=((note[index]-1)/2)*22;
        noteWeight+=(22*step);
    }

    private void drawNode(Canvas canvas){
        canvas.drawLine(75, y+86, 75, y+86+176, linePaint);
        canvas.drawLine(75, y+454,75, y+454+218, linePaint);
    }

    private void drawSharp(Canvas canvas, int y){
        canvas.drawBitmap(NoteRestExam.sharpBitmap[0], -10,y-40, null);
    }

    private void noteStartBeforeFirstStep(int index){
        noteWeight=(note[index]/2*22);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();

        note = noteData.tone;
        duration=noteData.duration;

        for(int i=0; i<6; i++){
            if(noteData.isAddBtn()){
                break;
            }
            if(note[i] != -1 && !noteData.rest){
                if(i==0){
                    if(note[i]>19) {
                        noteStartOdd(i,2);
                    }
                    else if(note[i]>12) {
                        noteStartEven(i,2);
                    }
                    else if(note[i]>7) {
                        noteStartOdd(i,1);
                    }
                    else if(note[i]>0) {
                        noteStartEven(i,1);
                    }
                    rect.top = y+(66*(6-i)) - noteWeight;
                    rect.bottom = y+40+(66*(6-i)) - noteWeight;
                }
                if(i==1){
                    if(note[i]>19) {
                        noteStartOdd(i,2);
                    }
                    else if(note[i]>14) {
                        noteStartEven(i,2);
                    }
                    else if(note[i]>7) {
                        noteStartOdd(i,1);
                    }
                    else if(note[i]>2) {
                        noteStartEven(i,1);
                    }
                    else if(note[i]>=0 && note[i]<3){
                        noteStartBeforeFirstStep(i);
                    }
                    rect.top = y+(66*(6-i)) - noteWeight;
                    rect.bottom = y+40+(66*(6-i)) - noteWeight;
                }
                if(i==2){
                    if(note[i]>14) {
                        noteStartEven(i,2);
                    }
                    else if(note[i]>9) {
                        noteStartOdd(i,1);
                    }
                    else if(note[i]>2) {
                        noteStartEven(i,1);
                    }
                    else if(note[i]>=0 && note[i]<3){
                        noteStartBeforeFirstStep(i);
                    }
                    rect.top = y+(66*(6-i)) - noteWeight;
                    rect.bottom = y+40+(66*(6-i)) - noteWeight;
                }
                if(i==3){
                    if(note[i]>16) {
                        noteStartEven(i,2);
                    }
                    else if(note[i]>9) {
                        noteStartOdd(i,1);
                    }
                    else if(note[i]>4) {
                        noteStartEven(i,1);
                    }
                    else if(note[i]>=0 && note[i]<5){
                        noteStartBeforeFirstStep(i);
                    }
                    rect.top = y+(66*(6-i)) - noteWeight;
                    rect.bottom = y+40+(66*(6-i)) - noteWeight;
                }
                if(i==4){
                    if(note[i]>17) {
                        noteStartOdd(i,2);
                    }
                    else if(note[i]>12) {
                        noteStartEven(i,2);
                    }
                    else if(note[i]>5) {
                        noteStartOdd(i,1);
                    }
                    else if(note[i]>0) {
                        noteStartEven(i,1);
                    }
                    rect.top = y+(66*(6-i))+22 - noteWeight;
                    rect.bottom = y+40+(66*(6-i))+22 - noteWeight;
                }
                if(i==5){
                    if(note[i]>19) {
                        noteStartOdd(i,2);
                    }
                    else if(note[i]>12) {
                        noteStartEven(i,2);
                    }
                    else if(note[i]>7) {
                        noteStartOdd(i,1);
                    }
                    else if(note[i]>0) {
                        noteStartEven(i,1);
                    }
                    rect.top = y+(66*(6-i))+22 - noteWeight;
                    rect.bottom = y+40+(66*(6-i))+22 - noteWeight;
                }


                rectX = rect.centerX();
                rectY = rect.centerY();

                //drawSharp(canvas, (int)rectY);

                if(rectY<210 || rectY>444){
                    canvas.drawLine(rectX-40, rectY, rectX+40, rectY, linePaint);
                }

                canvas.drawBitmap(NoteRestExam.bitmaps[duration],rectX-58,rectY-140,null);
                canvas.drawText(noteData.tone[i] +"", x, y+694-44*i, paint);

            }else if(noteData.rest){
                Log.d("duration", ""+duration);
                if(duration!=3){
                    canvas.drawBitmap(NoteRestExam.bitmaps[duration+5],20,y+120,null);
                }else{
                    canvas.drawBitmap(NoteRestExam.bitmaps[duration+5],20,y-24+120,null);
                }
            }

            else if(note[i] == -1 && !noteData.rest) {
                if (noteData.node) {
                    drawNode(canvas);
                    break;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(150, 900);
    }


}
