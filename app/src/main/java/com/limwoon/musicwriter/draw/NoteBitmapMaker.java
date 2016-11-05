package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.limwoon.musicwriter.R;

/**
 * Created by ejdej on 2016-08-11.
 */
public class NoteBitmapMaker {

    Resources res;
    Context context;


    private static int[] drawables = {R.drawable.note16, R.drawable.note8, R.drawable.note4, R.drawable.note2, R.drawable.note1313,
                     R.drawable.rest16, R.drawable.rest8, R.drawable.rest4, R.drawable.rest2, R.drawable.rest1};
    private static int[] drawablesReverse = {R.drawable.note16_reverse, R.drawable.note8_reverse, R.drawable.note4_reverse, R.drawable.note2_reverse};
    private static int[] hears = {R.drawable.note16_hear, R.drawable.note8_hear};
    private static int[] hearsReverse = {R.drawable.note16_reverse_hear, R.drawable.note8_reverse_hear};

    static Bitmap[] bitmaps = new Bitmap[drawables.length];
    static Bitmap[] bitmaps_preview = new Bitmap[drawables.length];
    static Bitmap[] bitmapsRev = new Bitmap[drawablesReverse.length];
    static Bitmap[] bitmapsHears = new Bitmap[hears.length];
    static Bitmap[] bitmapsHearsRev = new Bitmap[hearsReverse.length];

    private static int emptyNoteBlack = R.drawable.noteblackempty;
    private static int emptyNoteBlank = R.drawable.noteblankempty;
    private static int sharp = R.drawable.sharp;
    static Bitmap sharpBitmap;
    static Bitmap emptyNoteBlackBitmap;
    static Bitmap emptyNoteBlankBitmap;

    private static int bridge_8 = R.drawable.e1;
    private static int bridge_16 = R.drawable.e2;

    public static Bitmap small_note_for_tempo;

    public static int[] beats = {1, 2, 4, 8, 16};
    int width = 162;
    int height =width;

    int smallWidth = 100;
    int smallHeight = smallWidth;

    int sharpWidth = 70;
    int sharpHeight = sharpWidth;

    int emptyNoteWidth = 58;
    int emptyNoteHeight = emptyNoteWidth;

    public NoteBitmapMaker(Context context){
        this.context = context;
        init();
    }

    private void init() {
        res = context.getResources();
        BitmapFactory.Options opt = new BitmapFactory.Options();
        for(int i=0; i<bitmaps.length; i++) {
            bitmaps_preview[i] = BitmapFactory.decodeResource(res, drawables[i], opt);
            bitmaps_preview[i] = Bitmap.createScaledBitmap(bitmaps_preview[i], width, height, false);

            if(res != null) {
                if(i>4){
                    if(i==5){
                        smallWidth=100;
                        smallHeight=smallWidth;
                    }
                    else if(i==6){
                        smallWidth=70;
                        smallHeight=smallWidth;
                    }
                    else if(i==7){
                        smallWidth=100;
                        smallHeight=smallWidth;
                    }
                    else if(i==8){
                        smallWidth=100;
                        smallHeight=smallWidth;
                    }
                    else if(i==9){
                        smallWidth=100;
                        smallHeight=smallWidth;
                    }
                    bitmaps[i] = BitmapFactory.decodeResource(res, drawables[i], opt);
                    bitmaps[i] = Bitmap.createScaledBitmap(bitmaps[i], smallWidth, smallHeight, false);
                }else{
                    bitmaps[i] = BitmapFactory.decodeResource(res, drawables[i], opt);
                    bitmaps[i] = Bitmap.createScaledBitmap(bitmaps[i], width, height, false);
                }
            }
        }
        for(int i=0; i<bitmapsRev.length; i++){
            bitmapsRev[i] = BitmapFactory.decodeResource(res, drawablesReverse[i], opt);
            bitmapsRev[i] = Bitmap.createScaledBitmap(bitmapsRev[i], width, height, false);
        }

        sharpBitmap = BitmapFactory.decodeResource(res, sharp, opt);
        sharpBitmap = Bitmap.createScaledBitmap(sharpBitmap, sharpWidth, sharpHeight, false);

        //opt.inSampleSize=2;

        emptyNoteBlackBitmap = BitmapFactory.decodeResource(res, emptyNoteBlack, opt);
        emptyNoteBlackBitmap = Bitmap.createScaledBitmap(emptyNoteBlackBitmap, emptyNoteWidth, emptyNoteHeight, false);
        emptyNoteBlankBitmap = BitmapFactory.decodeResource(res, emptyNoteBlank, opt);
        emptyNoteBlankBitmap = Bitmap.createScaledBitmap(emptyNoteBlankBitmap, emptyNoteWidth, emptyNoteHeight, false);

        for(int i=0; i<2; i++){
            bitmapsHears[i] = BitmapFactory.decodeResource(res, hears[i], opt);
            bitmapsHears[i] = Bitmap.createScaledBitmap(bitmapsHears[i], width, height, false);
            bitmapsHearsRev[i] = BitmapFactory.decodeResource(res, hearsReverse[i], opt);
            bitmapsHearsRev[i] = Bitmap.createScaledBitmap(bitmapsHearsRev[i], width, height, false);
        }

        small_note_for_tempo = BitmapFactory.decodeResource(res, drawables[2], opt);
        small_note_for_tempo = Bitmap.createScaledBitmap(small_note_for_tempo, 80,80, false);

    }

    public Bitmap getBitmap(int num){
        return bitmaps[num];
    }

    public static Bitmap getBitmap_preview(int num) {
        return bitmaps_preview[num];
    }
}
