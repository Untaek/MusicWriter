package com.limwoon.musicwriter.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.MusicViewActivity;
import com.limwoon.musicwriter.NativeClass;
import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.SharedMusicViewActivity;
import com.limwoon.musicwriter.data.NoteData;
import com.limwoon.musicwriter.data.NoteParser;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.sounds.Sounds;

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


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.cardview_shared_sheet_list, parent, false);
        return new CustomViewHolder(v, list, context);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.author.setText(list.get(position).getAuthor());
        holder.comments.setText("댓글 "+ list.get(position).getComments());
        holder.likes.setText("추천 " + list.get(position).getLikes());
        holder.date.setText(list.get(position).getUploadTime());
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
        Button play;
        Button viewSheet;
        ArrayList<SheetData> list;
        Context context;

        public CustomViewHolder(View itemView, ArrayList<SheetData> list, Context context) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.shared_card_title);
            author = (TextView) itemView.findViewById(R.id.shared_card_author);
            comments = (TextView) itemView.findViewById(R.id.textView_card_sharedList_comments);
            likes = (TextView) itemView.findViewById(R.id.textView_card_sharedList_likes);
            date = (TextView) itemView.findViewById(R.id.textView_date);
            play = (Button) itemView.findViewById(R.id.button_play_in_list);
            viewSheet = (Button) itemView.findViewById(R.id.button_view_sheet_in_list);

            this.list = list;
            this.context = context;
            play.setOnClickListener(this);
            viewSheet.setOnClickListener(this);
        }
        Thread playThread;
        ArrayList<NoteData> notes;

        @Override
        public void onClick(View v) {
            final int index = getAdapterPosition();
            String jsonStr = list.get(index).getNote();
            if(v==play){
                Log.d("getnotestring", "onClick: "+ jsonStr);
                NoteParser noteParser = new NoteParser(jsonStr);
                Log.d("getnotestring", "onClick: "+ noteParser.getNotes());
                notes = new ArrayList<>();
                for(int i=0; i<noteParser.getNoteLength(); i++) {
                    notes.add(noteParser.getNoteAt(i));
                }

                playThread = new Thread(new Runnable() {
                    @Override
                    public void run(){
                        NativeClass.createEngine();
                        NativeClass.createAssetAudioPlayer(PUBLIC_APP_DATA.assetManager, "");
                        NativeClass.setStopAssetAudioPlayer(0);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        for(int i=0; i<notes.size(); i++) {
                            if (notes.get(i).node) continue;
                            for (int j = 0; j < 6; j++) {
                                if (notes.get(i).tone[j] != -1) {
                                    NativeClass.setPlayingAssetAudioPlayer(j, notes.get(i).tone[j]);
                                }
                            }
                            try {
                                Thread.sleep(Sounds.getDuration(notes.get(i).duration));
                                NativeClass.setStopAssetAudioPlayer(0);
                                if(i == notes.size()-1) NativeClass.releaseAll();
                            } catch (InterruptedException e) { // 정지버튼 클릭
                                NativeClass.setStopAssetAudioPlayer(0);
                                break;
                            }
                        }
                    }
                });
                playThread.start();

            }
            else if(v==viewSheet){
                Intent intent = new Intent(context, SharedMusicViewActivity.class);
                //intent.putExtra("data", list.get(index).getNote());
                //intent.putExtra("title", list.get(index).getTitle());
                intent.putExtra("data", list.get(index));
                Bundle bundle = new Bundle();
                context.startActivity(intent);
            }
        }
    }
}
