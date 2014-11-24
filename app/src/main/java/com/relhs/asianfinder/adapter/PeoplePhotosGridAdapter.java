package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.view.CustomTextView;

import java.util.ArrayList;

public class PeoplePhotosGridAdapter extends AsymmetricGridViewAdapter<PeoplePhotosInfo> {

	private ImageLoader mLoader;
    private Context context;


    public PeoplePhotosGridAdapter(Context context, AsymmetricGridView listView, ArrayList<PeoplePhotosInfo> peoplePhotosInfos) {
		super(context, listView, peoplePhotosInfos);
        this.mLoader = new ImageLoader(context);
        this.context = context;
	}

    @Override
    public View getActualView(final int position, View convertView, final ViewGroup parent) {
        PeoplePhotosInfo peoplePhotosInfos = getItem(position);
		
    	if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(context);
			convertView = layoutInflator.inflate(R.layout.row_gallery_photos, parent, false);
		}
		
		ImageView photoMain = (ImageView) convertView.findViewById(R.id.photoMain);
		
		mLoader.DisplayImage(peoplePhotosInfos.getFile(), photoMain);

        CustomTextView txtCategory = (CustomTextView) convertView.findViewById(R.id.txtCategory);

        txtCategory.setText(peoplePhotosInfos.getCategory());
		
        return convertView;
    }
    @Override
    protected int getRowHeight(int rowSpan) {
    	int threeFourthWidthSize = listView.getColumnWidth() + (listView.getColumnWidth()/2); // Change Robert Hughes June 25, 2014
        final int rowHeight = (threeFourthWidthSize) * rowSpan;
        // when the item spans multiple rows, we need to account for the vertical padding
        // and add that to the total final height
        return rowHeight + ((rowSpan - 1) * listView.getDividerHeight());
    }
}
