package com.relhs.asianfinder.operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.Preference;

import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.data.PreferenceInfo;
import com.relhs.asianfinder.data.UserInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PreferenceInfoOperations {
	// Database fields
	private DataBaseWrapper dbHelper;

    private String[] PREFERENCE_TABLE_COLUMNS = {
            DataBaseWrapper._ID,
            DataBaseWrapper.PREFERENCEINFO_CATEGORY,
            DataBaseWrapper.PREFERENCEINFO_DBNAME,
            DataBaseWrapper.PREFERENCEINFO_LABEL,
            DataBaseWrapper.PREFERENCEINFO_TYPE,
            DataBaseWrapper.PREFERENCEINFO_VALUE,
            DataBaseWrapper.PREFERENCEINFO_IDS,
            DataBaseWrapper._DATE };

	private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

	public PreferenceInfoOperations(Context context) {
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
     * Adding User to Database
     * @param category
     * @param dbname
     * @param label
     * @param type
     * @param value
     * @param ids
     * @return UserInfo Object
     */
    public void addUserPreference(String category, String dbname, String label, String type, String value, String ids) {
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.PREFERENCEINFO_CATEGORY, category.toLowerCase());
        values.put(DataBaseWrapper.PREFERENCEINFO_DBNAME, dbname);
        values.put(DataBaseWrapper.PREFERENCEINFO_LABEL, label);
        values.put(DataBaseWrapper.PREFERENCEINFO_TYPE, type);
        values.put(DataBaseWrapper.PREFERENCEINFO_VALUE, value);
        values.put(DataBaseWrapper.PREFERENCEINFO_IDS, ids);

        databaseWrite.insert(DataBaseWrapper.PREFERENCEINFO, null, values);

    }
    public PreferenceInfo getAllPreference() {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.PREFERENCEINFO,
                PREFERENCE_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
        cursor.moveToFirst();
        PreferenceInfo preferenceInfo = parsePreferenceInfo(cursor);
        cursor.close();
        return preferenceInfo;
    }
    public Cursor getAllPreferenceByCategory(String category) {
        Cursor cursor = databaseRead.query(DataBaseWrapper.PREFERENCEINFO, PREFERENCE_TABLE_COLUMNS,
                DataBaseWrapper.PREFERENCEINFO_CATEGORY+"=?", new String[]{category.trim().toLowerCase()}, null, null, null);
        return cursor;
    }
    public PreferenceInfo getAllPreferenceByCategory2(String category) {
        Cursor cursor = databaseRead.query(DataBaseWrapper.PREFERENCEINFO, PREFERENCE_TABLE_COLUMNS,
                DataBaseWrapper.PREFERENCEINFO_CATEGORY+"=?", new String[]{category.trim().toLowerCase()}, null, null, null);
        cursor.moveToFirst();
        PreferenceInfo preferenceInfo = parsePreferenceInfo(cursor);
        cursor.close();
        return preferenceInfo;
    }
    public int getPreferenceCountByCategory(String category) {
        Cursor cursor = databaseRead.query(DataBaseWrapper.PREFERENCEINFO,
                PREFERENCE_TABLE_COLUMNS, DataBaseWrapper.PREFERENCEINFO_CATEGORY + "=?", new String[]{category.toLowerCase()}, null, null, null);
        int cnt = cursor.getCount();
        return cnt;
    }
    public int getPreferenceCount() {
        String countQuery = "SELECT  * FROM " + DataBaseWrapper.PREFERENCEINFO;
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
	
	private PreferenceInfo parsePreferenceInfo(Cursor cursor) {
        PreferenceInfo preferenceInfo = new PreferenceInfo();

        preferenceInfo.setId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper._ID)));

        preferenceInfo.setCategory(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_CATEGORY)));
        preferenceInfo.setDbname(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_DBNAME)));
        preferenceInfo.setLabel(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_LABEL)));
        preferenceInfo.setType(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_TYPE)));
        preferenceInfo.setValue(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_VALUE)));
        preferenceInfo.setIds(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_IDS)));

		return preferenceInfo;
	}

    private ArrayList<PreferenceInfo> parsePreferenceInfoAsArray(Cursor cursor) {
        ArrayList<PreferenceInfo> items = new ArrayList<PreferenceInfo>();
        PreferenceInfo preferenceInfo = new PreferenceInfo();
        while (cursor.moveToNext()) {
            items.add(new PreferenceInfo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_DBNAME)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_LABEL)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_VALUE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.PREFERENCEINFO_IDS))));
        }


        return items;
    }


}
