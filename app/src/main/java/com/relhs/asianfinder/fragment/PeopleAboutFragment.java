package com.relhs.asianfinder.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.IAFPushService;
import com.relhs.asianfinder.PeopleProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.PeopleListAdapter;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;
import com.relhs.asianfinder.view.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PeopleAboutFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";


    private ImageLoader imageLoader;
    private View myFragmentView;

    private String mParamProfile;
    private String mParamMatches;
    private String mParamPhotos;

    private IAFPushService mIAFPushService;
    private boolean mBound;

    private int userId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeopleAboutFragment newInstance(int sectionNumber, double longitude, double latitude) {
        PeopleAboutFragment fragment = new PeopleAboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PeopleAboutFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_people_profile_about, container, false);

        ImageView header_imageview = (ImageView) myFragmentView.findViewById(R.id.header_imageview);
        ImageView photoMain = (ImageView) myFragmentView.findViewById(R.id.photoMain);

        CustomTextView txtHeading = (CustomTextView) myFragmentView.findViewById(R.id.txtHeading);

        CustomTextView txtUsername = (CustomTextView) myFragmentView.findViewById(R.id.txtUsername);
        CustomTextView txtLocation = (CustomTextView) myFragmentView.findViewById(R.id.txtLocation);

        CustomTextView txtDescription = (CustomTextView) myFragmentView.findViewById(R.id.txtDescription);

        ImageButton startChatting = (ImageButton) myFragmentView.findViewById(R.id.startChatting);
        startChatting.setOnClickListener(this);
        ImageButton btnSendGift = (ImageButton) myFragmentView.findViewById(R.id.btnSendGift);
        btnSendGift.setOnClickListener(this);
        ImageButton btnSendLoad = (ImageButton) myFragmentView.findViewById(R.id.btnSendLoad);
        btnSendLoad.setOnClickListener(this);
        try {
            // PROFILE
            JSONObject jsonObjectProfile = new JSONObject(mParamProfile);

            JSONArray jsonArrayBasic = jsonObjectProfile.getJSONArray("basic");
            JSONObject jsonObjectBasic = jsonArrayBasic.getJSONObject(0);

            txtUsername.setText(jsonObjectBasic.get("username").toString()+ " ("+jsonObjectBasic.get("aged").toString().trim()+")");
            if(jsonObjectBasic.has("city") && jsonObjectBasic.has("state") && jsonObjectBasic.has("country")) {
                txtLocation.setText(jsonObjectBasic.get("city").toString()+", "+jsonObjectBasic.get("state").toString()+", "+jsonObjectBasic.get("country").toString());
            }
            // HEADING
            if(jsonObjectBasic.has("heading")) {
                if(jsonObjectBasic.get("heading").toString().equalsIgnoreCase("")) {
                    txtHeading.setVisibility(View.INVISIBLE);
                } else {
                    txtHeading.setText(jsonObjectBasic.get("heading").toString());
                }
            }
            // DESCRIPTION
            if(jsonObjectBasic.has("description")) {
                if(jsonObjectBasic.get("description").toString().equalsIgnoreCase("")) {
                    txtDescription.setVisibility(View.INVISIBLE);
                } else {
                    txtDescription.setText(jsonObjectBasic.get("description").toString());
                }
            }

            userId = Integer.parseInt(jsonObjectBasic.getString("id"));

            // APPEARANCE
            JSONArray jsonArrayAppearance = jsonObjectProfile.getJSONArray("appearance");
            LinearLayout list_recycled_parts_appearance = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_appearance);
            showDetails(inflater, jsonArrayAppearance, list_recycled_parts_appearance);

            // LIFESTYLE
            JSONArray jsonArrayLifestyle = jsonObjectProfile.getJSONArray("lifestyle");
            LinearLayout list_recycled_parts_lifestyle = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_lifestle);
            showDetails(inflater, jsonArrayLifestyle, list_recycled_parts_lifestyle);

            // CULTURE VALUES
            JSONArray jsonArrayCultureValues = jsonObjectProfile.getJSONArray("culture_values");
            LinearLayout list_recycled_parts_culture_values = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_culture_values);
            showDetails(inflater, jsonArrayCultureValues, list_recycled_parts_culture_values);

            // PERSONAL
            JSONArray jsonArrayPersonal = jsonObjectProfile.getJSONArray("personal");
            LinearLayout list_recycled_parts_personal = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_personal);
            showDetails(inflater, jsonArrayPersonal, list_recycled_parts_personal);

            // INTEREST
            JSONArray jsonArrayInterest = jsonObjectProfile.getJSONArray("interest");
            LinearLayout list_recycled_parts_interest = (LinearLayout) myFragmentView.findViewById(R.id.list_recycled_parts_interest);
            showDetails(inflater, jsonArrayInterest, list_recycled_parts_interest);


            // PHOTOS
            if(!mParamPhotos.isEmpty()) {
                JSONArray jsonArrayPhotos = new JSONArray(mParamPhotos);

                JSONObject jsonObjectPhotos = jsonArrayPhotos.getJSONObject(0);

                imageLoader.DisplayImage(jsonObjectPhotos.getString("file"), header_imageview);
                imageLoader.DisplayImageRounded(jsonObjectPhotos.getString("file"), photoMain, 150, 150);
            } else {
                String noPhoto = getResources().getString(R.string.api_photos)+"/assets/img/no_photo_female.jpg";
                if(jsonObjectBasic.get("gender").toString().equalsIgnoreCase("m")) {
                    noPhoto = getResources().getString(R.string.api_photos)+"/assets/img/no_photo_male.jpg";
                } else if(jsonObjectBasic.get("gender").toString().equalsIgnoreCase("f")) {
                    noPhoto = getResources().getString(R.string.api_photos)+"/assets/img/no_photo_female.jpg";
                }

                imageLoader.DisplayImage(noPhoto, header_imageview);
                imageLoader.DisplayImageRounded(noPhoto, photoMain, 150, 150);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myFragmentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startChatting:
                try {
                    PeopleProfileActivity.mIAFPushService.initializeChatting(userId);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnEmoticons:
                break;
            case R.id.btnSendGift:
                showWindowFromUrl("http://store.pass-load.com/index.php");
                break;
            case R.id.btnSendLoad:
                showWindowFromUrl("http://store.pass-load.com/store.php?cat=cellphone-loads");
                break;
        }
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

    public void showWindowFromUrl(String url) {
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogSlideAnimZoom);

        dialog.setContentView(R.layout.dialog_webview);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final LinearLayout loadingBarLayout = (LinearLayout)dialog.findViewById(R.id.loadingBarLayout);

        final WebView webNewsFeed = (WebView)dialog.findViewById(R.id.webNewsFeed);

        webNewsFeed.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                loadingBarLayout.setVisibility(View.GONE);
                webNewsFeed.setVisibility(View.VISIBLE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });
        webNewsFeed.loadUrl(url);
        dialog.show();

    }
}
