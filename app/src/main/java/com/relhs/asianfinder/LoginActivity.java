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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class LoginActivity extends Activity implements View.OnClickListener{

    ProgressDialog progress;

    private CustomEditTextView etUsername;
    private CustomEditTextView etPassword;
    private String txtUsername;
    private String txtPassword;
    private UserInfoOperations userOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        Toast.makeText(this, "isLogin "+userOperations.isLogin(), Toast.LENGTH_LONG).show();

        etUsername = (CustomEditTextView)findViewById(R.id.etUsername);
        etPassword = (CustomEditTextView)findViewById(R.id.etPassword);

        CustomTextView signUp = (CustomTextView)findViewById(R.id.signUp);
        signUp.setOnClickListener(this);
        CustomButton btnLogin = (CustomButton)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        Notification notification=new Notification(R.drawable.ic_launcher,"Saanvi Birthday!", System.currentTimeMillis());
        Intent intent=new Intent(getApplicationContext(), DashboardActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        notification.setLatestEventInfo(getBaseContext(), "Reminder: Saanvi Birthday",
                "Today is your friend Saanvi's Birthday, please wish her", pendingIntent);

        if(userOperations.isLogin() == 1) {
            if(!isMyServiceRunning(AFPushService.class)) {
                startService(new Intent(LoginActivity.this, AFPushService.class));
            } else {
                Log.d("HUGHES", "Already Running");
            }
            Intent i = new Intent(this, DashboardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }



    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                txtUsername = etUsername.getText().toString();
                txtPassword = etPassword.getText().toString();
                if(!txtUsername.isEmpty()) {
                    progress = new ProgressDialog(LoginActivity.this);
                    progress.setCanceledOnTouchOutside(false);

                    new LoginTask(progress, txtUsername, txtPassword).execute();
                } else {
                    Toast.makeText(this, "Fill up form correctly", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.signUp:
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                stopService(new Intent(this, AFPushService.class));
                break;


        }
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


    private class LoginTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog progress;
        private String sUsername;
        private String sPassword;
        private JSONArray jsonArray;

        public LoginTask(ProgressDialog progress, String txtUsername, String txtPassword) {
            // TODO Auto-generated constructor stub
            this.progress = progress;
            this.sUsername = txtUsername;
            this.sPassword = txtPassword;
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            List<NameValuePair> paramsAPI = new ArrayList<NameValuePair>();
            paramsAPI.add(new BasicNameValuePair("act", "login"));
            paramsAPI.add(new BasicNameValuePair("did", ((AsianFinderApplication)getApplication()).getDeviceId()));
            paramsAPI.add(new BasicNameValuePair("user", sUsername));
            paramsAPI.add(new BasicNameValuePair("pass", sPassword));

            JSONParser jParser = new JSONParser();
            Log.d("HUGHES", paramsAPI.toString());

            return jParser.getJSONFromUrl(getString(R.string.api), paramsAPI);
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            progress.dismiss();
            Log.d("retResponse", jObj.toString());

            try {
                // Getting JSON Array from URL
                // Also Assume to return only one record
                jsonArray = jObj.getJSONArray(AsianFinderApplication.TAG_OS);
                JSONObject c = jsonArray.getJSONObject(0);

                Log.d("HUGHES LOGIN", c.toString());

                if(!c.getBoolean(AsianFinderApplication.TAG_STATUS)) {
                    Toast.makeText(getApplicationContext(), c.getString(AsianFinderApplication.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                } else {
                    //final
                    // Get Data inside the CamAppApplication.TAG_DATA
                    final JSONObject jsonArrayLoginDataObject = c.getJSONObject(AsianFinderApplication.TAG_DATA);

                    int userId = Integer.parseInt(jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_USERID));
                    String username = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_USERNAME);
                    String firstname = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_FIRSTNAME);
                    String gender = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_GENDER);
                    int country = jsonArrayLoginDataObject.getInt(DataBaseWrapper.USERINFO_COUNTRY_ID);
                    int state = jsonArrayLoginDataObject.getInt(DataBaseWrapper.USERINFO_STATE_ID);
                    int city = jsonArrayLoginDataObject.getInt(DataBaseWrapper.USERINFO_CITY_ID);
                    String email = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_EMAIL);
                    String membership_expiration = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRATION);
                    String membership_type = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_MEMBERSHIP_TYPE);
                    String main_photo = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_MAINPHOTO);
                    String user_type = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_TYPE);
                    int membership_expired = jsonArrayLoginDataObject.getInt(DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRED);
                    int validate = jsonArrayLoginDataObject.getInt(DataBaseWrapper.USERINFO_VALIDATE);
                    int domain_id = jsonArrayLoginDataObject.getInt(DataBaseWrapper.USERINFO_DOMAIN_ID);
                    String user_phone = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_USER_PHONE);
                    String session_id = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_SESSION_ID);
                    String user_token = jsonArrayLoginDataObject.getString(DataBaseWrapper.USERINFO_TOKEN);

                    JSONArray jsonArrayProfile = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PROFILE);
                    JSONObject jsonObjectProfile = jsonArrayProfile.getJSONObject(0);

                    String basic = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_BASIC).toString();
                    //Log.d("-- robert -- basic", basic);
                    String appearance = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_APPEARANCE).toString();
                    //Log.d("-- robert -- appearance", appearance);
                    String lifestyle = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_LIFESTYLE).toString();
                    //Log.d("-- robert -- lifestyle", lifestyle);
                    String culture_values = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_CULTURE_VALUES).toString();
                    //Log.d("-- robert -- culture_values", culture_values);
                    String personal = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_PERSONAL).toString();
                    //Log.d("-- robert -- personal", personal);
                    String interest = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_INTEREST).toString();
                    //Log.d("-- robert -- interest", interest);
                    String others = jsonObjectProfile.getJSONArray(DataBaseWrapper.USERINFO_JSON_OTHERS).toString();
                    //Log.d("-- robert -- others", others);

                    JSONArray jsonArrayPreferences = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PREFERENCES);
                    String preferences = jsonArrayPreferences.toString();
                    //Log.d("-- robert -- preferences", preferences);

                    JSONArray jsonArrayPhotos = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PHOTOS);
                    String photos = jsonArrayPhotos.toString();
                    //Log.d("-- robert -- photos", photos);

                    int isLogin = 1;
                    final UserInfo userInfo = userOperations.addUser(userId,username,firstname,gender,country,state,city,email,
                            membership_expiration,membership_type,main_photo,user_type,
                            membership_expired,validate,domain_id,user_phone,session_id,user_token,
                            basic, appearance, lifestyle, culture_values, personal, interest, others, preferences, photos, isLogin);
//                    userOperations.close();

                    if(user_type.equalsIgnoreCase("u")) {
                        // Start the service
                        startService(new Intent(LoginActivity.this, AFPushService.class));

                        Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        progress.dismiss();
                        LoginActivity.this.finish();
                        startActivity(i);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

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



}
