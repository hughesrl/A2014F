package com.relhs.asianfinder.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.DashboardActivity;
import com.relhs.asianfinder.PeopleProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.view.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    public static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ImageLoader imageLoader;
    private View myFragmentView;

    private String mParamProfile;
    private String mParamPhotos;
    private UserInfoOperations userOperations;
    private LayoutInflater mInflater;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParamProfile = getArguments().getString(Constants.ARG_PROFILE);
//            mParamMatches = getArguments().getString(Constants.ARG_MATCHES);
//            mParamPhotos = getArguments().getString(Constants.ARG_PHOTOS);
        }

        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(getActivity());
        userOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        imageLoader = new ImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        myFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        return myFragmentView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        new LoadProfile(mInflater).execute();
    }

//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.dashboard, menu);
//    }
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        ((DashboardActivity) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));
//
//    }
    private void showDetails(LayoutInflater inflater, JSONArray jsonArray, LinearLayout layout) {
        try {
            for (int i=0; i<jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                View vi = inflater.inflate(R.layout.list_item_profile_details, null);
                CustomTextView txtDetailName = (CustomTextView) vi.findViewById(R.id.txtDetailName);
                CustomTextView txtDetailValue = (CustomTextView) vi.findViewById(R.id.txtDetailValue);

                txtDetailName.setText(jsonObject.getString("label"));
                if(!jsonObject.getString("value").isEmpty()) {
                    txtDetailValue.setText(jsonObject.getString("value"));
                } else {
                    txtDetailValue.setText("-");
                }

                layout.addView(vi);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadProfile extends AsyncTask<Void, Void, String> {

        private ProgressDialog mProgressDialog;
        private LayoutInflater _inflater;

        public LoadProfile(LayoutInflater inflater) {
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
        protected String doInBackground(Void... args) {
            UserInfo userInfo = userOperations.getUser();
            ImageView header_imageView = (ImageView) myFragmentView.findViewById(R.id.header_imageview);
            ImageView photoMain = (ImageView) myFragmentView.findViewById(R.id.photoMain);

            TextView txtUsername = (TextView) myFragmentView.findViewById(R.id.txtUsername);
            TextView txtLocation = (TextView) myFragmentView.findViewById(R.id.txtLocation);

            try {
                // PHOTOS
                imageLoader.DisplayImage(userInfo.getMain_photo(), header_imageView);
                imageLoader.DisplayImageRounded(userInfo.getMain_photo(), photoMain, 150, 150);

                // BASIC
                JSONArray jsonArrayBasic = new JSONArray(userInfo.getBasic());
                JSONObject jsonObjectBasic = jsonArrayBasic.getJSONObject(0);

                txtUsername.setText(jsonObjectBasic.get("username").toString()+ " ("+jsonObjectBasic.get("aged").toString().trim()+")");
                if(jsonObjectBasic.has("city") && jsonObjectBasic.has("state") && jsonObjectBasic.has("country")) {
                    txtLocation.setText(jsonObjectBasic.get("city").toString()+", "+jsonObjectBasic.get("state").toString()+", "+jsonObjectBasic.get("country").toString());
                }
                final int userId = Integer.parseInt(jsonObjectBasic.getString("id"));

                // APPEARANCE
                JSONArray jsonArrayAppearance = new JSONArray(userInfo.getAppearance());
                LinearLayout list_recycled_parts_appearance = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_appearance);
                showDetails(_inflater, jsonArrayAppearance, list_recycled_parts_appearance);

                // LIFESTYLE
                JSONArray jsonArrayLifestyle = new JSONArray(userInfo.getLifestyle());
                LinearLayout list_recycled_parts_lifestyle = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_lifestle);
                showDetails(_inflater, jsonArrayLifestyle, list_recycled_parts_lifestyle);

                // CULTURE VALUES
                JSONArray jsonArrayCultureValues = new JSONArray(userInfo.getCulture_values());
                LinearLayout list_recycled_parts_culture_values = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_culture_values);
                showDetails(_inflater, jsonArrayCultureValues, list_recycled_parts_culture_values);

                // PERSONAL
                JSONArray jsonArrayPersonal = new JSONArray(userInfo.getPersonal());
                LinearLayout list_recycled_parts_personal = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_personal);
                showDetails(_inflater, jsonArrayPersonal, list_recycled_parts_personal);

                // INTEREST
                JSONArray jsonArrayInterest = new JSONArray(userInfo.getInterest());
                LinearLayout list_recycled_parts_interest = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_interest);
                showDetails(_inflater, jsonArrayInterest, list_recycled_parts_interest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String ret) {
            mProgressDialog.dismiss();
        }
    }


}
