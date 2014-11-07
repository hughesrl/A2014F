package com.relhs.asianfinder.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.AFPushService;
import com.relhs.asianfinder.ChatActivity;
import com.relhs.asianfinder.DashboardActivity;
import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.ChatRoomCursorAdapter;
import com.relhs.asianfinder.data.RoomInfo;
import com.relhs.asianfinder.operation.MessagesOperations;

public class MessagesChatRoomFragment extends ListFragment {
    public static final String TAG = MessagesChatRoomFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mParamItemNumber;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    ProgressDialog progress;

    private MessagesOperations messagesOperations;
    public ChatRoomCursorAdapter customAdapter;
    private BroadcastReceiver receiver;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesChatRoomFragment newInstance(int sectionNumber) {
        MessagesChatRoomFragment fragment = new MessagesChatRoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MessagesChatRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamItemNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        messagesOperations = new MessagesOperations(getActivity());
        messagesOperations.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup myFragmentView = (ViewGroup) inflater.inflate(R.layout.fragment_messages_chatroom, container, false);
        return myFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customAdapter = new ChatRoomCursorAdapter(
                getActivity(),
                messagesOperations.getChatRooms(),
                messagesOperations,
                0);

        getListView().setAdapter(customAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor c = (Cursor) customAdapter.getItem(position);

                Log.d("-- ROBERT", c.getInt(c.getColumnIndex(DataBaseWrapper.ROOMINFO_THREADID)) +" --");
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("threadId", c.getInt(c.getColumnIndex(DataBaseWrapper.ROOMINFO_THREADID))+"");
                startActivity(i);
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equalsIgnoreCase(AFPushService.AFPS_ACTION_REFRESH_CHATROOM)) {
                    customAdapter.changeCursor(messagesOperations.getChatRooms());
                }
            }
        };
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DashboardActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver), new IntentFilter(AFPushService.AFPS_RESULT));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }


}
