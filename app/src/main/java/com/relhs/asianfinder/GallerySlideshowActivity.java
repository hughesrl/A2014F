package com.relhs.asianfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.adapter.FullScreenImageAdapter;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.fragment.BrowseFragment;
import com.relhs.asianfinder.fragment.ProfileAboutFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.UserInfoOperations;

import org.json.JSONObject;

import java.util.ArrayList;

public class GallerySlideshowActivity extends Activity {

    public static String INTENT_POSITION = "position";
    public static String INTENT_PHOTOS = "photos";

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra(INTENT_POSITION, 0);
        ArrayList<PeoplePhotosInfo> peoplePhotosInfo = i.getParcelableArrayListExtra(INTENT_PHOTOS);

        adapter = new FullScreenImageAdapter(GallerySlideshowActivity.this, peoplePhotosInfo);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }


}

