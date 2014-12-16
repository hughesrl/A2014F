package com.relhs.asianfinder;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import com.relhs.asianfinder.adapter.PeopleListAdapter;
import com.relhs.asianfinder.data.PeopleInfo;
import com.relhs.asianfinder.data.UserInfo;

import com.relhs.asianfinder.fragment.BrowseFragment;
import com.relhs.asianfinder.fragment.PeopleAboutFragment;
import com.relhs.asianfinder.fragment.ProfileAboutFragment;
import com.relhs.asianfinder.fragment.ProfileGalleryFragment;
import com.relhs.asianfinder.fragment.ProfilePhotoEditDialogFragment;
import com.relhs.asianfinder.fragment.ProfilePreferenceEditDialogFragment;
import com.relhs.asianfinder.fragment.ProfilePreferenceFragment;
import com.relhs.asianfinder.fragment.SampleListFragment;
import com.relhs.asianfinder.fragment.ScrollTabHolder;
import com.relhs.asianfinder.fragment.ScrollTabHolderFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PhotosInfoOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;
import com.relhs.asianfinder.view.CustomButton;
import com.relhs.asianfinder.view.CustomEditTextView;
import com.relhs.asianfinder.view.CustomTextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.java_websocket.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends FragmentActivity {
    private ImageLoader imageLoader;
    private UserInfoOperations userOperations;
    private UserInfo userInfo;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;

    private JSONObject jsonObjectProfile;
    private JSONObject jsonObjectPhotos;

    public static IAFPushService mIAFPushService;
    private boolean mBound;
    private MenuItem menuSettings;

    private final int CAMERA_CAPTURE = 1;
    private final int UPLOAD_PHOTO = 2;
    private byte[] bytearray;

    InputStream inputStream;
    private PhotosInfoOperations photosInfoOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();
        userInfo = userOperations.getUser();

        photosInfoOperations = new PhotosInfoOperations(this);
        photosInfoOperations.open();

        // TODO: !IMPORTANT DATABASE OPERATION

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setOffscreenPageLimit(2);
        tabs.setViewPager(pager);
    }




    public class MyPagerAdapter extends FragmentPagerAdapter {
        //int[] resId = new int[]{R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer};

        private final String[] TITLES = { "About", "Gallery", "Preference" };
        private ActionBar mActionBar;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
        @Override
        public int getCount() {
            return TITLES.length;
        }
        @Override
        public Fragment getItem(int position) {
            Fragment f = new Fragment();
            Bundle args = new Bundle();

            args.putString(Constants.ARG_PROFILE, userInfo.getBasic());
//            args.putString(Constants.ARG_MATCHES, jsonObjectMatches.toString());
            args.putString(Constants.ARG_PHOTOS, userInfo.getPhotos());
            args.putString(Constants.ARG_PREFERENCE, userInfo.getPreference());

            switch(position){
                case 0: // About
                    ProfileAboutFragment peopleAboutFragment = new ProfileAboutFragment();
                    peopleAboutFragment.setArguments(args);
                    return peopleAboutFragment;
                case 1: // Patient Information
                    ProfileGalleryFragment profileGalleryFragment = new ProfileGalleryFragment();
                    profileGalleryFragment.setArguments(args);
                    return profileGalleryFragment;
                case 2: // Patient Information
                    ProfilePreferenceFragment profilePreferenceFragment = new ProfilePreferenceFragment();
                    profilePreferenceFragment.setArguments(args);
                    return profilePreferenceFragment;
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        if(id == R.id.action_upload_photo) {
            selectImage();
        }
        return super.onOptionsItemSelected(item);
    }

    public String getDeviceId() {
        return ((AsianFinderApplication) getApplication()).getDeviceId();
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
                        Toast toast = Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT);
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

//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        FragmentUtils.startActivityForResultWhileSavingOrigin(this, requestCode, intent, null);
//    }
//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                final Bitmap thePic = data.getExtras().getParcelable("data");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bytearray = stream.toByteArray();// get byte array here
                Log.d("ROBERT bytearray", bytearray.length+"");
                if (bytearray != null) {
                    String image_str = Base64.encodeBytes(bytearray);
                    final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

                    nameValuePairs.add(new BasicNameValuePair("act", "photo-image"));
                    nameValuePairs.add(new BasicNameValuePair("did", getDeviceId()));
                    nameValuePairs.add(new BasicNameValuePair("t", "upload"));
                    nameValuePairs.add(new BasicNameValuePair("img", image_str));

                    new SavePhoto(getResources().getString(R.string.api), nameValuePairs).execute();
                }
            } else if (requestCode == UPLOAD_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    final Bitmap thumbnail = decodeUri(selectedImage);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bytearray = stream.toByteArray();// get byte array here
                    if (bytearray != null) {
                        Log.w("path of image from gallery......******************.........", selectedImage + "");

                        if (bytearray != null) {
//                            final ParseFile photoFile = new ParseFile(myChild.getFirtname()+"_photo.jpg", bytearray);
//                            photoFile.saveInBackground(new SaveCallback() {
//                                public void done(ParseException e) {
//                                    if (e != null) {
//                                        Toast.makeText(getActivity(),
//                                                "Error saving: " + e.getMessage(),
//                                                Toast.LENGTH_LONG).show();
//                                    } else {
//                                        patients.setPhotoFile(photoFile);
//                                        imgPatientPhoto.setBackground(Utils.resizedBitmapDisplay(getActivity(), thumbnail));
//                                    }
//                                }
//                            });
                        }

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ProfileActivity.this.getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                ProfileActivity.this.getContentResolver().openInputStream(selectedImage), null, o2);
    }


    public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{
        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();
        final int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ProfileActivity.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
            }
        });

        if (contentLength < 0){
        }
        else{
            byte[] data = new byte[512];
            int len = 0;
            try
            {
                while (-1 != (len = inputStream.read(data)) )
                {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close(); // closing the stream…..
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..

            final String finalRes = res;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(ProfileActivity.this, "Result : " + finalRes, Toast.LENGTH_LONG).show();
                }
            });
            //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
    }


    private class SavePhoto extends AsyncTask<Void, Void, Integer> {
        private String api_url;
        private ProgressDialog mProgressDialog;
        private ArrayList<NameValuePair> nameValuePairs;
        JSONParser jParser;


        public SavePhoto(String api_url, ArrayList<NameValuePair> nameValuePairs) {
            // TODO Auto-generated constructor stub
            this.api_url = api_url;
            this.nameValuePairs = nameValuePairs;
            jParser = new JSONParser();
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(ProfileActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected Integer doInBackground(Void... args) {
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(api_url, nameValuePairs);
            try {
                JSONArray android = json.getJSONArray(Constants.TAG_OS);
                Log.d("-- robert", android.toString());
                JSONObject jsonObject = android.getJSONObject(0);
                if(!jsonObject.getBoolean(Constants.TAG_STATUS)) { // false
                    Log.d("-- robert", jsonObject.getString(Constants.TAG_MESSAGE));
                } else { // true
                    JSONObject jsonObjectData = jsonObject.getJSONObject(Constants.TAG_DATA);
                    Bundle arg = new Bundle();
                    arg.putString(ProfilePhotoEditDialogFragment.ARG_CATEGORIES, jsonObjectData.getJSONArray("categories").toString());
                    arg.putString(ProfilePhotoEditDialogFragment.ARG_PHOTOURL, jsonObjectData.getString("photo_url"));

                    // Add the Data to Database
                    photosInfoOperations.addPhoto(jsonObjectData.getString("photo_url"), "general", "0");


                    FragmentManager fm = getSupportFragmentManager();
                    ProfilePhotoEditDialogFragment profilePhotoEditDialogFragment = new ProfilePhotoEditDialogFragment();
                    profilePhotoEditDialogFragment.setArguments(arg);
                    profilePhotoEditDialogFragment.show(fm, "ProfilePhotoEditDialogFragment");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
        }
    }




}

