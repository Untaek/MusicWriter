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
    int selectedPos=-1;

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
        if(list.get(position).isSelected()){
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }else{
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        TextView flatTextView;

        public ItemHolder(final View itemView) {
            super(itemView);
            flatTextView = (TextView) itemView.findViewById(R.id.textview_choice_flat);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPos=getAdapterPosition();
                    for(int i=0;i<list.size();i++){
                        if(i == selectedPos) continue;
                        if(list.get(i).isSelected()) {
                            list.get(i).setSelected(false);
                        }
                    }
                    itemView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                    rv.getAdapter().notifyDataSetChanged();
                }
            });
        }
    }
}
