package com.relhs.asianfinder;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.adapter.ChatRoomCursorAdapter;
import com.relhs.asianfinder.operation.MessagesOperations;

public class ChatRoomsActivity extends ListActivity {
    public static final String TAG = ChatRoomsActivity.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mParamItemNumber;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    ProgressDialog progress;

    private MessagesOperations messagesOperations;
    public ChatRoomCursorAdapter customAdapter;
    private BroadcastReceiver receiver;
    private String threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_chatroom);



        messagesOperations = new MessagesOperations(this);
        messagesOperations.open();

        //Log.d("-- robert", messagesOperations.countChatRooms()+"");
        customAdapter = new ChatRoomCursorAdapter(
                this,
                messagesOperations.getChatRooms(),
                messagesOperations,
                0);

        getListView().setAdapter(customAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor c = (Cursor) customAdapter.getItem(position);

                Intent i = new Intent(ChatRoomsActivity.this, ChatActivity.class);
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

        Bundle intentExtra = getIntent().getExtras();
        if(intentExtra != null) {
            threadId = intentExtra.getString("threadId");
            Intent intent = new Intent(getBaseContext(), ChatActivity.class);
            intent.putExtra("threadId", threadId+"");
            startActivity(intent);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(AFPushService.AFPS_RESULT));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


    public String getDeviceId() {
        return ((AsianFinderApplication) getApplication()).getDeviceId();
    }





}

