package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteStore;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-08-03.
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.itemHolder> {

    Context context;
    ArrayList<NoteData> list;
    LayoutInflater inflater;
    public NoteData noteData;
    public int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public NoteRecyclerAdapter(Context context, ArrayList<NoteData> list){
        this.context=context;
        this.list=list;
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        index=0;
    }


    @Override
    public itemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LinearLayout v = (LinearLayout) inflater.from(context).inflate(R.layout.note, parent, false);
        View note = null;

        try {
            note = new Note(context, list.get(index));
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        index++;

        return new itemHolder(note);
    }

    @Override
    public void onBindViewHolder(itemHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class itemHolder extends RecyclerView.ViewHolder{
        public itemHolder(View itemView) {
            super(itemView);
        }
    }
}
