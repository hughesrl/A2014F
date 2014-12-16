package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.MyListOperations;
import com.relhs.asianfinder.view.CustomTextView;

import java.util.ArrayList;

public class PeoplePhotosCursorGridAdapter extends CursorAdapter {

    private MyListOperations myListOperations;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;

    public PeoplePhotosCursorGridAdapter(Context context, Cursor c, MyListOperations mO, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(context);

        myListOperations = mO;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView userName = (TextView) view.findViewById(R.id.userName);
        TextView location = (TextView) view.findViewById(R.id.location);
        ImageView photo = (ImageView) view.findViewById(R.id.photo);

        userName.setText(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_USERNAME)));
        location.setText(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_LOCATION)));
        imageLoader.DisplayImage(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_PHOTO1)), photo);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.list_item_my_list, parent, false);
    }

}