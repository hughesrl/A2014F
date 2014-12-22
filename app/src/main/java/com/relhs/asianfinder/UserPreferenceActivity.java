package com.relhs.asianfinder;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.relhs.asianfinder.adapter.ProfilePreferenceAdapter;
import com.relhs.asianfinder.data.PreferenceInfo;
import com.relhs.asianfinder.fragment.ProfilePreferenceEditDialogFragment;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.PreferenceInfoOperations;

import java.util.ArrayList;

public class UserPreferenceActivity extends FragmentActivity {

    ArrayList<PreferenceInfo> items = new ArrayList<PreferenceInfo>();
    private ListView mListView;
    private ProfilePreferenceAdapter adapter;
    private LinearLayout noPhoto;

    private PreferenceInfoOperations preferenceInfoOperations;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_pref);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageLoader = new ImageLoader(UserPreferenceActivity.this);

        preferenceInfoOperations = new PreferenceInfoOperations(UserPreferenceActivity.this);
        preferenceInfoOperations.open();

        mListView = (ListView) findViewById(R.id.listViewPrefs);

        noPhoto = (LinearLayout) findViewById(R.id.noPhoto);

        new LoadPhotosDataTask().execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                UserPreferenceActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadPhotosDataTask extends AsyncTask<Void, Void, ArrayList<PreferenceInfo>> {
        private ProgressDialog mProgressDialog;
        //private LayoutInflater _inflater;

        public LoadPhotosDataTask() {
            // TODO Auto-generated constructor stub
        }
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = Utils.createProgressDialog(UserPreferenceActivity.this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
        @Override
        protected ArrayList<PreferenceInfo> doInBackground(Void... args) {
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_BASIC) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_BASIC));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_BASIC));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_APPEARANCE) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_APPEARANCE));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_APPEARANCE));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_LIFESTYLE) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_LIFESTYLE));

                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_LIFESTYLE));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_CULTURE_VALUES) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_CULTURE_VALUES));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_CULTURE_VALUES));
            }
            if(preferenceInfoOperations.getPreferenceCountByCategory(Constants.TAG_PERSONAL) > 0) {
                items.add(new PreferenceInfo(Constants.TAG_PERSONAL));
                parsePreferenceInfoAsArray(preferenceInfoOperations.getAllPreferenceByCategory(Constants.TAG_PERSONAL));
            }
            return items;
        }
        @Override
        protected void onPostExecute(final ArrayList<PreferenceInfo> prefItems) {
            if(!prefItems.isEmpty()) {
                adapter = new ProfilePreferenceAdapter(UserPreferenceActivity.this, prefItems);

                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PreferenceInfo item = (PreferenceInfo)items.get(position);

                        Bundle arg = new Bundle();
                        arg.putString(ProfilePreferenceEditDialogFragment.ARG_DBNAME, item.getDbname());
                        arg.putString(ProfilePreferenceEditDialogFragment.ARG_LABEL, item.getLabel());
                        arg.putString(ProfilePreferenceEditDialogFragment.ARG_TYPE, item.getType());

                        FragmentManager fm = getSupportFragmentManager();
                        ProfilePreferenceEditDialogFragment editNameDialog = new ProfilePreferenceEditDialogFragment();
                        editNameDialog.setArguments(arg);
                        editNameDialog.show(fm, "fragment_edit_name");
                    }
                });
            } else {
            }

            mProgressDialog.dismiss();
        }
    }

    private void parsePreferenceInfoAsArray(Cursor cursor) {
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            items.add(new PreferenceInfo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_DBNAME)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_LABEL)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_VALUE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_IDS))));
        }
    }

    public String getDeviceId() {
        return ((AsianFinderApplication) getApplication()).getDeviceId();
    }



}
