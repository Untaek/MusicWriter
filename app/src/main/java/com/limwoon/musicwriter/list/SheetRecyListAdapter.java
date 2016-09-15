package com.limwoon.musicwriter.list;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.limwoon.musicwriter.MusicViewActivity;
import com.limwoon.musicwriter.MusicWriteActivity;
import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.SQLite.DefineSQL;
import com.limwoon.musicwriter.SQLite.SheetDbHelper;
import com.limwoon.musicwriter.data.SheetData;

import java.util.ArrayList;

/**
 * Created by ejdej on 2016-08-18.
 */
public class SheetRecyListAdapter extends RecyclerView.Adapter<SheetRecyListAdapter.CustomHolder> {

    ArrayList<SheetData> sheetList;
    Context context;

    SheetDbHelper sheetDbHelper;
    Cursor cursor;
    SQLiteDatabase db;

    public SheetRecyListAdapter(ArrayList<SheetData> sheetList, Context context){
        this.sheetList=sheetList;
        this.context=context;
    }

    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_sheet_recy_my, parent, false);

        return new CustomHolder(cardView);
    }

    @Override
    public void onBindViewHolder(CustomHolder holder, int position) {
        holder.titleTextView.setText("곡명 : " + sheetList.get(position).getTitle());
        myOnClickListener(holder.btnView, position);
        myOnClickListener(holder.btnModify, position);
        myOnClickListener(holder.btnDelete, position);
    }

    @Override
    public int getItemCount() {
        return sheetList.size();
    }

    private void myOnClickListener(View view, final int pos){
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(context,""+sheetList.get(pos).getId(),Toast.LENGTH_SHORT).show();
                sheetDbHelper  = new SheetDbHelper(context);
                db = sheetDbHelper.getReadableDatabase();

                String[] columns = {
                        DefineSQL._ID,
                        DefineSQL.COLUMN_NAME_TITLE,
                        DefineSQL.COLUMN_NAME_AUTHOR,
                        DefineSQL.COLUMN_NAME_BEATS,
                        DefineSQL.COLUMN_NAME_NOTE,
                };

                cursor = db.query(
                        DefineSQL.TABLE_NAME,
                        columns,
                        DefineSQL._ID+"="+sheetList.get(pos).getId(),
                        null,
                        null,
                        null,
                        null,
                        null
                );

                cursor.moveToFirst();

                switch (view.getId()){
                    case R.id.btn_view:
                        int beat = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_BEATS));
                        String musicData = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE));
                        int ID = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_TITLE));
                        String author = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_AUTHOR));
                        Intent intent = new Intent(context, MusicViewActivity.class);
                        intent.putExtra("id", ID);
                        intent.putExtra("beats", beat);
                        intent.putExtra("musicData", musicData);
                        intent.putExtra("title", title);
                        intent.putExtra("author", author);
                        context.startActivity(intent);

                        break;
                    case R.id.btn_modify:
                        int beats = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_BEATS));
                        String musicDatas = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_NOTE));
                        int IDs = cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID));
                        String title2 = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_TITLE));
                        String author2 = cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_AUTHOR));
                        Intent intentMod = new Intent(context, MusicWriteActivity.class);
                        intentMod.putExtra("isEdit", true);
                        intentMod.putExtra("id", sheetList.get(pos).getId());
                        intentMod.putExtra("beatIndex", beats);
                        intentMod.putExtra("title", title2);
                        intentMod.putExtra("author", author2);
                        context.startActivity(intentMod);

                        break;
                    case R.id.btn_delete:
                        AlertDialog dialog = new AlertDialog.Builder(context).
                                setTitle("정말로 삭제하시겠습니까").
                                setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SheetDbHelper dbHelper = new SheetDbHelper(context);
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        db.delete(DefineSQL.TABLE_NAME, DefineSQL._ID+"="+sheetList.get(pos).getId(),null);
                                        sheetList.remove(pos);
                                        notifyItemRemoved(pos);

                                        SharedPreferences sharedPreferences = context.getSharedPreferences("TempNoteData", context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.apply();
                                    }
                                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        }).show();
                        break;

                }
            }
        });
    }


    public static final class CustomHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        TextView btnView;
        TextView btnModify;
        TextView btnDelete;

        public CustomHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.testTextView);
            btnView= (TextView) itemView.findViewById(R.id.btn_view);
            btnModify= (TextView) itemView.findViewById(R.id.btn_modify);
            btnDelete= (TextView) itemView.findViewById(R.id.btn_delete);
        }
    }
}
