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
import com.relhs.asianfinder.loader.ImageLoader;

import java.util.ArrayList;

public class PeopleListAdapter extends AsymmetricGridViewAdapter<PeopleInfo> {

	private ImageLoader mLoader;
	

    public PeopleListAdapter(Context context, AsymmetricGridView listView, ArrayList<PeopleInfo> businessList) {
		super(context, listView, businessList);
		mLoader = new ImageLoader(context);
	}

    @Override
    public View getActualView(final int position, View convertView, final ViewGroup parent) {
        PeopleInfo business = getItem(position);
		
    	if (convertView == null) {
//			LayoutInflater layoutInflator = LayoutInflater.from(getContext());
//			convertView = layoutInflator.inflate(R.layout.row_people, parent, false);
		}
		
		ImageView photoMain = (ImageView) convertView.findViewById(R.id.photoMain);
        ImageView photoThumb1 = (ImageView) convertView.findViewById(R.id.photoThumb1);
        ImageView photoThumb2 = (ImageView) convertView.findViewById(R.id.photoThumb2);
		
		mLoader.DisplayImage(business.getImage_filename(), photoMain);
		
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
