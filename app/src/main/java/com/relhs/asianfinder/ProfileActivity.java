package com.relhs.asianfinder;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.nineoldandroids.view.ViewHelper;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.flavienlaurent.notboringactionbar.AlphaForegroundColorSpan;
import com.relhs.asianfinder.flavienlaurent.notboringactionbar.KenBurnsSupportView;
import com.relhs.asianfinder.fragment.BrowseFragment;
import com.relhs.asianfinder.fragment.PeopleAboutFragment;
import com.relhs.asianfinder.fragment.ProfileAboutFragment;
import com.relhs.asianfinder.fragment.SampleListFragment;
import com.relhs.asianfinder.fragment.ScrollTabHolder;
import com.relhs.asianfinder.fragment.ScrollTabHolderFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;
import com.relhs.asianfinder.view.CustomEditTextView;
import com.relhs.asianfinder.view.CustomTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends FragmentActivity {
    private ImageLoader imageLoader;
    private UserInfoOperations userOperations;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;

    private JSONObject jsonObjectProfile;
    private JSONObject jsonObjectPhotos;

    public static IAFPushService mIAFPushService;
    private boolean mBound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        //int[] resId = new int[]{R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer};

        private final String[] TITLES = { "About", "Gallery", "Preference" };
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
                case 0: // About
                    ProfileAboutFragment peopleAboutFragment = new ProfileAboutFragment();
                    return peopleAboutFragment;
                case 1: // Patient Information
                    BrowseFragment galleryFragment = new BrowseFragment();

                    return galleryFragment;
                case 2: // Patient Information
                    BrowseFragment moreFragment = new BrowseFragment();

                    return moreFragment;
            }
            return null;
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
//            int resIdLenght = resId.length;
//            if (position < 0 || position >= resIdLenght)
//                return;
//            int drawableId = resId[position];
//            mActionBar.setIcon(drawableId);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    
}

