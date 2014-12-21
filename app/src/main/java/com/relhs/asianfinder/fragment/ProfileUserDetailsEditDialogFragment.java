package com.relhs.asianfinder.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.relhs.asianfinder.AsianFinderApplication;
import com.relhs.asianfinder.ProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.ProfileUserDetailsMultipleSelectionArrayAdapter;
import com.relhs.asianfinder.adapter.UserDetailsRangeSelectionAdapter;
import com.relhs.asianfinder.data.UserDetailsMultipleSelectionModel;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomEditTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProfileUserDetailsEditDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG = ProfileUserDetailsEditDialogFragment.class.getSimpleName();
    private View myFragmentView;
    public static final String ARG_DBNAME = "dbName";
    public static final String ARG_LABEL = "label";
    public static final String ARG_TYPE = "type";
    public static final String ARG_VALUE = "value";
    public static final String ARG_IDS = "ids";

    private String mParamDbName;
    private String mParamLabel;
    private String mParamType;
    private String mParamValue;
    private String mParamIds;

    private RelativeLayout layoutData;
    private LinearLayout layoutRange;
    private LinearLayout layoutSingle;
    private Spinner spinnerSingle;

    private CustomEditTextView etTextInput;
    private ListView listMultipleSelection;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private ProfileUserDetailsEditDialogListener mListener;
    private LinearLayout layoutButtons;
    private Button btnOkay;
    private Button btnCancel;

    @Override
    public void onClick(View view) {

    }

    public interface ProfileUserDetailsEditDialogListener {
        public void onProfileUserDetailsEditDialogPositiveClick(DialogFragment dialog, String k, String value);
        public void onProfileUserDetailsEditDialogNegativeClick(DialogFragment dialog);
    }

    public ProfileUserDetailsEditDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();

        int width = getResources().getDisplayMetrics().widthPixels - (getResources().getDisplayMetrics().widthPixels/4);
        window.setLayout(width, windowParams.WRAP_CONTENT);
        windowParams.dimAmount = 0.90f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // To verify whether acrivity inherits the callback interface host
        try {
            // Instantiate a NoticeDialogListener so that we can pass the event to the host
            mListener = (ProfileUserDetailsEditDialogListener) activity;
        } catch (ClassCastException e) {
            // Activity no inheritance interface exception is thrown
            throw new ClassCastException(activity.toString()
                    + " must implement LocationSelectionDialogFragment");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_Panel);

        if (getArguments() != null) {
            mParamDbName = getArguments().getString(ARG_DBNAME);
            mParamLabel = getArguments().getString(ARG_LABEL);
            mParamType = getArguments().getString(ARG_TYPE);
            mParamValue = getArguments().getString(ARG_VALUE);
            mParamIds = getArguments().getString(ARG_IDS);
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_profile_user_details_edit_dialog, container, false);
        TextView lbl_your_name = (TextView) myFragmentView.findViewById(R.id.lbl_your_name);
        lbl_your_name.setText(mParamLabel);

        layoutData = (RelativeLayout) myFragmentView.findViewById(R.id.layoutData);

        layoutRange = (LinearLayout) myFragmentView.findViewById(R.id.layoutRange);
        spinnerFrom = (Spinner) myFragmentView.findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) myFragmentView.findViewById(R.id.spinnerTo);

        layoutSingle = (LinearLayout) myFragmentView.findViewById(R.id.layoutSingle);
        spinnerSingle = (Spinner) myFragmentView.findViewById(R.id.spinnerSingle);

        layoutButtons = (LinearLayout) myFragmentView.findViewById(R.id.layoutButtons);

        listMultipleSelection = (ListView) myFragmentView.findViewById(R.id.listMultipleSelection);



        etTextInput = (CustomEditTextView) myFragmentView.findViewById(R.id.etTextInput);


        btnCancel = (Button)myFragmentView.findViewById(R.id.btnDialogCancel);
        btnOkay = (Button)myFragmentView.findViewById(R.id.btnDialogOkay);

        if(mParamType.equalsIgnoreCase("text")) {
            layoutRange.setVisibility(View.GONE);
            listMultipleSelection.setVisibility(View.GONE);

            layoutData.setVisibility(View.VISIBLE);
            etTextInput.setVisibility(View.VISIBLE);
            layoutButtons.setVisibility(View.VISIBLE);

            etTextInput.setText(mParamValue);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onProfileUserDetailsEditDialogNegativeClick(ProfileUserDetailsEditDialogFragment.this);
                }
            });

            btnOkay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onProfileUserDetailsEditDialogPositiveClick(ProfileUserDetailsEditDialogFragment.this, mParamDbName, etTextInput.getText().toString());
                }
            });
        } else {
            new LoadMultipleSelectionDataTask(inflater, mParamDbName, null).execute();
        }

        return myFragmentView;
    }

     private class LoadMultipleSelectionDataTask extends AsyncTask<Void, Void, JSONObject> {
        private ProgressDialog mProgressDialog;
        private JSONParser jParser;
        private JSONArray jsonArray;
        private LayoutInflater _inflater;
        private String _k;
        private String _lid;
        private List<UserDetailsMultipleSelectionModel> list;
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
            String did = ((ProfileActivity)getActivity()).getDeviceId();
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
                    list = new ArrayList<UserDetailsMultipleSelectionModel>();
                    JSONObject jsonArrayLoginDataObject = c.getJSONObject(AsianFinderApplication.TAG_DATA);
                    selectionType = jsonArrayLoginDataObject.getString("type");

                    JSONArray jsonArrayOptions = jsonArrayLoginDataObject.getJSONArray("options");
                    for (int i=0; i<jsonArrayOptions.length(); i++) {
                        JSONObject jsonObjectOptions = jsonArrayOptions.getJSONObject(i);
                        UserDetailsMultipleSelectionModel preferenceMultipleSelectionModel;

                        String[] values = mParamIds.trim().split("\\s*,\\s*");
                        if (Arrays.asList(values).contains(jsonObjectOptions.getString("value"))) {
                            preferenceMultipleSelectionModel = new UserDetailsMultipleSelectionModel(jsonObjectOptions.getString("value"),
                                    jsonObjectOptions.getString("caption"), selectionType, true);
                        } else {
                            preferenceMultipleSelectionModel = new UserDetailsMultipleSelectionModel(jsonObjectOptions.getString("value"),
                                    jsonObjectOptions.getString("caption"), selectionType, false);
                        }
                        list.add(preferenceMultipleSelectionModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                Log.d("-- robert", "Selection Type : "+mParamType);
                if(mParamType.equalsIgnoreCase("single")) {
                    layoutData.setVisibility(View.VISIBLE);
                    layoutSingle.setVisibility(View.VISIBLE);

                    layoutButtons.setVisibility(View.VISIBLE);

                    UserDetailsRangeSelectionAdapter userDetailsRangeSelectionAdapter = new UserDetailsRangeSelectionAdapter(getActivity(), R.layout.spinner_text, (ArrayList<UserDetailsMultipleSelectionModel>) list);
                    spinnerSingle.setAdapter(userDetailsRangeSelectionAdapter);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onProfileUserDetailsEditDialogNegativeClick(ProfileUserDetailsEditDialogFragment.this);
                        }
                    });

                    btnOkay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final UserDetailsMultipleSelectionModel item = (UserDetailsMultipleSelectionModel) spinnerSingle.getItemAtPosition(spinnerSingle.getSelectedItemPosition());
                            //Toast.makeText(getActivity(), item.getValue(), Toast.LENGTH_SHORT).show();
                            mListener.onProfileUserDetailsEditDialogPositiveClick(ProfileUserDetailsEditDialogFragment.this, mParamDbName, item.getValue());
                        }
                    });
                } else if(mParamType.equalsIgnoreCase("multiple")) {
                    layoutButtons.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onProfileUserDetailsEditDialogNegativeClick(ProfileUserDetailsEditDialogFragment.this);
                        }
                    });

                    btnOkay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (selectionType.equalsIgnoreCase("text")) {
                                mListener.onProfileUserDetailsEditDialogPositiveClick(ProfileUserDetailsEditDialogFragment.this, mParamDbName, etTextInput.getText().toString());
                            }
                        }
                    });

                    ArrayAdapter<UserDetailsMultipleSelectionModel> adapter = new ProfileUserDetailsMultipleSelectionArrayAdapter(getActivity(), list, mParamIds);
                    listMultipleSelection.setAdapter(adapter);

                    layoutData.setVisibility(View.VISIBLE);
                }

                mProgressDialog.dismiss();
            }
        }
    }

}
