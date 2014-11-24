package com.relhs.asianfinder;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.relhs.asianfinder.adapter.ChatRoomCursorAdapter;
import com.relhs.asianfinder.adapter.ThreadCursorAdapter;
import com.relhs.asianfinder.data.RoomInfo;
import com.relhs.asianfinder.data.ThreadsInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.MessagesOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.view.CustomButton;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends ListActivity implements View.OnClickListener {

    private RelativeLayout chatInputLayout;
    private LinearLayout emoticsContent;
    private static boolean keyboardHidden = true;
    private static int reduceHeight = 0;
    private int position = 0;
    private InputMethodManager imm;
    private Socket socketIO;
    private String performerId;


    private String threadId;
    private MessagesOperations messagesOperations;
    private UserInfoOperations userInfoOperations;
    private ThreadCursorAdapter customAdapter;
    private BroadcastReceiver receiver;
    private UserInfo userInfo;
    private ThreadsInfo lastThreadInfo;

    IAFPushService mIAFPushService;
    private Cursor threadMessages;
    private RoomInfo roomDetails;

    private boolean mBound;

    public EditText txtMessage;
    private ImageView chatPhoto;
    private ImageLoader imageLoader;
    private RelativeLayout profileData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageLoader = new ImageLoader(this);
        profileData = (RelativeLayout) findViewById(R.id.profileData);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        chatPhoto = (ImageView) findViewById(R.id.chatPhoto);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        Bundle intentExtra = getIntent().getExtras();
        threadId = intentExtra.getString("threadId");

        messagesOperations = new MessagesOperations(this);
        messagesOperations.open();
        roomDetails = messagesOperations.getChatRoomDetails(threadId);

        userInfoOperations = new UserInfoOperations(this);
        userInfoOperations.open();
        userInfo = userInfoOperations.getUser();


        customAdapter = new ThreadCursorAdapter(
                this,
                messagesOperations.getThreadMessages(threadId),
                messagesOperations,userInfoOperations,
                0);

        getListView().setAdapter(customAdapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
//        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
//            int mLastFirstVisibleItem = 0;
//            boolean mIsScrollingUp;
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                if (view.getId() == getListView().getId()) {
//                    final int currentFirstVisibleItem = getListView().getFirstVisiblePosition();
//
//                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
//                        mIsScrollingUp = false;
////                        Toast.makeText(ChatActivity.this, "Down", Toast.LENGTH_LONG).show();
//                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
//                        mIsScrollingUp = true;
////                        Toast.makeText(ChatActivity.this, "Up", Toast.LENGTH_LONG).show();
//                    }
//                    mLastFirstVisibleItem = currentFirstVisibleItem;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if(mLastFirstVisibleItem<firstVisibleItem) {
////                    Log.i("SCROLLING DOWN","TRUE");
//                    if(profileData.getVisibility() == View.INVISIBLE) {
//                        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//                        profileData.startAnimation(slide);
//                        profileData.setVisibility(View.VISIBLE);
//                    }
//                }
//                if(mLastFirstVisibleItem>firstVisibleItem) {
////                    Log.i("SCROLLING UP","TRUE");
//                    if(profileData.getVisibility() == View.VISIBLE) {
//                        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
//                        profileData.startAnimation(slide);
//                        profileData.setVisibility(View.INVISIBLE);
//                    }
//                }
//                mLastFirstVisibleItem=firstVisibleItem;
//            }
//        });
        final ImageButton sendMessage = (ImageButton) findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(this);
        final ImageButton btnEmoticons = (ImageButton) findViewById(R.id.btnEmoticons);
        btnEmoticons.setOnClickListener(this);
        final CustomButton btnSendGift = (CustomButton) findViewById(R.id.btnSendGift);
        btnSendGift.setOnClickListener(this);
        final CustomButton btnSendLoad = (CustomButton) findViewById(R.id.btnSendLoad);
        btnSendLoad.setOnClickListener(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                customAdapter.changeCursor(messagesOperations.getThreadMessages(intent.getStringExtra(AFPushService.AFPS_CHAT_TID)));
            }
        };
        imageLoader.DisplayImage(roomDetails.getMain_photo(), chatPhoto);

        getActionBar().setTitle(roomDetails.getUserName());
    }

    private boolean listIsAtTop()   {
        if(getListView().getChildCount() == 0) return true;
        return getListView().getChildAt(0).getTop() == 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(ChatActivity.this, AFPushService.class), mConnection, BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(AFPushService.AFPS_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mIAFPushService = null;
            mBound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mIAFPushService = IAFPushService.Stub.asInterface(service);
            mBound = true;
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        AsianFinderApplication.activityResumed();

    }
    @Override
    protected void onPause() {
        super.onPause();
        AsianFinderApplication.activityPaused();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendMessage:
                if(txtMessage.getText().toString().trim().equalsIgnoreCase("")) {
                    //Toast.makeText(this, "0", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        lastThreadInfo = messagesOperations.getLastThread(threadId);
                        mIAFPushService.sendChatMessage(roomDetails.getUserId(), txtMessage.getText().toString().trim(), lastThreadInfo.getLocalId() + 1);

                        txtMessage.setText("");
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            break;
            case R.id.btnEmoticons:
                break;
            case R.id.btnSendGift:
                showWindowFromUrl("http://store.pass-load.com/index.php");
                break;
            case R.id.btnSendLoad:
                showWindowFromUrl("http://store.pass-load.com/store.php?cat=cellphone-loads");
                break;
        }
    }

    public void showWindowFromUrl(String url) {
        final Dialog dialog = new Dialog(this, R.style.DialogSlideAnimZoom);

        dialog.setContentView(R.layout.dialog_webview);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final LinearLayout loadingBarLayout = (LinearLayout)dialog.findViewById(R.id.loadingBarLayout);

        final WebView webNewsFeed = (WebView)dialog.findViewById(R.id.webNewsFeed);

        webNewsFeed.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                loadingBarLayout.setVisibility(View.GONE);
                webNewsFeed.setVisibility(View.VISIBLE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });
        webNewsFeed.loadUrl(url);
        dialog.show();

    }
}
