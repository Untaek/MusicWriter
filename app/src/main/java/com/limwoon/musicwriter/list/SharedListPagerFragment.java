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

    static public boolean listLoading=false;
    ArrayList<SheetData> sheetList;
    RecyclerView mRecyclerView;
    SharedSheetRecyclerAdapter mRecyclerAdapter;
    LinearLayoutManager mLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView;
        int fragNum = getArguments().getInt("num");
        if(fragNum == 1){

            sheetList = new ArrayList<>();

            mView = inflater.inflate(R.layout.fragment_shared_list, container, false);

            mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_shared_sheet);
            mRecyclerAdapter = new SharedSheetRecyclerAdapter(sheetList, container.getContext());
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            new LoadSharedSheetList(sheetList, mRecyclerAdapter).execute(0);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
                    int itemCount = mLinearLayoutManager.getItemCount();
                    if(lastItem >= itemCount-1 && dy>0 && !listLoading && sheetList.size()%7==0){
                        listLoading=true;
                        new LoadSharedSheetList(sheetList, mRecyclerAdapter).execute(itemCount/7);
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
