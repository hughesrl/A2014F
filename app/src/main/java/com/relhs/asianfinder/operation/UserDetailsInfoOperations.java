package com.relhs.asianfinder.operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.data.UserDetailsInfo;

import java.util.ArrayList;

public class UserDetailsInfoOperations {
	// Database fields
	private DataBaseWrapper dbHelper;

    private String[] USERDETAILSINFO_TABLE_COLUMNS = {
            DataBaseWrapper._ID,
            DataBaseWrapper.USERDETAILSINFO_CATEGORY,
            DataBaseWrapper.USERDETAILSINFO_DBNAME,
            DataBaseWrapper.USERDETAILSINFO_LABEL,
            DataBaseWrapper.USERDETAILSINFO_TYPE,
            DataBaseWrapper.USERDETAILSINFO_VALUE,
            DataBaseWrapper.USERDETAILSINFO_IDS,
            DataBaseWrapper._DATE };

	private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

	public UserDetailsInfoOperations(Context context) {
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
    public void addUserDetails(String category, String dbname, String label, String type, String value, String ids) {
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.USERDETAILSINFO_CATEGORY, category.toLowerCase());
        values.put(DataBaseWrapper.USERDETAILSINFO_DBNAME, dbname);
        values.put(DataBaseWrapper.USERDETAILSINFO_LABEL, label);
        values.put(DataBaseWrapper.USERDETAILSINFO_TYPE, type);
        values.put(DataBaseWrapper.USERDETAILSINFO_VALUE, value);
        values.put(DataBaseWrapper.USERDETAILSINFO_IDS, ids);

        databaseWrite.insert(DataBaseWrapper.USERDETAILSINFO, null, values);

    }
    public UserDetailsInfo getAllUserDetails() {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.USERDETAILSINFO,
                USERDETAILSINFO_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
        cursor.moveToFirst();
        UserDetailsInfo userDetailsInfo = parseUserDetailsInfo(cursor);
        cursor.close();
        return userDetailsInfo;
    }
    public Cursor getAllUserDetailsByCategory(String category) {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.USERDETAILSINFO,
                USERDETAILSINFO_TABLE_COLUMNS, DataBaseWrapper.USERDETAILSINFO_CATEGORY+"=?", new String[]{category.toLowerCase()}, null, null, null);
        return cursor;
    }
    public int getUserDetailsCountByCategory(String category) {
        Cursor cursor = databaseRead.query(DataBaseWrapper.USERDETAILSINFO,
                USERDETAILSINFO_TABLE_COLUMNS, DataBaseWrapper.USERDETAILSINFO_CATEGORY + "=?", new String[]{category.toLowerCase()}, null, null, null);
        int cnt = cursor.getCount();
        return cnt;
    }
    public int getAllUserDetailsCount() {
        String countQuery = "SELECT  * FROM " + DataBaseWrapper.USERDETAILSINFO;
        Cursor cursor = databaseRead.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    /**
     * Update User Details
     * @return
     */
    public void updateUserDetails() {

    }


//	public void deleteUser(UserInfo userInfo) {
//		long id = userInfo.getId();
//		System.out.println("Comment deleted with id: " + id);
//        databaseWrite.delete(DataBaseWrapper.USERINFO, DataBaseWrapper._ID
//				+ " = " + id, null);
//	}
	
	private UserDetailsInfo parseUserDetailsInfo(Cursor cursor) {
        UserDetailsInfo userDetailsInfo = new UserDetailsInfo();

        userDetailsInfo.setId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper._ID)));

        userDetailsInfo.setCategory(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_CATEGORY)));
        userDetailsInfo.setDbname(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_DBNAME)));
        userDetailsInfo.setLabel(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_LABEL)));
        userDetailsInfo.setType(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_TYPE)));
        userDetailsInfo.setValue(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_VALUE)));
        userDetailsInfo.setIds(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_IDS)));

		return userDetailsInfo;
	}

    private ArrayList<UserDetailsInfo> parseUserDetailsInfoAsArray(Cursor cursor) {
        ArrayList<UserDetailsInfo> items = new ArrayList<UserDetailsInfo>();
        UserDetailsInfo preferenceInfo = new UserDetailsInfo();
        while (cursor.moveToNext()) {
            items.add(new UserDetailsInfo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_DBNAME)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_LABEL)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_VALUE)),
                    cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERDETAILSINFO_IDS))));
        }


        return items;
    }


}
