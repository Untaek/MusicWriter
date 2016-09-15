package com.limwoon.musicwriter.sounds;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.limwoon.musicwriter.R;

/**
 * Created by ejdej on 2016-08-24.
 */
public class Sounds {
    public final static int[] soundID = {R.raw.steel_e2, R.raw.steel_a2,R.raw.steel_d3,R.raw.steel_g3,R.raw.steel_b3,R.raw.steel_e4};
    public final static int[] sounds = new int[6];
    public final static SoundPool soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);

    public Sounds() {
    }

    public void loadSound(Context context){
        for(int i=0; i<6; i++){
            sounds[i] = soundPool.load(context, soundID[i], 1);
        }
    }

    public static float getTone(int tone){
        return (float)Math.pow(2, (double)(tone)/12.0);
    }

    public static int getDuration(int duration){
        if(duration==0){
            return 150;
        }else if(duration==1){
            return 300;
        }else if(duration==2){
            return 600;
        }else if(duration==3){
            return 1200;
        }else if(duration==4){
            return 2400;
        }
        return 1000;
    }
}
