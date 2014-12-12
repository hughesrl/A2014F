package com.relhs.asianfinder.fragment;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.relhs.asianfinder.ChatActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.EmoticonsGridAdapter;
import com.relhs.asianfinder.adapter.EmoticonsPagerAdapter;
import com.relhs.asianfinder.data.EmoticonsInfo;
import com.relhs.asianfinder.operation.EmoticonsOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SmileysFragment extends Fragment implements EmoticonsGridAdapter.KeyClickListener{
    private static final int NO_OF_EMOTICONS = 67;
    public static final String TAG = SmileysFragment.class.getSimpleName();
    private static final String ARG_STICKERS = "stickers";

    //DisplayImageOptions options;
    private Bitmap[] emoticons;
    private View popUpView;

    private String mParamStickersJsonObjString;

    //private ArrayList<Stickers> stickersArrayList;
    private View myFragmentView;

    EmoticonsOperations emoticonsOperations;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stickersJsonObjString Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SmileysFragment newInstance(String stickersJsonObjString) {
        SmileysFragment fragment = new SmileysFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STICKERS, stickersJsonObjString);
        fragment.setArguments(args);
        return fragment;
    }

    public SmileysFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamStickersJsonObjString = getArguments().getString(ARG_STICKERS);
        }

        emoticonsOperations = new EmoticonsOperations(getActivity());
        emoticonsOperations.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (myFragmentView == null) {
            myFragmentView = inflater.inflate(R.layout.emoticons_popup, container, false);
        }else {
            ((ViewGroup) myFragmentView.getParent()).removeView(myFragmentView);
        }

        ViewPager pager = (ViewPager) myFragmentView.findViewById(R.id.emoticons_pager);
        pager.setOffscreenPageLimit(3);

        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
            paths.add(i + ".png");
        }

        EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(getActivity(), paths, this);
        pager.setAdapter(adapter);

        return myFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void keyClickedIndex(final String index) {
        Log.d("ROBERT index", index);
        EmoticonsInfo emoticonDetails = emoticonsOperations.getEmoticon(index);
        Log.d("ROBERT", emoticonDetails.getEmoticonText());
        ((ChatActivity) getActivity()).appendMessage(emoticonDetails.getEmoticonText());

    }

}
