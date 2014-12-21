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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PhotosInfoOperations;
import com.relhs.asianfinder.operation.PreferenceInfoOperations;
import com.relhs.asianfinder.operation.UserDetailsInfoOperations;
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
    private CustomEditTextView etUsername;
    private CustomEditTextView etPassword;
    private String txtUsername;
    private String txtPassword;
    private UserInfoOperations userOperations;
    private UserDetailsInfoOperations userDetailsInfoOperations;
    private PreferenceInfoOperations preferenceInfoOperations;
    private PhotosInfoOperations photoInfoOperations;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();

        userDetailsInfoOperations = new UserDetailsInfoOperations(this);
        userDetailsInfoOperations.open();

        preferenceInfoOperations = new PreferenceInfoOperations(this);
        preferenceInfoOperations.open();

        photoInfoOperations = new PhotosInfoOperations(this);
        photoInfoOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        //Toast.makeText(this, "isLogin "+userOperations.isLogin(), Toast.LENGTH_LONG).show();

        etUsername = (CustomEditTextView)findViewById(R.id.etUsername);
        etPassword = (CustomEditTextView)findViewById(R.id.etPassword);

        CustomTextView signUp = (CustomTextView)findViewById(R.id.signUp);
        signUp.setOnClickListener(this);
        CustomButton btnLogin = (CustomButton)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        Notification notification=new Notification(R.drawable.ic_launcher,"Welcome to AsianFinder", System.currentTimeMillis());
        Intent intent=new Intent(getApplicationContext(), IndexActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        notification.setLatestEventInfo(getBaseContext(), "Welcome to AsianFinder",
                "Welcome to AsianFinder!", pendingIntent);

        if(userOperations.isLogin() == 1) {
            if(!isMyServiceRunning(AFPushService.class)) {
                startService(new Intent(LoginActivity.this, AFPushService.class));
            } else {
                Log.d("HUGHES", "Already Running");
            }
            Intent i = new Intent(this, IndexActivity.class);
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
                    new LoginTask(txtUsername, txtPassword).execute();
                } else {
                    Toast.makeText(this, "Fill up form correctly", Toast.LENGTH_LONG).show();
                }

                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        private ProgressDialog mProgressDialog;

        private String sUsername;
        private String sPassword;
        private JSONArray jsonArray;

        public LoginTask(String txtUsername, String txtPassword) {
            // TODO Auto-generated constructor stub
            this.sUsername = txtUsername;
            this.sPassword = txtPassword;
        }

        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(LoginActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            userOperations.emptyAllUserData();
            List<NameValuePair> paramsAPI = new ArrayList<NameValuePair>();
            paramsAPI.add(new BasicNameValuePair("act", "login"));
            paramsAPI.add(new BasicNameValuePair("did", ((AsianFinderApplication)getApplication()).getDeviceId()));
            paramsAPI.add(new BasicNameValuePair("user", sUsername));
            paramsAPI.add(new BasicNameValuePair("pass", sPassword));

            JSONParser jParser = new JSONParser();
            return jParser.getJSONFromUrl(getString(R.string.api), paramsAPI);
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            String personal = "";
            try {
                // Getting JSON Array from URL
                // Also Assume to return only one record
                jsonArray = jObj.getJSONArray(AsianFinderApplication.TAG_OS);
                JSONObject c = jsonArray.getJSONObject(0);

                if(!c.getBoolean(AsianFinderApplication.TAG_STATUS)) {
                    mProgressDialog.dismiss();
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


                    /********USER DETAILS********/
                    JSONArray jsonArrayProfile = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PROFILE);
                    JSONObject jsonObjectProfile = jsonArrayProfile.getJSONObject(0);

                    if(jsonObjectProfile.has(Constants.TAG_BASIC)) {
                        JSONArray jsonArrayPrefBasic = jsonObjectProfile.getJSONArray(Constants.TAG_BASIC);
                        saveUserDetailsToDatabase(Constants.TAG_BASIC, jsonArrayPrefBasic);
                    }
                    if(jsonObjectProfile.has(Constants.TAG_APPEARANCE)) {
                        JSONArray jsonArrayPrefAppearance = jsonObjectProfile.getJSONArray(Constants.TAG_APPEARANCE);
                        saveUserDetailsToDatabase(Constants.TAG_APPEARANCE, jsonArrayPrefAppearance);
                    }
                    if(jsonObjectProfile.has(Constants.TAG_LIFESTYLE)) {
                        JSONArray jsonArrayPrefLifestyle = jsonObjectProfile.getJSONArray(Constants.TAG_LIFESTYLE);
                        saveUserDetailsToDatabase(Constants.TAG_LIFESTYLE, jsonArrayPrefLifestyle);
                    }
                    if(jsonObjectProfile.has(Constants.TAG_CULTURE_VALUES)) {
                        JSONArray jsonArrayPrefCultureValues = jsonObjectProfile.getJSONArray(Constants.TAG_CULTURE_VALUES);
                        saveUserDetailsToDatabase(Constants.TAG_CULTURE_VALUES, jsonArrayPrefCultureValues);
                    }
                    if(jsonObjectProfile.has(Constants.TAG_PERSONAL)) {
                        JSONArray jsonArrayPrefPersonal = jsonObjectProfile.getJSONArray(Constants.TAG_PERSONAL);
                        saveUserDetailsToDatabase(Constants.TAG_PERSONAL, jsonArrayPrefPersonal);
                    }
                    if(jsonObjectProfile.has(Constants.TAG_INTEREST)) {
                        JSONArray jsonArrayPrefInterest = jsonObjectProfile.getJSONArray(Constants.TAG_INTEREST);
                        saveUserDetailsToDatabase(Constants.TAG_INTEREST, jsonArrayPrefInterest);
                    }
                    if(jsonObjectProfile.has(Constants.TAG_OTHERS)) {
                        JSONArray jsonArrayPrefOthers = jsonObjectProfile.getJSONArray(Constants.TAG_OTHERS);
                        saveUserDetailsToDatabase(Constants.TAG_OTHERS, jsonArrayPrefOthers);
                    }

                    /********PREFERENCE********/
                    JSONArray jsonArrayPreferences = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PREFERENCES);
                    JSONObject jsonObjectPreference = jsonArrayPreferences.getJSONObject(0);

                    if(jsonObjectPreference.has(Constants.TAG_BASIC)) {
                        JSONArray jsonArrayPrefBasic = jsonObjectPreference.getJSONArray(Constants.TAG_BASIC);
                        savePreferenceToDatabase(Constants.TAG_BASIC, jsonArrayPrefBasic);
                    }
                    if(jsonObjectPreference.has(Constants.TAG_APPEARANCE)) {
                        JSONArray jsonArrayPrefAppearance = jsonObjectPreference.getJSONArray(Constants.TAG_APPEARANCE);
                        savePreferenceToDatabase(Constants.TAG_APPEARANCE, jsonArrayPrefAppearance);
                    }
                    if(jsonObjectPreference.has(Constants.TAG_LIFESTYLE)) {
                        JSONArray jsonArrayPrefLifestyle = jsonObjectPreference.getJSONArray(Constants.TAG_LIFESTYLE);
                        savePreferenceToDatabase(Constants.TAG_LIFESTYLE, jsonArrayPrefLifestyle);
                    }
                    if(jsonObjectPreference.has(Constants.TAG_CULTURE_VALUES)) {
                        JSONArray jsonArrayPrefCultureValues = jsonObjectPreference.getJSONArray(Constants.TAG_CULTURE_VALUES);
                        savePreferenceToDatabase(Constants.TAG_CULTURE_VALUES, jsonArrayPrefCultureValues);
                    }
                    if(jsonObjectPreference.has(Constants.TAG_PERSONAL)) {
                        JSONArray jsonArrayPrefPersonal = jsonObjectPreference.getJSONArray(Constants.TAG_PERSONAL);
                        savePreferenceToDatabase(Constants.TAG_PERSONAL, jsonArrayPrefPersonal);
                    }

                    String preferences = jsonArrayPreferences.toString();
                    /********PREFERENCE********/

                    JSONArray jsonArrayPhotos = jsonArrayLoginDataObject.getJSONArray(Constants.TAG_PHOTOS);
                    String photos = jsonArrayPhotos.toString();
                    savePhotosToDatabase(jsonArrayPhotos);
                    //Log.d("-- robert -- photos", photos);

                    int isLogin = 1;
                    final UserInfo userInfo = userOperations.addUser(userId,username,firstname,gender,country,state,city,email,
                            membership_expiration,membership_type,main_photo,user_type,
                            membership_expired,validate,domain_id,user_phone,session_id,user_token,
                            "", "", "", "", "", "", "", preferences, photos, isLogin);
//                    userOperations.close();

                    if(user_type.equalsIgnoreCase("u")) {
                        // Start the service
                        startService(new Intent(LoginActivity.this, AFPushService.class));

                        Intent i = new Intent(LoginActivity.this, IndexActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mProgressDialog.dismiss();
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

    private void saveUserDetailsToDatabase(String category, JSONArray jsonArray) {
        try {
            for (int i=0; i<jsonArray.length()-1; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String value = jsonObject.getString("value");
                if (jsonObject.getString("label").equalsIgnoreCase("location")) {
                    if(!value.isEmpty()) {
                        JSONArray jsonArrayPrefBasicLivingIn = new JSONArray(value);
                        JSONObject jsonObjectPrefBasicLivingIn = jsonArrayPrefBasicLivingIn.getJSONObject(0);
                        value = jsonObjectPrefBasicLivingIn.getString("city") + ", " +
                                jsonObjectPrefBasicLivingIn.getString("state") + ", " + jsonObjectPrefBasicLivingIn.getString("country");
                    }
                }
                userDetailsInfoOperations.addUserDetails(category, jsonObject.getString("dbname"), jsonObject.getString("label"), jsonObject.getString("type"),
                        value, jsonObject.getString("ids"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void savePreferenceToDatabase(String category, JSONArray jsonArray) {
        try {
            for (int i=0; i<jsonArray.length()-1; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String value = jsonObject.getString("value");
                if (jsonObject.getString("dbname").equalsIgnoreCase("living_in")) {
                    if(!value.isEmpty()) {
                        JSONArray jsonArrayPrefBasicLivingIn = new JSONArray(value);
                        JSONObject jsonObjectPrefBasicLivingIn = jsonArrayPrefBasicLivingIn.getJSONObject(0);
                        value = jsonObjectPrefBasicLivingIn.getString("city") + ", " +
                                jsonObjectPrefBasicLivingIn.getString("state") + ", " + jsonObjectPrefBasicLivingIn.getString("country");
                        if (jsonObjectPrefBasicLivingIn.has("length")) {
                            value = jsonObjectPrefBasicLivingIn.getString("length") + " within " + value;
                        }
                    }
                }
                preferenceInfoOperations.addUserPreference(category, jsonObject.getString("dbname"), jsonObject.getString("label"), jsonObject.getString("type"),
                        value, jsonObject.getString("ids"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void savePhotosToDatabase(JSONArray jsonArray) {
        try {
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String file = jsonObject.getString("file");
                String category = jsonObject.getString("category");
                String number_of_comments = jsonObject.getString("number_of_comments");

                photoInfoOperations.addPhoto(file, category, number_of_comments);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
