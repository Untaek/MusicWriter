package com.limwoon.musicwriter.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by ejdej on 2016-10-04.
 */

public class SharedSheetViewPagerAdapter extends FragmentStatePagerAdapter {

    public SharedSheetViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ViewPagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }


}
