package com.relhs.asianfinder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.relhs.asianfinder.adapter.ThreadCursorAdapter;
import com.relhs.asianfinder.data.RoomInfo;
import com.relhs.asianfinder.data.ThreadsInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.fragment.EmoticonsStickerFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.MessagesOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;

public class ChatActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout chatInputLayout;
    private FrameLayout emoticsContent;
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

    private EditText txtMessage;
    private ImageView chatPhoto;
    private ImageLoader imageLoader;
    private RelativeLayout profileData;

    private int emoticonsShowing = 0;

    public FragmentManager mFragmentManager;

    ListView lv;
    private int actionBarHeight;

    private final int CAMERA_CAPTURE = 1;
    private final int UPLOAD_PHOTO = 2;
    private byte[] bytearray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageLoader = new ImageLoader(this);
        profileData = (RelativeLayout) findViewById(R.id.profileData);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        chatPhoto = (ImageView) findViewById(R.id.chatPhoto);

        mFragmentManager = getSupportFragmentManager();
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        Bundle intentExtra = getIntent().getExtras();
        threadId = intentExtra.getString("threadId");

        messagesOperations = new MessagesOperations(this);
        messagesOperations.open();
        roomDetails = messagesOperations.getChatRoomDetails(threadId);

        userInfoOperations = new UserInfoOperations(this);
        userInfoOperations.open();
        userInfo = userInfoOperations.getUser();

        Log.d("-- robert", threadId+" Total Thread : "+messagesOperations.countLastThread(threadId));
                customAdapter = new ThreadCursorAdapter(
                        this,
                        messagesOperations.getThreadMessages(threadId),
                        messagesOperations, userInfoOperations,
                        0);

        lv = (ListView) findViewById(R.id.chatMessages);
        lv.setAdapter(customAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        // Defining default height of keyboard which is equal to 230 dip
        final float popUpheight = getResources().getDimension(
                R.dimen.keyboard_height);

        emoticsContent = (FrameLayout) findViewById(R.id.emoticsContentFrame);
        ViewGroup.LayoutParams emoticsContentParams = emoticsContent.getLayoutParams();
        emoticsContentParams.height = (int) popUpheight;
        emoticsContent.setLayoutParams(emoticsContentParams);
        emoticsContent.setVisibility(View.GONE);

        txtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    emoticsContent.setVisibility(View.GONE);
                    emoticonsShowing = 0;
                }
            }
        });
        txtMessage.setOnClickListener(this);
        final ImageButton sendMessage = (ImageButton) findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(this);
        final ImageButton btnEmoticons = (ImageButton) findViewById(R.id.btnEmoticons);
        btnEmoticons.setOnClickListener(this);

        final ImageButton btnAttach = (ImageButton) findViewById(R.id.btnAttach);
        btnAttach.setOnClickListener(this);

//        final CustomButton btnSendGift = (CustomButton) findViewById(R.id.btnSendGift);
//        btnSendGift.setOnClickListener(this);
//        final CustomButton btnSendLoad = (CustomButton) findViewById(R.id.btnSendLoad);
//        btnSendLoad.setOnClickListener(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                customAdapter.changeCursor(messagesOperations.getThreadMessages(intent.getStringExtra(AFPushService.AFPS_CHAT_TID)));
            }
        };
        imageLoader.DisplayImage(roomDetails.getMain_photo(), chatPhoto);

        TextView upTextView = (TextView) getLayoutInflater().inflate(R.layout.chat_name, null);
        getActionBar().setIcon(AsianFinderApplication.getTextAsBitmap(ChatActivity.this, upTextView, roomDetails.getUserName()));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send_gift) {
            showWindowFromUrl("http://store.pass-load.com/index.php");
        }
        if (id == R.id.action_send_load) {
            showWindowFromUrl("http://store.pass-load.com/store.php?cat=cellphone-loads");
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean listIsAtTop()   {
        if(lv.getChildCount() == 0) return true;
        return lv.getChildAt(0).getTop() == 0;
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
    public void onBackPressed() {
        if(emoticonsShowing == 1) {
            emoticsContent.setVisibility(View.GONE);
            emoticonsShowing = 0;
        } else {
            this.finish();
        }
    }
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.txtMessage:
                if(emoticonsShowing == 1) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        emoticsContent.setVisibility(View.GONE);
                        emoticonsShowing = 0;
                    }
                }
            break;
            case R.id.sendMessage:
                if(txtMessage.getText().toString().trim().equalsIgnoreCase("")) {
                    //Toast.makeText(this, "0", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if(messagesOperations.countLastThread(threadId) > 0) {
                            lastThreadInfo = messagesOperations.getLastThread(threadId);
                            mIAFPushService.sendChatMessage(roomDetails.getUserId(), txtMessage.getText().toString().trim(), lastThreadInfo.getLocalId() + 1);
                        } else {
                            mIAFPushService.sendChatMessage(roomDetails.getUserId(), txtMessage.getText().toString().trim(), 1);
                        }

                        txtMessage.setText("");
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        emoticsContent.setVisibility(View.GONE);
                        emoticonsShowing = 0;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            break;
            case R.id.btnEmoticons:
                if(emoticonsShowing == 0) {
                    ChatActions();
                    emoticonsShowing = 1;
                    if(imm.isActive()) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        emoticsContent.setVisibility(View.VISIBLE);
                    }
                } else {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        emoticsContent.setVisibility(View.GONE);
                        emoticonsShowing = 0;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }


                break;
            case R.id.btnAttach:
                selectImage();
                break;
//            case R.id.btnSendLoad:
//                showWindowFromUrl("http://store.pass-load.com/store.php?cat=cellphone-loads");
//                break;
        }
    }

    public void ChatActions() {
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        EmoticonsStickerFragment myFragment = new EmoticonsStickerFragment();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.emoticsContentFrame, myFragment, "EMOTICONS_FRAGMENT");
        transaction.commit();
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

    public void appendMessage(String text) {
        txtMessage.append(text);
    }

    public void sendStickerMsg(String folder, String file) throws RemoteException {
        lastThreadInfo = messagesOperations.getLastThread(threadId);
        mIAFPushService.sendSticker(folder, file, roomDetails.getUserId(), lastThreadInfo.getLocalId() + 1);
    }


    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    try {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    } catch (ActivityNotFoundException anfe) {
                        //display an error message
                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                        Toast toast = Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, UPLOAD_PHOTO);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


}
