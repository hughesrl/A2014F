package com.relhs.asianfinder.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.relhs.asianfinder.ChatActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.EmoticonsGridAdapter;
import com.relhs.asianfinder.adapter.StickersGridAdapter;
import com.relhs.asianfinder.data.EmoticonsInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StickersFragment extends Fragment {
    public static final String TAG = StickersFragment.class.getSimpleName();

    private ArrayList<String> stickersArrayList;
    private View myFragmentView;
    private GridView mGridView;
    private ImageLoader mImageLoader;

    public StickersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = new ImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (myFragmentView == null) {
            myFragmentView = inflater.inflate(R.layout.fragment_stickers, container, false);
        }else {
            ((ViewGroup) myFragmentView.getParent()).removeView(myFragmentView);
        }
        return myFragmentView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGridView = (GridView) myFragmentView.findViewById(R.id.grid);
        try {
            stickersArrayList = Utils.getStickersFromAssets(getActivity());
            StickersGridAdapter adapter = new StickersGridAdapter(getActivity(), stickersArrayList);
            mGridView.setAdapter(adapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = stickersArrayList.get(position);
                    try {
                        ((ChatActivity) getActivity()).sendStickerMsg("default", item);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


}
