package com.limwoon.musicwriter.data;

/**
 * Created by ejdej on 2016-08-04.
 */
public class NoteData {
    public int duration = 1; // 음의 길이
    public int[] tone = new int[6]; // 기타 음의 저장 배열 1번줄 ~ 6번줄
    public boolean node = false; // 마디 인지?
    public boolean rest = false; // 쉼표 인지?

    public boolean isAddBtn() {
        return isAddBtn;
    }

    public void setAddBtn(boolean addBtn) {
        isAddBtn = addBtn;
    }

    private boolean isAddBtn = false;

    public NoteData(){
        for(int i=0; i<6; i++){
            tone[i] = -1;
        }
    }

}
