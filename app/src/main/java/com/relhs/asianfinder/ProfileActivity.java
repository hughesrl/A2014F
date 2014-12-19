package com.relhs.asianfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.adapter.ProfileUserDetailsAdapter;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.data.UserDetailsInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.fragment.ProfileAboutFragment;
import com.relhs.asianfinder.fragment.ProfileFragment;
import com.relhs.asianfinder.fragment.ProfileGalleryFragment;
import com.relhs.asianfinder.fragment.ProfilePhotoEditDialogFragment;
import com.relhs.asianfinder.fragment.ProfilePreferenceEditDialogFragment;
import com.relhs.asianfinder.fragment.ProfilePreferenceFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PhotosInfoOperations;
import com.relhs.asianfinder.operation.UserDetailsInfoOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.java_websocket.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ProfileActivity extends FragmentActivity implements View.OnClickListener {
//    private ImageLoader imageLoader;
//    private UserInfoOperations userOperations;
    private UserInfo userInfo;

    private PeoplePhotosInfo peoplePhotosInfo;

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
//    private PhotosInfoOperations photosInfoOperations;

    UpdateListListener mListener;

    public interface UpdateListListener {
        public void onUpdateListClick(Activity activity, String action);
    }

    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_PROFILE = "arg_profile";
    public static final String ARG_PHOTOS = "arg_photos";

    private ImageLoader imageLoader;
//    private View myFragmentView;

    private String mParamProfile;
    private String mParamPhotos;

    private UserInfoOperations userOperations;
    private PhotosInfoOperations photosInfoOperations;
    private UserDetailsInfoOperations userDetailsInfoOperations;

    ArrayList<UserDetailsInfo> items = new ArrayList<UserDetailsInfo>();
    private ProfileUserDetailsAdapter adapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_about);


        // TODO: !IMPORTANT DATABASE OPERATION
        userOperations = new UserInfoOperations(this);
        userOperations.open();

        userDetailsInfoOperations = new UserDetailsInfoOperations(this);
        userDetailsInfoOperations.open();

        photosInfoOperations = new PhotosInfoOperations(this);
        photosInfoOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        imageLoader = new ImageLoader(this);

        mListView = (ListView) findViewById(R.id.listViewPrefs);

        peoplePhotosInfo = photosInfoOperations.getLastPhoto();
        userInfo = userOperations.getUser();



        new LoadUserDetailsDataTask(null).execute();



        TextView upTextView = (TextView) getLayoutInflater().inflate(R.layout.chat_name, null);
        getActionBar().setIcon(AsianFinderApplication.getTextAsBitmap(ProfileActivity.this, upTextView, userInfo.getUsername()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.editAppearance:
//                Toast.makeText(getActivity(), "Edit Appearance", Toast.LENGTH_LONG).show();
//                break;
            case R.id.galleryLayoutPhotos:
                Intent intentActivity = new Intent(this, GalleryActivity.class);
                startActivity(intentActivity);
                break;

        }
    }


    private class LoadUserDetailsDataTask extends AsyncTask<Void, Void, ArrayList<UserDetailsInfo>> {
        private ProgressDialog mProgressDialog;
        private LayoutInflater _inflater;

        public LoadUserDetailsDataTask(LayoutInflater inflater) {
            // TODO Auto-generated constructor stub
            this._inflater = inflater;
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
        protected ArrayList<UserDetailsInfo> doInBackground(Void... args) {
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_BASIC) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_BASIC));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_BASIC));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_APPEARANCE) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_APPEARANCE));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_APPEARANCE));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_LIFESTYLE) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_LIFESTYLE));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_LIFESTYLE));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_CULTURE_VALUES) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_CULTURE_VALUES));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_CULTURE_VALUES));
            }
            if(userDetailsInfoOperations.getUserDetailsCountByCategory(Constants.TAG_PERSONAL) > 0) {
                items.add(new UserDetailsInfo(Constants.TAG_PERSONAL));
                parsePreferenceInfoAsArray(userDetailsInfoOperations.getAllUserDetailsByCategory(Constants.TAG_PERSONAL));
            }
            return items;
        }
        @Override
        protected void onPostExecute(final ArrayList<UserDetailsInfo> userDetailsInfoArrayList) {
            if(!userDetailsInfoArrayList.isEmpty()) {
                adapter = new ProfileUserDetailsAdapter(ProfileActivity.this, userDetailsInfoArrayList);

                View header = View.inflate(ProfileActivity.this, R.layout.view_profile_header, null);
                mListView.addHeaderView(header);


                ImageView myPhotosThumb = (ImageView) header.findViewById(R.id.myPhotosThumb);
                imageLoader.DisplayImage(peoplePhotosInfo.getFile(), myPhotosThumb);

                LinearLayout galleryLayoutPhotos = (LinearLayout)header.findViewById(R.id.galleryLayoutPhotos);
                galleryLayoutPhotos.setOnClickListener(ProfileActivity.this);


                ImageView header_imageview = (ImageView) header.findViewById(R.id.header_imageview);
                ImageView photoMain = (ImageView) header.findViewById(R.id.photoMain);

                TextView txtUsername = (TextView) header.findViewById(R.id.txtUsername);

                ImageButton startChatting = (ImageButton) header.findViewById(R.id.startChatting);
                imageLoader.DisplayImage(userInfo.getMain_photo(), header_imageview);
                imageLoader.DisplayImageRounded(userInfo.getMain_photo(), photoMain, 150, 150);

                txtUsername.setText(userInfo.getUsername());

                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserDetailsInfo item = (UserDetailsInfo)items.get(position);

                        if(!item.getDbname().equalsIgnoreCase("gender")) {
                            Bundle arg = new Bundle();
                            arg.putString(ProfilePreferenceEditDialogFragment.ARG_DBNAME, item.getDbname());
                            arg.putString(ProfilePreferenceEditDialogFragment.ARG_LABEL, item.getLabel());
                            arg.putString(ProfilePreferenceEditDialogFragment.ARG_TYPE, item.getType());

                            FragmentManager fm = getSupportFragmentManager();
                            ProfilePreferenceEditDialogFragment editNameDialog = new ProfilePreferenceEditDialogFragment();
                            editNameDialog.setArguments(arg);
                            editNameDialog.show(fm, "fragment_edit_name");
                        } else if(item.getDbname().equalsIgnoreCase("email")) {
                            // Redirect to Setting to change Email
                        }
                    }
                });
            } else {
                //Toast.makeText(getActivity(), "No More data", Toast.LENGTH_LONG).show();
//                noPhoto.setVisibility(View.VISIBLE);
            }

            mProgressDialog.dismiss();
        }
    }

    private void parsePreferenceInfoAsArray(Cursor cursor) {
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            items.add(new UserDetailsInfo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_DBNAME)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_LABEL)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_VALUE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_IDS))));
        }
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        //int[] resId = new int[]{R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer, R.drawable.ic_launcher, R.drawable.ic_drawer};

        private final String[] TITLES = { "About", "Preference" };
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
            args.putString(Constants.ARG_PHOTOS, userInfo.getPhotos());
            args.putString(Constants.ARG_PREFERENCE, userInfo.getPreference());

            switch(position){
                case 0: // About
                    ProfileAboutFragment peopleAboutFragment = new ProfileAboutFragment();
                    peopleAboutFragment.setArguments(args);
                    return peopleAboutFragment;
//                case 1: // Patient Information
//                    ProfileGalleryFragment profileGalleryFragment = new ProfileGalleryFragment();
//                    profileGalleryFragment.setArguments(args);
//
//                    return profileGalleryFragment;
                case 1: // Patient Information
                    ProfilePreferenceFragment profilePreferenceFragment = new ProfilePreferenceFragment();
                    profilePreferenceFragment.setArguments(args);
                    return profilePreferenceFragment;
            }
            return null;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.profile_menu, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            this.finish();
//        }
//        if(id == R.id.action_upload_photo) {
//            selectImage();
//        }
//        return super.onOptionsItemSelected(item);
//    }

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
        private int status;


        public SavePhoto(String api_url, ArrayList<NameValuePair> nameValuePairs) {
            // TODO Auto-generated constructor stub
            this.api_url = api_url;
            this.nameValuePairs = nameValuePairs;
            jParser = new JSONParser();
            this.status = 0;
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
                status = 0;
            } finally {
                status = 1;
            }

            return status;
        }
        @Override
        protected void onPostExecute(Integer status) {
            if(status == 1) {
                if(mListener != null)
                    mListener.onUpdateListClick(ProfileActivity.this, "reload");

                mProgressDialog.dismiss();
            }
        }
    }






}

