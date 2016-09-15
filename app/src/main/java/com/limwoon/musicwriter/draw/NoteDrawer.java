package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.limwoon.musicwriter.data.NoteData;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-08-02.
 */
public class NoteDrawer {
    Context context;
    RecyclerView recyclerView;
    NoteRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<NoteData> list;

    public NoteDrawer(Context context, RecyclerView recyclerView, ArrayList<NoteData> list, NoteRecyclerAdapter adapter){
        this.context = context;
        this.recyclerView = recyclerView;
        this.list = list;
        this.adapter = adapter;
    }

    private void init(){
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void dataAddNotify(){
        adapter.notifyItemChanged(list.size()-1);
    }
    public void dataNotify(){
        adapter.notifyDataSetChanged();
    }

    public void start(){
        init();
    }

}
