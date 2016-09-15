package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.limwoon.musicwriter.R;

/**
 * Created by ejdej on 2016-08-11.
 */
public class NoteRestExam {

    Resources res;
    Context context;


    static int[] drawables = {R.drawable.note16, R.drawable.note8, R.drawable.note4, R.drawable.note2, R.drawable.note1313,
                     R.drawable.rest16, R.drawable.rest8, R.drawable.rest4, R.drawable.rest2, R.drawable.rest1};
    static int[] drawablesReverse = {R.drawable.note16_reverse, R.drawable.note8_reverse, R.drawable.note4_reverse, R.drawable.note2_reverse};
    public static Bitmap[] bitmaps = new Bitmap[drawables.length];

    static int sharp = R.drawable.sharp;
    public static Bitmap[] sharpBitmap = new Bitmap[1];

    public static int[] beats = {1, 2, 4, 8, 16};
    int width = 162;
    int height =width;

    int smallWidth = 100;
    int smallHeight = smallWidth;

    int sharpWidth = 70;
    int sharpHeight = sharpWidth;

    public NoteRestExam(Context context){
        this.context = context;
        init();
    }

    private void init() {
        res = context.getResources();
        BitmapFactory.Options opt = new BitmapFactory.Options();
        for(int i=0; i<bitmaps.length; i++) {
            if(res != null) {
                if(i>4){
                    bitmaps[i] = BitmapFactory.decodeResource(res, drawables[i], opt);
                    bitmaps[i] = Bitmap.createScaledBitmap(bitmaps[i], smallWidth, smallHeight, false);
                }else{
                    bitmaps[i] = BitmapFactory.decodeResource(res, drawables[i], opt);
                    bitmaps[i] = Bitmap.createScaledBitmap(bitmaps[i], width, height, false);
                }
            }
        }

        sharpBitmap[0] = BitmapFactory.decodeResource(res, sharp, opt);
        sharpBitmap[0] = Bitmap.createScaledBitmap(sharpBitmap[0], sharpWidth, sharpHeight, false);
    }

    public Bitmap getBitmap(int num){
        return bitmaps[num];
    }
}
