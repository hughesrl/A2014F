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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.adapter.ChatRoomCursorAdapter;
import com.relhs.asianfinder.adapter.MailMainAdapter;
import com.relhs.asianfinder.adapter.NavigationDrawerAdapter;
import com.relhs.asianfinder.data.MailMainItem;
import com.relhs.asianfinder.data.NavDrawerItem;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.MessagesOperations;

import java.util.ArrayList;

public class MailActivity extends ListActivity {
    public static final String TAG = MailActivity.class.getSimpleName();

    private ArrayList<MailMainItem> mListItems;
    private MailMainAdapter adapter;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_main);

        imageLoader = new ImageLoader(this);
        mListItems = new ArrayList<MailMainItem>();

        ArrayList<MailMainItem> mailMainItems = prepareListData();

        // Set Adapter
        adapter = new MailMainAdapter(this, mailMainItems, imageLoader);

        getListView().setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mail_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public String getDeviceId() {
        return ((AsianFinderApplication) getApplication()).getDeviceId();
    }

    private ArrayList<MailMainItem> prepareListData() {
        mListItems.add(0,new MailMainItem("Inbox", false, ""));
        mListItems.add(1,new MailMainItem("Sent", false, ""));
        mListItems.add(2,new MailMainItem("Trash", false, ""));
        return mListItems;
    }

}

