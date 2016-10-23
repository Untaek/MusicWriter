package com.limwoon.musicwriter.data;

import android.util.Log;

import com.limwoon.musicwriter.draw.NoteRecyclerAdapter;
import com.limwoon.musicwriter.draw.NoteBitmapMaker;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-08-02.
 */


// 노트를 리스트에 저장하는 클래스

public class NoteStore {
    private ArrayList<NoteData> list;
    private NoteData tempData = new NoteData();
    private NoteRecyclerAdapter noteRecyclerAdapter;
    private int totalNoteDuration =0;
    public int pos =0;
    int beat;

    public NoteStore(ArrayList<NoteData> list, NoteRecyclerAdapter noteRecyclerAdapter, int beat){
        this.list = list;
        this.noteRecyclerAdapter = noteRecyclerAdapter;
        //beat= MusicWriteActivity.beatIndex;
        this.beat= beat;
        if(beat==0) this.beat=8;
        else if (beat==1) this.beat=12;
        else if (beat==2) this.beat=16;
        //init();
    }

    public int getLength(){
        return list.size();
    }

    // 노트를 임시 배열에 저장해 놓는다
    public void cacheNote(int pos, int value){
        tempData.tone[pos] = value;
    }

    // 임시 저장한 노트를 제거한다(위치지정)
    public void cacheDelNote(int pos){
        tempData.tone[pos] = -1;
    }

    public void cacheTone(int[] value){ tempData.tone = value; }

    public void cacheDuration(int value){
        tempData.duration= value;
    }

    public void cacheIsRest(boolean bool){
        tempData.rest=bool;
    }

    // 임시 배열에 저장되어있는 데이터(노트)를 리스트에 추가한다
    public void saveNote(int pos){
        if(pos==-1) {
            saveNote();
            return;
        }
        noteRecyclerAdapter.noteData=tempData;
        noteRecyclerAdapter.index=pos;
        list.set(pos, tempData);
        noteRecyclerAdapter.notifyItemChanged(pos);
        totalNoteDuration+= NoteBitmapMaker.beats[tempData.duration];
       // if(totalNoteDuration >=beat){
       //     totalNoteDuration =0;
       //     tempData = new NoteData();
       //     saveNode();
       // }
        tempData = new NoteData();
    }

    public void saveNote(){
        noteRecyclerAdapter.noteData=tempData;
        list.add(tempData);
        noteRecyclerAdapter.notifyItemChanged(pos);
        totalNoteDuration+= NoteBitmapMaker.beats[tempData.duration];
        pos++;
        if(totalNoteDuration >=beat){
            totalNoteDuration =0;
            tempData = new NoteData();
            saveNode();
        }
        tempData = new NoteData();
    }

    public void saveNode(){
        tempData.node=true;
        noteRecyclerAdapter.noteData=tempData;
        list.add(tempData);
        tempData = new NoteData();
        Log.d("saveNote pos", ""+pos);
        pos++;
    }

    //임시 저장한 노트를 초기화 한다.
    public void clearCache(){
         for(int i = 0; i < 6; i++){
             tempData.tone[i]=-1;
         }
    }

    //임시 저장한 노트를 검사한다(하나라도 저장 되어 있는지.. -1은 저장이 안 된 상태)
    public boolean isHasCache(){
        for(int i=0; i<6; i++){
            if(tempData.tone[i] != -1) return true;
        }
        if(tempData.node || tempData.rest) return true;
        return false;
    }

    // 리스트 아이템 하나를 삭제한다
    public void delNote(int pos){
        try {
            list.remove(pos);
        }catch (NullPointerException e){
            return;
        }
    }
    // 리스트 최 후입 아이템을 삭제한다
    public void delNote(){
        try {
            list.remove(list.size()-1);
        }catch (NullPointerException e){
            return;
        }
    }

    private void init(){
        tempData.setAddBtn(true);
        list.add(tempData);
        noteRecyclerAdapter.notifyItemChanged(pos);
    }
}
