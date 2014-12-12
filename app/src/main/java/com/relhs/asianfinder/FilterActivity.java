package com.relhs.asianfinder;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.data.SpinnerItems;
import com.relhs.asianfinder.fragment.AgeRangeDialogFragment;
import com.relhs.asianfinder.fragment.LocationSelectionDialogFragment;
import com.relhs.asianfinder.fragment.SampleListFragment;
import com.relhs.asianfinder.fragment.SearchFragment;

public class FilterActivity extends FragmentActivity implements
        AgeRangeDialogFragment.AgeRangeDialogListener,
        LocationSelectionDialogFragment.LocationSelectionDialogListener{

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    public class MyPagerAdapter extends FragmentPagerAdapter implements ActionBar.TabListener , ViewPager.OnPageChangeListener {
//        int[] resId = new int[]{R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer};

        private final String[] TITLES = { "Search", "My Filters" };
        private ActionBar mActionBar;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
        @Override
        public int getCount() {
            return TITLES.length;
        }
        @Override
        public Fragment getItem(int position) {
            Fragment f = new Fragment();
//            Bundle args = getArguments();
            switch(position){
                case 0: // Check In
                    SearchFragment searchFragment = new SearchFragment();
                    return searchFragment;
                case 1: // Patient Information
                    SampleListFragment browseFragment = new SampleListFragment();
                    return browseFragment;
            }
            return null;
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int position) {
//            mActionBar.setSelectedNavigationItem(position);
//            int resIdLenght = resId.length;
//            if (position < 0 || position >= resIdLenght)
//                return;
//            int drawableId = resId[position];
//            mActionBar.setIcon(drawableId);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        }
    }

    public void showAgeRangeDialog(Fragment fragment) {
        // To create an instance of DialogFragment and displays
        DialogFragment ageRangeDialogFragment = AgeRangeDialogFragment.newInstance();

        ageRangeDialogFragment.setTargetFragment(fragment, 1);
        ageRangeDialogFragment.show(getSupportFragmentManager(), "AgeRangeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, SpinnerItems from, SpinnerItems to) { }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }


    public void showLocationSelectionDialog(Fragment fragment) {
        // To create an instance of DialogFragment and displays
        DialogFragment locationSelectionDialogFragment = LocationSelectionDialogFragment.newInstance();

        locationSelectionDialogFragment.setTargetFragment(fragment, 1);
        locationSelectionDialogFragment.show(getSupportFragmentManager(), "LocationSelectionDialogFragment");
    }
    @Override
    public void onLocationSelectionDialogPositiveClick(DialogFragment dialog, SpinnerItems country, SpinnerItems state, SpinnerItems city) { }
    @Override
    public void onLocationSelectionDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }



}
