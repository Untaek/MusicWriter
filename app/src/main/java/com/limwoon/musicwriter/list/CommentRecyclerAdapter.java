package com.limwoon.musicwriter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.CommentData;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-10-10.
 */

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.Holder> {

    ArrayList<CommentData> list;
    Context context;

    public CommentRecyclerAdapter(Context context, ArrayList<CommentData> list){
        this.context=context;
        this.list=list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.userStrID.setText(list.get(position).getUserID()+"");
        holder.commentText.setText(list.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public final class Holder extends RecyclerView.ViewHolder{
        TextView userStrID;
        TextView uploadTime;
        TextView commentText;
        ImageView userPic;
        public Holder(View itemView) {
            super(itemView);
            userStrID = (TextView) itemView.findViewById(R.id.textView_comment_userStrID);
            uploadTime = (TextView) itemView.findViewById(R.id.textView_comment_time);
            commentText = (TextView) itemView.findViewById(R.id.textView_comment_comment);
            userPic = (ImageView) itemView.findViewById(R.id.imageView_comment_userPic);
        }
    }
}
