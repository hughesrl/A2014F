package com.relhs.asianfinder.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.PeopleProfileActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.MyListCursorAdapter;
import com.relhs.asianfinder.adapter.PeopleListAdapter;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.operation.MyListOperations;
import com.relhs.asianfinder.utils.JSONParser;

import org.json.JSONArray;

import java.util.ArrayList;


public class ListFavoritesFragment extends ListFragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_LATITUDE = "latitude";

    private View myFragmentView;
    private int mParamItemNumber;
    private double mParamLong;
    private double mParamLat;

    private int currentOffset = 0;

    JSONParser jParser;
    private AsymmetricGridView mListView;
    private PeopleListAdapter adapter;
    private ArrayList<PeopleInfo> peopleInfoArrayList = new ArrayList<PeopleInfo>();

    private JSONArray jsonArrayServerResponse;
    private int totalRecordCount;
    private int totalPageCount;
    private int currentPage;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFavoritesFragment newInstance(int sectionNumber, double longitude, double latitude) {
        ListFavoritesFragment fragment = new ListFavoritesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putDouble(ARG_LATITUDE, latitude);
        fragment.setArguments(args);
        return fragment;
    }

    public ListFavoritesFragment() {
        // Required empty public constructor
    }


    private MyListOperations myListOperations;
    public MyListCursorAdapter customAdapter;
    private BroadcastReceiver receiver;
    private String threadId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamItemNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mParamLong = getArguments().getDouble(ARG_LONGITUDE);
            mParamLat = getArguments().getInt(ARG_LATITUDE);
        }

        myListOperations = new MyListOperations(getActivity());
        myListOperations.open();

        jParser = new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_favorites_list, container, false);

        customAdapter = new MyListCursorAdapter(
                getActivity(),
                myListOperations.getMyListByType(Constants.TAG_MYLIST_FAVORITES),
                myListOperations,
                0);
        return myFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getListView().setAdapter(customAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor c = (Cursor) customAdapter.getItem(position);
                Intent i = new Intent(getActivity(), PeopleProfileActivity.class);
                i.putExtra(PeopleProfileActivity.INTENT_PID, c.getInt(c.getColumnIndex(DataBaseWrapper.MYLISTINFO_USERID)));
                startActivity(i);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }



}
