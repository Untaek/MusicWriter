package com.limwoon.musicwriter.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limwoon.musicwriter.R;
import com.limwoon.musicwriter.data.SheetData;

import java.util.ArrayList;

/**
 * Created by 운택 on 2016-10-06.
 */

public class SharedListPagerFragment extends Fragment {

    int num;

    public SharedListPagerFragment(){}

    public static SharedListPagerFragment getInstance(int num){
        SharedListPagerFragment fragment = new SharedListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView;
        int fragNum = getArguments().getInt("num");

        if(fragNum == 1){

            ArrayList<SheetData> sheetList = new ArrayList<>();

            mView = inflater.inflate(R.layout.fragment_shared_list, container, false);
            RecyclerView mRecyclerView;
            SharedSheetRecyclerAdapter mRecyclerAdapter;
            LinearLayoutManager mLinearLayoutManager;

            mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_shared_sheet);
            mRecyclerAdapter = new SharedSheetRecyclerAdapter(sheetList);
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            for (int i=0; i<100; i++){
                sheetList.add(new SheetData());
            }
            mRecyclerAdapter.notifyDataSetChanged();

            return mView;
        }



        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
