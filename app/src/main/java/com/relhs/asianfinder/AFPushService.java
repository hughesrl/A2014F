package com.relhs.asianfinder;

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


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Arrays;


public class AFPushService extends Service {
    public static final String SERVERIP = "http://119.81.103.164"; //your computer IP address should be written here
    public static final int SERVERPORT = 8000;
    PrintWriter out;
    Socket socket;
    InetAddress serverAddr;

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
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
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
        socket = IO.socket(SERVERIP+":"+SERVERPORT);
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject credentials = new JSONObject();
                    credentials.putOpt("userId", 27310);
                    credentials.putOpt("userType", "u");
                    credentials.putOpt("userName", "DjMike2312");
                    credentials.putOpt("domainId", 3);
                    credentials.putOpt("userSessionId", "c1g3t6");
                    credentials.putOpt("main_photo", "http://www.asianfinder.com//media/photos/30000/27310/1413385363_3e5658934f39f5e4d8ac9dbb4063a808.jpg");
                    credentials.putOpt("gender", "m");
                    credentials.putOpt("userToken", "32fca34402e197198460d74a60e3bcfc");

                    Log.d("HUGHES", "Go ONLINE: " +credentials.toString());

                    socket.emit("online", credentials);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("onlineOk", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(Arrays.toString(args).equalsIgnoreCase("true")) {
                    Log.d("HUGHES STARTVIEWING DATA", "onlineOkay");
//                    socket.emit("onlineChat", credentials); // show online in chat
                }

            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
            }

        });
        socket.connect();
    }

}
