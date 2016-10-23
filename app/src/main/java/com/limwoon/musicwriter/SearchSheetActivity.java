package com.limwoon.musicwriter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.http.LoadSharedSheetList;
import com.limwoon.musicwriter.list.SharedSheetRecyclerAdapter;

import java.util.ArrayList;

public class SearchSheetActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    SharedSheetRecyclerAdapter mRecyclerAdapter;
    LinearLayoutManager mLinearLayoutManager;
    ArrayList<SheetData> list;

    EditText editText_query;
    TextView textView_result_query;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        else if(item.getItemId() == R.id.search){
            list.clear();
            String text = editText_query.getText().toString();
            if(text.equals(""))
                 text = "'";
            new LoadSharedSheetList(list, mRecyclerAdapter, text, textView_result_query).execute(0, 0, 0);
            Func.closeKeyboard(this, editText_query);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sheet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_search);
        mRecyclerAdapter = new SharedSheetRecyclerAdapter(list, this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        editText_query = (EditText) findViewById(R.id.editText_query);
        textView_result_query = (TextView) findViewById(R.id.textView_result_query);
    }
}
