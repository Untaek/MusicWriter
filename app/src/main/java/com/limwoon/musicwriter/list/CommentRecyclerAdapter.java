package com.limwoon.musicwriter.list;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.CommentData;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.http.DeleteCommentAsync;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-10-10.
 */

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.Holder> {

    ArrayList<CommentData> list;
    Context context;
    CommentRecyclerAdapter adapter = this;

    public void setUserPicture(Holder holder, int position, Bitmap bitmap){
        holder.userPic.setImageBitmap(list.get(position).getUserPicture());
    }

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
        holder.userStrID.setText(list.get(position).getUserStrID());
        holder.commentText.setText(list.get(position).getComment());
        holder.uploadTime.setText(list.get(position).getUploadTime());
        holder.userPic.setImageBitmap(list.get(position).getUserPicture());
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
        LinearLayout root_itemView;
        public Holder(View itemView) {
            super(itemView);
            userStrID = (TextView) itemView.findViewById(R.id.textView_comment_userStrID);
            uploadTime = (TextView) itemView.findViewById(R.id.textView_comment_time);
            commentText = (TextView) itemView.findViewById(R.id.textView_comment_comment);
            userPic = (ImageView) itemView.findViewById(R.id.imageView_comment_userPic);
            root_itemView = (LinearLayout) itemView.findViewById(R.id.itemView);

            root_itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int index = getAdapterPosition();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("삭제하시겠습니까")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(PUBLIC_APP_DATA.getUserID()==list.get(index).getUserID()){
                                        new DeleteCommentAsync().setDeleteCommentCallback(new DeleteCommentAsync.DeleteCommentCallback() {
                                            @Override
                                            public void onResult(int result) {

                                            }
                                        }).execute(list.get(index).getCommentID(), list.get(index).getSheetID());
                                        adapter.notifyItemRemoved(index);
                                        list.remove(index);
                                    }
                                    else{
                                        Toast.makeText(context, "내 댓글이 아닙니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("취소", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });
        }
    }
}
