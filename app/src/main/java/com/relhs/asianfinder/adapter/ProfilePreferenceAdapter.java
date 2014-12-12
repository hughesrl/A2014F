package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.PreferenceInfo;
import com.relhs.asianfinder.data.ProfilePreferenceDataInfo;
import com.relhs.asianfinder.data.ProfilePreferenceHeaderInfo;
import com.relhs.asianfinder.view.Item;

import java.io.InputStream;
import java.util.ArrayList;

public class ProfilePreferenceAdapter extends ArrayAdapter<PreferenceInfo> {

    private Context context;
    private ArrayList<PreferenceInfo> items;
    private LayoutInflater vi;

    public ProfilePreferenceAdapter(Context context,ArrayList<PreferenceInfo> items) {
        super(context,0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final PreferenceInfo i = items.get(position);
        if (i != null) {
            if(i.getIsSection()){
                PreferenceInfo si = (PreferenceInfo)i;
                v = vi.inflate(R.layout.list_item_section_pref, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText(si.getCategory().replace("_"," "));

            }else{
                PreferenceInfo ei = (PreferenceInfo)i;
                v = vi.inflate(R.layout.list_item_entry_pref, null);
                final TextView title = (TextView)v.findViewById(R.id.list_item_entry_title);
                final TextView subtitle = (TextView)v.findViewById(R.id.list_item_entry_summary);


                if (title != null) {
                    title.setText(ei.getLabel());
                }
                if(subtitle != null) {
                    if(ei.getValue().isEmpty()) {
                        subtitle.setText("None");
                        subtitle.setTextColor(Color.RED);
                    } else {
                        subtitle.setText(ei.getValue());
                        subtitle.setTextColor(Color.BLUE);
                    }
                }


            }
        }
        return v;
    }

}