package com.relhs.asianfinder.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.PeopleListAdapter;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BrowseFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_LATITUDE = "latitude";

    private View myFragmentView;
    private int mParamItemNumber;
    private double mParamLong;
    private double mParamLat;

    private int currentOffset = 0;

    private int currentPage = 0;

    JSONParser jParser;
    private AsymmetricGridView mListView;
    private PeopleListAdapter adapter;
    private ArrayList<PeopleInfo> peopleInfoArrayList = new ArrayList<PeopleInfo>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(int sectionNumber, double longitude, double latitude) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putDouble(ARG_LATITUDE, latitude);
        fragment.setArguments(args);
        return fragment;
    }

    public BrowseFragment() {
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
        myFragmentView = inflater.inflate(R.layout.fragment_browse, container, false);

        mListView = (AsymmetricGridView) myFragmentView.findViewById(R.id.listView);
        mListView.setRequestedColumnCount(2);
        new GetAllNearest(mParamLong, mParamLat).execute();

        return myFragmentView;
    }
    /************************* ASYNSTASK HERE *************************/
    private class GetAllNearest extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {
        private double longitude;
        private double latitude;

        private JSONArray android;
        private JSONArray androidData;
        private ProgressDialog mProgressDialog;

        public GetAllNearest(double longitude, double latitude) {
            // TODO Auto-generated constructor stub
            this.longitude = longitude;
            this.latitude = latitude;
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

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(getResources().getString(R.string.api)+"?act=search&t=all&did=fasdfasdfasd", null);
            try {
                android = json.getJSONArray(Constants.TAG_OS);
                JSONObject jsonObject = android.getJSONObject(0);
                if(!jsonObject.getBoolean(Constants.TAG_STATUS)) { // false
                    Toast.makeText(getActivity(), jsonObject.getString(Constants.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                } else { // true
                    JSONObject jsonObjectData = jsonObject.getJSONObject(Constants.TAG_DATA);

                    JSONArray jsonArrayAccount = jsonObjectData.getJSONArray(Constants.TAG_ACCOUNTS);

                    for (int i = 0; i < jsonArrayAccount.length(); i++) {
                        JSONObject jsonObjectAccounts = jsonArrayAccount.getJSONObject(i);
                        // Storing  JSON item in a Variable
                        // Height of the row is 3/4 of the screen width
                        int colSpan = 1;
                        int rowSpan = 1;

                        JSONArray jsonArrayProfile = jsonObjectAccounts.getJSONArray(Constants.TAG_PROFILE);
                        JSONObject jsonObjectProfile = jsonArrayProfile.getJSONObject(0);

                        String username = jsonObjectProfile.getString("username");
                        String gender = jsonObjectProfile.getString("gender");
                        String aged = jsonObjectProfile.getString("aged");
                        String country = jsonObjectProfile.getString("country");
                        String state = jsonObjectProfile.getString("state");
                        String city = jsonObjectProfile.getString("city");
                        int is_online = jsonObjectProfile.getInt("is_online");




                        JSONArray jsonArrayPHOTOS = jsonObjectAccounts.getJSONArray(Constants.TAG_PHOTOS);
                        JSONObject jsonObjectPHOTOS = jsonArrayPHOTOS.getJSONObject(0);


                        String main_photo = jsonObjectPHOTOS.getString("main_photo");
                        String subphoto_1 = jsonObjectPHOTOS.getString("subphoto_1");
                        String subphoto_2 = jsonObjectPHOTOS.getString("subphoto_2");

                        PeopleInfo peopleInfo = new PeopleInfo(colSpan, rowSpan, currentOffset + i, username, gender,
                                aged, country, state, city, is_online,
                                main_photo, subphoto_1, subphoto_2);

                        peopleInfoArrayList.add(peopleInfo);


                    }
                    JSONArray jsonArrayServerResponse = jsonObjectData.getJSONArray(Constants.TAG_SERVER_RESPONSE);
                    JSONObject jsonObjectServerResponse = jsonArrayServerResponse.getJSONObject(0);
                    //Log.d("-- robert", "currentpage : " + jsonObjectServerResponse.getString("current_page"));
                    currentPage = jsonObjectServerResponse.getInt("current_page");

                    currentOffset += android.length();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return peopleInfoArrayList;
        }
        @Override
        protected void onPostExecute(ArrayList<PeopleInfo> peopleInfoArrayList) {
            adapter = new PeopleListAdapter(getActivity(), mListView, new ArrayList<PeopleInfo>());
            adapter.setItems(peopleInfoArrayList);

            mListView.setAdapter(adapter);
            mListView.setDebugging(false);

            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // TODO Auto-generated method stub
                    int threshold = 1;
                    int count = mListView.getCount();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (mListView.getLastVisiblePosition() >= count-threshold) {
                            new LoadMoreDataTask().execute();
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub
                }
            });

            mProgressDialog.dismiss();
        }
    }

    private class LoadMoreDataTask extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {
        private JSONArray android;
        private ProgressDialog mProgressDialog;

        public LoadMoreDataTask() {
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
        protected ArrayList<PeopleInfo> doInBackground(Void... args) {
            //peopleInfoArrayList.clear();

            // Getting JSON from URL
            //int _p = currentPage + 1;
            currentPage += 1;

            JSONObject json = jParser.getJSONFromUrl(getResources().getString(R.string.api)+"?act=search&t=all&did=fasdfasdfasd&_p="+currentPage, null);
            Log.d("-- robert", json.toString());
            try {
                android = json.getJSONArray(Constants.TAG_OS);
                JSONObject jsonObject = android.getJSONObject(0);
                if(!jsonObject.getBoolean(Constants.TAG_STATUS)) { // false
                    Toast.makeText(getActivity(), jsonObject.getString(Constants.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                } else { // true
                    JSONObject jsonObjectData = jsonObject.getJSONObject(Constants.TAG_DATA);

                    JSONArray jsonArrayAccount = jsonObjectData.getJSONArray(Constants.TAG_ACCOUNTS);

                    for (int i = 0; i < jsonArrayAccount.length(); i++) {
                        JSONObject jsonObjectAccounts = jsonArrayAccount.getJSONObject(i);
                        // Storing  JSON item in a Variable
                        // Height of the row is 3/4 of the screen width
                        int colSpan = 1;
                        int rowSpan = 1;

                        JSONArray jsonArrayProfile = jsonObjectAccounts.getJSONArray(Constants.TAG_PROFILE);
                        JSONObject jsonObjectProfile = jsonArrayProfile.getJSONObject(0);


                        String username = jsonObjectProfile.getString("username");
                        Log.d("-- robert", username);
                        String gender = jsonObjectProfile.getString("gender");
                        String aged = jsonObjectProfile.getString("aged");
                        String country = jsonObjectProfile.getString("country");
                        String state = jsonObjectProfile.getString("state");
                        String city = jsonObjectProfile.getString("city");
                        int is_online = jsonObjectProfile.getInt("is_online");

                        JSONArray jsonArrayPHOTOS = jsonObjectAccounts.getJSONArray(Constants.TAG_PHOTOS);
                        JSONObject jsonObjectPHOTOS = jsonArrayPHOTOS.getJSONObject(0);
                        String main_photo = jsonObjectPHOTOS.getString("main_photo");
                        String subphoto_1 = jsonObjectPHOTOS.getString("subphoto_1");
                        String subphoto_2 = jsonObjectPHOTOS.getString("subphoto_2");

                        PeopleInfo peopleInfo = new PeopleInfo(colSpan, rowSpan, currentOffset + i, username, gender, aged, country, state, city, is_online, main_photo, subphoto_1, subphoto_2);

                        peopleInfoArrayList.add(peopleInfo);
                    }
                    JSONArray jsonArrayServerResponse = jsonObjectData.getJSONArray(Constants.TAG_SERVER_RESPONSE);
                    JSONObject jsonObjectServerResponse = jsonArrayServerResponse.getJSONObject(0);

                    Log.d("-- robert LOADMORE", "currentpage : " + jsonObjectServerResponse.getString("current_page"));

                    //jsonObjectServerResponse.getInt("current_page");

                    currentOffset += android.length();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //((AsymmetricGridViewAdapter<PeopleInfo>) mListView.getAdapter()).appendItems(peopleInfoArrayList);
            return peopleInfoArrayList;
        }
        @Override
        protected void onPostExecute(ArrayList<PeopleInfo> peopleInfoArrayList) {
            adapter.appendItems(peopleInfoArrayList);
            mProgressDialog.dismiss();
        }
    }
}
