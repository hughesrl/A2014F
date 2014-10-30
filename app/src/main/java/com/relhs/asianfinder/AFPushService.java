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
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.operation.UserInfoOperations;


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
    public static final String SERVERIP = "http://119.81.103.164"; //your computer IP address should be written here
    public static final int SERVERPORT = 8000;
    PrintWriter out;
    Socket socket;
    InetAddress serverAddr;
    JSONObject credentials = new JSONObject();
    private UserInfoOperations userOperations;
    private UserInfo userInformation;

    Notification notification;
    private static final int NOTIFICATION_ID=1;
    private NotificationManager mgr;

    private final IBinder myBinder = new LocalBinder();

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
        //Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        // !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();
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
            credentials.putOpt("userId", userInformation.getUserId());
            credentials.putOpt("userType", userInformation.getUserType());
            credentials.putOpt("userName", userInformation.getUserName());
            credentials.putOpt("domainId", userInformation.getDomainId());
            credentials.putOpt("userSessionId", userInformation.getUserSessionId());
            credentials.putOpt("main_photo", userInformation.getMain_photo());
            credentials.putOpt("gender", userInformation.getGender());
            credentials.putOpt("userToken", userInformation.getUserToken());

            socket = IO.socket(SERVERIP+":"+SERVERPORT);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("HUGHES", "Go ONLINE: " +credentials.toString());
                    socket.emit("online", credentials);
                }
            }).on("onlineOk", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("HUGHES STARTVIEWING DATA", "onlineOk");
                    socket.emit("onlineChat", credentials); // show online in chat
                }
            }).on("onlineChatOk", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("HUGHES STARTVIEWING DATA", "onlineChatOk " + Arrays.toString(args));
                    //socket.emit("onlineChat", credentials); // show online in chat

                    notification = new Notification(R.drawable.ic_launcher, "Online", System.currentTimeMillis());
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    notification.setLatestEventInfo(AFPushService.this, "Reminder: Saanvi Birthday",
                            "Today is your friend Saanvi's Birthday, please wish her", pendingIntent);

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
            }).on("chat", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("HUGHES STARTVIEWING DATA", "chat " + Arrays.toString(args));
                    if(!AsianFinderApplication.isActivityVisible()) {
                        notification = new Notification(R.drawable.ic_launcher, "New Message", System.currentTimeMillis());

                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                        notification.setLatestEventInfo(AFPushService.this, "Message",
                                Arrays.toString(args), pendingIntent);

                        mgr.notify(NOTIFICATION_ID, notification);
                    } else {
                        // Do something else here
                    }
                }
            }).on("receiveSticker", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("HUGHES STARTVIEWING DATA", "chat " + Arrays.toString(args));
                    if(!AsianFinderApplication.isActivityVisible()) {
                        notification=new Notification(R.drawable.ic_launcher, "New Message", System.currentTimeMillis());

                        Intent intent=new Intent(getApplicationContext(), DashboardActivity.class);
                        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                        notification.setLatestEventInfo(AFPushService.this, "Sticker",
                                Arrays.toString(args), pendingIntent);

                        mgr.notify(NOTIFICATION_ID,notification);
                    } else {
                        // Do something else here
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

}
