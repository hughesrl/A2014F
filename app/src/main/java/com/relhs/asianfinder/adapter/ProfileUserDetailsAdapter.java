package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.UserDetailsInfo;

import java.util.ArrayList;

public class ProfileUserDetailsAdapter extends ArrayAdapter<UserDetailsInfo> {

    private Context context;
    private ArrayList<UserDetailsInfo> items;
    private LayoutInflater vi;

    public ProfileUserDetailsAdapter(Context context, ArrayList<UserDetailsInfo> items) {
        super(context,0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final UserDetailsInfo i = items.get(position);
        if (i != null) {
            if(i.getIsSection()){
                UserDetailsInfo si = (UserDetailsInfo)i;
                v = vi.inflate(R.layout.list_item_section_pref, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText(si.getCategory().replace("_"," "));

            }else{
                UserDetailsInfo ei = (UserDetailsInfo)i;
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
                        if(ei.getLabel().equalsIgnoreCase("location")) {

                        }
                        subtitle.setText(ei.getValue());
                        subtitle.setTextColor(Color.BLUE);
                    }
                }
            }
        }
        return v;
    }

}