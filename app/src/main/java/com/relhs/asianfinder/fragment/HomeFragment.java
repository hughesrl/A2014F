package com.relhs.asianfinder.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.DashboardActivity;
import com.relhs.asianfinder.R;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mParamItemNumber;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;

    ProgressDialog progress;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamItemNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup myFragmentView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        tabs = (PagerSlidingTabStrip) myFragmentView.findViewById(R.id.tabs);
        pager = (ViewPager) myFragmentView.findViewById(R.id.pager);
        pagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);

        return myFragmentView;
    }
    public class MyPagerAdapter extends FragmentPagerAdapter implements ActionBar.TabListener , ViewPager.OnPageChangeListener {
//        int[] resId = new int[]{R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer};

        private final String[] TITLES = { "Recommendation", "Browse" };
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
            Bundle args = getArguments();
            switch(position){
                case 0: // Check In
                    RecommendationsFragment recommendationsFragment = new RecommendationsFragment();
                    recommendationsFragment.setArguments(args);

                    return recommendationsFragment;
                case 1: // Patient Information
                    BrowseFragment browseFragment = new BrowseFragment();
                    browseFragment.setArguments(args);

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



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DashboardActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

    }


}
