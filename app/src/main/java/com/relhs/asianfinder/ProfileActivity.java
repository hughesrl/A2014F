package com.relhs.asianfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.relhs.asianfinder.adapter.ProfileUserDetailsAdapter;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.data.SpinnerItems;
import com.relhs.asianfinder.data.UserDetailsInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.fragment.LocationSelectionDialogFragment;
import com.relhs.asianfinder.fragment.ProfileUserDetailsEditDialogFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PhotosInfoOperations;
import com.relhs.asianfinder.operation.UserDetailsInfoOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends FragmentActivity implements View.OnClickListener,
        ProfileUserDetailsEditDialogFragment.ProfileUserDetailsEditDialogListener,
        LocationSelectionDialogFragment.LocationSelectionDialogListener{
    private UserInfo userInfo;

    private PeoplePhotosInfo peoplePhotosInfo;

    @Override
    public void onLocationSelectionDialogPositiveClick(DialogFragment dialog, SpinnerItems country, SpinnerItems state, SpinnerItems city) {

    }

    @Override
    public void onLocationSelectionDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onProfileUserDetailsEditDialogPositiveClick(DialogFragment dialog, String k, String value) {
        dialog.dismiss();
        new SaveUserDetailsDataTask(k, value).execute();
    }

    @Override
    public void onProfileUserDetailsEditDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_PROFILE = "arg_profile";
    public static final String ARG_PHOTOS = "arg_photos";

    private ImageLoader imageLoader;
    private UserInfoOperations userOperations;
    private PhotosInfoOperations photosInfoOperations;
    private UserDetailsInfoOperations userDetailsInfoOperations;

    ArrayList<UserDetailsInfo> items = new ArrayList<UserDetailsInfo>();
    private ProfileUserDetailsAdapter adapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_about);

        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();

        userDetailsInfoOperations = new UserDetailsInfoOperations(this);
        userDetailsInfoOperations.open();

        photosInfoOperations = new PhotosInfoOperations(this);
        photosInfoOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        imageLoader = new ImageLoader(this);

        mListView = (ListView) findViewById(R.id.listViewPrefs);

        peoplePhotosInfo = photosInfoOperations.getLastPhoto();
        userInfo = userOperations.getUser();

        new LoadUserDetailsDataTask(0).execute();


        TextView upTextView = (TextView) getLayoutInflater().inflate(R.layout.chat_name, null);
        getActionBar().setIcon(AsianFinderApplication.getTextAsBitmap(ProfileActivity.this, upTextView, userInfo.getUsername()));

    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.editAppearance:
//                Toast.makeText(getActivity(), "Edit Appearance", Toast.LENGTH_LONG).show();
//                break;
            case R.id.galleryLayoutPhotos:
                Intent intentActivity = new Intent(this, GalleryActivity.class);
                startActivity(intentActivity);
                break;

        }
    }


    private class LoadUserDetailsDataTask extends AsyncTask<Void, Void, ArrayList<UserDetailsInfo>> {
        private ProgressDialog mProgressDialog;
        private int update;

        public LoadUserDetailsDataTask(int update) {
            // TODO Auto-generated constructor stub
            this.update = update;
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(ProfileActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected ArrayList<UserDetailsInfo> doInBackground(Void... args) {
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_BASIC) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_BASIC));
                parseUserDetailsAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_BASIC));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_APPEARANCE) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_APPEARANCE));
                parseUserDetailsAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_APPEARANCE));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_LIFESTYLE) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_LIFESTYLE));
                parseUserDetailsAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_LIFESTYLE));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_CULTURE_VALUES) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_CULTURE_VALUES));
                parseUserDetailsAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_CULTURE_VALUES));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_PERSONAL) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_PERSONAL));
                parseUserDetailsAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_PERSONAL));
            }
            return items;
        }
        @Override
        protected void onPostExecute(final ArrayList<UserDetailsInfo> userDetailsInfoArrayList) {
            if(!userDetailsInfoArrayList.isEmpty()) {
                adapter = new ProfileUserDetailsAdapter(ProfileActivity.this, userDetailsInfoArrayList);

                if(update == 0) {
                    View header = View.inflate(ProfileActivity.this, R.layout.view_profile_header, null);
                    mListView.addHeaderView(header);

                    ImageView myPhotosThumb = (ImageView) header.findViewById(R.id.myPhotosThumb);
                    imageLoader.DisplayImage(peoplePhotosInfo.getFile(), myPhotosThumb);

                    LinearLayout galleryLayoutPhotos = (LinearLayout) header.findViewById(R.id.galleryLayoutPhotos);
                    galleryLayoutPhotos.setOnClickListener(ProfileActivity.this);


                    ImageView header_imageview = (ImageView) header.findViewById(R.id.header_imageview);
                    ImageView photoMain = (ImageView) header.findViewById(R.id.photoMain);

                    TextView txtUsername = (TextView) header.findViewById(R.id.txtUsername);

                    ImageButton startChatting = (ImageButton) header.findViewById(R.id.startChatting);
                    imageLoader.DisplayImage(userInfo.getMain_photo(), header_imageview);
                    imageLoader.DisplayImageRounded(userInfo.getMain_photo(), photoMain, 150, 150);

                    txtUsername.setText(userInfo.getUsername());
                }
                mListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserDetailsInfo item = (UserDetailsInfo)items.get(position-1);
                        if(!item.getDbname().equalsIgnoreCase("gender")) {
                            if(item.getLabel().equalsIgnoreCase("Location")) {
                                try {
                                    JSONArray jsonArrayLocations = new JSONArray(item.getIds());
                                    JSONObject jsonObjectLocations = jsonArrayLocations.getJSONObject(0);

                                    Bundle arg = new Bundle();
                                    arg.putString(LocationSelectionDialogFragment.ARG_COUNTRY, jsonObjectLocations.getString("country"));
                                    arg.putString(LocationSelectionDialogFragment.ARG_STATE, jsonObjectLocations.getString("state"));
                                    arg.putString(LocationSelectionDialogFragment.ARG_CITY, jsonObjectLocations.getString("city"));

                                    FragmentManager fm = getSupportFragmentManager();
                                    LocationSelectionDialogFragment editLocationDialog = new LocationSelectionDialogFragment();
                                    editLocationDialog.setArguments(arg);
                                    editLocationDialog.show(fm, "fragment_edit_Location");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Bundle arg = new Bundle();
                                arg.putString(ProfileUserDetailsEditDialogFragment.ARG_DBNAME, item.getDbname());
                                arg.putString(ProfileUserDetailsEditDialogFragment.ARG_LABEL, item.getLabel());
                                arg.putString(ProfileUserDetailsEditDialogFragment.ARG_TYPE, item.getType());
                                arg.putString(ProfileUserDetailsEditDialogFragment.ARG_VALUE, item.getValue());
                                arg.putString(ProfileUserDetailsEditDialogFragment.ARG_IDS, item.getIds());

                                FragmentManager fm = getSupportFragmentManager();
                                ProfileUserDetailsEditDialogFragment editNameDialog = new ProfileUserDetailsEditDialogFragment();
                                editNameDialog.setArguments(arg);
                                editNameDialog.show(fm, "fragment_edit_name");
                            }
                        } else if(item.getDbname().equalsIgnoreCase("email")) {
                            // Redirect to Setting to change Email
                        }
                    }
                });
            } else {
                //Toast.makeText(getActivity(), "No More data", Toast.LENGTH_LONG).show();
//                noPhoto.setVisibility(View.VISIBLE);
            }

            mProgressDialog.dismiss();
        }
    }

    private void parseUserDetailsAsArray(Cursor cursor) {
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            items.add(new UserDetailsInfo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_DBNAME)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_LABEL)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_VALUE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_IDS))));
        }
    }

    public String getDeviceId() {
        return ((AsianFinderApplication) getApplication()).getDeviceId();
    }


    private class SaveUserDetailsDataTask extends AsyncTask<Void, Void, JSONObject> {
        private ProgressDialog mProgressDialog;
        private LayoutInflater _inflater;

        private String _k;
        private String _value;

        public SaveUserDetailsDataTask(String k, String value) {
            // TODO Auto-generated constructor stub
            this._k = k;
            this._value = value;
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(ProfileActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected JSONObject doInBackground(Void... args) {
            List<NameValuePair> paramsAPI = new ArrayList<NameValuePair>();
            paramsAPI.add(new BasicNameValuePair("act", "edit-profile"));
            paramsAPI.add(new BasicNameValuePair("did", ((AsianFinderApplication)getApplication()).getDeviceId()));
            paramsAPI.add(new BasicNameValuePair("t", "save"));
            paramsAPI.add(new BasicNameValuePair(_k, _value));

            JSONParser jParser = new JSONParser();
            return jParser.getJSONFromUrl(getString(R.string.api), paramsAPI);
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(AsianFinderApplication.TAG_OS);
                JSONObject c = jsonArray.getJSONObject(0);

                if(!c.getBoolean(AsianFinderApplication.TAG_STATUS)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), c.getString(AsianFinderApplication.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                } else {
                    //JSONArray jsonArrayData = c.getJSONArray(AsianFinderApplication.TAG_DATA);
                    JSONObject jsonObjectData = c.getJSONObject(AsianFinderApplication.TAG_DATA);
                    //JSONObject jsonObjectData = jsonArrayData.getJSONObject(0);
                    String value = jsonObjectData.getString("value");
                    if (jsonObjectData.getString("label").equalsIgnoreCase("location")) {
                        if(!value.isEmpty()) {
                            JSONArray jsonArrayLocation = new JSONArray(value);
                            JSONObject jsonObjectLocation = jsonArrayLocation.getJSONObject(0);
                            value = jsonObjectLocation.getString("city") + ", " +
                                    jsonObjectLocation.getString("state") + ", " + jsonObjectLocation.getString("country");
                        }
                    }
                    userDetailsInfoOperations.updateUserDetails(jsonObjectData.getString("dbname"),
                            jsonObjectData.getString("label"), jsonObjectData.getString("type"), value, jsonObjectData.getString("ids"));
                    //userDetailsInfoOperations.updateUserDetails("firstname", "First Name", "text", _value, _value); // testing
                    //adapter.notifyDataSetChanged();

                    adapter.clear();
                    new LoadUserDetailsDataTask(1).execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
        }
    }
}

