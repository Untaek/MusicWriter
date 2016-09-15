package com.limwoon.musicwriter.data;

/**
 * Created by ejdej on 2016-08-05.
 */
public class ChoiceFlatData {
    int flat;
    boolean isSelected = false;
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getFlat() {
        return flat;
    }

    public void setFlat(int flat) {
        this.flat = flat;
    }

    public ChoiceFlatData(int flat){
        this.flat = flat;
    }
}
