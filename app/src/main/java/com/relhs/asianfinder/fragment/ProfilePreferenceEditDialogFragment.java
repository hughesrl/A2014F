package com.relhs.asianfinder.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.relhs.asianfinder.AsianFinderApplication;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.UserPreferenceActivity;
import com.relhs.asianfinder.adapter.PreferenceMultipleSelectionArrayAdapter;
import com.relhs.asianfinder.adapter.PreferenceRangeSelectionAdapter;
import com.relhs.asianfinder.data.PreferenceMultipleSelectionModel;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProfilePreferenceEditDialogFragment extends DialogFragment {
    public static final String TAG = ProfilePreferenceEditDialogFragment.class.getSimpleName();
    private View myFragmentView;
    public static final String ARG_DBNAME = "dbName";
    public static final String ARG_LABEL = "label";
    public static final String ARG_TYPE = "type";

    private String mParamDbName;
    private String mParamLabel;
    private String mParamType;

    private RelativeLayout layoutData;
    private LinearLayout layoutLoadingBar;
    private CustomButton btnOk;
    private LinearLayout layoutRange;

    private ListView listMultipleSelection;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;

    public ProfilePreferenceEditDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.90f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_Panel);

        if (getArguments() != null) {
            mParamDbName = getArguments().getString(ARG_DBNAME);
            mParamLabel = getArguments().getString(ARG_LABEL);
            mParamType = getArguments().getString(ARG_TYPE);
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_profile_pref_edit_dialog, container, false);
        TextView lbl_your_name = (TextView) myFragmentView.findViewById(R.id.lbl_your_name);
        lbl_your_name.setText(mParamLabel);

        layoutData = (RelativeLayout) myFragmentView.findViewById(R.id.layoutData);
        layoutLoadingBar = (LinearLayout) myFragmentView.findViewById(R.id.layoutLoadingBar);
        layoutRange = (LinearLayout) myFragmentView.findViewById(R.id.layoutRange);
        btnOk = (CustomButton) myFragmentView.findViewById(R.id.btnOk);

        listMultipleSelection = (ListView) myFragmentView.findViewById(R.id.listMultipleSelection);

        spinnerFrom = (Spinner) myFragmentView.findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) myFragmentView.findViewById(R.id.spinnerTo);

        new LoadMultipleSelectionDataTask(inflater, mParamDbName, null).execute();

        return myFragmentView;
    }

     private class LoadMultipleSelectionDataTask extends AsyncTask<Void, Void, JSONObject> {
        private ProgressDialog mProgressDialog;
        private JSONParser jParser;
        private JSONArray jsonArray;
        private LayoutInflater _inflater;
        private String _k;
        private String _lid;
        private List<PreferenceMultipleSelectionModel> list;
        private String selectionType;

        public LoadMultipleSelectionDataTask(LayoutInflater inflater, String k, String lid) {
            // TODO Auto-generated constructor stub
            this._inflater = inflater;
            this._k = k;
            this._lid = lid;

            jParser = new JSONParser();
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(getActivity());
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected JSONObject doInBackground(Void... args) {
            String did = ((UserPreferenceActivity)getActivity()).getDeviceId();
            return jParser.getJSONFromUrl(getResources().getString(R.string.api) + "?act=preference&t=fetch&did=" + did + "&k=" + _k, null);
        }
        @Override
        protected void onPostExecute(JSONObject jObj) {
            try {
                // Getting JSON Array from URL
                // Also Assume to return only one record
                jsonArray = jObj.getJSONArray(AsianFinderApplication.TAG_OS);
                JSONObject c = jsonArray.getJSONObject(0);

                if (!c.getBoolean(AsianFinderApplication.TAG_STATUS)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), c.getString(AsianFinderApplication.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                } else {
                    list = new ArrayList<PreferenceMultipleSelectionModel>();
                    JSONObject jsonArrayLoginDataObject = c.getJSONObject(AsianFinderApplication.TAG_DATA);
                    selectionType = jsonArrayLoginDataObject.getString("type");

                    JSONArray jsonArrayOptions = jsonArrayLoginDataObject.getJSONArray("options");
                    for (int i=0; i<jsonArrayOptions.length(); i++) {
                        JSONObject jsonObjectOptions = jsonArrayOptions.getJSONObject(i);
                        PreferenceMultipleSelectionModel preferenceMultipleSelectionModel = new PreferenceMultipleSelectionModel(jsonObjectOptions.getString("value"),
                                jsonObjectOptions.getString("caption"), selectionType, false);
                        list.add(preferenceMultipleSelectionModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(selectionType.equalsIgnoreCase("minmax")) {
                    layoutData.setVisibility(View.VISIBLE);
                    layoutRange.setVisibility(View.VISIBLE);

                    listMultipleSelection.setVisibility(View.GONE);
                    layoutLoadingBar.setVisibility(View.GONE);
                    btnOk.setVisibility(View.VISIBLE);

                    PreferenceRangeSelectionAdapter rangeSelectionAdapter = new PreferenceRangeSelectionAdapter(getActivity(), R.layout.spinner_text, (ArrayList<PreferenceMultipleSelectionModel>) list);
                    spinnerFrom.setAdapter(rangeSelectionAdapter);
                    spinnerTo.setAdapter(rangeSelectionAdapter);

                } else {
                    if(selectionType.equalsIgnoreCase("multiple")) {
                        btnOk.setVisibility(View.VISIBLE);
                    }

                    ArrayAdapter<PreferenceMultipleSelectionModel> adapter = new PreferenceMultipleSelectionArrayAdapter(getActivity(), list);
                    listMultipleSelection.setAdapter(adapter);

                    layoutData.setVisibility(View.VISIBLE);
                    layoutLoadingBar.setVisibility(View.GONE);
                }

                mProgressDialog.dismiss();
            }
        }
    }

}
