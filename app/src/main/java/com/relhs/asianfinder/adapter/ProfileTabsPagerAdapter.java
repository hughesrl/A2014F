package com.relhs.asianfinder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.relhs.asianfinder.fragment.ProfileFragment;

/**
 * Created by Admin on 8/19/2014.
 */
public class ProfileTabsPagerAdapter extends FragmentPagerAdapter {

    public ProfileTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new ProfileFragment();
            case 1:
                // Games fragment activity
                return new ProfileFragment();
            case 2:
                // Movies fragment activity
                return new ProfileFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}