package com.limwoon.musicwriter;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.http.LoadSharedSheetList;
import com.limwoon.musicwriter.list.SharedListPagerAdapter;
import com.limwoon.musicwriter.list.SharedListPagerFragment;
import com.limwoon.musicwriter.list.SharedSheetRecyclerAdapter;
import com.limwoon.musicwriter.list.SharedSheetViewPagerAdapter;

import java.util.ArrayList;

public class SharedSheetActivity extends AppCompatActivity {

    ViewPager mViewPager;
    SharedSheetViewPagerAdapter mViewPagerAdapter;

    RecyclerView mRecyclerView;
    SharedSheetRecyclerAdapter mRecyclerAdapter;
    GridLayoutManager mLayoutManager;
    LinearLayoutManager mLinearLayoutManager;
    LinearLayout mViewPagerIndicator;

    ArrayList<SheetData> sheetList;
    ViewPager listViewPager;
    TabLayout listTabLayout;
    SharedListPagerAdapter listPagerAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shared_sheet, menu);

        MenuItem searchItem = menu.findItem(R.id.shared_menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_sheet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sheetList = new ArrayList<>();

        mViewPager= (ViewPager) findViewById(R.id.viewPager_nice_sheet);
        mViewPagerAdapter = new SharedSheetViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(1000000/2);

        listViewPager = (ViewPager) findViewById(R.id.shared_list_pager);
        listPagerAdapter = new SharedListPagerAdapter(getSupportFragmentManager());
        listViewPager.setAdapter(listPagerAdapter);

        listTabLayout = (TabLayout) findViewById(R.id.shared_tabs);
        listTabLayout.setupWithViewPager(listViewPager);

        mViewPagerIndicator = (LinearLayout) findViewById(R.id.viewPager_indicator);
        mViewPagerIndicator.getChildAt(0).setBackgroundColor(Color.BLUE);

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
                int current = mViewPager.getCurrentItem() % 5;

                for(int i=0; i<5; i++)
                    mViewPagerIndicator.getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"));

                mViewPagerIndicator.getChildAt(current).setBackgroundColor(Color.BLUE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home: finish();
                break;
            case R.id.shared_menu_reload:
                RecyclerView rl = ((RecyclerView)listViewPager.getChildAt(0).findViewById(R.id.recycler_shared_sheet));
                SharedSheetRecyclerAdapter ad = (SharedSheetRecyclerAdapter)rl.getAdapter();

                ad.getList().clear();
                new LoadSharedSheetList(ad.getList(), ad).execute(0);
                ad.notifyDataSetChanged();
                break;
            case R.id.shared_menu_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
