package com.relhs.asianfinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.operation.EmoticonsOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class IndexActivity extends Activity {

    UserInfoOperations userOperations;
    EmoticonsOperations emoticonsOperations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("-- ROB DEVICE", ((AsianFinderApplication)getApplication()).getDeviceId());
        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();

        emoticonsOperations = new EmoticonsOperations(this);
        emoticonsOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION
        int emoticonsCount = emoticonsOperations.getEmoticonsCount();
        if(emoticonsCount == 0) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jObjectSmileys = jsonParser.loadJSONFromAsset(this, "smiley.json");
            try {
                JSONArray jArraySmileys = jObjectSmileys.getJSONArray("smileys");

                for (int sjo=0; sjo < jArraySmileys.length(); sjo++){
                    JSONObject jsonObject = jArraySmileys.getJSONObject(sjo);
                    emoticonsOperations.addEmoticons(jsonObject.getString("f"), jsonObject.getString("s"));
                    Log.d("ROBERT INSERT", jsonObject.toString());

                }
                emoticonsOperations.close();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                isLoginDetails();
            }
        } else {
            isLoginDetails();
        }



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void isLoginDetails() {
        int profileCount = userOperations.getUserCount();
        if(profileCount > 1) {
            userOperations.emptyUser();

            Intent i = new Intent(getApplication(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else if(profileCount == 1) {
            if(userOperations.isLogin() == 1) {
                if(!isMyServiceRunning(AFPushService.class)) {
                    startService(new Intent(IndexActivity.this, AFPushService.class));
                } else {
                    Log.d("HUGHES", "Already Running");
                }

                Intent i = new Intent(this, DashboardActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            Intent i = new Intent(getApplication(), DashboardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            Intent i = new Intent(getApplication(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        finish();
    }
}
