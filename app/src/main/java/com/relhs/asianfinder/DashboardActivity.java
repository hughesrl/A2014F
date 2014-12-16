package com.relhs.asianfinder;

import android.app.Activity;

import android.app.ActionBar;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.SearchView;

import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.fragment.HomeFragment;
import com.relhs.asianfinder.fragment.MessagesFragment;
import com.relhs.asianfinder.fragment.MyListFragment;
import com.relhs.asianfinder.fragment.NavigationDrawerFragment;
import com.relhs.asianfinder.fragment.ProfileFragment;
import com.relhs.asianfinder.operation.UserInfoOperations;

public class DashboardActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private UserInfoOperations userOperations;
    private UserInfo userInformation;

    private long backPressedTime = 0;    // used by onBackPressed()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Log.d("-- HUGHES", ((AsianFinderApplication) getApplication()).getDeviceId());

//        Log.d("-- HUGHES", AsianFinderApplication.getCurrentTimeStamp());
//        Log.d("-- HUGHES", AsianFinderApplication.getDateCurrentTimeZone(Long.parseLong("1415192638387")));

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        AsianFinderApplication.activityResumed();
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        AsianFinderApplication.activityPaused();
//    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case 0:
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                break;
            case 1 :
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2 :
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MyListFragment.newInstance(position + 1))
                        .commit();
                break;
            case 3 :
                Intent mailActivity = new Intent(this, MailActivity.class);
                startActivity(mailActivity);
                break;

            case 4 :
                Intent chatRoomsActivity = new Intent(this, ChatRoomsActivity.class);
                startActivity(chatRoomsActivity);
                break;
            case 6 :
                Intent iSettings = new Intent(this, SettingsActivity.class);
                startActivity(iSettings);
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = "Profile";
                break;
            case 1:
                mTitle = "Home";
                break;
            case 2:
                mTitle = "Home";
                break;
            case 3:
                mTitle = "My List";
                break;
            case 4:
                mTitle = "Mail";
                break;
            case 5:
                mTitle = "Chat";
                break;
            case 6:
                mTitle = "Notifications";
                break;
            case 7:
                mTitle = "Settings";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
//        long t = System.currentTimeMillis();
//        if (t - backPressedTime > 2000) {    // 2 secs
//            backPressedTime = t;
//            Toast.makeText(this, "Press back again to logout",
//                    Toast.LENGTH_SHORT).show();
//        } else {    // this guy is serious
//            // clean up
//            super.onBackPressed();       // bye
//        }

        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        DashboardActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.dashboard, menu);
            // Associate searchable configuration with the SearchView
//            SearchManager searchManager =
//                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//            SearchView searchView =
//                    (SearchView) menu.findItem(R.id.action_filter).getActionView();
//            searchView.setSearchableInfo(
//                    searchManager.getSearchableInfo(getComponentName()));

            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_filter) {
            Intent intentFilter = new Intent(this, SearchActivity.class);
            startActivity(intentFilter);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DashboardActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public String getDeviceId() {
        return ((AsianFinderApplication) getApplication()).getDeviceId();
    }


}
