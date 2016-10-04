package com.limwoon.musicwriter.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.SheetData;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-10-04.
 */

public class SharedSheetRecyclerAdapter extends RecyclerView.Adapter<SharedSheetRecyclerAdapter.CustomViewHolder> {

    ArrayList<SheetData> list;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public SharedSheetRecyclerAdapter(ArrayList<SheetData> list){
        this.list=list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.cardview_shared_sheet_list, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder{
        public CustomViewHolder(View itemView) {
            super(itemView);

        }
    }
}
