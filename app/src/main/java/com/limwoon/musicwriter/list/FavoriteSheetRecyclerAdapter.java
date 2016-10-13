package com.limwoon.musicwriter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.SheetData;

import java.util.ArrayList;

/**
 * Created by 운택 on 2016-10-13.
 */

public class FavoriteSheetRecyclerAdapter extends RecyclerView.Adapter<FavoriteSheetRecyclerAdapter.VH> {

    ArrayList<SheetData> list;
    Context context;

    public FavoriteSheetRecyclerAdapter(ArrayList<SheetData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_shared_sheet_list, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder{
        public VH(View itemView) {
            super(itemView);
        }
    }
}
