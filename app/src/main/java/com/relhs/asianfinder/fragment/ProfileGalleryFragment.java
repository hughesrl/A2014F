package com.relhs.asianfinder.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.GallerySlideshowActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.PeoplePhotosGridAdapter;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileGalleryFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_PROFILE = "arg_profile";
    public static final String ARG_PHOTOS = "arg_photos";

    private ImageLoader imageLoader;
    private View myFragmentView;

    private String mParamProfile;
    private String mParamMatches;
    private String mParamPhotos;

    private ArrayList<PeoplePhotosInfo> peoplePhotosArrayList = new ArrayList<PeoplePhotosInfo>();
    private AsymmetricGridView mListView;
    private PeoplePhotosGridAdapter adapter;
    private int currentOffset;
    private LinearLayout noPhoto;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileGalleryFragment newInstance(int sectionNumber, double longitude, double latitude) {
        ProfileGalleryFragment fragment = new ProfileGalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileGalleryFragment() {
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
        myFragmentView = inflater.inflate(R.layout.fragment_people_profile_gallery, container, false);
        mListView = (AsymmetricGridView) myFragmentView.findViewById(R.id.listView);
        mListView.setRequestedColumnCount(2);

        noPhoto = (LinearLayout) myFragmentView.findViewById(R.id.noPhoto);

        new LoadPhotosDataTask(inflater).execute();

        return myFragmentView;
    }

    private class LoadPhotosDataTask extends AsyncTask<Void, Void, ArrayList<PeoplePhotosInfo>> {
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
        protected ArrayList<PeoplePhotosInfo> doInBackground(Void... args) {
            try {
                JSONArray jsonArrayPhotos = new JSONArray(mParamPhotos);
                for (int i = 0; i < jsonArrayPhotos.length(); i++) {
                    JSONObject jsonObjectPhotos = jsonArrayPhotos.getJSONObject(i);
                    int colSpan = 1;
                    int rowSpan = 1;

                    PeoplePhotosInfo peoplePhotosInfo = new PeoplePhotosInfo(colSpan, rowSpan, currentOffset + i, jsonObjectPhotos.getString("category"), jsonObjectPhotos.getString("file"), jsonObjectPhotos.getString("number_of_comments"));
                    peoplePhotosArrayList.add(peoplePhotosInfo);
                }
                currentOffset += jsonArrayPhotos.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return peoplePhotosArrayList;
        }
        @Override
        protected void onPostExecute(final ArrayList<PeoplePhotosInfo> peoplePhotosArrayList) {
            if(!peoplePhotosArrayList.isEmpty()) {
                adapter = new PeoplePhotosGridAdapter(getActivity(), mListView, new ArrayList<PeoplePhotosInfo>());
                adapter.setItems(peoplePhotosArrayList);

                mListView.setAdapter(adapter);
                mListView.setDebugging(false);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PeoplePhotosInfo peoplePhotosInfo = (PeoplePhotosInfo) parent.getItemAtPosition(position);

                        Intent intentDetails = new Intent(getActivity(), GallerySlideshowActivity.class);

                        intentDetails.putExtra(GallerySlideshowActivity.INTENT_POSITION, position);
                        intentDetails.putParcelableArrayListExtra(GallerySlideshowActivity.INTENT_PHOTOS, peoplePhotosArrayList);

                        startActivity(intentDetails);
                    }
                });
            } else {
                //Toast.makeText(getActivity(), "No More data", Toast.LENGTH_LONG).show();
                noPhoto.setVisibility(View.VISIBLE);
            }

            mProgressDialog.dismiss();
        }
    }
}
