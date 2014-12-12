package com.relhs.asianfinder.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.GallerySlideshowActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.PeoplePhotosGridAdapter;
import com.relhs.asianfinder.adapter.ProfilePreferenceAdapter;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.data.PreferenceInfo;
import com.relhs.asianfinder.data.ProfilePreferenceDataInfo;
import com.relhs.asianfinder.data.ProfilePreferenceHeaderInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PreferenceInfoOperations;
import com.relhs.asianfinder.view.CustomTextView;
import com.relhs.asianfinder.view.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfilePreferenceFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ImageLoader imageLoader;
    private View myFragmentView;

    private String mParamProfile;
    private String mParamPhotos;
    private String mParamPreference;

    private ArrayList<PeoplePhotosInfo> peoplePhotosArrayList = new ArrayList<PeoplePhotosInfo>();

    ArrayList<PreferenceInfo> items = new ArrayList<PreferenceInfo>();
    private ListView mListView;
    private ProfilePreferenceAdapter adapter;
    private int currentOffset;
    private LinearLayout noPhoto;

    private PreferenceInfoOperations preferenceInfoOperations;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilePreferenceFragment newInstance(int sectionNumber, double longitude, double latitude) {
        ProfilePreferenceFragment fragment = new ProfilePreferenceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfilePreferenceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamProfile = getArguments().getString(Constants.ARG_PROFILE);
            mParamPhotos = getArguments().getString(Constants.ARG_PHOTOS);
            mParamPreference = getArguments().getString(Constants.ARG_PREFERENCE);
        }
        imageLoader = new ImageLoader(getActivity());

        preferenceInfoOperations = new PreferenceInfoOperations(getActivity());
        preferenceInfoOperations.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_profile_pref, container, false);
        mListView = (ListView) myFragmentView.findViewById(R.id.listViewPrefs);

        noPhoto = (LinearLayout) myFragmentView.findViewById(R.id.noPhoto);
        Log.d("-- robert", mParamPreference);

        new LoadPhotosDataTask(inflater).execute();

        return myFragmentView;
    }

    private class LoadPhotosDataTask extends AsyncTask<Void, Void, ArrayList<PreferenceInfo>> {
        private ProgressDialog mProgressDialog;
        private LayoutInflater _inflater;

        public LoadPhotosDataTask(LayoutInflater inflater) {
            // TODO Auto-generated constructor stub
            this._inflater = inflater;
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
        protected ArrayList<PreferenceInfo> doInBackground(Void... args) {
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_BASIC) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_BASIC));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_BASIC));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_APPEARANCE) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_APPEARANCE));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_APPEARANCE));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_LIFESTYLE) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_LIFESTYLE));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_LIFESTYLE));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_CULTURE_VALUES) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_CULTURE_VALUES));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_CULTURE_VALUES));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_PERSONAL) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_PERSONAL));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_PERSONAL));
            }
            return items;
        }
        @Override
        protected void onPostExecute(final ArrayList<PreferenceInfo> prefItems) {
            if(!prefItems.isEmpty()) {
                adapter = new ProfilePreferenceAdapter(getActivity(), prefItems);

                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PreferenceInfo item = (PreferenceInfo)items.get(position);

                        Bundle arg = new Bundle();
                        arg.putString(ProfilePreferenceEditDialogFragment.ARG_DBNAME, item.getDbname());
                        arg.putString(ProfilePreferenceEditDialogFragment.ARG_LABEL, item.getLabel());
                        arg.putString(ProfilePreferenceEditDialogFragment.ARG_TYPE, item.getType());

                        FragmentManager fm = getFragmentManager();
                        ProfilePreferenceEditDialogFragment editNameDialog = new ProfilePreferenceEditDialogFragment();
                        editNameDialog.setArguments(arg);
                        editNameDialog.show(fm, "fragment_edit_name");
                    }
                });
            } else {
                //Toast.makeText(getActivity(), "No More data", Toast.LENGTH_LONG).show();
//                noPhoto.setVisibility(View.VISIBLE);
            }

            mProgressDialog.dismiss();
        }
    }


//    private void addToList(JSONArray jsonArray) {
//        try {
//            for (int i=0; i<jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                String value = jsonObject.getString("value");
//                if (jsonObject.getString("dbname").equalsIgnoreCase("living_in")) {
//                    if(!value.isEmpty()) {
//                        JSONArray jsonArrayPrefBasicLivingIn = new JSONArray(value);
//                        JSONObject jsonObjectPrefBasicLivingIn = jsonArrayPrefBasicLivingIn.getJSONObject(0);
//                        value = jsonObjectPrefBasicLivingIn.getString("city") + ", " +
//                                jsonObjectPrefBasicLivingIn.getString("state") + ", " + jsonObjectPrefBasicLivingIn.getString("country");
//                        if (jsonObjectPrefBasicLivingIn.has("length")) {
//                            value = jsonObjectPrefBasicLivingIn.getString("length") + " within " + value;
//                        }
//                    }
//                }
//
//                items.add(new ProfilePreferenceDataInfo(
//                        jsonObject.getString("dbname"), jsonObject.getString("label"), jsonObject.getString("type"),
//                        value, jsonObject.getString("ids")));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void parsePreferenceInfoAsArray(Cursor cursor) {
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            items.add(new PreferenceInfo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_DBNAME)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_LABEL)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_VALUE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_IDS))));
        }
    }
}
