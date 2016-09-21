package com.limwoon.musicwriter;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.limwoon.musicwriter.SQLite.DefineSQL;
import com.limwoon.musicwriter.SQLite.SheetDbHelper;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.list.SheetRecyListAdapter;
import com.limwoon.musicwriter.list.SheetRecyListItemClickListener;
import com.limwoon.musicwriter.sounds.Sounds;

import java.util.ArrayList;

public class MainNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ViewPager mViewPager;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    TabLayout tabLayout;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mViewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        Sounds sounds = new Sounds();
        sounds.loadSound(this);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        //////// /////// ////////

        Button btnStart;
        Spinner spinnerSelectBeats;
        int beatIndex;
        View rootView;

        //악보리스트
        RecyclerView recyclerViewMySheet;
        SheetRecyListAdapter sheetRecyListAdapter;
        SheetRecyListItemClickListener sheetRecyListItemClickListener;
        LinearLayoutManager linearLayoutManager;
        ArrayList<SheetData> sheetList;

        //SQLite 열기
        SheetDbHelper sheetDbHelper;
        SQLiteDatabase db;
        Cursor cursor;

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int page = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (page){
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_start_write_music, container, false);

                    btnStart = (Button) rootView.findViewById(R.id.btn_select_bakja);
                    spinnerSelectBeats = (Spinner) rootView.findViewById(R.id.spinner_select_beats);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(inflater.getContext(),
                            R.array.beats_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSelectBeats.setAdapter(adapter);

                    spinnerSelectBeats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d("Item ->", ""+i);

                            beatIndex = i;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    btnStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(inflater.getContext(), MusicWriteActivity.class);
                            intent.putExtra("beatIndex", beatIndex);
                            startActivity(intent);

                        }
                    });
                    break;

                case 2:
                    rootView = inflater.inflate(R.layout.fragment_sheet_list_local, container, false);

                    recyclerViewMySheet = (RecyclerView) rootView.findViewById(R.id.recycler_my_sheet);
                    sheetList = new ArrayList<>();
                    sheetRecyListAdapter = new SheetRecyListAdapter(sheetList, rootView.getContext());
                    linearLayoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, true);
                    recyclerViewMySheet.setLayoutManager(linearLayoutManager);
                    recyclerViewMySheet.setAdapter(sheetRecyListAdapter);
                    break;

                case 3:
                    rootView = inflater.inflate(R.layout.fragment_sheet_list_favorite, container, false);

                    Button button = (Button) rootView.findViewById(R.id.fevobtn);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(rootView.getContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    break;
            }

            return rootView;
        }
        int listCount=-1;
        @Override
        public void onResume() {
            super.onResume();
            int page = getArguments().getInt(ARG_SECTION_NUMBER);
            if (page==2){
                Log.d("Resume-","true");

                sheetDbHelper  = new SheetDbHelper(rootView.getContext());
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
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                cursor.moveToFirst();
                sheetList.clear();
                for(int i=0; i<cursor.getCount(); i++){
                    Log.d("_id", ""+cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID)));
                    SheetData sheetData = new SheetData();
                    sheetData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DefineSQL._ID)));
                    sheetData.setAuthor("히히");
                    sheetData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DefineSQL.COLUMN_NAME_TITLE)));
                    sheetList.add(sheetData);
                    Log.d("sheetList",""+ sheetData.getId());
                    cursor.moveToNext();
                }
                sheetRecyListAdapter.notifyDataSetChanged();

                if(listCount==-1){
                    listCount=cursor.getCount();
                    recyclerViewMySheet.scrollToPosition(sheetList.size()-1);
                }

                if(listCount!=sheetList.size()){
                    recyclerViewMySheet.scrollToPosition(sheetList.size()-1);
                    listCount=-1;
                }
            }
        }
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "작곡하기";
                case 1: return  "악보 목록";
                case 2: return "즐겨찾는 목록";
            }
            return null;
        }
    }
}
