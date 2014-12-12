package com.relhs.asianfinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.adapter.PeopleListAdapter;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.data.ProfilePreferenceDataInfo;
import com.relhs.asianfinder.data.ProfilePreferenceHeaderInfo;
import com.relhs.asianfinder.data.Searches;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PreferenceInfoOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;
import com.relhs.asianfinder.view.CustomEditTextView;
import com.relhs.asianfinder.view.CustomTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends Activity {
    private int currentOffset = 0;
    public static String INTENT_SEARCHES = "_searches";
    private List<NameValuePair> paramsAPI;

    JSONParser jParser;
    private AsymmetricGridView mListView;
    private PeopleListAdapter adapter;
    private ArrayList<PeopleInfo> peopleInfoArrayList = new ArrayList<PeopleInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mListView = (AsymmetricGridView) findViewById(R.id.listView);
        mListView.setRequestedColumnCount(2);

        jParser = new JSONParser();

        Intent i = getIntent();
        ArrayList<Searches> searches = i.getParcelableArrayListExtra(INTENT_SEARCHES);

        String displayName = searches.get(0).getDisplayName();
        String gender = searches.get(0).getGenderSearch();
        int ageFrom = searches.get(0).getAgeFrom();
        int ageTo= searches.get(0).getAgeTo();
        String country = searches.get(0).getLocationCounty();
        String state = searches.get(0).getLocationState();
        String city = searches.get(0).getLocationCity();

        paramsAPI = new ArrayList<NameValuePair>();
        paramsAPI.add(new BasicNameValuePair("act", "search"));
        paramsAPI.add(new BasicNameValuePair("t", "general"));
        paramsAPI.add(new BasicNameValuePair("did", ((AsianFinderApplication)getApplication()).getDeviceId()));
        paramsAPI.add(new BasicNameValuePair(Constants.TAG_SEARCH_DISPLAYNAME, displayName));
        paramsAPI.add(new BasicNameValuePair(Constants.TAG_SEARCH_GENDER, gender));
        paramsAPI.add(new BasicNameValuePair(Constants.TAG_SEARCH_AGE_FROM, ageFrom+""));
        paramsAPI.add(new BasicNameValuePair(Constants.TAG_SEARCH_AGE_TO, ageTo+""));
        paramsAPI.add(new BasicNameValuePair(Constants.TAG_SEARCH_COUNTRY, country));
        paramsAPI.add(new BasicNameValuePair(Constants.TAG_SEARCH_STATE, state));
        paramsAPI.add(new BasicNameValuePair(Constants.TAG_SEARCH_CITY, city));
        new SearchResult().execute();
    }

    /************************* ASYNSTASK HERE *************************/
    private class SearchResult extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {


        private JSONArray jsonArrayAccounts;
        private ProgressDialog mProgressDialog;


        public SearchResult() {
            // TODO Auto-generated constructor stub
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(SearchResultActivity.this);
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
            JSONObject json = jParser.getJSONFromUrl(getResources().getString(R.string.api), paramsAPI);
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
                adapter = new PeopleListAdapter(SearchResultActivity.this, mListView, new ArrayList<PeopleInfo>());
                adapter.setItems(peopleInfoArrayList);

                mListView.setAdapter(adapter);
                mListView.setDebugging(false);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PeopleInfo peopleInfo = (PeopleInfo) parent.getItemAtPosition(position);
                        Intent intentDetails = new Intent(SearchResultActivity.this, PeopleProfileActivity.class);

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
                                new LoadMoreSearchResult().execute();
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
                Toast.makeText(SearchResultActivity.this, "No More data", Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
        }
    }

    private class LoadMoreSearchResult extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {
        //        private JSONArray android;
        private JSONArray jsonArrayAccounts;
        private ProgressDialog mProgressDialog;

        public LoadMoreSearchResult() {
            // TODO Auto-generated constructor stub
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(SearchResultActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected ArrayList<PeopleInfo> doInBackground(Void... args) {
            peopleInfoArrayList.clear();
            paramsAPI.add(new BasicNameValuePair("_p", currentOffset+""));
            JSONObject json = jParser.getJSONFromUrl(getResources().getString(R.string.api), paramsAPI);
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
                Toast.makeText(SearchResultActivity.this, "No More data", Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
        }
    }
}
