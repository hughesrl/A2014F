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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.relhs.asianfinder.AsianFinderApplication;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.SpinnerCustomAdapter;
import com.relhs.asianfinder.data.SpinnerItems;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationSelectionDialogFragment extends DialogFragment {

    private String purposeToOpen;

    private Spinner spinnerLocationCountry;
    private Spinner spinnerLocationState;
    private Spinner spinnerLocationCity;


    final ArrayList<SpinnerItems> spinnerCountryItems = new ArrayList<SpinnerItems>();
    final ArrayList<SpinnerItems> spinnerStateItems = new ArrayList<SpinnerItems>();
    final ArrayList<SpinnerItems> spinnerCityItems = new ArrayList<SpinnerItems>();


    public LocationSelectionDialogFragment() { }

    /*In order to receive event callback, create a dialog box activity must implement this interface.
     * In case the host need to query the properties dialog box, each method will pass a DialogFragment instance.  */
    public interface LocationSelectionDialogListener {
        public void onLocationSelectionDialogPositiveClick(DialogFragment dialog, SpinnerItems country, SpinnerItems state, SpinnerItems city);
        public void onLocationSelectionDialogNegativeClick(DialogFragment dialog);
    }

    // Examples of the use of this interface to transmit motion events
    LocationSelectionDialogListener mListener;

    public static LocationSelectionDialogFragment newInstance() {
        LocationSelectionDialogFragment f = new LocationSelectionDialogFragment();
        return f;
    }

    // Override the Fragment.onAttach () method to instantiate NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // To verify whether acrivity inherits the callback interface host
        try {
            // Instantiate a NoticeDialogListener so that we can pass the event to the host
            mListener = (LocationSelectionDialogListener) activity;
        } catch (ClassCastException e) {
            // Activity no inheritance interface exception is thrown
            throw new ClassCastException(activity.toString()
                    + " must implement LocationSelectionDialogFragment");
        }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_Panel);


    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View myFragmentView = inflater.inflate(R.layout.dialog_location_selection, container, false);

        spinnerLocationCountry = (Spinner) myFragmentView.findViewById(R.id.spinnerLocationCountry);
        spinnerLocationState = (Spinner) myFragmentView.findViewById(R.id.spinnerLocationState);
        spinnerLocationCity = (Spinner) myFragmentView.findViewById(R.id.spinnerLocationCity);

        SpinnerCustomAdapter adapterCountry = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, populateDefaultSpinnerData("Country"));
        spinnerLocationCountry.setAdapter(adapterCountry);

        SpinnerCustomAdapter adapterState = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, populateDefaultSpinnerData("State"));
        spinnerLocationState.setAdapter(adapterState);
        spinnerLocationState.setEnabled(false);

        SpinnerCustomAdapter adapterCity = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, populateDefaultSpinnerData("City"));
        spinnerLocationCity.setAdapter(adapterCity);
        spinnerLocationCity.setEnabled(false);

        new PopulateCountries().execute();

        Button btnCancel = (Button)myFragmentView.findViewById(R.id.btnDialogCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLocationSelectionDialogNegativeClick(LocationSelectionDialogFragment.this);
            }
        });
        Button btnOkay = (Button)myFragmentView.findViewById(R.id.btnDialogOkay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpinnerItems itemLocationCountry = null;
                SpinnerItems itemLocationState = null;
                SpinnerItems itemLocationCity = null;
                if(spinnerLocationCity.getVisibility() == View.VISIBLE) {
                    if(!spinnerLocationCity.getSelectedItem().toString().isEmpty() && !spinnerLocationState.getSelectedItem().toString().equalsIgnoreCase("city")) {
                        itemLocationCity = (SpinnerItems) spinnerLocationCity.getItemAtPosition(spinnerLocationCity.getSelectedItemPosition());
                    }
                }

                if(spinnerLocationState.getVisibility() == View.VISIBLE) {
                    if(!spinnerLocationState.getSelectedItem().toString().isEmpty() && !spinnerLocationState.getSelectedItem().toString().equalsIgnoreCase("states")) {
                        itemLocationState = (SpinnerItems) spinnerLocationState.getItemAtPosition(spinnerLocationState.getSelectedItemPosition());
                    }
                }

                if(spinnerLocationCountry.getVisibility() == View.VISIBLE) {
                    if(!spinnerLocationCountry.getSelectedItem().toString().isEmpty() && !spinnerLocationState.getSelectedItem().toString().equalsIgnoreCase("country")) {
                        itemLocationCountry = (SpinnerItems) spinnerLocationCountry.getItemAtPosition(spinnerLocationCountry.getSelectedItemPosition());

                    }
                }
                mListener.onLocationSelectionDialogPositiveClick(LocationSelectionDialogFragment.this, itemLocationCountry, itemLocationState, itemLocationCity);
            }
        });

        return myFragmentView;
    }

    public static ArrayList<SpinnerItems> populateDefaultSpinnerData(String title){
        final ArrayList<SpinnerItems> spinnerItems = new ArrayList<SpinnerItems>();
        spinnerItems.add(new SpinnerItems(title, "", false));
        return spinnerItems;
    }

    /************************* ASYNSTASK HERE *************************/
    private class PopulateCountries extends AsyncTask<Void, Void, ArrayList<SpinnerItems>> {
        private ProgressDialog mProgressDialog;

        public PopulateCountries() {
            // TODO Auto-generated constructor stub
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
        protected ArrayList<SpinnerItems> doInBackground(Void... args) {
            JSONParser jParser = new JSONParser();
            JSONObject jsonObject = jParser.getJSONFromUrl(getResources().getString(R.string.api) + "?act=country", null);
            spinnerCountryItems.add(new SpinnerItems("Country", "", false));
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(Constants.TAG_OS);
                JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(0);
                if(!jsonArrayJSONObject.getBoolean(Constants.TAG_STATUS)) { // false
                    Log.d("-- robert", jsonArrayJSONObject.getString(Constants.TAG_MESSAGE));
                } else { // true
                    JSONArray jsonObjectData = jsonArrayJSONObject.getJSONArray(Constants.TAG_DATA);
                    for (int i = 0; i < jsonObjectData.length(); i++) {
                        JSONObject c = jsonObjectData.getJSONObject(i);
                        spinnerCountryItems.add(new SpinnerItems(c.getString("name"), c.getString("id"), true));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return spinnerCountryItems;
        }
        @Override
        protected void onPostExecute(final ArrayList<SpinnerItems> spinnerCountryItems) {
            mProgressDialog.dismiss();
            spinnerLocationCountry.setVisibility(View.VISIBLE);
            SpinnerCustomAdapter adapterTo = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, spinnerCountryItems);
            spinnerLocationCountry.setAdapter(adapterTo);
            spinnerLocationCountry.setEnabled(true);

            spinnerLocationCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    final SpinnerItems item = (SpinnerItems) parent.getItemAtPosition(position);
                    if(!item.getSpinnerValue().isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!item.getSpinnerValue().isEmpty()) {
                                    new PopulateStates(item.getSpinnerValue()).execute();
                                } else {
                                    spinnerLocationState.setVisibility(View.GONE);
                                    spinnerLocationState.setEnabled(false);
                                    spinnerLocationCity.setVisibility(View.GONE);
                                    spinnerLocationCity.setEnabled(false);
                                }
                            }
                        });
                    } else {
                        spinnerLocationState.setVisibility(View.GONE);
                        spinnerLocationState.setEnabled(false);
                        spinnerLocationCity.setVisibility(View.GONE);
                        spinnerLocationCity.setEnabled(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


        private class PopulateStates extends AsyncTask<Void, Void, ArrayList<SpinnerItems>> {
            private ProgressDialog mProgressDialog;
            private String countryId;


            public PopulateStates(String countryId) {
                // TODO Auto-generated constructor stub
                this.countryId = countryId;
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
            protected ArrayList<SpinnerItems> doInBackground(Void... args) {
                spinnerStateItems.clear();
                JSONParser jParser = new JSONParser();
                JSONObject jsonObject = jParser.getJSONFromUrl(getResources().getString(R.string.api) + "?act=state&_lid=" + countryId, null);
                spinnerStateItems.add(new SpinnerItems("States", "", false));
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray(Constants.TAG_OS);
                    JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(0);
                    if (!jsonArrayJSONObject.getBoolean(Constants.TAG_STATUS)) { // false
                        Log.d("-- robert", jsonArrayJSONObject.getString(Constants.TAG_MESSAGE));
                    } else { // true
                        JSONArray jsonObjectData = jsonArrayJSONObject.getJSONArray(Constants.TAG_DATA);
                        for (int i = 0; i < jsonObjectData.length(); i++) {
                            JSONObject c = jsonObjectData.getJSONObject(i);
                            spinnerStateItems.add(new SpinnerItems(c.getString("name"), c.getString("id"), true));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return spinnerStateItems;
            }

            @Override
            protected void onPostExecute(final ArrayList<SpinnerItems> spinnerStateItems) {
                mProgressDialog.dismiss();
                spinnerLocationState.setVisibility(View.VISIBLE);
                SpinnerCustomAdapter adapterTo = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, spinnerStateItems);
                spinnerLocationState.setAdapter(adapterTo);
                spinnerLocationState.setEnabled(true);
                spinnerLocationState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final SpinnerItems item = (SpinnerItems) parent.getItemAtPosition(position);
                        if(!item.getSpinnerValue().isEmpty()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!item.getSpinnerValue().isEmpty()) {
                                        new PopulateCity(item.getSpinnerValue()).execute();
                                    } else {
                                        spinnerLocationCity.setVisibility(View.GONE);
                                        spinnerLocationCity.setEnabled(false);
                                    }
                                }
                            });
                        } else {
                            spinnerLocationCity.setVisibility(View.GONE);
                            spinnerLocationCity.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }

        private class PopulateCity extends AsyncTask<Void, Void, ArrayList<SpinnerItems>> {
            private ProgressDialog mProgressDialog;
            private String statesId;


            public PopulateCity(String statesId) {
                // TODO Auto-generated constructor stub
                this.statesId = statesId;
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
            protected ArrayList<SpinnerItems> doInBackground(Void... args) {
                spinnerCityItems.clear();
                JSONParser jParser = new JSONParser();
                JSONObject jsonObject = jParser.getJSONFromUrl(getResources().getString(R.string.api) + "?act=city&_lid=" + statesId, null);
                spinnerCityItems.add(new SpinnerItems("City", "", false));
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray(Constants.TAG_OS);
                    JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(0);
                    if (!jsonArrayJSONObject.getBoolean(Constants.TAG_STATUS)) { // false
                        Log.d("-- robert", jsonArrayJSONObject.getString(Constants.TAG_MESSAGE));
                    } else { // true
                        JSONArray jsonObjectData = jsonArrayJSONObject.getJSONArray(Constants.TAG_DATA);
                        for (int i = 0; i < jsonObjectData.length(); i++) {
                            JSONObject c = jsonObjectData.getJSONObject(i);
                            spinnerCityItems.add(new SpinnerItems(c.getString("name"), c.getString("id"), true));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return spinnerCityItems;
            }

            @Override
            protected void onPostExecute(final ArrayList<SpinnerItems> spinnerStateItems) {
                mProgressDialog.dismiss();
                spinnerLocationCity.setVisibility(View.VISIBLE);
                SpinnerCustomAdapter adapterTo = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, spinnerStateItems);
                spinnerLocationCity.setAdapter(adapterTo);
                spinnerLocationCity.setEnabled(true);
            }
        }
    }


}
