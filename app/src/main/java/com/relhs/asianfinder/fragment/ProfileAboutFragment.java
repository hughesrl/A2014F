package com.relhs.asianfinder.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.GalleryActivity;
import com.relhs.asianfinder.IAFPushService;
import com.relhs.asianfinder.PeopleProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.ProfilePreferenceAdapter;
import com.relhs.asianfinder.adapter.ProfileUserDetailsAdapter;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.data.UserDetailsInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PhotosInfoOperations;
import com.relhs.asianfinder.operation.UserDetailsInfoOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.view.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileAboutFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_PROFILE = "arg_profile";
    public static final String ARG_PHOTOS = "arg_photos";

    private ImageLoader imageLoader;
    private View myFragmentView;

    private String mParamProfile;
    private String mParamPhotos;

    private UserInfoOperations userOperations;
    private PhotosInfoOperations photosInfoOperations;
    private UserDetailsInfoOperations userDetailsInfoOperations;

    ArrayList<UserDetailsInfo> items = new ArrayList<UserDetailsInfo>();
    private ProfileUserDetailsAdapter adapter;
    private ListView mListView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileAboutFragment newInstance(int sectionNumber, double longitude, double latitude) {
        ProfileAboutFragment fragment = new ProfileAboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileAboutFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamProfile = getArguments().getString(ARG_PROFILE);
            mParamPhotos = getArguments().getString(ARG_PHOTOS);
        }

        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(getActivity());
        userOperations.open();

        userDetailsInfoOperations = new UserDetailsInfoOperations(getActivity());
        userDetailsInfoOperations.open();

        photosInfoOperations = new PhotosInfoOperations(getActivity());
        photosInfoOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        imageLoader = new ImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_profile_about, container, false);

        mListView = (ListView) myFragmentView.findViewById(R.id.listViewPrefs);


        new LoadUserDetailsDataTask(inflater).execute();
        PeoplePhotosInfo peoplePhotosInfo = photosInfoOperations.getLastPhoto();
        ImageView myPhotosThumb = (ImageView) myFragmentView.findViewById(R.id.myPhotosThumb);
        imageLoader.DisplayImage(peoplePhotosInfo.getFile(), myPhotosThumb);

        LinearLayout galleryLayoutPhotos = (LinearLayout)myFragmentView.findViewById(R.id.galleryLayoutPhotos);
        galleryLayoutPhotos.setOnClickListener(this);

        UserInfo userInfo = userOperations.getUser();
        ImageView header_imageview = (ImageView) myFragmentView.findViewById(R.id.header_imageview);
        ImageView photoMain = (ImageView) myFragmentView.findViewById(R.id.photoMain);

        TextView txtUsername = (TextView) myFragmentView.findViewById(R.id.txtUsername);

        ImageButton startChatting = (ImageButton) myFragmentView.findViewById(R.id.startChatting);
//        try {

            // PHOTOS
            imageLoader.DisplayImage(userInfo.getMain_photo(), header_imageview);
            imageLoader.DisplayImageRounded(userInfo.getMain_photo(), photoMain, 150, 150);

            txtUsername.setText(userInfo.getUsername());

//            // BASIC
//            JSONArray jsonArrayBasic = new JSONArray(userInfo.getBasic());
//            LinearLayout list_recycled_parts_basic = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_basic);
//            showDetails(inflater, jsonArrayBasic, list_recycled_parts_basic);
//
//            // APPEARANCE
//            JSONArray jsonArrayAppearance = new JSONArray(userInfo.getAppearance());
//            LinearLayout list_recycled_parts_appearance = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_appearance);
//            showDetails(inflater, jsonArrayAppearance, list_recycled_parts_appearance);
//
//            // LIFESTYLE
//            JSONArray jsonArrayLifestyle = new JSONArray(userInfo.getLifestyle());
//            LinearLayout list_recycled_parts_lifestyle = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_lifestle);
//            showDetails(inflater, jsonArrayLifestyle, list_recycled_parts_lifestyle);
//
//            // CULTURE VALUES
//            JSONArray jsonArrayCultureValues = new JSONArray(userInfo.getCulture_values());
//            LinearLayout list_recycled_parts_culture_values = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_culture_values);
//            showDetails(inflater, jsonArrayCultureValues, list_recycled_parts_culture_values);
//
//            // PERSONAL
////            if(!userInfo.getPersonal().isEmpty()) {
//                JSONArray jsonArrayPersonal = new JSONArray(userInfo.getPersonal());
//                LinearLayout list_recycled_parts_personal = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_personal);
//                showDetails(inflater, jsonArrayPersonal, list_recycled_parts_personal);
////            }
//
//            // INTEREST
//            JSONArray jsonArrayInterest = new JSONArray(userInfo.getInterest());
//            LinearLayout list_recycled_parts_interest = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_interest);
//            showDetails(inflater, jsonArrayInterest, list_recycled_parts_interest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        ImageButton editUsername = (ImageButton) myFragmentView.findViewById(R.id.editUsername);
//        editUsername.setOnClickListener(this);

//        ImageButton editAppearance = (ImageButton) myFragmentView.findViewById(R.id.editAppearance);
//        editAppearance.setOnClickListener(this);
//        ImageButton editLifestyle = (ImageButton) myFragmentView.findViewById(R.id.editLifestyle);
//        editLifestyle.setOnClickListener(this);
//        ImageButton editCultureValues = (ImageButton) myFragmentView.findViewById(R.id.editCultureValues);
//        editCultureValues.setOnClickListener(this);
//        ImageButton editPersonal = (ImageButton) myFragmentView.findViewById(R.id.editPersonal);
//        editPersonal.setOnClickListener(this);
//        ImageButton editInterest = (ImageButton) myFragmentView.findViewById(R.id.editInterest);
//        editInterest.setOnClickListener(this);


        return myFragmentView;
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.editAppearance:
//                Toast.makeText(getActivity(), "Edit Appearance", Toast.LENGTH_LONG).show();
//                break;
            case R.id.galleryLayoutPhotos:
                Intent intentActivity = new Intent(getActivity(), GalleryActivity.class);
                startActivity(intentActivity);
                break;

        }
    }


    private class LoadUserDetailsDataTask extends AsyncTask<Void, Void, ArrayList<UserDetailsInfo>> {
        private ProgressDialog mProgressDialog;
        private LayoutInflater _inflater;

        public LoadUserDetailsDataTask(LayoutInflater inflater) {
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
        protected ArrayList<UserDetailsInfo> doInBackground(Void... args) {
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_BASIC) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_BASIC));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_BASIC));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_APPEARANCE) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_APPEARANCE));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_APPEARANCE));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_LIFESTYLE) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_LIFESTYLE));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_LIFESTYLE));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_CULTURE_VALUES) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_CULTURE_VALUES));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_CULTURE_VALUES));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_PERSONAL) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_PERSONAL));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_PERSONAL));
            }
            return items;
        }
        @Override
        protected void onPostExecute(final ArrayList<UserDetailsInfo> userDetailsInfoArrayList) {
            if(!userDetailsInfoArrayList.isEmpty()) {
                adapter = new ProfileUserDetailsAdapter(getActivity(), userDetailsInfoArrayList);

                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserDetailsInfo item = (UserDetailsInfo)items.get(position);

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

//                setListViewHeightBasedOnChildren(mListView);
                mListView.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
            } else {
                //Toast.makeText(getActivity(), "No More data", Toast.LENGTH_LONG).show();
//                noPhoto.setVisibility(View.VISIBLE);
            }

            mProgressDialog.dismiss();
        }
    }

    private void parsePreferenceInfoAsArray(Cursor cursor) {
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            items.add(new UserDetailsInfo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_DBNAME)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_LABEL)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_VALUE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_IDS))));
        }
    }

//    /**** Method for Setting the Height of the ListView dynamically.
//     **** Hack to fix the issue of not showing all the items of the ListView
//     **** when placed inside a ScrollView  ****/
//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null)
//            return;
//
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
//        int totalHeight = 0;
//        View view = null;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            view = listAdapter.getView(i, view, listView);
//            if (i == 0)
//                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            view.measure(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
//            totalHeight += view.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//    }
}
