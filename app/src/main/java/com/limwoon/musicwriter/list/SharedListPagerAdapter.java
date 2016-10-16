package com.limwoon.musicwriter.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by 운택 on 2016-10-06.
 */

public class SharedListPagerAdapter extends FragmentStatePagerAdapter {
    public SharedListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return SharedListPagerFragment.getInstance(position+1);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "최신 순";
            case 1: return "추천 순";
            case 2: return "게시 된 내 악보";
        }
        return null;
    }
}
