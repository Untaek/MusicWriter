package com.limwoon.musicwriter.data;

import android.util.Log;

import com.limwoon.musicwriter.draw.NoteRecyclerAdapter;

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
    public int cursor = 0;
    private int beat;
    StoreCallBack callBack;

    public interface StoreCallBack{
        void onStored(int index);
        void onNodePutted(int index);
    }

    public int getBeat() {
        return beat;
    }

    public int getTotalNoteDuration() {
        return totalNoteDuration;
    }

    public void setTotalNoteDuration(int totalNoteDuration) {
        this.totalNoteDuration = totalNoteDuration;
    }

    public NoteData getTempData() {
        return tempData;
    }

    public void setTempData(NoteData tempData) {
        this.tempData = tempData;
    }

    public NoteStore(ArrayList<NoteData> list, NoteRecyclerAdapter noteRecyclerAdapter, int beat){
        this.list = list;
        this.noteRecyclerAdapter = noteRecyclerAdapter;
        this.beat= beat;
        if(beat==0) this.beat=8;
        else if (beat==1) this.beat=12;
        else if (beat==2) this.beat=16;
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
        list.add(pos, tempData);
        if(!tempData.node)
            noteRecyclerAdapter.notifyDataSetChanged();
        int duration = (int)Math.pow(2.0, (double)tempData.duration);
        totalNoteDuration += duration;
        tempData = new NoteData();

        if(pos>0){
            NoteData prev = list.get(pos-1);
            NoteData curr = list.get(pos);

            if(!prev.isBind && !curr.isBind && prev.duration==1 && curr.duration==1 && !prev.node && !curr.node && !prev.rest && !curr.rest ){
                prev.isBind = true;
                prev.isPrev = true;
                curr.isBind = true;

                list.remove(pos);
                list.remove(pos-1);
                list.add(pos-1, prev);
                list.add(pos, curr);
            }else if(!prev.isBind && !curr.isBind && prev.duration==0 && curr.duration==0 && !prev.node && !curr.node) {
                prev.isBind = true;
                prev.isPrev = true;
                curr.isBind = true;

                list.remove(pos);
                list.remove(pos - 1);
                list.add(pos - 1, prev);
                list.add(pos, curr);
            }
        }

        if(getTotalNoteDuration() >= getBeat()) {
            cursor++;
            saveNode(pos + 1);
            setTotalNoteDuration(0);
        }
        Log.d("cursor", "saveNote: "+cursor);
    }

    public void saveNode(int pos){
        tempData = new NoteData();
        tempData.node=true;
        list.add(pos, tempData);
        tempData = new NoteData();
        noteRecyclerAdapter.notifyItemInserted(pos);
        Log.d("cursor", "saveNode: "+cursor);
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
}
