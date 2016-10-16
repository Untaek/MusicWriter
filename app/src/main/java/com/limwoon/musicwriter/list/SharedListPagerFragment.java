package com.limwoon.musicwriter.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.SheetData;
import com.limwoon.musicwriter.http.LoadSharedSheetList;

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

    //좋아요
    static public boolean listLoading2=false;
    ArrayList<SheetData> sheetList2;
    RecyclerView mRecyclerView2;
    SharedSheetRecyclerAdapter mRecyclerAdapter2;
    LinearLayoutManager mLinearLayoutManager2;

    //게시된 내 악보
    static public boolean listLoading3=false;
    ArrayList<SheetData> sheetList3;
    RecyclerView mRecyclerView3;
    SharedSheetRecyclerAdapter mRecyclerAdapter3;
    LinearLayoutManager mLinearLayoutManager3;

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
            mRecyclerAdapter = new SharedSheetRecyclerAdapter(sheetList, container.getContext());
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            new LoadSharedSheetList(sheetList, mRecyclerAdapter).execute(0, SORT_NEW, LOAD_DEFAULT); // 최신순

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
            mRecyclerAdapter2 = new SharedSheetRecyclerAdapter(sheetList2, container.getContext());
            mRecyclerView2.setAdapter(mRecyclerAdapter2);
            mLinearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView2.setLayoutManager(mLinearLayoutManager2);
            new LoadSharedSheetList(sheetList2, mRecyclerAdapter2).execute(0, SORT_LIKE, LOAD_DEFAULT); //좋아요 순

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
            sheetList3 = new ArrayList<>();
            mRecyclerView3 = (RecyclerView) mView.findViewById(R.id.recycler_shared_sheet);
            mRecyclerAdapter3 = new SharedSheetRecyclerAdapter(sheetList3, container.getContext());
            mRecyclerView3.setAdapter(mRecyclerAdapter3);
            mLinearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView3.setLayoutManager(mLinearLayoutManager3);
            new LoadSharedSheetList(sheetList3, mRecyclerAdapter3).execute(0, SORT_LIKE, LOAD_MYSHEET); //좋아요 순

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

            return mView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        //sheetList.clear();
       // new LoadSharedSheetList(sheetList, mRecyclerAdapter).execute(0);
        super.onResume();
    }
}
