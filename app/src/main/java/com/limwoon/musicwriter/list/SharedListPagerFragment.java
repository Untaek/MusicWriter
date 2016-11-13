package com.limwoon.musicwriter.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.http.ListStateListener;
import com.limwoon.musicwriter.http.LoadSharedSheetList;
import com.limwoon.musicwriter.http.RefreshListItem;

import java.util.ArrayList;

/**
 * Created by 운택 on 2016-10-06.
 */

public class SharedListPagerFragment extends Fragment {

    public SharedListPagerFragment(){}

    public static SharedListPagerFragment getInstance(int num){
        SharedListPagerFragment fragment = new SharedListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        fragment.setArguments(bundle);
        return fragment;
    }

    // 최신
    static public boolean listLoading=false;
    ArrayList<SheetData> sheetList;
    RecyclerView mRecyclerView;
    SharedSheetRecyclerAdapter mRecyclerAdapter;
    LinearLayoutManager mLinearLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;

    //좋아요
    static public boolean listLoading2=false;
    ArrayList<SheetData> sheetList2;
    RecyclerView mRecyclerView2;
    SharedSheetRecyclerAdapter mRecyclerAdapter2;
    LinearLayoutManager mLinearLayoutManager2;
    SwipeRefreshLayout mSwipeRefreshLayout2;

    //게시된 내 악보
    static public boolean listLoading3=false;
    ArrayList<SheetData> sheetList3;
    RecyclerView mRecyclerView3;
    SharedSheetRecyclerAdapter mRecyclerAdapter3;
    LinearLayoutManager mLinearLayoutManager3;
    SwipeRefreshLayout mSwipeRefreshLayout3;

    static final int SORT_NEW = 0;
    static final int SORT_LIKE = 1;
    static final int LOAD_DEFAULT = 0;
    static final int LOAD_MYSHEET = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView;
        int fragNum = getArguments().getInt("num");
        if(fragNum == 1){
            mView = inflater.inflate(R.layout.fragment_shared_list, container, false);
            TextView textView_title = (TextView) mView.findViewById(R.id.textView_pageTitle);
            textView_title.setText("최근 게시된 악보");
            sheetList = new ArrayList<>();
            mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_shared_sheet);
            mRecyclerAdapter = new SharedSheetRecyclerAdapter(sheetList, container.getContext(), this);
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new SheetRecyDivider());
            mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.refresh_layout);
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    sheetList.clear();
                    new LoadSharedSheetList(sheetList, mRecyclerAdapter)
                            .setListStateListener(new ListStateListener() {
                        @Override
                        public void onLoaded() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                                @Override
                                public void onLoaded(SheetData sheetData) {

                                }
                            }).execute(0, SORT_NEW, LOAD_DEFAULT); // 최신순

                }
            });
            final ProgressBar progressBar_loading = (ProgressBar) mView.findViewById(R.id.progress_loading);

            new LoadSharedSheetList(sheetList, mRecyclerAdapter)
                    .setListStateListener(new ListStateListener() {
                        @Override
                        public void onLoaded() {
                            progressBar_loading.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onLoaded(SheetData sheetData) {

                        }
                    }).execute(0, SORT_NEW, LOAD_DEFAULT); // 최신순

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
                    int itemCount = mLinearLayoutManager.getItemCount();
                    if(lastItem >= itemCount-1 && dy>0 && !listLoading && sheetList.size()%7==0){
                        listLoading=true;
                        new LoadSharedSheetList(sheetList, mRecyclerAdapter).execute(itemCount/7, SORT_NEW, LOAD_DEFAULT);
                    }
                }
            });
            return mView;
        }
        else if(fragNum==2){
            mView = inflater.inflate(R.layout.fragment_shared_list, container, false);
            TextView textView_title = (TextView) mView.findViewById(R.id.textView_pageTitle);
            textView_title.setText("추천을 많이 받은 악보");
            sheetList2 = new ArrayList<>();
            mRecyclerView2 = (RecyclerView) mView.findViewById(R.id.recycler_shared_sheet);
            mRecyclerAdapter2 = new SharedSheetRecyclerAdapter(sheetList2, container.getContext(), this);
            mRecyclerView2.setAdapter(mRecyclerAdapter2);
            mLinearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView2.setLayoutManager(mLinearLayoutManager2);
            mRecyclerView2.addItemDecoration(new SheetRecyDivider());
            mSwipeRefreshLayout2 = (SwipeRefreshLayout) mView.findViewById(R.id.refresh_layout);
            mSwipeRefreshLayout2.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
            mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    sheetList2.clear();
                    new LoadSharedSheetList(sheetList2, mRecyclerAdapter2)
                            .setListStateListener(new ListStateListener() {
                                @Override
                                public void onLoaded() {
                                    mSwipeRefreshLayout2.setRefreshing(false);
                                }

                                @Override
                                public void onLoaded(SheetData sheetData) {

                                }
                            }).execute(0, SORT_LIKE, LOAD_DEFAULT); //좋아요 순

                }
            });
            final ProgressBar progressBar_loading2 = (ProgressBar) mView.findViewById(R.id.progress_loading);
            new LoadSharedSheetList(sheetList2, mRecyclerAdapter2)
                    .setListStateListener(new ListStateListener() {
                        @Override
                        public void onLoaded() {
                            progressBar_loading2.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onLoaded(SheetData sheetData) {

                        }
                    }).execute(0, SORT_LIKE, LOAD_DEFAULT); //좋아요 순

            mRecyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int lastItem = mLinearLayoutManager2.findLastVisibleItemPosition();
                    int itemCount = mLinearLayoutManager2.getItemCount();
                    if(lastItem >= itemCount-1 && dy>0 && !listLoading2 && sheetList2.size()%7==0){
                        listLoading2=true;
                        new LoadSharedSheetList(sheetList2, mRecyclerAdapter2).execute(itemCount/7, SORT_LIKE, LOAD_DEFAULT);
                    }
                }
            });

            return mView;
        }
        else if(fragNum==3){
            mView = inflater.inflate(R.layout.fragment_shared_list, container, false);
            TextView textView_title = (TextView) mView.findViewById(R.id.textView_pageTitle);
            textView_title.setText("내가 게시한 악보");
            if(PUBLIC_APP_DATA.isLogin()){
                sheetList3 = new ArrayList<>();
                mRecyclerView3 = (RecyclerView) mView.findViewById(R.id.recycler_shared_sheet);
                mRecyclerAdapter3 = new SharedSheetRecyclerAdapter(sheetList3, container.getContext(), this);
                mRecyclerView3.setAdapter(mRecyclerAdapter3);
                mLinearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView3.setLayoutManager(mLinearLayoutManager3);
                mRecyclerView3.addItemDecoration(new SheetRecyDivider());
                mSwipeRefreshLayout3 = (SwipeRefreshLayout) mView.findViewById(R.id.refresh_layout);
                mSwipeRefreshLayout3.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
                mSwipeRefreshLayout3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        sheetList3.clear();
                        new LoadSharedSheetList(sheetList3, mRecyclerAdapter3)
                                .setListStateListener(new ListStateListener() {
                                    @Override
                                    public void onLoaded() {
                                        mSwipeRefreshLayout3.setRefreshing(false);
                                    }

                                    @Override
                                    public void onLoaded(SheetData sheetData) {

                                    }
                                }).execute(0, SORT_NEW, LOAD_MYSHEET); // 내 거

                    }
                });
                final ProgressBar progressBar_loading3 = (ProgressBar) mView.findViewById(R.id.progress_loading);
                new LoadSharedSheetList(sheetList3, mRecyclerAdapter3)
                        .setListStateListener(new ListStateListener() {
                            @Override
                            public void onLoaded() {
                                progressBar_loading3.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onLoaded(SheetData sheetData) {

                            }
                        }).execute(0, SORT_NEW, LOAD_MYSHEET); // 내 거

                mRecyclerView3.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int lastItem = mLinearLayoutManager3.findLastVisibleItemPosition();
                        int itemCount = mLinearLayoutManager3.getItemCount();
                        if(lastItem >= itemCount-1 && dy>0 && !listLoading3 && sheetList3.size()%7==0){
                            listLoading3=true;
                            new LoadSharedSheetList(sheetList3, mRecyclerAdapter3).execute(itemCount/7, SORT_LIKE, LOAD_MYSHEET);
                        }
                    }
                });
            }else {
                mView.findViewById(R.id.progress_loading).setVisibility(View.INVISIBLE);
            }

            return mView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int fragNum = getArguments().getInt("num");
        long sheetID = data.getLongExtra("id", -1);
        final int index = data.getIntExtra("pos", -1);
        boolean deleted = data.getBooleanExtra("deleted", false);
        Log.d("data", "onActivityResult: "+data.getLongExtra("id", -1));
        if(fragNum==1){
            if(!deleted){
                new RefreshListItem(sheetList, index)
                        .setOnListStateListener(new ListStateListener() {
                            @Override
                            public void onLoaded() {

                            }

                            @Override
                            public void onLoaded(SheetData sheetData) {
                                sheetList.set(index, sheetData);
                                mRecyclerAdapter.notifyDataSetChanged();
                            }
                        }).execute(sheetID, PUBLIC_APP_DATA.getUserID());
            }else{
                sheetList.remove(index);
                mRecyclerAdapter.notifyDataSetChanged();
            }
        }else if(fragNum==2){
            if(!deleted){
                new RefreshListItem(sheetList2, index)
                        .setOnListStateListener(new ListStateListener() {
                            @Override
                            public void onLoaded() {

                            }

                            @Override
                            public void onLoaded(SheetData sheetData) {
                                sheetList2.set(index, sheetData);
                                mRecyclerAdapter2.notifyDataSetChanged();
                            }
                        }).execute(sheetID, PUBLIC_APP_DATA.getUserID());
            }else{
                sheetList2.remove(index);
                mRecyclerAdapter2.notifyDataSetChanged();
            }
        }else if(fragNum==3){
            if(!deleted){
                new RefreshListItem(sheetList3, index)
                        .setOnListStateListener(new ListStateListener() {
                            @Override
                            public void onLoaded() {

                            }

                            @Override
                            public void onLoaded(SheetData sheetData) {
                                sheetList3.set(index, sheetData);
                                mRecyclerAdapter3.notifyDataSetChanged();
                            }
                        }).execute(sheetID, PUBLIC_APP_DATA.getUserID());
            }else{
                sheetList3.remove(index);
                mRecyclerAdapter3.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
