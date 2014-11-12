package com.relhs.asianfinder.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.PeopleListAdapter;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BrowseFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_LATITUDE = "latitude";

    private View myFragmentView;
    private int mParamItemNumber;
    private double mParamLong;
    private double mParamLat;

    private int currentOffset = 0;


    public AsymmetricGridView mListView;
    public ArrayList<PeopleInfo> businessList = new ArrayList<PeopleInfo>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(int sectionNumber, double longitude, double latitude) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putDouble(ARG_LATITUDE, latitude);
        fragment.setArguments(args);
        return fragment;
    }

    public BrowseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamItemNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mParamLong = getArguments().getDouble(ARG_LONGITUDE);
            mParamLat = getArguments().getInt(ARG_LATITUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_browse, container, false);

//        mListView = (AsymmetricGridView) myFragmentView.findViewById(R.id.listView);
//
//        mListView.setRequestedColumnCount(2);
//        new GetAllNearest(mParamLong, mParamLat).execute();

        return myFragmentView;
    }
    /************************* ASYNSTASK HERE *************************/
    private class GetAllNearest extends AsyncTask<Void, Void, ArrayList<PeopleInfo>> {
        private double longitude;
        private double latitude;

        private JSONArray android;
        private JSONArray androidData;
        private ProgressDialog mProgressDialog;

        public GetAllNearest(double longitude, double latitude) {
            // TODO Auto-generated constructor stub
            this.longitude = longitude;
            this.latitude = latitude;
        }
        @Override
        protected void onPreExecute() {
//            if (mProgressDialog == null) {
//                mProgressDialog = Utils.createProgressDialog(getActivity());
//                mProgressDialog.show();
//            } else {
//                mProgressDialog.show();
//            }
        }
        @Override
        protected ArrayList<PeopleInfo> doInBackground(Void... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(getResources().getString(R.string.api)+"/get_business/android/"+longitude+"/"+latitude, null);
            try {
                android = json.getJSONArray(Constants.TAG_OS);
                JSONObject jsonObject = android.getJSONObject(0);
                if(!jsonObject.getBoolean("success")) { // false
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } else { // true
                    androidData = jsonObject.getJSONArray(Constants.TAG_DATA);
                    for (int i = 0; i < androidData.length(); i++) {
                        JSONObject c = androidData.getJSONObject(i);
                        // Storing  JSON item in a Variable
                        // Height of the row is 3/4 of the screen width
                        int colSpan = 1;
                        int rowSpan = 1;

                        String name = c.getString(Constants.TAG_BUSNAME);
                        String category = c.getString(Constants.TAG_BUSCATEGORY);
                        String ownership = c.getString(Constants.TAG_BUSOWNERSHIP);
                        String image_filename = c.getString(Constants.TAG_BUSIMAGEFILE);
                        String image_width = c.getString(Constants.TAG_BUSIMAGEWIDTH);
                        String image_height = c.getString(Constants.TAG_BUSIMAGEHEIGHT);
                        String tile_size = c.getString(Constants.TAG_BUSTILESIZE);
                        String source = c.getString(Constants.TAG_BUSSOURCE);
                        String id = c.getString(Constants.TAG_BUSID);
                        String lat = c.getString(Constants.TAG_BUSLATITUDE);
                        String lng = c.getString(Constants.TAG_BUSLONGITUDE);
                        String distance = c.getString(Constants.TAG_BUSDISTANCE);

                        Log.i("FS", "ADD ITEMS");
                        if (tile_size.equals("double")) {
                            colSpan = 2;
                        } else if (tile_size.equals("quad")) {
                            colSpan = 2;
                            rowSpan = 2;
                        }

                        Log.d("ADD", image_filename);
                        PeopleInfo businessInfo = new PeopleInfo(colSpan, rowSpan, currentOffset + i,
                                name, category, ownership, image_filename, image_width, image_height, tile_size, source, id, lat, lng, distance);
                        businessList.add(businessInfo);
                    }
                    currentOffset += android.length();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return businessList;
        }
        @Override
        protected void onPostExecute(ArrayList<PeopleInfo> businessListRet) {
            PeopleListAdapter adapter = new PeopleListAdapter(getActivity(), mListView, new ArrayList<PeopleInfo>());
            adapter.setItems(businessListRet);

            mListView.setAdapter(adapter);
            mListView.setDebugging(false);

            adapter.notifyDataSetChanged();

//            mListView.setAllowReordering(true);
//            mListView.isAllowReordering();

//            mListView.setOnItemClickListener(new OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent,
//                                        View view, int position, long id) {
//                    // TODO Auto-generated method stub
//                    final Businessinfo businessInfo = (Businessinfo) parent.getItemAtPosition(position);
//
//                    Toast.makeText(getActivity(), businessInfo.getName(), Toast.LENGTH_SHORT).show();
//
//                    final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "...", true);
//                    ringProgressDialog.setCancelable(false);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(2000);
//                            } catch (Exception e) {
//
//                            } finally {
//                                Intent businessDetails = new Intent(getActivity(), BusinessDetailsActivity.class);
//                                businessDetails.putExtra("businessInfo", businessInfo);
//                                businessDetails.putExtra("businessInfoId", businessInfo.getId());
//                                startActivity(businessDetails);
//                            }
//                            ringProgressDialog.dismiss();
//                        }
//                    }).start();
//                }
//            });
//
//            mListView.setOnScrollListener(new OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    // TODO Auto-generated method stub
//                    int threshold = 1;
//                    int count = mListView.getCount();
//                    if (scrollState == SCROLL_STATE_IDLE) {
//                        if (mListView.getLastVisiblePosition() >= count-threshold) {
//                            new LoadMoreDataTask().execute();
//                        }
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem,
//                                     int visibleItemCount, int totalItemCount) {
//                    // TODO Auto-generated method stub
//                }
//            });

            mProgressDialog.dismiss();
        }
    }
}
