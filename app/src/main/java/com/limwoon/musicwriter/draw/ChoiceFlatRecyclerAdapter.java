package com.limwoon.musicwriter.draw;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.ChoiceFlatData;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-08-05.
 */
public class ChoiceFlatRecyclerAdapter extends RecyclerView.Adapter<ChoiceFlatRecyclerAdapter.ItemHolder> {
    Context context;
    ArrayList<ChoiceFlatData> list;
    RecyclerView rv;
    View.OnClickListener onClick;
    int selectedpos=0;

    public ChoiceFlatRecyclerAdapter(Context context, ArrayList<ChoiceFlatData> list, RecyclerView rv){
        this.context=context;
        this.list=list;
        this.rv=rv;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.choice_flat,parent, false);
        ItemHolder itemHolder = new ItemHolder(v);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        holder.flatTextView.setText(list.get(position).getFlat()+" 번 플랫");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        TextView flatTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            flatTextView = (TextView) itemView.findViewById(R.id.textview_choice_flat);

        }
    }
}
