package com.limwoon.musicwriter.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.SheetData;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-10-04.
 */

public class SharedSheetRecyclerAdapter extends RecyclerView.Adapter<SharedSheetRecyclerAdapter.CustomViewHolder> {

    ArrayList<SheetData> list;


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
        holder.title.setText(list.get(position).getTitle());
        holder.author.setText(list.get(position).getAuthor());
        holder.comments.setText("댓글 "+ list.get(position).getComments());
        holder.likes.setText("추천 " + list.get(position).getLikes());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView author;
        TextView comments;
        TextView likes;

        public CustomViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.shared_card_title);
            author = (TextView) itemView.findViewById(R.id.shared_card_author);
            comments = (TextView) itemView.findViewById(R.id.textView_card_sharedList_comments);
            likes = (TextView) itemView.findViewById(R.id.textView_card_sharedList_likes);
        }
    }
}
