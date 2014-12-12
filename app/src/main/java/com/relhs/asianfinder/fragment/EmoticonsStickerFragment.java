package com.relhs.asianfinder.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;

import com.relhs.asianfinder.R;

import java.util.ArrayList;

public class EmoticonsStickerFragment extends Fragment {

	private static final String ARG_POSITION = "position";
    private View myFragmentView;
    private FragmentTabHost mTabHost;

    public static Fragment newInstance(int position) {
		EmoticonsStickerFragment f = new EmoticonsStickerFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_emoticons_and_stickers, null);

        mTabHost = (FragmentTabHost)myFragmentView.findViewById(android.R.id.tabhost);

        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(setIndicator(getActivity(), mTabHost.newTabSpec("Smiley"), R.drawable.ic_smiley),
                SmileysFragment.class, null);

        mTabHost.addTab(setIndicator(getActivity(), mTabHost.newTabSpec("Sticker"), R.drawable.ic_sticker),
                StickersFragment.class, null);




        return myFragmentView;
	}
    public TabHost.TabSpec setIndicator(Context ctx, TabHost.TabSpec spec, int resid) {
        // TODO Auto-generated method stub
        View v = LayoutInflater.from(ctx).inflate(R.layout.custom_tab, null);
        v.setBackgroundResource(R.drawable.emoticons_tab_indicator);

        ImageView icon = (ImageView) v.findViewById(R.id.tab_icon);

        icon.setImageDrawable(getResources().getDrawable(resid));

        return spec.setIndicator(v);
    }


    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
    public void stickerClick(View view) {

    }


}