package com.limwoon.musicwriter;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.list.SharedSheetRecyclerAdapter;
import com.limwoon.musicwriter.list.SharedSheetViewPagerAdapter;
import com.limwoon.musicwriter.list.ViewPagerFragment;

import java.util.ArrayList;

public class SharedSheetActivity extends AppCompatActivity {

    ViewPager mViewPager;
    SharedSheetViewPagerAdapter mViewPagerAdapter;

    RecyclerView mRecyclerView;
    SharedSheetRecyclerAdapter mRecyclerAdapter;
    GridLayoutManager mLayoutManager;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerViewHeader mRecyclerViewHeader;

    ArrayList<SheetData> sheetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_sheet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sheetList = new ArrayList<>();

        mViewPager= (ViewPager) findViewById(R.id.viewPager_nice_sheet);
        mViewPagerAdapter = new SharedSheetViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_shared_sheet);
        mRecyclerAdapter = new SharedSheetRecyclerAdapter(sheetList);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewHeader = (RecyclerViewHeader) findViewById(R.id.recycler_header);
        mRecyclerViewHeader.attachTo(mRecyclerView);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("TAG", "onPageScrolled: "+position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TAG", "onPageSelected: " + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("TAG", "onPageScrollStateChanged: " + state);
            }
        });

        for(int i=0; i<300; i++)
            sheetList.add(new SheetData());

        mRecyclerAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable() {
            Handler handler = new Handler();
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                                if(mViewPager.getCurrentItem()==mViewPagerAdapter.getCount()){
                                    mViewPager.setCurrentItem(0);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
       // thread.start();

    }
}
