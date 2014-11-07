package com.relhs.asianfinder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.relhs.asianfinder.AFPushService;

public class SocketReceiver extends BroadcastReceiver {
    public SocketReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AFPushService.class);
        context.startService(service);
    }
}
