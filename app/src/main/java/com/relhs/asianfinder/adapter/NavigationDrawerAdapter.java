package com.relhs.asianfinder.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.NavDrawerItem;
import com.relhs.asianfinder.loader.ImageLoader;

import java.util.ArrayList;

public class NavigationDrawerAdapter extends BaseAdapter {

    private final ImageLoader imageLoader;
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavigationDrawerAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems, ImageLoader imageLoader) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public NavDrawerItem getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_items, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
//        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        //imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        if(!navDrawerItems.get(position).getTitle().isEmpty()) {
            txtTitle.setText(navDrawerItems.get(position).getTitle());
        }
        Log.d("HUGHES", navDrawerItems.get(position).getUrl());
        if(!navDrawerItems.get(position).getUrl().equalsIgnoreCase("")) { // not empty
            imgIcon.setVisibility(View.VISIBLE);

            imageLoader.DisplayImageRounded(navDrawerItems.get(position).getUrl(), imgIcon, context.getResources().getInteger(R.integer.resize_user_photo_size_size),
                    context.getResources().getInteger(R.integer.resize_user_photo_size_size));
        }
        // displaying count
        // check whether it set visible or not
//        if(navDrawerItems.get(position).getCounterVisibility()){
//            txtCount.setText(navDrawerItems.get(position).getCount());
//        }else{
//            // hide the counter view
//            txtCount.setVisibility(View.GONE);
//        }
        return convertView;
    }
}

