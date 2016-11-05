package com.limwoon.musicwriter.list;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.limwoon.musicwriter.LoginActivity;
import com.limwoon.musicwriter.MusicViewActivity;
import com.limwoon.musicwriter.NativeClass;
import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.SharedMusicViewActivity;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.http.DisFavoriteSheetAsync;
import com.limwoon.musicwriter.http.FavoriteSheetAsync;
import com.limwoon.musicwriter.sounds.Sounds;
import com.nineoldandroids.animation.Animator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-10-04.
 */

public class SharedSheetRecyclerAdapter extends RecyclerView.Adapter<SharedSheetRecyclerAdapter.CustomViewHolder> {

    ArrayList<SheetData> list;
    Context context;
    Fragment fragment;

    public ArrayList<SheetData> getList() {
        return list;
    }

    public void setList(ArrayList<SheetData> list) {
        this.list = list;
    }

    public SharedSheetRecyclerAdapter(ArrayList<SheetData> list, Context context){
        this.list=list;
        this.context=context;
    }

    public SharedSheetRecyclerAdapter(ArrayList<SheetData> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.cardview_shared_sheet_list, parent, false);
        return new CustomViewHolder(v, list, context, fragment);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.author.setText(list.get(position).getAuthor());
        holder.comments.setText(String.valueOf(list.get(position).getComments()));
        holder.likes.setText(String.valueOf(list.get(position).getLikes()));
        holder.date.setText(list.get(position).getUploadTime());
        if(list.get(position).isFavorite()){
            holder.favorite.setImageResource(R.drawable.star_fill);
        }else{
            holder.favorite.setImageResource(R.drawable.star_blank);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView author;
        TextView comments;
        TextView likes;
        TextView date;
        ImageView play;
        ImageView viewSheet;
        ImageView favorite;
        ArrayList<SheetData> list;
        Context context;
        Activity activity;
        Fragment fragment;
        LinearLayout linear_viewSheet;
        boolean isPlay;
        boolean isFavorite;
        Thread playThread;

        public CustomViewHolder(View itemView, ArrayList<SheetData> list, Context context, Fragment fragment) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.shared_card_title);
            author = (TextView) itemView.findViewById(R.id.shared_card_author);
            comments = (TextView) itemView.findViewById(R.id.textView_card_sharedList_comments);
            likes = (TextView) itemView.findViewById(R.id.textView_card_sharedList_likes);
            date = (TextView) itemView.findViewById(R.id.textView_date);
            play = (ImageView) itemView.findViewById(R.id.button_play_in_list);
            viewSheet = (ImageView) itemView.findViewById(R.id.button_view_sheet_in_list);
            favorite = (ImageView) itemView.findViewById(R.id.imageView_favorite_in_list);
            linear_viewSheet = (LinearLayout) itemView.findViewById(R.id.linear_viewSheet);

            this.list = list;
            this.context = context;
            this.activity = (Activity) context;
            this.fragment = fragment;
            play.setOnClickListener(this);
            viewSheet.setOnClickListener(this);
            favorite.setOnClickListener(this);
            linear_viewSheet.setOnClickListener(this);
        }
        ArrayList<NoteData> notes;

        @Override
        public void onClick(View v) {
            final int index = getAdapterPosition();
            String jsonStr = list.get(index).getNote();
            if(v==play){
                if(!isPlay){
                    NoteParser noteParser = new NoteParser(jsonStr);
                    notes = new ArrayList<>();
                    for(int i=0; i<noteParser.getNoteLength(); i++) {
                        notes.add(noteParser.getNoteAt(i));
                    }

                    playThread = new Thread(new Runnable() {
                        @Override
                        public void run(){
                            play.post(new Runnable() {
                                @Override
                                public void run() {
                                    play.setImageResource(R.drawable.ic_pause_black_24dp);
                                }
                            });
                            isPlay=true;
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            for(int i=0; i<notes.size(); i++) {
                                if (notes.get(i).node) continue;
                                for (int j = 0; j < 6; j++) {
                                    if (notes.get(i).tone[j] != -1) {
                                        NativeClass.setPlayingBufferQueue(j, notes.get(i).tone[j]);
                                    }
                                }
                                try {
                                    Thread.sleep(Sounds.getDuration(notes.get(i).duration, list.get(index).getTempo()));
                                    NativeClass.setStopBufferQueue();

                                } catch (InterruptedException e) { // 정지버튼 클릭
                                    play.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                        }
                                    });
                                    isPlay=false;
                                    NativeClass.setStopBufferQueue();
                                    break;
                                }
                            }
                            play.post(new Runnable() {
                                @Override
                                public void run() {
                                    play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                }
                            });
                            isPlay=false;
                        }
                    });
                    playThread.start();
                }
                else {
                    playThread.interrupt();
                }

            }
            else if(v==linear_viewSheet){
                Intent intent = new Intent(activity, SharedMusicViewActivity.class);
                intent.putExtra("data", list.get(index));
                intent.putExtra("pos", index);

                if(fragment!=null){
                    fragment.startActivityForResult(intent, 1);
                }else{
                    activity.startActivityForResult(intent, 1);
                }
            }
            else if(v==favorite){
                if(PUBLIC_APP_DATA.isLogin()){
                    isFavorite=list.get(index).isFavorite();
                    if(!isFavorite){
                        favorite.setImageResource(R.drawable.star_fill);
                        list.get(index).setIsFavorite(!isFavorite);
                        new FavoriteSheetAsync().execute(list.get(index).getId());
                        YoYo.with(Techniques.FadeIn).duration(400).playOn(favorite);
                    }
                    else{
                        list.get(index).setIsFavorite(!isFavorite);
                        new DisFavoriteSheetAsync().execute(list.get(index).getId());
                        favorite.setImageResource(R.drawable.star_blank);
                    }
                }
                else{
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setMessage("로그인이 필요합니다")
                            .setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", null).create();
                    dialog.show();
                }
            }
        }
    }
}
