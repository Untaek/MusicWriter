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
    public int index;
    NotifyListener listener;

    public interface NotifyListener{
        void onNotified(int index);
    }

    public void setNotifyListener(NotifyListener listener){
        this.listener = listener;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public NoteRecyclerAdapter(Context context, ArrayList<NoteData> list){
        this.context=context;
        this.list=list;
        index=0;
    }



    @Override
    public itemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note, parent, false);
        return new itemHolder(view);
    }


    @Override
    public void onBindViewHolder(itemHolder holder, int position) {
        if(holder.note.getChildCount()==0){
            View v;
            if(list.get(position).isBind && position>0 && position+1<list.size()){
                v = new Note(context, list.get(position), list.get(position-1), list.get(position+1));
            }else if(list.get(position).isBind && position>0 && position+1>=list.size()) {
                v = new Note(context, list.get(position), list.get(position - 1), null);
            }else if(list.get(position).isBind && position==0){
                v = new Note(context, list.get(position), null, list.get(position+1));
            }
            else{
                v = new Note(context, list.get(position));
            }
            holder.note.addView(v);
            Log.d("position", "onBindViewHolder: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class itemHolder extends RecyclerView.ViewHolder{
        LinearLayout note;
        itemHolder(View itemView) {
            super(itemView);
            note = (LinearLayout) itemView.findViewById(R.id.note);
        }
    }
}
