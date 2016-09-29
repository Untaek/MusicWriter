package com.limwoon.musicwriter;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.limwoon.musicwriter.SQLite.DefineSQL;
import com.limwoon.musicwriter.SQLite.SheetDbHelper;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.http.FaceBookUserData;
import com.limwoon.musicwriter.list.SheetRecyListAdapter;
import com.limwoon.musicwriter.list.SheetRecyListItemClickListener;

import java.util.ArrayList;

public class MainNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ViewPager mViewPager;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    TabLayout tabLayout;
    Toolbar toolbar;

    // navigationView 안에 있는 View들 //
    LinearLayout linearUserInfContainer;
    TextView textViewUserStrID;
    TextView textViewUserEmail;
    Button buttonLogin;
    ImageView userImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        startActivity(new Intent(this, SplashActivity.class));

        if(AccessToken.getCurrentAccessToken() != null){
            Log.d("starttoken2", AccessToken.getCurrentAccessToken()+"");
            FaceBookUserData faceBookUserData = new FaceBookUserData(getApplicationContext());
            faceBookUserData.setUserData(AccessToken.getCurrentAccessToken());
        }
        super.onStart();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationview_main);
        mNavigationView.setNavigationItemSelectedListener(this);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mViewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        linearUserInfContainer = (LinearLayout) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_userInf_container);
        textViewUserStrID = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_userID);
        textViewUserEmail = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_userEmail);
        buttonLogin = (Button) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_loginBtn);
        userImage = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.imageView_user_picture);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("t", "onResume: ");

        Log.d("time2", ""+ System.currentTimeMillis());
        if(PUBLIC_APP_DATA.isLogin()){
            linearUserInfContainer.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.GONE);
            textViewUserStrID.setText(PUBLIC_APP_DATA.getUserStrID());
            textViewUserEmail.setText(PUBLIC_APP_DATA.getUserEmail());
            mNavigationView.getMenu().setGroupVisible(R.id.nav_group_user, true);
        }
        else {
            linearUserInfContainer.setVisibility(View.INVISIBLE);
            buttonLogin.setVisibility(View.VISIBLE);
            mNavigationView.getMenu().setGroupVisible(R.id.nav_group_user, false);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d("z", "onNavigationItemSelected: "+item);

        switch (id){
            case R.id.nav_menu_logout:
                Log.d("z", "onNavigationItemSelected: "+item);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                        .setTitle("로그아웃 확인")
                        .setMessage("정말 로그아웃 하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PUBLIC_APP_DATA.logout();
                                LoginManager.getInstance().logOut();
                                SharedPreferences al = getSharedPreferences("al", MODE_PRIVATE);
                                SharedPreferences.Editor edit = al.edit();
                                edit.clear();
                                edit.apply();
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                myFragmentPagerAdapter.notifyDataSetChanged();
                                onResume();
                            }
                        })
                        .setNegativeButton("아니오", null);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                break;
            case R.id.nav_menu_user_information:
                startActivity(new Intent(this, UserInfActivity.class));

                break;

        }
        return true;
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.``
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

                    LinearLayout loginContainer = (LinearLayout) rootView.findViewById(R.id.please_login_container);
                    if(PUBLIC_APP_DATA.isLogin()) loginContainer.setVisibility(View.GONE);
                    else loginContainer.setVisibility(View.VISIBLE);

                    // 로그인 버튼 클릭
                    rootView.findViewById(R.id.favorite_login).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), LoginActivity.class);
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
            if(page==3){
                LinearLayout loginContainer = (LinearLayout) rootView.findViewById(R.id.please_login_container);
                if(PUBLIC_APP_DATA.isLogin()) loginContainer.setVisibility(View.GONE);
                else loginContainer.setVisibility(View.VISIBLE);
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
        public int getItemPosition(Object object) {
            return -2;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "작곡하기";
                case 1: return "만든 악보";
                case 2: return "즐겨 찾는 악보";
            }
            return null;
        }
    }
}
