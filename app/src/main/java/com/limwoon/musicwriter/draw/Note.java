package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

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

    public Note(Context context, NoteData data, NoteData prevData, @Nullable NoteData nextData) {
        super(context);
        this.context = context;
        this.noteData = data;
        this.prevData = prevData;
        this.nextData = nextData;

        if(prevData!=null)
            prevInit();
        if(nextData!=null)
            nextInit();
    }
    Paint paint;
    Paint linePaint;
    Paint bridgePaint;
    RectF rect;
    int[] centerY = new int[6];
    int[] centerX = new int[6];

    NoteData noteData;
    NoteData prevData;
    NoteData nextData;

    int[] note;
    boolean isRest;
    boolean isBind;
    int duration;
    int noteWeight;
    Context context;
    Resources res;
    int numOfNote =0;
    int lastOfNote = 0;
    int firstOfNote = -1;
    boolean direction = false;
    int directionHigh;
    int directionLow;
    public Bundle bundle;

    int prevLastY;
    int prevLastX;
    int prevFirstY;
    int prevFirstX;
    int nextLastY;
    int nextLastX;
    int nextFirstY;
    int nextFirstX;

    int x = 70;
    int y = 160;

    float rectX;
    float rectY;
    int thirdLineY = 308;

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        linePaint = new Paint();
        linePaint.setStrokeWidth(5);

        bridgePaint = new Paint();
        bridgePaint.setStrokeWidth(12);

        rect = new RectF(40, 345 ,100, 385);
        res = context.getResources();
    }

    private void prevInit(){
        numOfNote =0;
        lastOfNote = 0;
        firstOfNote = -1;

        rect = new RectF(40, 345 ,100, 385);
        for(int i=0; i<6; i++){
            if(prevData.tone[i] != -1 && !prevData.rest){
                if(firstOfNote==-1) firstOfNote = i;
                lastOfNote=i;
                numOfNote++;
            }
        }

        for(int i=0; i<6; i++) {
            if (prevData.tone[i] != -1 && !prevData.rest) {
                if (i == 0) {
                    if (prevData.tone[i] > 19) {
                        noteStartOdd(i, 2, prevData);
                    } else if (prevData.tone[i] > 12) {
                        noteStartEven(i, 2, prevData);
                    } else if (prevData.tone[i] > 7) {
                        noteStartOdd(i, 1, prevData);
                    } else if (prevData.tone[i] > 0) {
                        noteStartEven(i, 1, prevData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 1) {
                    if (prevData.tone[i] > 19) {
                        noteStartOdd(i, 2, prevData);
                    } else if (prevData.tone[i] > 14) {
                        noteStartEven(i, 2, prevData);
                    } else if (prevData.tone[i] > 7) {
                        noteStartOdd(i, 1, prevData);
                    } else if (prevData.tone[i] > 2) {
                        noteStartEven(i, 1, prevData);
                    } else if (prevData.tone[i] >= 0 && prevData.tone[i] < 3) {
                        noteStartBeforeFirstStep(i, prevData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 2) {
                    if (prevData.tone[i] > 14) {
                        noteStartEven(i, 2, prevData);
                    } else if (prevData.tone[i] > 9) {
                        noteStartOdd(i, 1, prevData);
                    } else if (prevData.tone[i] > 2) {
                        noteStartEven(i, 1, prevData);
                    } else if (prevData.tone[i] >= 0 && prevData.tone[i] < 3) {
                        noteStartBeforeFirstStep(i, prevData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 3) {
                    if (prevData.tone[i] > 16) {
                        noteStartEven(i, 2, prevData);
                    } else if (prevData.tone[i] > 9) {
                        noteStartOdd(i, 1, prevData);
                    } else if (prevData.tone[i] > 4) {
                        noteStartEven(i, 1, prevData);
                    } else if (prevData.tone[i] >= 0 && prevData.tone[i] < 5) {
                        noteStartBeforeFirstStep(i, prevData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 4) {
                    if (prevData.tone[i] > 17) {
                        noteStartOdd(i, 2, prevData);
                    } else if (prevData.tone[i] > 12) {
                        noteStartEven(i, 2 ,prevData);
                    } else if (prevData.tone[i] > 5) {
                        noteStartOdd(i, 1 ,prevData);
                    } else if (prevData.tone[i] > 0) {
                        noteStartEven(i, 1, prevData);
                    }
                    rect.top = y + (66 * (6 - i)) + 22 - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) + 22 - noteWeight;
                }
                if (i == 5) {
                    if (prevData.tone[i] > 19) {
                        noteStartOdd(i, 2, prevData);
                    } else if (prevData.tone[i] > 12) {
                        noteStartEven(i, 2, prevData);
                    } else if (prevData.tone[i] > 7) {
                        noteStartOdd(i, 1, prevData);
                    } else if (prevData.tone[i] > 0) {
                        noteStartEven(i, 1, prevData);
                    }
                    rect.top = y + (66 * (6 - i)) + 22 - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) + 22 - noteWeight;
                }

                rectX = rect.centerX();
                rectY = rect.centerY();

                centerX[i] = (int) rect.centerX();
                centerY[i] = (int) rect.centerY();

                prevFirstX = centerX[firstOfNote];
                prevFirstY = centerY[firstOfNote];
                prevLastX = centerX[lastOfNote];
                prevLastY = centerY[lastOfNote];
            }
        }

        if(Math.abs(directionHigh) > Math.abs(directionLow)){
            direction=true;
        }
    }

    private void nextInit(){
        numOfNote =0;
        lastOfNote = 0;
        firstOfNote = -1;

        rect = new RectF(40, 345 ,100, 385);
        for(int i=0; i<6; i++){
            if(nextData.tone[i] != -1 && !nextData.rest){
                if(firstOfNote==-1) firstOfNote = i;
                lastOfNote=i;
                numOfNote++;
            }
        }

        for(int i=0; i<6; i++) {
            if (nextData.tone[i] != -1 && !nextData.rest) {
                if (i == 0) {
                    if (nextData.tone[i] > 19) {
                        noteStartOdd(i, 2, nextData);
                    } else if (nextData.tone[i] > 12) {
                        noteStartEven(i, 2, nextData);
                    } else if (nextData.tone[i] > 7) {
                        noteStartOdd(i, 1, nextData);
                    } else if (nextData.tone[i] > 0) {
                        noteStartEven(i, 1, nextData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 1) {
                    if (nextData.tone[i] > 19) {
                        noteStartOdd(i, 2, nextData);
                    } else if (nextData.tone[i] > 14) {
                        noteStartEven(i, 2, nextData);
                    } else if (nextData.tone[i] > 7) {
                        noteStartOdd(i, 1, nextData);
                    } else if (nextData.tone[i] > 2) {
                        noteStartEven(i, 1, nextData);
                    } else if (nextData.tone[i] >= 0 && nextData.tone[i] < 3) {
                        noteStartBeforeFirstStep(i, nextData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 2) {
                    if (nextData.tone[i] > 14) {
                        noteStartEven(i, 2, nextData);
                    } else if (nextData.tone[i] > 9) {
                        noteStartOdd(i, 1, nextData);
                    } else if (nextData.tone[i] > 2) {
                        noteStartEven(i, 1, nextData);
                    } else if (nextData.tone[i] >= 0 && nextData.tone[i] < 3) {
                        noteStartBeforeFirstStep(i, nextData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 3) {
                    if (nextData.tone[i] > 16) {
                        noteStartEven(i, 2, nextData);
                    } else if (nextData.tone[i] > 9) {
                        noteStartOdd(i, 1, nextData);
                    } else if (nextData.tone[i] > 4) {
                        noteStartEven(i, 1, nextData);
                    } else if (nextData.tone[i] >= 0 && nextData.tone[i] < 5) {
                        noteStartBeforeFirstStep(i, nextData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 4) {
                    if (nextData.tone[i] > 17) {
                        noteStartOdd(i, 2, nextData);
                    } else if (nextData.tone[i] > 12) {
                        noteStartEven(i, 2, nextData);
                    } else if (nextData.tone[i] > 5) {
                        noteStartOdd(i, 1, nextData);
                    } else if (nextData.tone[i] > 0) {
                        noteStartEven(i, 1, nextData);
                    }
                    rect.top = y + (66 * (6 - i)) + 22 - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) + 22 - noteWeight;
                }
                if (i == 5) {
                    if (nextData.tone[i] > 19) {
                        noteStartOdd(i, 2, nextData);
                    } else if (nextData.tone[i] > 12) {
                        noteStartEven(i, 2, nextData);
                    } else if (nextData.tone[i] > 7) {
                        noteStartOdd(i, 1, nextData);
                    } else if (nextData.tone[i] > 0) {
                        noteStartEven(i, 1, nextData);
                    }
                    rect.top = y + (66 * (6 - i)) + 22 - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) + 22 - noteWeight;
                }

                rectX = rect.centerX();
                rectY = rect.centerY();

                centerX[i] = (int) rect.centerX();
                centerY[i] = (int) rect.centerY();

                nextFirstX = centerX[firstOfNote];
                nextFirstY = centerY[firstOfNote];
                nextLastX = centerX[lastOfNote];
                nextLastY = centerY[lastOfNote];
            }
        }

        if(Math.abs(directionHigh) > Math.abs(directionLow)){
            direction=true;
        }
    }

    private void noteStartOdd(int index, int step, NoteData noteData){
        noteWeight=((noteData.tone[index]/2)*22);
        noteWeight+=(22*step);
    }

    private void noteStartEven(int index, int step, NoteData noteData){
        noteWeight=((noteData.tone[index]-1)/2)*22;
        noteWeight+=(22*step);
    }

    private void drawNode(Canvas canvas){
        canvas.drawLine(75, y+86, 75, y+86+176, linePaint);
        canvas.drawLine(75, y+454,75, y+454+218, linePaint);
    }

    private void drawSharp(Canvas canvas, int y){
        canvas.drawBitmap(NoteBitmapMaker.sharpBitmap, -10, y-40, null);
    }

    private void noteStartBeforeFirstStep(int index, NoteData noteData){
        noteWeight=(noteData.tone[index]/2*22);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        noteWeight=0;

        note = noteData.tone;
        duration=noteData.duration;
        isBind = noteData.isBind;

        firstOfNote=-1;
        lastOfNote=0;
        numOfNote=0;

        for(int i=0; i<6; i++){
            if(note[i] != -1 && !noteData.rest){
                if(firstOfNote==-1) firstOfNote = i;
                lastOfNote=i;
                numOfNote++;
            }
        }

        for(int i=0; i<6; i++) {
            if (noteData.isAddBtn()) {
                break;
            }
            if (note[i] != -1 && !noteData.rest) {
                if (i == 0) {
                    if (note[i] > 19) {
                        noteStartOdd(i, 2, noteData);
                    } else if (note[i] > 12) {
                        noteStartEven(i, 2, noteData);
                    } else if (note[i] > 7) {
                        noteStartOdd(i, 1, noteData);
                    } else if (note[i] > 0) {
                        noteStartEven(i, 1, noteData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 1) {
                    if (note[i] > 19) {
                        noteStartOdd(i, 2, noteData);
                    } else if (note[i] > 14) {
                        noteStartEven(i, 2, noteData);
                    } else if (note[i] > 7) {
                        noteStartOdd(i, 1, noteData);
                    } else if (note[i] > 2) {
                        noteStartEven(i, 1, noteData);
                    } else if (note[i] >= 0 && note[i] < 3) {
                        noteStartBeforeFirstStep(i, noteData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 2) {
                    if (note[i] > 14) {
                        noteStartEven(i, 2, noteData);
                    } else if (note[i] > 9) {
                        noteStartOdd(i, 1, noteData);
                    } else if (note[i] > 2) {
                        noteStartEven(i, 1, noteData);
                    } else if (note[i] >= 0 && note[i] < 3) {
                        noteStartBeforeFirstStep(i, noteData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 3) {
                    if (note[i] > 16) {
                        noteStartEven(i, 2, noteData);
                    } else if (note[i] > 9) {
                        noteStartOdd(i, 1, noteData);
                    } else if (note[i] > 4) {
                        noteStartEven(i, 1, noteData);
                    } else if (note[i] >= 0 && note[i] < 5) {
                        noteStartBeforeFirstStep(i, noteData);
                    }
                    rect.top = y + (66 * (6 - i)) - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) - noteWeight;
                }
                if (i == 4) {
                    if (note[i] > 17) {
                        noteStartOdd(i, 2, noteData);
                    } else if (note[i] > 12) {
                        noteStartEven(i, 2, noteData);
                    } else if (note[i] > 5) {
                        noteStartOdd(i, 1, noteData);
                    } else if (note[i] > 0) {
                        noteStartEven(i, 1, noteData);
                    }
                    rect.top = y + (66 * (6 - i)) + 22 - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) + 22 - noteWeight;
                }
                if (i == 5) {
                    if (note[i] > 19) {
                        noteStartOdd(i, 2, noteData);
                    } else if (note[i] > 12) {
                        noteStartEven(i, 2, noteData);
                    } else if (note[i] > 7) {
                        noteStartOdd(i, 1, noteData);
                    } else if (note[i] > 0) {
                        noteStartEven(i, 1, noteData);
                    }
                    rect.top = y + (66 * (6 - i)) + 22 - noteWeight;
                    rect.bottom = y + 40 + (66 * (6 - i)) + 22 - noteWeight;
                }

                rectX = rect.centerX();
                rectY = rect.centerY();

                centerX[i] = (int) rect.centerX();
                centerY[i] = (int) rect.centerY();

                if(directionHigh<centerY[i]-thirdLineY) directionHigh = centerY[i]-thirdLineY;
                if(directionLow>centerY[i]-thirdLineY) directionLow = centerY[i]-thirdLineY;
            }
        }
        if(Math.abs(directionHigh) > Math.abs(directionLow)){
            direction=true;
        }

        for(int i=0; i<6; i++){
            if (note[i] != -1 && !noteData.rest) {
                if(i==0 && note[0]==2 ||note[0]==4 ||note[0]==6 ||note[0]==9 ||note[0]==11 ||note[0]==14 || note[0]==16 ||note[0]==18){
                    drawSharp(canvas, (int)rect.centerY());
                }
                else if(i==1 &&note[1]==1 ||note[1]==4 ||note[1]==6 ||note[1]==9 ||note[1]==11 ||note[1]==13 || note[1]==16 ||note[1]==18){
                    drawSharp(canvas, (int)rect.centerY());
                }
                else if(i==2 &&note[2]==1 ||note[2]==4 ||note[2]==6 ||note[2]==8 ||note[2]==11 ||note[2]==13 || note[2]==16 ||note[2]==18 || note[2]==20){
                    drawSharp(canvas, (int)rect.centerY());
                }
                else if(i==3 &&note[3]==1 ||note[3]==3 ||note[3]==6 ||note[3]==8 ||note[3]==11 ||note[3]==13 || note[3]==15 ||note[3]==18 || note[3]==20){
                    drawSharp(canvas, (int)rect.centerY());
                }
                else if(i==4 &&note[4]==2 ||note[4]==4 ||note[4]==7 ||note[4]==9 ||note[4]==11 ||note[4]==14 || note[4]==16 ||note[4]==19){
                    drawSharp(canvas, (int)rect.centerY());
                }
                else if(i==5 &&note[5]==2 ||note[5]==4 ||note[5]==6 ||note[5]==9 ||note[5]==11 ||note[5]==14 || note[5]==16 ||note[5]==18){
                    drawSharp(canvas, (int)rect.centerY());
                }

                if(numOfNote ==1 && rectY<210 || rectY>444){
                    canvas.drawLine(centerX[i]-40, centerY[i], centerX[i]+40, centerY[i], linePaint);
                }
                if(numOfNote>1){
                    if(direction && centerY[i]>450){
                        canvas.drawLine(centerX[i]-40, centerY[i], centerX[i]+40, centerY[i], linePaint);
                    }

                    if(!direction && centerY[i]<210){
                        canvas.drawLine(centerX[i]-40, centerY[i], centerX[i]+40, centerY[i], linePaint);
                    }
                }

                if(numOfNote>1){    // 여러 음표일때
                    if(duration<=2){
                        canvas.drawBitmap(NoteBitmapMaker.emptyNoteBlackBitmap, centerX[i]-28, centerY[i]-29, null);
                    }
                    else if(duration==3){
                        canvas.drawBitmap(NoteBitmapMaker.emptyNoteBlankBitmap, centerX[i]-28, centerY[i]-30, null);
                    }
                    else if(duration==4){
                        canvas.drawBitmap(NoteBitmapMaker.bitmaps[4], centerX[i]-40, centerY[i]-140, null);
                    }

                    if(i==lastOfNote && direction && duration < 4 && !isBind) {
                        if(duration<=1){
                            canvas.drawBitmap(NoteBitmapMaker.bitmapsHears[duration], centerX[i] - 58, centerY[i] - 140, null);
                        }
                        canvas.drawLine(centerX[i]+27, centerY[firstOfNote], centerX[i]+27, centerY[lastOfNote], linePaint);
                        canvas.drawLine(centerX[i]+27, centerY[lastOfNote], centerX[i]+27, centerY[lastOfNote]-126, linePaint);
                    }
                    else if(i==firstOfNote && !direction && duration < 4 && !isBind){
                        if(duration<=1){
                            canvas.drawBitmap(NoteBitmapMaker.bitmapsHearsRev[duration], centerX[i] - 80, centerY[i] - 22, null);
                        }
                        else if(duration==3){
                            canvas.drawLine(centerX[i]-26, centerY[lastOfNote], centerX[i]-26, centerY[firstOfNote]+126, linePaint);

                        }
                        canvas.drawLine(centerX[i]-25, centerY[lastOfNote], centerX[i]-25, centerY[firstOfNote]+126, linePaint);
                    }
                    else if(isBind){
                        canvas.drawLine(centerX[i]+27, centerY[lastOfNote], centerX[i]+27, centerY[lastOfNote]-130, linePaint);
                        canvas.drawLine(centerX[i]+27, centerY[lastOfNote], centerX[i]+27, centerY[firstOfNote], linePaint);
                        if(!noteData.isPrev){
                            canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-130, centerX[i]-126, prevLastY-130, bridgePaint);
                            if(prevData.duration==0){
                                canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-105, centerX[i]-126, prevLastY-105, bridgePaint);
                            }
                        }
                        else if(nextData != null){
                            canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-130, centerX[i]+170, nextLastY-130, bridgePaint);
                            if(duration==0){
                                canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-105, centerX[i]+170, nextLastY-105, bridgePaint);
                            }
                        }
                    }
                }
                else{   // 음표 하나일 때
                    if(duration<=3 && !isRest && i>=4 || note[0]>=19 || note[1]>=14 || note[2]>=9 || note[3]>=4){
                        if(!isBind)
                            canvas.drawBitmap(NoteBitmapMaker.bitmapsRev[duration],rectX-74,rectY-24,null);
                        else if(isBind && duration<=1){
                            canvas.drawLine(centerX[i]+27, centerY[lastOfNote], centerX[i]+27, centerY[lastOfNote]-130, linePaint);
                            canvas.drawBitmap(NoteBitmapMaker.emptyNoteBlackBitmap, centerX[i]-28, centerY[i]-29, null);
                            if(!noteData.isPrev){
                                canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-130, centerX[i]-126, prevLastY-130, bridgePaint);
                            }
                            else if(nextData != null){
                                canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-130, centerX[i]+170, nextLastY-130, bridgePaint);
                            }
                        }
                    }else{
                        if(!isBind)
                            canvas.drawBitmap(NoteBitmapMaker.bitmaps[duration],rectX-58,rectY-142,null);
                        else if(isBind && duration<=1){
                            canvas.drawLine(centerX[i]+27, centerY[lastOfNote], centerX[i]+27, centerY[lastOfNote]-130, linePaint);
                            canvas.drawBitmap(NoteBitmapMaker.emptyNoteBlackBitmap, centerX[i]-28, centerY[i]-29, null);
                            if(!noteData.isPrev){
                                canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-130, centerX[i]-126, prevLastY-130, bridgePaint);
                                if(prevData.duration==0){
                                    canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-105, centerX[i]-126, prevLastY-105, bridgePaint);
                                }
                            }
                            else if(nextData != null){
                                canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-130, centerX[i]+170, nextLastY-130, bridgePaint);
                                if(duration==0){
                                    canvas.drawLine(centerX[i]+27, centerY[lastOfNote]-105, centerX[i]+170, nextLastY-105, bridgePaint);
                                }
                            }
                        }
                    }
                }

            canvas.drawText(noteData.tone[i] +"", x, y+694-44*i, paint);

            }else if(noteData.rest){
                Log.d("duration", ""+duration);
                if(duration!=3){
                    canvas.drawBitmap(NoteBitmapMaker.bitmaps[duration+5],20,y+120,null);
                }else{
                    canvas.drawBitmap(NoteBitmapMaker.bitmaps[duration+5],20,y-24+120,null);
                }
            }

            else if(note[i] == -1 && !noteData.rest) {
                if (noteData.node) {
                    drawNode(canvas);
                    break;
                }
            }
        }
        numOfNote=0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(150, 900);
    }


}
