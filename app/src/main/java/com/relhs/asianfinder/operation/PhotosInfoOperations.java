package com.relhs.asianfinder.operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.data.PeoplePhotosInfo;
import com.relhs.asianfinder.data.PreferenceInfo;
import com.relhs.asianfinder.data.UserInfo;

import java.util.ArrayList;

public class PhotosInfoOperations {
	// Database fields
	private DataBaseWrapper dbHelper;

    private String[] PHOTOS_TABLE_COLUMNS = {
            DataBaseWrapper._ID,
            DataBaseWrapper.PHOTOSINFO_URL,
            DataBaseWrapper.PHOTOSINFO_CATEGORY,
            DataBaseWrapper.PHOTOSINFO_NUM_COMMENTS,
            DataBaseWrapper._DATE };

	private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

	public PhotosInfoOperations(Context context) {
		dbHelper = new DataBaseWrapper(context);
	}
	
	public void open() throws SQLException {
		databaseWrite = dbHelper.getWritableDatabase();
        databaseRead = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}

    /**
     * Photo to Database
     * @param url
     * @param category
     * @param num_comments
     * @return UserInfo Object
     */
    public void addPhoto(String url, String category, String num_comments) {
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.PHOTOSINFO_URL, url);
        values.put(DataBaseWrapper.PHOTOSINFO_CATEGORY, category);
        values.put(DataBaseWrapper.PHOTOSINFO_NUM_COMMENTS, num_comments);

        databaseWrite.insert(DataBaseWrapper.PHOTOSINFO, null, values);

    }
    public ArrayList<PeoplePhotosInfo> getAllPhotos() {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.PHOTOSINFO,
                PHOTOS_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<PeoplePhotosInfo> peoplePhotosInfo = parsePreferenceInfoAsArray(cursor);
        cursor.close();

        return peoplePhotosInfo;
    }
//    public Cursor getAllPreferenceByCategory(String category) {
//        // now that the user is created return it ...
//        Cursor cursor = databaseRead.query(DataBaseWrapper.PREFERENCEINFO,
//                PREFERENCE_TABLE_COLUMNS, DataBaseWrapper.PREFERENCEINFO_CATEGORY+"=?", new String[]{category.toLowerCase()}, null, null, null);
//        return cursor;
//    }
//    public int getPreferenceCountByCategory(String category) {
//        Cursor cursor = databaseRead.query(DataBaseWrapper.PREFERENCEINFO,
//                PREFERENCE_TABLE_COLUMNS, DataBaseWrapper.PREFERENCEINFO_CATEGORY + "=?", new String[]{category.toLowerCase()}, null, null, null);
//        int cnt = cursor.getCount();
//        return cnt;
//    }
    public int getPhotoCount() {
        String countQuery = "SELECT  * FROM " + DataBaseWrapper.PHOTOSINFO;
        Cursor cursor = databaseRead.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void emptyAllUserData() {
        databaseWrite.delete(DataBaseWrapper.USERINFO, null, null);
        databaseWrite.delete(DataBaseWrapper.MESSAGESTHREADINFO, null, null);
        databaseWrite.delete(DataBaseWrapper.ROOMINFO, null, null);
    }
	
	public void deleteUser(UserInfo userInfo) {
		long id = userInfo.getId();
		System.out.println("Comment deleted with id: " + id);
        databaseWrite.delete(DataBaseWrapper.USERINFO, DataBaseWrapper._ID
				+ " = " + id, null);
	}
	
	private PeoplePhotosInfo parsePhotoInfo(Cursor cursor) {
        PeoplePhotosInfo peoplePhotosInfo = new PeoplePhotosInfo();

        peoplePhotosInfo.setFile(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PHOTOSINFO_URL)));
        peoplePhotosInfo.setCategory(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PHOTOSINFO_CATEGORY)));
        peoplePhotosInfo.setCommentsCount(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PHOTOSINFO_NUM_COMMENTS)));

		return peoplePhotosInfo;
	}

    private ArrayList<PeoplePhotosInfo> parsePreferenceInfoAsArray(Cursor cursor) {
        ArrayList<PeoplePhotosInfo> items = new ArrayList<PeoplePhotosInfo>();
        PeoplePhotosInfo preferenceInfo = new PeoplePhotosInfo();
        int position = 0;
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            items.add(
                    new PeoplePhotosInfo(1,1, position, cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PHOTOSINFO_CATEGORY)),
                            cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PHOTOSINFO_URL)),
                            cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PHOTOSINFO_NUM_COMMENTS)))
            );

            Log.d(" -- robert", cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PHOTOSINFO_URL)));
            position+=1;
        }


        return items;
    }


}
