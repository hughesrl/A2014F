package com.relhs.asianfinder.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.relhs.asianfinder.GallerySlideshowActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.view.CustomTextView;

public class FullScreenImageAdapter extends PagerAdapter {

    private final ImageLoader imageLoader;
    private Activity _activity;
    private ArrayList<PeoplePhotosInfo> _imagePaths;
    private LayoutInflater inflater;

    private PeoplePhotosInfo _peoplePhotosInfo;

    // constructor
    public FullScreenImageAdapter(Activity activity,
                                  ArrayList<PeoplePhotosInfo> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;

        imageLoader = new ImageLoader(activity);
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        ImageView btnClose;
        CustomTextView photoTitle;
        CustomTextView photoComments;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (ImageView) viewLayout.findViewById(R.id.btnClose);

        photoTitle = (CustomTextView) viewLayout.findViewById(R.id.photoTitle);
        photoComments = (CustomTextView) viewLayout.findViewById(R.id.photoComments);

        photoTitle.setText(_imagePaths.get(position).getCategory());
        imageLoader.DisplayImage(_imagePaths.get(position).getFile(), imgDisplay);

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        //view Comments
        photoComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(_activity, "Show Comments", Toast.LENGTH_LONG).show();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}