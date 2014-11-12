package com.relhs.asianfinder;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.relhs.asianfinder.data.RoomInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.operation.MessagesOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


public class AFPushService extends Service {
    static final public String AFPS_ACTION_REFRESH_CHATROOM = "com.relhs.asianfinder.AFPushService.REFRESHCHATROOM";
    static final public String AFPS_ACTION_REFRESH_THREAD = "com.relhs.asianfinder.AFPushService.REFRESHTREAD";


    static final public String AFPS_RESULT = "com.relhs.asianfinder.AFPushService.REQUEST_PROCESSED";
    static final public String AFPS_CHAT_F = "com.relhs.asianfinder.AFPushService.F";
    static final public String AFPS_CHAT_LOCALID = "com.relhs.asianfinder.AFPushService.LOCALID";
    static final public String AFPS_CHAT_TID = "com.relhs.asianfinder.AFPushService.TID";
    static final public String AFPS_CHAT_T = "com.relhs.asianfinder.AFPushService.T";
    static final public String AFPS_CHAT_MESSAGE = "com.relhs.asianfinder.AFPushService.MESSAGE";
    static final public String AFPS_CHAT_SEEN = "com.relhs.asianfinder.AFPushService.SEEN";

    static final public String AFPS_CHATROOM = "com.relhs.asianfinder.AFPushService.CHATROOM";

    public static final String SERVERIP = "http://119.81.103.164"; //your computer IP address should be written here
    public static final int SERVERPORT = 8000;
    PrintWriter out;
    public Socket socket;
    InetAddress serverAddr;
    JSONObject credentials = new JSONObject();
    private UserInfoOperations userOperations;
    private UserInfo userInformation;

    Notification notification;
    private static final int NOTIFICATION_ID=1;
    private NotificationManager mgr;

//    private final IBinder myBinder = new LocalBinder();
    //public IBinder myBinder;
    private MessagesOperations messagesOperations;
    private LocalBroadcastManager broadcaster;

    public AFPushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        // TODO Auto-generated method stub
        System.out.println("I am in Ibinder onBind method");
        return myBinder;
    }

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);

        // !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();

        messagesOperations = new MessagesOperations(this);
        messagesOperations.open();
        // !IMPORTANT DATABASE OPERATION


        userInformation = userOperations.getUser();
        userOperations.close();
        mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //to clear the notification
        //mgr.cancel(NOTIFICATION_ID);

//        notification=new Notification(R.drawable.ic_launcher,"Online", System.currentTimeMillis());
//        Intent intent=new Intent(getApplicationContext(), DashboardActivity.class);
//        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//        notification.setLatestEventInfo(AFPushService.this, "Reminder: Saanvi Birthday",
//                "Today is your friend Saanvi's Birthday, please wish her", pendingIntent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }

    public class LocalBinder extends Binder {
        public AFPushService getService() {
            System.out.println("I am in Localbinder");
            return AFPushService.this;
        }
    }
    public void IsBoundable(){
        Toast.makeText(this,"I bind like butter", Toast.LENGTH_LONG).show();
    }

    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            System.out.println("in sendMessage"+message);
            out.println(message);
            out.flush();
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
        return START_STICKY;
    }

    class connectSocket implements Runnable {
        @Override
        public void run() {
            try {
                socketFunctions();
            } catch (URISyntaxException e) {
                Log.e("TCP", "C: Error " + e.getLocalizedMessage(), e);
            }
        }
    }
    public void socketFunctions() throws URISyntaxException {
        try {
            credentials.putOpt("userId", userInformation.getUser_id());
            credentials.putOpt("userType", userInformation.getUser_type());
            credentials.putOpt("userName", userInformation.getUsername());
            credentials.putOpt("domainId", userInformation.getDomain_id());
            credentials.putOpt("userSessionId", userInformation.getSession_id());
            credentials.putOpt("main_photo", userInformation.getMain_photo());
            credentials.putOpt("gender", userInformation.getGender());
            credentials.putOpt("userToken", userInformation.getUser_token());

            socket = IO.socket(SERVERIP+":"+SERVERPORT);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("HUGHES", "Go ONLINE: " + credentials.toString());
                    socket.emit("online", credentials);
                }
            }).on("onlineOk", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("-- onlineOk", Arrays.toString(args));
                    socket.emit("onlineChat", credentials); // show online in chat
                }
            }).on("onlineChatOk", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Log.d("-- onlineChatOk", Arrays.toString(args));
                        JSONArray argsArray = new JSONArray(Arrays.toString(args));
                        JSONObject viewingData = argsArray.getJSONObject(0);
                        JSONObject jsonObjectStickers = viewingData.getJSONObject("stickers");
                        JSONObject jsonObjectStickersFiles = jsonObjectStickers.getJSONObject("files");

                        //String stickersUrl = jsonObjectStickersFiles.getString("url");

                        //Log.d("-- onlineChatOk STICKERS", stickersUrl);

                        JSONArray jsonObjectRoomMembers = viewingData.getJSONArray("roomMembers");
                        for (int i = 0; i < jsonObjectRoomMembers.length(); i++) {
                            JSONObject c = jsonObjectRoomMembers.getJSONObject(i);

                            int userId = c.getInt("userId");
                            int threadId = c.getInt("threadId");
                            String userType = c.getString("userType");
                            String userName = c.getString("userName");
                            String main_photo = getString(R.string.api_photos) + c.getString("main_photo");
                            int is_chatting = c.getInt("is_chatting");
                            String lastOnline = c.getString("lastOnline");

                            messagesOperations.createRoom(userId, threadId, userType, userName,
                                    main_photo, is_chatting, lastOnline);
                        }
                        JSONArray jsonObjectThreads = viewingData.getJSONArray("threads");
                        for (int i = 0; i < jsonObjectThreads.length(); i++) {
                            JSONObject c = jsonObjectThreads.getJSONObject(i);
                            int threadId = c.getInt("threadId");

                            JSONArray jsonObjectThreadsMessages = c.getJSONArray("messages");
                            for (int ii = 0; ii < jsonObjectThreadsMessages.length(); ii++) {
                                JSONObject cM = jsonObjectThreadsMessages.getJSONObject(ii);

                                int f = cM.getInt("f");
                                int localId = cM.getInt("localId");
                                String m = cM.getString("m");
                                String timestamp = cM.getString("t");
                                int tid = cM.getInt("tid");
                                int seen = cM.getInt("seen");

                                if (cM.has("s")) {
                                    JSONObject cMSticker = cM.getJSONObject("s");
                                    Log.d("-- robert", cMSticker.toString());
                                    Log.d("-- robert",  cMSticker.getString("folder")+"/"+ cMSticker.getString("file"));
                                    messagesOperations.createThread(Constants.TEXT_STICKER, threadId, f, localId, "", timestamp, seen, cMSticker.getString("folder"), cMSticker.getString("file"));
                                } else {
                                    messagesOperations.createThread(Constants.TEXT_CHAT, threadId, f, localId, m, timestamp, seen, "", "");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notification = new Notification(R.drawable.ic_launcher, "Online", System.currentTimeMillis());
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    notification.setLatestEventInfo(AFPushService.this, "AsianFinder",
                            "You are now online", pendingIntent);

                    mgr.notify(NOTIFICATION_ID, notification);
                }
            }).on("receiveNotification", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("HUGHES STARTVIEWING DATA", "receiveNotification " + Arrays.toString(args));
                    notification = new Notification(R.drawable.ic_launcher, Arrays.toString(args), System.currentTimeMillis());

                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    notification.setLatestEventInfo(AFPushService.this, "Reminder: Saanvi Birthday",
                            "Today is your friend Saanvi's Birthday, please wish her", pendingIntent);

                    mgr.notify(NOTIFICATION_ID, notification);
                }
            }).on("chatWithOk", new Emitter.Listener() { // Initialize Communication
                @Override
                public void call(Object... args) {
                    Log.d("-- Robert", "chatWithOk " + Arrays.toString(args));
                    try {
                        JSONArray argsArray = new JSONArray(Arrays.toString(args));
                        JSONObject viewingData = argsArray.getJSONObject(0);

                        JSONArray jsonObjectRoomMembers = viewingData.getJSONArray("userData");
                        for (int i = 0; i < jsonObjectRoomMembers.length(); i++) {
                            JSONObject c = jsonObjectRoomMembers.getJSONObject(i);

                            int userId = c.getInt("userId");
                            int threadId = c.getInt("threadId");
                            String userType = c.getString("userType");
                            String userName = c.getString("userName");
                            String main_photo = getString(R.string.api_photos) + c.getString("main_photo");
                            int is_chatting = c.getInt("is_chatting");
                            String lastOnline = c.getString("lastOnline");

                            messagesOperations.createRoom(userId, threadId, userType, userName,
                                    main_photo, is_chatting, lastOnline);

                            sendResultChatRoom();
                        }
                        JSONArray jsonObjectThreads = viewingData.getJSONArray("threads");
                        for (int i = 0; i < jsonObjectThreads.length(); i++) {
                            JSONObject c = jsonObjectThreads.getJSONObject(i);
                            int threadId = c.getInt("threadId");

                            JSONArray jsonObjectThreadsMessages = c.getJSONArray("messages");
                            for (int ii = 0; ii < jsonObjectThreadsMessages.length(); ii++) {
                                JSONObject cM = jsonObjectThreadsMessages.getJSONObject(ii);

                                int f = cM.getInt("f");
                                int localId = cM.getInt("localId");
                                String m = cM.getString("m");
                                String timestamp = cM.getString("t");
                                int tid = cM.getInt("tid");
                                int seen = cM.getInt("seen");

                                if (cM.has("s")) {
                                    JSONObject cMSticker = cM.getJSONObject("s");
                                    messagesOperations.createThread(Constants.TEXT_STICKER, threadId, f, localId, m, timestamp, seen, cMSticker.getString("folder"), cMSticker.getString("file"));
                                } else {
                                    messagesOperations.createThread(Constants.TEXT_CHAT, threadId, f, localId, m, timestamp, seen, "", "");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).on("chat", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("-- hughes", "chat " + Arrays.toString(args));
                    try {
                        myBinder.getChatMessage(Arrays.toString(args));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).on("receiveSticker", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("-- hughes", "chat " + Arrays.toString(args));
                    try {
                        myBinder.getChatMessage(Arrays.toString(args));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                }

            });
            socket.connect();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public static void isSeen(JSONObject timestamps) {
//        try {
//            JSONObject isSeenData = new JSONObject();
//            isSeenData.putOpt("userId", userInformation.getUserId());
//
//            isSeenData.putOpt("unseenIds", timestamps);
//            socket.emit("seen", isSeenData);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void sendResultChatRoom() {
        Intent intent = new Intent(AFPS_RESULT);
        intent.setAction(AFPS_ACTION_REFRESH_CHATROOM);
        broadcaster.sendBroadcast(intent);
    }

    public void sendResultChat(String tid) {
        Intent intent = new Intent(AFPS_RESULT);
        intent.putExtra(AFPS_CHAT_TID, tid);

        broadcaster.sendBroadcast(intent);
    }

    public IAFPushService.Stub myBinder = new IAFPushService.Stub() {

        public void sendChatMessage(int userId, String message, int localId) {
            try {
                JSONObject sendMessage = new JSONObject();
                sendMessage.putOpt("userId", userId);
                sendMessage.putOpt("message", message);
                sendMessage.putOpt("localId", localId);

                socket.emit("chat", sendMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        public void getChatMessage(String jsonData) {
            try {
                JSONArray argsArray = new JSONArray(jsonData);
                JSONObject cM = argsArray.getJSONObject(0);
                int f = cM.getInt("f");
                int localId = cM.getInt("localId");
                String m = cM.getString("m");
                String timestamp = cM.getString("t");
                int threadId = cM.getInt("tid");
                Boolean seen = cM.getBoolean("seen");
                int mySeen = (seen) ? 1 : 0;

                RoomInfo roomDetails = messagesOperations.getChatRoomDetails(threadId+"");

                // show notification
                notification = new Notification(R.drawable.ic_launcher, "New Message", System.currentTimeMillis());
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("threadId", cM.getString("tid"));

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (cM.has("s")) {
                    JSONObject cMSticker = cM.getJSONObject("s");
                    Log.d("-- robert",  cMSticker.getString("folder")+"/"+ cMSticker.getString("file"));
                    messagesOperations.createThread(Constants.TEXT_STICKER, threadId, f, localId, "", timestamp, mySeen, cMSticker.getString("folder"), cMSticker.getString("file"));


                    notification.setLatestEventInfo(AFPushService.this, roomDetails.getUserName(), "Sticker", pendingIntent);

                    mgr.notify(NOTIFICATION_ID, notification);
                } else {
                    messagesOperations.createThread(Constants.TEXT_CHAT, threadId, f, localId, m, timestamp, mySeen, "", "");

                    notification.setLatestEventInfo(AFPushService.this, roomDetails.getUserName(),
                            cM.getString("m"), pendingIntent);

                    mgr.notify(NOTIFICATION_ID, notification);
                }



                if(AsianFinderApplication.isActivityVisible()) {
                    sendResultChat(cM.getString("tid"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
