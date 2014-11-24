package com.relhs.asianfinder.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.IAFPushService;
import com.relhs.asianfinder.PeopleProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.view.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PeopleMatchFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_PROFILE = "arg_profile";
    public static final String ARG_PHOTOS = "arg_photos";

    private ImageLoader imageLoader;
    private View myFragmentView;

    private String mParamProfile;
    private String mParamMatches;
    private String mParamPhotos;

    private IAFPushService mIAFPushService;
    private boolean mBound;
    private String[] titles = {"Criteria", "Match Their Criteria","Match  My Criteria"};

    private TableLayout tl;

    private int width;
    private int columnWidthCriteria;
    private int columnWidth;
    private UserInfoOperations userInfoOperations;
    private UserInfo userInfo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeopleMatchFragment newInstance(int sectionNumber, double longitude, double latitude) {
        PeopleMatchFragment fragment = new PeopleMatchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PeopleMatchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamProfile = getArguments().getString(Constants.ARG_PROFILE);
            mParamMatches = getArguments().getString(Constants.ARG_MATCHES);
            mParamPhotos = getArguments().getString(Constants.ARG_PHOTOS);
        }

        imageLoader = new ImageLoader(getActivity());
        userInfoOperations = new UserInfoOperations(getActivity());
        userInfoOperations.open();
        userInfo = userInfoOperations.getUser();



        width = (getActivity().getResources().getDisplayMetrics().widthPixels - 20);
        columnWidthCriteria = width-(width/3);
        columnWidth = (width-columnWidthCriteria)/2;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_people_profile_match, container, false);

        // Get the TableLayout
        tl = (TableLayout) myFragmentView.findViewById(R.id.maintable);

        /*HEADERS*/
        View vi = inflater.inflate(R.layout.list_item_profile_details_matches, null);
        CustomTextView txtDetailName = (CustomTextView) vi.findViewById(R.id.txtDetailName);
        LinearLayout layoutYours = (LinearLayout) vi.findViewById(R.id.layoutYours);
        LinearLayout layoutMine = (LinearLayout) vi.findViewById(R.id.layoutMine);
        ImageView yours = (ImageView) vi.findViewById(R.id.yours);
        ImageView mine = (ImageView) vi.findViewById(R.id.mine);

        try {
            JSONArray jsonArrayPhotos = new JSONArray(mParamPhotos);
            JSONObject jsonObjectPhotos = jsonArrayPhotos.getJSONObject(0);

            imageLoader.DisplayImage(jsonObjectPhotos.getString("file"), yours);
            imageLoader.DisplayImage(userInfo.getMain_photo(), mine);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtDetailName.setText("Criteria");
        txtDetailName.setWidth(columnWidthCriteria);

        layoutYours.setLayoutParams(new LinearLayout.LayoutParams(columnWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutMine.setLayoutParams(new LinearLayout.LayoutParams(columnWidth, ViewGroup.LayoutParams.MATCH_PARENT));

        // Add the TableRow to the TableLayout
        tl.addView(vi, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        /*HEADERS*/

        try {
            // PROFILE
            JSONObject jsonObjectMatches = new JSONObject(mParamMatches);

            // APPEARANCE
            JSONArray jsonArrayAppearance = jsonObjectMatches.getJSONArray("appearance");
            showDetails(inflater, jsonArrayAppearance, "APPEARANCE");

            // LIFESTYLE
            JSONArray jsonArrayLifestyle = jsonObjectMatches.getJSONArray("lifestyle");
            showDetails(inflater, jsonArrayLifestyle, "LIFESTYLE");

            // CULTURE VALUES
            JSONArray jsonArrayCultureValues = jsonObjectMatches.getJSONArray("culture_values");
            showDetails(inflater, jsonArrayCultureValues, "CULTURE VALUES");

            // PERSONAL
            JSONArray jsonArrayPersonal = jsonObjectMatches.getJSONArray("personal");
            showDetails(inflater, jsonArrayPersonal, "PERSONAL");

            // INTEREST
            JSONArray jsonArrayInterest = jsonObjectMatches.getJSONArray("interest");
            showDetails(inflater, jsonArrayInterest, "INTEREST");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myFragmentView;
    }

    private void showDetails(LayoutInflater inflater, JSONArray jsonArray, String criteria) {
        View criteriaView = inflater.inflate(R.layout.list_item_profile_details_matches_criteria, null);
        CustomTextView txtCriteria = (CustomTextView) criteriaView.findViewById(R.id.txtCriteria);
        txtCriteria.setText(criteria.toUpperCase());
        tl.addView(criteriaView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        try {
            for (int i=0; i<jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                View vi = inflater.inflate(R.layout.list_item_profile_details_matches, null);
                CustomTextView txtDetailName = (CustomTextView) vi.findViewById(R.id.txtDetailName);
                LinearLayout layoutYours = (LinearLayout) vi.findViewById(R.id.layoutYours);
                LinearLayout layoutMine = (LinearLayout) vi.findViewById(R.id.layoutMine);

                ImageView yours = (ImageView) vi.findViewById(R.id.yours);
                ImageView mine = (ImageView) vi.findViewById(R.id.mine);

                getMatchImage(jsonObject.getInt("yours"), yours);
                getMatchImage(jsonObject.getInt("theirs"), mine);

                txtDetailName.setText(jsonObject.getString("label"));
                txtDetailName.setWidth(columnWidthCriteria);

                layoutYours.setLayoutParams(new LinearLayout.LayoutParams(columnWidth, ViewGroup.LayoutParams.MATCH_PARENT));
                layoutMine.setLayoutParams(new LinearLayout.LayoutParams(columnWidth, ViewGroup.LayoutParams.MATCH_PARENT));


                // Add the TableRow to the TableLayout
                tl.addView(vi, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMatchImage(int tag, ImageView imageView) {
        switch (tag) {
            case 0:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.star_off));
                break;
            case 1:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.star_on));
                break;
            case 2:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.star_half));
                break;
            default:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.star_off));
                break;
        }

    }
}
