package com.limwoon.musicwriter.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limwoon.musicwriter.R;

/**
 * Created by ejdej on 2016-10-04.
 */

public class ViewPagerFragment extends Fragment {

    int mNum;

    public ViewPagerFragment(){}

    public static ViewPagerFragment newInstance(int num) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mNum = getArguments().getInt("num");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v =inflater.inflate(R.layout.nav_header, container, false);
        return v;
    }

}
