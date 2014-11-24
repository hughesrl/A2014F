package com.relhs.asianfinder;

import android.app.Activity;

import android.app.ActionBar;

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

import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.fragment.HomeFragment;
import com.relhs.asianfinder.fragment.MessagesFragment;
import com.relhs.asianfinder.fragment.NavigationDrawerFragment;
import com.relhs.asianfinder.fragment.ProfileFragment;
import com.relhs.asianfinder.operation.UserInfoOperations;

public class DashboardActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private UserInfoOperations userOperations;
    private UserInfo userInformation;

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
            case 4 :
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MessagesFragment.newInstance(position + 1))
                        .commit();
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
                mTitle = "Matches";
                break;
            case 4:
                mTitle = "My List";
                break;
            case 5:
                mTitle = "Messages";
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.dashboard, menu);
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


}
