package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.UserDetailsMultipleSelectionModel;

import java.util.List;

public class ProfileUserDetailsMultipleSelectionArrayAdapter extends ArrayAdapter<UserDetailsMultipleSelectionModel> {

    private final List<UserDetailsMultipleSelectionModel> list;
    private final Context context;
    private LayoutInflater vi;
    private final String ids;

    public ProfileUserDetailsMultipleSelectionArrayAdapter(Context context, List<UserDetailsMultipleSelectionModel> list, String Ids) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.ids = Ids;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = vi.inflate(R.layout.list_item_edit_pref_mutiple, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            UserDetailsMultipleSelectionModel element = (UserDetailsMultipleSelectionModel) viewHolder.checkbox
                                    .getTag();
                            element.setSelected(buttonView.isChecked());
                        }
                    });

            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.text.setText(list.get(position).getCaption());
        if(list.get(position).getSelectionType().equalsIgnoreCase("multiple")) { // multiple, single, minmax, text
            holder.checkbox.setChecked(list.get(position).isSelected());
        } else if(list.get(position).getSelectionType().equalsIgnoreCase("single")) {
            holder.checkbox.setVisibility(View.GONE);
        }
        return view;
    }
}