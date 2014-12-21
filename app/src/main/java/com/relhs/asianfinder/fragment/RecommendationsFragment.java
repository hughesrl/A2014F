package com.relhs.asianfinder.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.DashboardActivity;
import com.relhs.asianfinder.PeopleProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.UserPreferenceActivity;
import com.relhs.asianfinder.adapter.PeopleListAdapter;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.data.SpinnerItems;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RecommendationsFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_LATITUDE = "latitude";

    private View myFragmentView;
    private int mParamItemNumber;
    private double mParamLong;
    private double mParamLat;

    private int currentOffset = 0;



    JSONParser jParser;
    private AsymmetricGridView mListView;
    private PeopleListAdapter adapter;
    private ArrayList<PeopleInfo> peopleInfoArrayList = new ArrayList<PeopleInfo>();
    private LinearLayout updatePreference;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendationsFragment newInstance(int sectionNumber, double longitude, double latitude) {
        RecommendationsFragment fragment = new RecommendationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putDouble(ARG_LATITUDE, latitude);
        fragment.setArguments(args);
        return fragment;
    }

    public RecommendationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamItemNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mParamLong = getArguments().getDouble(ARG_LONGITUDE);
            mParamLat = getArguments().getInt(ARG_LATITUDE);
        }

        jParser = new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_recommendations, container, false);

        updatePreference = (LinearLayout) myFragmentView.findViewById(R.id.updatePreference);

        mListView = (AsymmetricGridView) myFragmentView.findViewById(R.id.listView);
        mListView.setRequestedColumnCount(2);


        String url = getResources().getString(R.string.api)+"?act=search&t=mymatches&did="+ ((DashboardActivity)getActivity()).getDeviceId();
        new GetAllNearest(mParamLong, mParamLat, url).execute();


        return myFragmentView;
    }
    /************************* ASYNSTASK HERE *************************/
    private class GetAllNearest extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {
        private double longitude;
        private double latitude;
        private String api_url;

        private JSONArray jsonArrayAccounts;
        private ProgressDialog mProgressDialog;


        public GetAllNearest(double longitude, double latitude, String api_url) {
            // TODO Auto-generated constructor stub
            this.longitude = longitude;
            this.latitude = latitude;
            this.api_url = api_url;
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
        protected ArrayList<PeopleInfo> doInBackground(Void... args) {
            if(!peopleInfoArrayList.isEmpty()) {
                peopleInfoArrayList.clear();
            }
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(api_url, null);
            try {
                JSONArray android = json.getJSONArray(Constants.TAG_OS);
                JSONObject jsonObject = android.getJSONObject(0);
                if(!jsonObject.getBoolean(Constants.TAG_STATUS)) { // false
                    Log.d("-- robert", jsonObject.getString(Constants.TAG_MESSAGE));
                } else { // true
                    JSONObject jsonObjectData = jsonObject.getJSONObject(Constants.TAG_DATA);

                    jsonArrayAccounts = jsonObjectData.getJSONArray(Constants.TAG_ACCOUNTS);

                    for (int i = 0; i < jsonArrayAccounts.length(); i++) {
                        JSONObject jsonObjectAccounts = jsonArrayAccounts.getJSONObject(i);
                        int colSpan = 1;
                        int rowSpan = 1;

                        JSONArray jsonArrayProfile = jsonObjectAccounts.getJSONArray(Constants.TAG_PROFILE);
                        JSONObject jsonObjectProfile = jsonArrayProfile.getJSONObject(0);

                        int id = jsonObjectProfile.getInt("id");
                        String username = jsonObjectProfile.getString("username");
                        String gender = jsonObjectProfile.getString("gender");
                        String aged = jsonObjectProfile.getString("aged");
                        String country = jsonObjectProfile.getString("country");
                        String state = jsonObjectProfile.getString("state");
                        String city = jsonObjectProfile.getString("city");
                        int is_online = jsonObjectProfile.getInt("is_online");

                        JSONArray jsonArrayPHOTOS = jsonObjectAccounts.getJSONArray(Constants.TAG_PHOTOS);
                        JSONObject jsonObjectPHOTOS = jsonArrayPHOTOS.getJSONObject(0);

                        String main_photo = "";
                        String subphoto_1 = "";
                        String subphoto_2 = "";

                        if(jsonObjectPHOTOS.has("main_photo")) {
                            main_photo = jsonObjectPHOTOS.getString("main_photo");
                        }
                        if(jsonObjectPHOTOS.has("subphoto_1")) {
                            subphoto_1 = jsonObjectPHOTOS.getString("subphoto_1");
                        }
                        if(jsonObjectPHOTOS.has("subphoto_2")) {
                            subphoto_2 = jsonObjectPHOTOS.getString("subphoto_2");
                        }

                        PeopleInfo peopleInfo = new PeopleInfo(colSpan, rowSpan, currentOffset + i,
                                id, username, gender,
                                aged, country, state, city, is_online,
                                main_photo, subphoto_1, subphoto_2);

                        peopleInfoArrayList.add(peopleInfo);
                    }
                    currentOffset += jsonArrayAccounts.length();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return peopleInfoArrayList;
        }
        @Override
        protected void onPostExecute(ArrayList<PeopleInfo> peopleInfoArrayList) {
            if(!peopleInfoArrayList.isEmpty()) {
                adapter = new PeopleListAdapter(getActivity(), mListView, new ArrayList<PeopleInfo>());
                adapter.setItems(peopleInfoArrayList);

                mListView.setAdapter(adapter);
                mListView.setDebugging(false);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PeopleInfo peopleInfo = (PeopleInfo) parent.getItemAtPosition(position);
                        Intent intentDetails = new Intent(getActivity(), PeopleProfileActivity.class);

                        intentDetails.putExtra(PeopleProfileActivity.INTENT_PID, peopleInfo.getId());
                        intentDetails.putExtra(PeopleProfileActivity.INTENT_PDATA, peopleInfo);

                        startActivity(intentDetails);
                    }
                });
                mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        // TODO Auto-generated method stub
                        int threshold = 1;
                        int count = mListView.getCount();
                        if (scrollState == SCROLL_STATE_IDLE) {
                            if (mListView.getLastVisiblePosition() >= count-threshold) {
                                new LoadMoreDataTask(api_url).execute();
                            }
                        }
                    }
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                                         int visibleItemCount, int totalItemCount) {
                        // TODO Auto-generated method stub
                    }
                });
            } else {
                updatePreference.setVisibility(View.VISIBLE);
                CustomButton btnUpdatePreference = (CustomButton) myFragmentView.findViewById(R.id.btnUpdatePreference);
                btnUpdatePreference.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent testIntent = new Intent(getActivity(), UserPreferenceActivity.class);
                        startActivity(testIntent);
                    }
                });

            }

            mProgressDialog.dismiss();
        }
    }

    private class LoadMoreDataTask extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {
//        private JSONArray android;
        private JSONArray jsonArrayAccounts;
        private ProgressDialog mProgressDialog;
        private String api_url;

        public LoadMoreDataTask(String api_url) {
            // TODO Auto-generated constructor stub
            this.api_url = api_url;
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
        protected ArrayList<PeopleInfo> doInBackground(Void... args) {
            peopleInfoArrayList.clear();

            JSONObject json = jParser.getJSONFromUrl(api_url+"&_p="+currentOffset, null);
            Log.d("-- robert", json.toString());
            try {
                JSONArray android = json.getJSONArray(Constants.TAG_OS);
                JSONObject jsonObject = android.getJSONObject(0);
                if(!jsonObject.getBoolean(Constants.TAG_STATUS)) { // false
                    Log.d("-- robert", jsonObject.getString(Constants.TAG_MESSAGE));
                } else { // true
                    JSONObject jsonObjectData = jsonObject.getJSONObject(Constants.TAG_DATA);

                    jsonArrayAccounts = jsonObjectData.getJSONArray(Constants.TAG_ACCOUNTS);

                    for (int i = 0; i < jsonArrayAccounts.length(); i++) {
                        JSONObject jsonObjectAccounts = jsonArrayAccounts.getJSONObject(i);
                        // Storing  JSON item in a Variable
                        // Height of the row is 3/4 of the screen width
                        int colSpan = 1;
                        int rowSpan = 1;

                        JSONArray jsonArrayProfile = jsonObjectAccounts.getJSONArray(Constants.TAG_PROFILE);
                        JSONObject jsonObjectProfile = jsonArrayProfile.getJSONObject(0);

                        int id = jsonObjectProfile.getInt("id");
                        String username = jsonObjectProfile.getString("username");
                        String gender = jsonObjectProfile.getString("gender");
                        String aged = jsonObjectProfile.getString("aged");
                        String country = jsonObjectProfile.getString("country");
                        String state = jsonObjectProfile.getString("state");
                        String city = jsonObjectProfile.getString("city");
                        int is_online = jsonObjectProfile.getInt("is_online");

                        JSONArray jsonArrayPHOTOS = jsonObjectAccounts.getJSONArray(Constants.TAG_PHOTOS);
                        JSONObject jsonObjectPHOTOS = jsonArrayPHOTOS.getJSONObject(0);

                        String main_photo = "";
                        String subphoto_1 = "";
                        String subphoto_2 = "";

                        if(jsonObjectPHOTOS.has("main_photo")) {
                            main_photo = jsonObjectPHOTOS.getString("main_photo");
                        }
                        if(jsonObjectPHOTOS.has("subphoto_1")) {
                            subphoto_1 = jsonObjectPHOTOS.getString("subphoto_1");
                        }
                        if(jsonObjectPHOTOS.has("subphoto_2")) {
                            subphoto_2 = jsonObjectPHOTOS.getString("subphoto_2");
                        }

                        PeopleInfo peopleInfo = new PeopleInfo(colSpan, rowSpan, currentOffset + i, id, username, gender, aged, country, state,
                                city, is_online, main_photo, subphoto_1, subphoto_2);

                        peopleInfoArrayList.add(peopleInfo);
                    }
                    currentOffset += jsonArrayAccounts.length();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return peopleInfoArrayList;
        }
        @Override
        protected void onPostExecute(ArrayList<PeopleInfo> peopleInfoArrayList) {
            if(!peopleInfoArrayList.isEmpty()) {
                adapter.appendItems(peopleInfoArrayList);
            } else {
                Toast.makeText(getActivity(), "No More data", Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
        }
    }




    public static ArrayList<SpinnerItems> populateShowPeopleFilter(){
        final ArrayList<SpinnerItems> spinnerItems = new ArrayList<SpinnerItems>();
        spinnerItems.add(new SpinnerItems("All", "all", true));
        spinnerItems.add(new SpinnerItems("Male", "all", true));
        spinnerItems.add(new SpinnerItems("Female", "all", true));
        spinnerItems.add(new SpinnerItems("Transgender", "all", true));
        return spinnerItems;
    }

    private class CustomAdapter extends ArrayAdapter<SpinnerItems> {
        private Activity context;
        ArrayList<SpinnerItems> spinnerItems;

        public CustomAdapter(Activity context, int resource, ArrayList<SpinnerItems> spinnerItems) {
            super(context, resource, spinnerItems);
            this.context = context;
            this.spinnerItems = spinnerItems;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerItems current = spinnerItems.get(position);

            LayoutInflater inflater = getLayoutInflater(getArguments());
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView name = (TextView) row.findViewById(R.id.spinnerTxtTitle);
            Typeface myTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/VAGRoundedLight.ttf");

            name.setTypeface(myTypeFace, Typeface.NORMAL);
            name.setGravity(Gravity.LEFT);
            name.setTextSize(18);
            if(!current.getSpinnerStatus()) {
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
            if(!current.getSpinnerStatus()) {
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

}
