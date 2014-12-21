package com.relhs.asianfinder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.relhs.asianfinder.adapter.PeoplePhotosGridAdapter;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.fragment.HomeFragment;
import com.relhs.asianfinder.fragment.ProfilePhotoEditDialogFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PhotosInfoOperations;
import com.relhs.asianfinder.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.java_websocket.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class GalleryActivity extends FragmentActivity {

    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_PROFILE = "arg_profile";
    public static final String ARG_PHOTOS = "arg_photos";

    private final int CAMERA_CAPTURE = 1;
    private final int UPLOAD_PHOTO = 2;
    private byte[] bytearray;


    private ImageLoader imageLoader;

    private String mParamProfile;
    private String mParamMatches;
    private String mParamPhotos;

    private ArrayList<PeoplePhotosInfo> peoplePhotosArrayList = new ArrayList<PeoplePhotosInfo>();
    private AsymmetricGridView mListView;
    private PeoplePhotosGridAdapter adapter;
    private int currentOffset;
    private LinearLayout noPhoto;

    InputStream inputStream;
    private PhotosInfoOperations photosInfoOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_profile_gallery);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // TODO: !IMPORTANT DATABASE OPERATION
        photosInfoOperations = new PhotosInfoOperations(this);
        photosInfoOperations.open();
        // TODO: !IMPORTANT DATABASE OPERATION

        Bundle intentExtra = getIntent().getExtras();
//        mParamPhotos = intentExtra.getString(Constants.ARG_PHOTOS);
        imageLoader = new ImageLoader(this);

        mListView = (AsymmetricGridView) findViewById(R.id.listView);
        mListView.setRequestedColumnCount(2);

        noPhoto = (LinearLayout) findViewById(R.id.noPhoto);

        new LoadPhotosDataTask().execute();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                GalleryActivity.this.finish();
                break;
            case R.id.action_upload_photo:
                selectImage();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    private class LoadPhotosDataTask extends AsyncTask<Void, Void, ArrayList<PeoplePhotosInfo>> {
        private ProgressDialog mProgressDialog;

        public LoadPhotosDataTask() {
            // TODO Auto-generated constructor stub
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(GalleryActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected ArrayList<PeoplePhotosInfo> doInBackground(Void... args) {
            if(photosInfoOperations.getPhotoCount() > 0) {
                peoplePhotosArrayList = photosInfoOperations.getAllPhotos();
            }
            return peoplePhotosArrayList;
        }
        @Override
        protected void onPostExecute(final ArrayList<PeoplePhotosInfo> peoplePhotosArrayList) {
            if(!peoplePhotosArrayList.isEmpty()) {
                adapter = new PeoplePhotosGridAdapter(GalleryActivity.this, mListView, new ArrayList<PeoplePhotosInfo>());
                adapter.setItems(peoplePhotosArrayList);

                mListView.setAdapter(adapter);
                mListView.setDebugging(false);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PeoplePhotosInfo peoplePhotosInfo = (PeoplePhotosInfo) parent.getItemAtPosition(position);

                        Intent intentDetails = new Intent(GalleryActivity.this, GallerySlideshowActivity.class);

                        intentDetails.putExtra(GallerySlideshowActivity.INTENT_POSITION, position);
                        intentDetails.putParcelableArrayListExtra(GallerySlideshowActivity.INTENT_PHOTOS, peoplePhotosArrayList);

                        startActivity(intentDetails);
                    }
                });
            } else {
                noPhoto.setVisibility(View.VISIBLE);
            }

            mProgressDialog.dismiss();
        }
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
                        Toast toast = Toast.makeText(GalleryActivity.this, errorMessage, Toast.LENGTH_SHORT);
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
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                final Bitmap thePic = data.getExtras().getParcelable("data");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bytearray = stream.toByteArray();// get byte array here
                Log.d("ROBERT bytearray", bytearray.length + "");
                if (bytearray != null) {
                    String image_str = Base64.encodeBytes(bytearray);
                    final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

                    nameValuePairs.add(new BasicNameValuePair("act", "photo-image"));
                    nameValuePairs.add(new BasicNameValuePair("did", ((AsianFinderApplication) getApplication()).getDeviceId()));
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
                        String image_str = Base64.encodeBytes(bytearray);
                        final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

                        nameValuePairs.add(new BasicNameValuePair("act", "photo-image"));
                        nameValuePairs.add(new BasicNameValuePair("did", ((AsianFinderApplication) getApplication()).getDeviceId()));
                        nameValuePairs.add(new BasicNameValuePair("t", "upload"));
                        nameValuePairs.add(new BasicNameValuePair("img", image_str));

                        new SavePhoto(getResources().getString(R.string.api), nameValuePairs).execute();
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
        BitmapFactory.decodeStream(GalleryActivity.this.getContentResolver().openInputStream(selectedImage), null, o);

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
                GalleryActivity.this.getContentResolver().openInputStream(selectedImage), null, o2);
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
                mProgressDialog = Utils.createProgressDialog(GalleryActivity.this);
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
                mProgressDialog.dismiss();
                new LoadPhotosDataTask().execute();
            }
        }
    }



}
