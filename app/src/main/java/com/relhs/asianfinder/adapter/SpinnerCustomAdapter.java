package com.relhs.asianfinder.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.SpinnerItems;

import java.util.ArrayList;

/**
 * Created by Admin on 9/13/2014.
 */
public class SpinnerCustomAdapter extends ArrayAdapter<SpinnerItems> {
    private Activity context;
    ArrayList<SpinnerItems> spinnerItems;

    public SpinnerCustomAdapter(Activity context, int resource, ArrayList<SpinnerItems> spinnerItems) {
        super(context, resource, spinnerItems);
        this.context = context;
        this.spinnerItems = spinnerItems;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpinnerItems current = spinnerItems.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.spinner_row, parent, false);
        TextView name = (TextView) row.findViewById(R.id.spinnerTxtTitle);
        Typeface myTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadProCond.ttf");

        name.setTypeface(myTypeFace, Typeface.NORMAL);
        name.setGravity(Gravity.LEFT);
        name.setTextSize(18);
        if(current.getSpinnerStatus() == false) {
            name.setTextColor(Color.GRAY);
        } else {
            name.setTextColor(Color.BLACK);
        }

        name.setText(current.getSpinnerTitle());

        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_row, parent, false);
        }
        SpinnerItems current = spinnerItems.get(position);
        TextView name = (TextView) row.findViewById(R.id.spinnerTxtTitle);
        if(current.getSpinnerStatus() == false) {
            name.setTextColor(Color.GRAY);
        } else {
            name.setTextColor(Color.BLACK);
        }
        name.setText(current.getSpinnerTitle());
        return row;
    }

    public int getPosition(String text) {
        for(int s=0;s<=(spinnerItems.size()-1);s++) {
            if(spinnerItems.get(s).getSpinnerTitle().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return 0;
    }
}