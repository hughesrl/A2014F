package com.relhs.asianfinder.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.UserDetailsMultipleSelectionModel;
import com.relhs.asianfinder.view.CustomTextView;

import java.util.ArrayList;

public class UserDetailsRangeSelectionAdapter extends ArrayAdapter<UserDetailsMultipleSelectionModel> {
    private Activity context;
    ArrayList<UserDetailsMultipleSelectionModel> spinnerItems;

    public UserDetailsRangeSelectionAdapter(Activity context, int resource, ArrayList<UserDetailsMultipleSelectionModel> spinnerItems) {
        super(context, resource, spinnerItems);
        this.context = context;
        this.spinnerItems = spinnerItems;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserDetailsMultipleSelectionModel current = spinnerItems.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.spinner_row, parent, false);
        CustomTextView spinnerTxtTitle = (CustomTextView) row.findViewById(R.id.spinnerTxtTitle);

        spinnerTxtTitle.setText(current.getCaption());
        return row;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_row, parent, false);
        }

        UserDetailsMultipleSelectionModel item = spinnerItems.get(position);

        if(item != null) {   // Parse the data from each object and set it.
            CustomTextView spinnerTxtTitle = (CustomTextView) row.findViewById(R.id.spinnerTxtTitle);
            if(spinnerTxtTitle != null)
                spinnerTxtTitle.setText(item.getCaption());
        }

        return row;
    }
    public int getPosition(String text) {
        for(int s=0;s<=(spinnerItems.size()-1);s++) {
            if(spinnerItems.get(s).getCaption().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return 0;
    }

}
