package com.limwoon.musicwriter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.limwoon.musicwriter.SQLite.DefineSQL;
import com.limwoon.musicwriter.SQLite.SheetDbHelper;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.list.SheetRecyListAdapter;
import com.limwoon.musicwriter.list.SheetRecyListItemClickListener;
import com.limwoon.musicwriter.sounds.Sounds;

import java.util.ArrayList;

public class TabbedActivity extends AppCompatActivity {


/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Sounds sounds = new Sounds();
        sounds.loadSound(this);
        NativeClass nativeClass = new NativeClass();
    }

    long clickTime=0;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        long systemTime = System.currentTimeMillis();
        Toast toast = Toast.makeText(this,"종료하려면 한번 더 누르세요", Toast.LENGTH_SHORT);

        if(clickTime < systemTime){
            clickTime = systemTime+1500;
            toast.show();
        }else if(clickTime >= systemTime){
            toast.cancel();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
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
                            Intent intent = new Intent(rootView.getContext(), MainNavActivity.class);
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "작곡하기";
                case 1:
                    return "악보 목록";
                case 2:
                    return "즐겨찾는 악보";
            }
            return null;
        }
    }
}
