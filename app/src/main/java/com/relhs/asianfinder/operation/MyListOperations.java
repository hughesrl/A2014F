package com.relhs.asianfinder.operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.data.MyListInfo;
import com.relhs.asianfinder.data.RoomInfo;
import com.relhs.asianfinder.data.ThreadsInfo;

public class MyListOperations {
	// Database fields
	private DataBaseWrapper dbHelper;
	private String[] USERS_MYLISTINFO_COLUMNS = {
            DataBaseWrapper._ID,
            DataBaseWrapper.MYLISTINFO_USERID,
            DataBaseWrapper.MYLISTINFO_USERNAME,
            DataBaseWrapper.MYLISTINFO_AGE,
            DataBaseWrapper.MYLISTINFO_GENDER,
            DataBaseWrapper.MYLISTINFO_LOCATION,
            DataBaseWrapper.MYLISTINFO_LIST_TYPE,
            DataBaseWrapper.MYLISTINFO_PHOTO1,
            DataBaseWrapper.MYLISTINFO_PHOTO2,
            DataBaseWrapper.MYLISTINFO_PHOTO3,
            DataBaseWrapper._DATE
    };

	private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

	public MyListOperations(Context context) {
		dbHelper = new DataBaseWrapper(context);
	}
	
	public void open() throws SQLException {
		databaseWrite = dbHelper.getWritableDatabase();
        databaseRead = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}

    public void addToMyList(int userId, String userName, String age, String gender,
                                String location, String listype, String main_photo, String subphoto_1, String subphoto_2) {
                ContentValues values = new ContentValues();

        if(!CheckIsDataAlreadyInDBorNot(DataBaseWrapper.MYLISTINFO, DataBaseWrapper.MYLISTINFO_USERID, userId)) {
            values.put(DataBaseWrapper.MYLISTINFO_USERID, userId);
            values.put(DataBaseWrapper.MYLISTINFO_USERNAME, userName);
            values.put(DataBaseWrapper.MYLISTINFO_AGE, age);
            values.put(DataBaseWrapper.MYLISTINFO_GENDER, gender);
            values.put(DataBaseWrapper.MYLISTINFO_LOCATION, location);
            values.put(DataBaseWrapper.MYLISTINFO_LIST_TYPE, listype);
            values.put(DataBaseWrapper.MYLISTINFO_PHOTO1, main_photo);
            values.put(DataBaseWrapper.MYLISTINFO_PHOTO2, subphoto_1);
            values.put(DataBaseWrapper.MYLISTINFO_PHOTO3, subphoto_2);

            long Id = databaseWrite.insert(DataBaseWrapper.MYLISTINFO, null, values);

            Log.d("-- robert", userName+" INSERTED "+Id);
        } else {
            Log.d("-- robert", userName+" IN DB");
        }
    }

    public Cursor getMyListByType(String listType) {
        Cursor cursor = databaseRead.query(DataBaseWrapper.MYLISTINFO,
                USERS_MYLISTINFO_COLUMNS, DataBaseWrapper.MYLISTINFO_LIST_TYPE+"=?", new String[]{listType}, null, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }


    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                                      String dbfield, int fieldValue) {
        String Query = "Select * from " + TableName + " where " + dbfield + "=" + fieldValue;
        Cursor cursor = databaseRead.rawQuery(Query, null);
        if(cursor.getCount()<=0) {
            return false;
        }
        return true;
    }
    public RoomInfo createThread(String type, int threadId, int f, int localId, String message, String t, int isSeen, String folder, String file) {
        ContentValues values = new ContentValues();

        if(!CheckIsDataAlreadyInDBorNot(DataBaseWrapper.MESSAGESTHREADINFO, DataBaseWrapper.MESSAGESTHREADINFO_LOCALID, localId)) {
            //Log.d("-- Robert", "NOT IN DB, INSERT -- " + t );
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_THREADID, threadId);
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_F, f);
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_LOCALID, localId);
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_MESSAGE, DatabaseUtils.sqlEscapeString(message));
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_T, t);
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_BOOL_SEEN, isSeen);

            values.put(DataBaseWrapper.MESSAGESTHREADINFO_TYPE, type);
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_FOLDER, folder);
            values.put(DataBaseWrapper.MESSAGESTHREADINFO_FILE, file);

            long Id = databaseWrite.insert(DataBaseWrapper.MESSAGESTHREADINFO, null, values);

        } else {
            Log.d("-- Robert", "IN DB");
        }
        return null;
    }

    private MyListInfo parseMyListInfo(Cursor cursor) {
        MyListInfo myListInfo = new MyListInfo();

        myListInfo.setId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_USERID)));
        myListInfo.setUsername(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_USERNAME)));
        myListInfo.setAge(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_AGE)));
        myListInfo.setGender(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_GENDER)));
        myListInfo.setLocation(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_LOCATION)));
        myListInfo.setListType(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_LIST_TYPE)));
        myListInfo.setMain_photo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_MAINPHOTO)));
        myListInfo.setSubphoto_1(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_ISCHATTING)));
        myListInfo.setSubphoto_2(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_LASTONLINE)));

		return myListInfo;
	}

}
