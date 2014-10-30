package com.relhs.asianfinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;
import com.relhs.asianfinder.view.CustomEditTextView;
import com.relhs.asianfinder.view.CustomTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity implements View.OnClickListener{
    private UserInfoOperations userOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        Toast.makeText(this, "isLogin " + userOperations.isLogin(), Toast.LENGTH_LONG).show();


    }
    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btnLogin:
//                txtUsername = etUsername.getText().toString();
//                txtPassword = etPassword.getText().toString();
//                if(!txtUsername.isEmpty()) {
//                    progress = new ProgressDialog(LoginActivity.this);
//                    progress.setCanceledOnTouchOutside(false);
//
//                    new LoginTask(progress, txtUsername, txtPassword).execute();
//                } else {
//                    Toast.makeText(this, "Fill up form correctly", Toast.LENGTH_LONG).show();
//                }
//
//                break;
//            case R.id.signUp:
//                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(i);
//                stopService(new Intent(this, AFPushService.class));
//                break;
//
//
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
