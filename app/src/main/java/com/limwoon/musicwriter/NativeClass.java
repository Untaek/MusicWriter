package com.limwoon.musicwriter;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.limwoon.musicwriter.data.NoteData;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-08-23.
 */
public class NativeClass {
    static {
        System.loadLibrary("myJNI");
    }
    public native static void createEngine();
    public native static void createBefferQueueAudioPlayer();
    public native static void createBufferFromAsset(AssetManager assetManager, String fileDir);
    public native static void createAssetAudioPlayer(AssetManager assetManager, String fileDir);
    public native static void setPlayingAssetAudioPlayer(int tone, int pitch);
    public native static void setPlayingBufferQueue(int tone, int pitch);
    public native static void setStopBufferQueue();
    public native static void setStopAssetAudioPlayer(int tone);
    public native static void setPitch(AssetManager assetManager, String[] filename);
    public native static boolean getArrayList(ArrayList<NoteData> arrayList);
    public native static void releaseAll();
}
