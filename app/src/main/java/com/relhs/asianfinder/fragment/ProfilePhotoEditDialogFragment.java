package com.relhs.asianfinder.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.relhs.asianfinder.AsianFinderApplication;
import com.relhs.asianfinder.ProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.SpinnerCustomAdapter;
import com.relhs.asianfinder.data.PreferenceMultipleSelectionModel;
import com.relhs.asianfinder.data.SpinnerItems;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProfilePhotoEditDialogFragment extends DialogFragment {
    public static final String TAG = ProfilePhotoEditDialogFragment.class.getSimpleName();
    private View myFragmentView;
    public static final String ARG_CATEGORIES = "categories";
    public static final String ARG_PHOTOURL = "photoUrl";

    private String mParamCategories;
    private String mParamPhotoUrl;

    private RelativeLayout layoutData;
    private LinearLayout layoutLoadingBar;
    private ImageView uploadedPhoto;
    private Spinner spinnerCategories;
    private CustomButton btnOk;
    private ImageLoader imageLoader;

    public ProfilePhotoEditDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.90f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_Panel);

        if (getArguments() != null) {
            mParamCategories = getArguments().getString(ARG_CATEGORIES);
            mParamPhotoUrl = getArguments().getString(ARG_PHOTOURL);
        }

        imageLoader = new ImageLoader(getActivity());
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_profile_photo_edit_dialog, container, false);

        uploadedPhoto = (ImageView) myFragmentView.findViewById(R.id.uploadedPhoto);
        spinnerCategories = (Spinner) myFragmentView.findViewById(R.id.spinnerCategories);
        btnOk = (CustomButton) myFragmentView.findViewById(R.id.btnOk);

        imageLoader.DisplayImage(mParamPhotoUrl, uploadedPhoto);
        ArrayList<SpinnerItems> spinnerItems = new ArrayList<SpinnerItems>();

        try {
            JSONArray jsonArrayCategories = new JSONArray(mParamCategories);
            for (int i = 0; i < jsonArrayCategories.length(); i++) {
                JSONObject jsonObjectPhotos = jsonArrayCategories.getJSONObject(i);
                spinnerItems.add(new SpinnerItems(jsonObjectPhotos.getString("label"),jsonObjectPhotos.getString("value"), false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(getActivity(), R.layout.spinner_text, (ArrayList<SpinnerItems>) spinnerItems);
            spinnerCategories.setAdapter(spinnerCustomAdapter);
        }


        return myFragmentView;
    }

    private class LoadMultipleSelectionDataTask extends AsyncTask<Void, Void, JSONObject> {
        private ProgressDialog mProgressDialog;
        private JSONParser jParser;
        private JSONArray jsonArray;
        private LayoutInflater _inflater;
        private String _k;
        private String _lid;
        private List<PreferenceMultipleSelectionModel> list;
        private String selectionType;

        public LoadMultipleSelectionDataTask(LayoutInflater inflater, String k, String lid) {
            // TODO Auto-generated constructor stub
            this._inflater = inflater;
            this._k = k;
            this._lid = lid;

            jParser = new JSONParser();
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
        protected JSONObject doInBackground(Void... args) {
            String did = ((ProfileActivity)getActivity()).getDeviceId();
            return jParser.getJSONFromUrl(getResources().getString(R.string.api) + "?act=preference&t=fetch&did=" + did + "&k=" + _k, null);
        }
        @Override
        protected void onPostExecute(JSONObject jObj) {
            try {
                // Getting JSON Array from URL
                // Also Assume to return only one record
                jsonArray = jObj.getJSONArray(AsianFinderApplication.TAG_OS);
                JSONObject c = jsonArray.getJSONObject(0);

                if (!c.getBoolean(AsianFinderApplication.TAG_STATUS)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), c.getString(AsianFinderApplication.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                } else {
                    list = new ArrayList<PreferenceMultipleSelectionModel>();
                    JSONObject jsonArrayLoginDataObject = c.getJSONObject(AsianFinderApplication.TAG_DATA);
                    selectionType = jsonArrayLoginDataObject.getString("type");

                    JSONArray jsonArrayOptions = jsonArrayLoginDataObject.getJSONArray("options");
                    for (int i=0; i<jsonArrayOptions.length(); i++) {
                        JSONObject jsonObjectOptions = jsonArrayOptions.getJSONObject(i);
                        PreferenceMultipleSelectionModel preferenceMultipleSelectionModel = new PreferenceMultipleSelectionModel(jsonObjectOptions.getString("value"),
                                jsonObjectOptions.getString("caption"), selectionType, true);
                        list.add(preferenceMultipleSelectionModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {


                mProgressDialog.dismiss();
            }
        }
    }

}
