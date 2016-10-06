package com.limwoon.musicwriter.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ejdej on 2016-10-04.
 */

public class SharedSheetViewPagerAdapter extends FragmentStatePagerAdapter {

    public SharedSheetViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TopViewPagerFragment.newInstance(position%3);
    }

    @Override
    public int getCount() {
        return 999999;
    }




}
