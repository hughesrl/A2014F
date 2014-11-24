package com.relhs.asianfinder;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.nineoldandroids.view.ViewHelper;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.fragment.BrowseFragment;
import com.relhs.asianfinder.fragment.PeopleAboutFragment;
import com.relhs.asianfinder.fragment.PeopleGalleryFragment;
import com.relhs.asianfinder.fragment.PeopleMatchFragment;
import com.relhs.asianfinder.fragment.ScrollTabHolder;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PeopleProfileActivity extends FragmentActivity {
    public static String INTENT_PID = "pid";
    public static String INTENT_PDATA = "pdata";

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;
    private int intentPID;
    private PeopleInfo peopleInfo;

    private JSONObject jsonObjectProfile;
    private JSONObject jsonObjectMatches;
    private JSONArray jsonObjectPhotos;

    public static IAFPushService mIAFPushService;
    private boolean mBound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_profile);

        Intent i = getIntent();
        intentPID = i.getIntExtra(INTENT_PID, 0);
        peopleInfo = i.getParcelableExtra(INTENT_PDATA);

        new LoadProfileDataTask(intentPID).execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(PeopleProfileActivity.this, AFPushService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mIAFPushService = null;
            mBound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mIAFPushService = IAFPushService.Stub.asInterface(service);
            mBound = true;
        }
    };

    public class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        //int[] resId = new int[]{R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer};

        private final String[] TITLES = { "About", "Gallery", "Match" };
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
            Bundle args = new Bundle();

            args.putString(Constants.ARG_PROFILE, jsonObjectProfile.toString());
            args.putString(Constants.ARG_MATCHES, jsonObjectMatches.toString());
            args.putString(Constants.ARG_PHOTOS, jsonObjectPhotos.toString());

            switch(position){
                case 0: // About
                    PeopleAboutFragment peopleAboutFragment = new PeopleAboutFragment();
                    peopleAboutFragment.setArguments(args);
                    return peopleAboutFragment;
                case 1: // Gallery
                    PeopleGalleryFragment peopleGalleryFragment = new PeopleGalleryFragment();
                    peopleGalleryFragment.setArguments(args);
                    return peopleGalleryFragment;
                case 2: // Matches
                    PeopleMatchFragment peopleMatchFragment = new PeopleMatchFragment();
                    peopleMatchFragment.setArguments(args);
                    return peopleMatchFragment;
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

    private class LoadProfileDataTask extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {

        private ProgressDialog mProgressDialog;
        private JSONParser jParser;
        private int pid;

        public LoadProfileDataTask(int pid) {
            // TODO Auto-generated constructor stub
            jParser = new JSONParser();
            this.pid = pid;

        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(PeopleProfileActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected ArrayList<PeopleInfo> doInBackground(Void... args) {

            JSONObject json = jParser.getJSONFromUrl(getResources().getString(R.string.api)+"?act=profile&did=fasdfasdfasd&_pid="+pid, null);
            Log.d("-- robert", json.toString());
            try {
                JSONArray android = json.getJSONArray(Constants.TAG_OS);
                JSONObject jsonObject = android.getJSONObject(0);
                if(!jsonObject.getBoolean(AsianFinderApplication.TAG_STATUS)) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString(AsianFinderApplication.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                } else {
                    //final
                    // Get Data inside the CamAppApplication.TAG_DATA
                    final JSONObject jsonArrayLoginDataObject = jsonObject.getJSONObject(AsianFinderApplication.TAG_DATA);

                    JSONArray jsonArrayProfile = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PROFILE);
                    jsonObjectProfile = jsonArrayProfile.getJSONObject(0);

                    JSONArray jsonArrayMatches = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_MATCHES);
                    jsonObjectMatches = jsonArrayMatches.getJSONObject(0);

                    JSONArray jsonArrayPhotos = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PHOTOS);
                    jsonObjectPhotos = jsonArrayPhotos; //jsonArrayPhotos.getJSONObject(0);

//                    jsonObjectProfile.getJSONArray("basic");

//                    String basic = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_BASIC).toString();
//                    //Log.d("-- robert -- basic", basic);
//                    String appearance = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_APPEARANCE).toString();
//                    //Log.d("-- robert -- appearance", appearance);
//                    String lifestyle = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_LIFESTYLE).toString();
//                    //Log.d("-- robert -- lifestyle", lifestyle);
//                    String culture_values = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_CULTURE_VALUES).toString();
//                    //Log.d("-- robert -- culture_values", culture_values);
//                    String personal = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_PERSONAL).toString();
//                    //Log.d("-- robert -- personal", personal);
//                    String interest = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_INTEREST).toString();
//                    //Log.d("-- robert -- interest", interest);
//                    String others = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_OTHERS).toString();
//                    //Log.d("-- robert -- others", others);
//
//                    JSONArray jsonArrayPreferences = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PREFERENCES);
//                    String preferences = jsonArrayPreferences.toString();
//                    //Log.d("-- robert -- preferences", preferences);
//
//                    JSONArray jsonArrayPhotos = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PHOTOS);
//                    String photos = jsonArrayPhotos.toString();
                    //Log.d("-- robert -- photos", photos);

//                    int isLogin = 1;
//                    final UserInfo userInfo = userOperations.addUser(userId,username,firstname,gender,country,state,city,email,
//                            membership_expiration,membership_type,main_photo,user_type,
//                            membership_expired,validate,domain_id,user_phone,session_id,user_token,
//                            basic, appearance, lifestyle, culture_values, personal, interest, others, preferences, photos, isLogin);
////                    userOperations.close();
//
//                    if(user_type.equalsIgnoreCase("u")) {
//                        // Start the service
//                        startService(new Intent(LoginActivity.this, AFPushService.class));
//
//                        Intent i = new Intent(getApplication(), DashboardActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                        progress.dismiss();
//                        startActivity(i);
//                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<PeopleInfo> peopleInfoArrayList) {
            mProgressDialog.dismiss();

            tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            pager = (ViewPager) findViewById(R.id.pager);
            pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(pagerAdapter);

            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            pager.setOffscreenPageLimit(2);
            tabs.setViewPager(pager);
        }
    }

}

