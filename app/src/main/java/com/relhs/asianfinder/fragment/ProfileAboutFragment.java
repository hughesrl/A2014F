package com.relhs.asianfinder.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        // TODO: !IMPORTANT DATABASE OPERATION

        imageLoader = new ImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_profile_about, container, false);

        UserInfo userInfo = userOperations.getUser();
        ImageView header_imageview = (ImageView) myFragmentView.findViewById(R.id.header_imageview);
        ImageView photoMain = (ImageView) myFragmentView.findViewById(R.id.photoMain);

        TextView txtUsername = (TextView) myFragmentView.findViewById(R.id.txtUsername);


        ImageButton startChatting = (ImageButton) myFragmentView.findViewById(R.id.startChatting);
        try {

            // PHOTOS
            imageLoader.DisplayImage(userInfo.getMain_photo(), header_imageview);
            imageLoader.DisplayImageRounded(userInfo.getMain_photo(), photoMain, 150, 150);

            txtUsername.setText(userInfo.getUsername());

            // BASIC
            JSONArray jsonArrayBasic = new JSONArray(userInfo.getBasic());
            LinearLayout list_recycled_parts_basic = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_basic);
            showDetails(inflater, jsonArrayBasic, list_recycled_parts_basic);

            // APPEARANCE
            JSONArray jsonArrayAppearance = new JSONArray(userInfo.getAppearance());
            LinearLayout list_recycled_parts_appearance = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_appearance);
            showDetails(inflater, jsonArrayAppearance, list_recycled_parts_appearance);

            // LIFESTYLE
            JSONArray jsonArrayLifestyle = new JSONArray(userInfo.getLifestyle());
            LinearLayout list_recycled_parts_lifestyle = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_lifestle);
            showDetails(inflater, jsonArrayLifestyle, list_recycled_parts_lifestyle);

            // CULTURE VALUES
            JSONArray jsonArrayCultureValues = new JSONArray(userInfo.getCulture_values());
            LinearLayout list_recycled_parts_culture_values = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_culture_values);
            showDetails(inflater, jsonArrayCultureValues, list_recycled_parts_culture_values);

            // PERSONAL
//            if(!userInfo.getPersonal().isEmpty()) {
                JSONArray jsonArrayPersonal = new JSONArray(userInfo.getPersonal());
                LinearLayout list_recycled_parts_personal = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_personal);
                showDetails(inflater, jsonArrayPersonal, list_recycled_parts_personal);
//            }

            // INTEREST
            JSONArray jsonArrayInterest = new JSONArray(userInfo.getInterest());
            LinearLayout list_recycled_parts_interest = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_interest);
            showDetails(inflater, jsonArrayInterest, list_recycled_parts_interest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageButton editUsername = (ImageButton) myFragmentView.findViewById(R.id.editUsername);
        editUsername.setOnClickListener(this);

        ImageButton editAppearance = (ImageButton) myFragmentView.findViewById(R.id.editAppearance);
        editAppearance.setOnClickListener(this);
        ImageButton editLifestyle = (ImageButton) myFragmentView.findViewById(R.id.editLifestyle);
        editLifestyle.setOnClickListener(this);
        ImageButton editCultureValues = (ImageButton) myFragmentView.findViewById(R.id.editCultureValues);
        editCultureValues.setOnClickListener(this);
        ImageButton editPersonal = (ImageButton) myFragmentView.findViewById(R.id.editPersonal);
        editPersonal.setOnClickListener(this);
        ImageButton editInterest = (ImageButton) myFragmentView.findViewById(R.id.editInterest);
        editInterest.setOnClickListener(this);


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
            case R.id.editAppearance:
                Toast.makeText(getActivity(), "Edit Appearance", Toast.LENGTH_LONG).show();
                break;
            case R.id.editUsername:
                Toast.makeText(getActivity(), "Edit Username", Toast.LENGTH_LONG).show();
                break;

        }
    }
}
