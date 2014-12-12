package com.relhs.asianfinder.operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.data.RoomInfo;
import com.relhs.asianfinder.data.ThreadsInfo;
import com.relhs.asianfinder.data.UserInfo;

public class MessagesOperations {
	// Database fields
	private DataBaseWrapper dbHelper;
	private String[] USERS_ROOMINFO_COLUMNS = {  DataBaseWrapper._ID, DataBaseWrapper.ROOMINFO_USERID,
                                                DataBaseWrapper.ROOMINFO_USERTYPE, DataBaseWrapper.ROOMINFO_USERNAME, DataBaseWrapper.ROOMINFO_ISCHATTING,
                                                DataBaseWrapper.ROOMINFO_THREADID, DataBaseWrapper.ROOMINFO_LASTONLINE, DataBaseWrapper.ROOMINFO_MAINPHOTO,
                                                DataBaseWrapper._DATE};

    private String[] USERS_MESSAGESTHREADINFO_COLUMNS = {  DataBaseWrapper._ID, DataBaseWrapper.MESSAGESTHREADINFO_THREADID,
            DataBaseWrapper.MESSAGESTHREADINFO_F, DataBaseWrapper.MESSAGESTHREADINFO_LOCALID, DataBaseWrapper.MESSAGESTHREADINFO_MESSAGE,
            DataBaseWrapper.MESSAGESTHREADINFO_T, DataBaseWrapper.MESSAGESTHREADINFO_BOOL_SEEN, DataBaseWrapper.MESSAGESTHREADINFO_TYPE, DataBaseWrapper.MESSAGESTHREADINFO_FOLDER,
            DataBaseWrapper.MESSAGESTHREADINFO_FILE, DataBaseWrapper._DATE};

	private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

	public MessagesOperations(Context context) {
		dbHelper = new DataBaseWrapper(context);
	}
	
	public void open() throws SQLException {
		databaseWrite = dbHelper.getWritableDatabase();
        databaseRead = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}

    public RoomInfo createRoom(int userId,int threadId, String userType, String userName,
                               String main_photo, int isChatting, String lastOnline) {
                ContentValues values = new ContentValues();

        if(!CheckIsDataAlreadyInDBorNot(DataBaseWrapper.ROOMINFO, DataBaseWrapper.ROOMINFO_USERID, userId)) {
            //Log.d("-- Robert", "NOT IN DB = "+ main_photo);
            values.put(DataBaseWrapper.ROOMINFO_USERID, userId);
            values.put(DataBaseWrapper.ROOMINFO_THREADID, threadId);
            values.put(DataBaseWrapper.ROOMINFO_USERTYPE, userType);
            values.put(DataBaseWrapper.ROOMINFO_USERNAME, userName);
            values.put(DataBaseWrapper.ROOMINFO_MAINPHOTO, main_photo);
            values.put(DataBaseWrapper.ROOMINFO_ISCHATTING, isChatting);
            values.put(DataBaseWrapper.ROOMINFO_LASTONLINE, lastOnline);

            long Id = databaseWrite.insert(DataBaseWrapper.ROOMINFO, null, values);

            Cursor cursor = databaseRead.query(DataBaseWrapper.ROOMINFO,
                    USERS_ROOMINFO_COLUMNS, DataBaseWrapper._ID + " = " + Id, null, null, null, null);

            cursor.moveToFirst();
            RoomInfo roomInfo = parseRoomInfo(cursor);
            cursor.close();
            return roomInfo;
        } else {
            //Log.d("-- Robert", "IN DB");
            return null;
        }
    }
    public Cursor getChatRooms() {
        // now that the user is created return it ...
        String Query = "SELECT * FROM "+DataBaseWrapper.ROOMINFO+" AS ri " +
                "INNER JOIN "+DataBaseWrapper.MESSAGESTHREADINFO+" AS mti " +
                "ON ri."+DataBaseWrapper.MESSAGESTHREADINFO_THREADID+" = mti."+DataBaseWrapper.ROOMINFO_THREADID +" " +
                "GROUP BY mti."+DataBaseWrapper.ROOMINFO_THREADID+" " +
                "ORDER BY mti."+DataBaseWrapper._DATE+" DESC";

        Cursor cursor = databaseRead.rawQuery(Query, null);
//        Cursor cursor = databaseRead.query(DataBaseWrapper.ROOMINFO,
//                USERS_ROOMINFO_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public RoomInfo getChatRoomDetails(String tid) {
        Cursor cursor = databaseRead.query(DataBaseWrapper.ROOMINFO,
                USERS_ROOMINFO_COLUMNS, DataBaseWrapper.ROOMINFO_THREADID+"=?", new String[]{tid}, null, null, null, null);
        cursor.moveToFirst();
        RoomInfo roomInfo = parseRoomInfo(cursor);
//        cursor.close();
        return roomInfo;
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

//            Cursor cursor = databaseRead.query(DataBaseWrapper.MESSAGESTHREADINFO,
//                    USERS_MESSAGESTHREADINFO_COLUMNS, DataBaseWrapper._ID + " = " + Id, null, null, null, null);
//            cursor.moveToFirst();
//            RoomInfo roomInfo = parseRoomInfo(cursor);
//            cursor.close();
//            return roomInfo;
//             return roomInfo;
        } else {
            //Log.d("-- Robert", "IN DB");
        }
        return null;
    }
    public ThreadsInfo getLastThread(String anInt) {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.MESSAGESTHREADINFO,
                USERS_MESSAGESTHREADINFO_COLUMNS, DataBaseWrapper.MESSAGESTHREADINFO_THREADID+"=?", new String[]{anInt}, null, null, DataBaseWrapper.MESSAGESTHREADINFO_LOCALID+" DESC", "1");

        cursor.moveToFirst();

        ThreadsInfo threadsInfo = parseThreadInfo(cursor);
        return threadsInfo;
    }



    public Cursor getThreadMessages(String anInt) {
        // now that the user is created return it ...
        //Log.d("-- tid", anInt+"");
        Cursor cursor = databaseRead.query(DataBaseWrapper.MESSAGESTHREADINFO,
                USERS_MESSAGESTHREADINFO_COLUMNS, DataBaseWrapper.MESSAGESTHREADINFO_THREADID+"=?", new String[]{anInt}, null, null, DataBaseWrapper.MESSAGESTHREADINFO_LOCALID+" ASC", null);

        cursor.moveToFirst();
        return cursor;
    }
















//	public UserInfo addUser(String sessionId, String userToken, String userDomainId, int userId, String gender, String userEmail, String username, String main_photo, String userType, int isLogin) {
//
//        emptyUser(); // Remove existing user information
//
//        ContentValues values = new ContentValues();
//
//        values.put(DataBaseWrapper.USERINFO_SESSION, sessionId);
//        values.put(DataBaseWrapper.USERINFO_TOKEN, userToken);
//        values.put(DataBaseWrapper.USERINFO_DOMAIN, userDomainId);
//        values.put(DataBaseWrapper.USERINFO_USERID, userId);
//        values.put(DataBaseWrapper.USERINFO_GENDER, gender);
//        values.put(DataBaseWrapper.USERINFO_EMAIL, userEmail);
//        values.put(DataBaseWrapper.USERINFO_USERNAME, username);
//        values.put(DataBaseWrapper.USERINFO_MAINPHOTO, main_photo);
//        values.put(DataBaseWrapper.USERINFO_TYPE, userType);
//        values.put(DataBaseWrapper.USERINFO_ISLOGIN, isLogin);
//
//		long Id = databaseWrite.insert(DataBaseWrapper.USERINFO, null, values);
//
//		Cursor cursor = databaseRead.query(DataBaseWrapper.USERINFO,
//                USERS_TABLE_COLUMNS, DataBaseWrapper._ID + " = " + Id, null, null, null, null);
//
//		cursor.moveToFirst();
//		UserInfo userInfo = parseUserInfo(cursor);
//		cursor.close();
//		return userInfo;
//	}
//    public int isLogin() {
//        int isLogin = 0;
//        Cursor cursor = databaseRead.query(DataBaseWrapper.USERINFO,
//                USERS_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
//        cursor.moveToFirst();
//        if(cursor.getCount() > 0) {
//            isLogin = cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_ISLOGIN));
//        }
//        cursor.close();
//        return isLogin;
//    }
//    public UserInfo getUser() {
//        // now that the user is created return it ...
//        Cursor cursor = databaseRead.query(DataBaseWrapper.USERINFO,
//                USERS_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
//
//        cursor.moveToFirst();
//
//        UserInfo userInfo = parseUserInfo(cursor);
//
//        return userInfo;
//    }
//    public int getUserCount() {
//        String countQuery = "SELECT  * FROM " + DataBaseWrapper.USERINFO;
//        Cursor cursor = databaseRead.rawQuery(countQuery, null);
//        int cnt = cursor.getCount();
//        cursor.close();
//        return cnt;
//    }
//
//    public void emptyUser() {
//        databaseWrite.delete(DataBaseWrapper.USERINFO, null, null);
//    }
//
//	public void deleteUser(UserInfo userInfo) {
//		long id = userInfo.getId();
//		System.out.println("Comment deleted with id: " + id);
//        databaseWrite.delete(DataBaseWrapper.USERINFO, DataBaseWrapper._ID
//				+ " = " + id, null);
//	}
//
//	private UserInfo parseUserInfo(Cursor cursor) {
//		UserInfo user = new UserInfo();
//
//        user.setId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper._ID)));
//        user.setUserId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_USERID)));
//        user.setUserType(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_TYPE)));
//        user.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_USERNAME)));
//        user.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_EMAIL)));
//        user.setGender(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_GENDER)));
//        user.setUserSessionId(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_SESSION)));
//        user.setDomainId(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_DOMAIN)));
//        user.setUserToken(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_TOKEN)));
//        user.setIsLogin(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_ISLOGIN)));
//        user.setMain_photo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_MAINPHOTO)));
//        user.setDate(cursor.getString(cursor.getColumnIndex(DataBaseWrapper._DATE)));
//
//
//		return user;
//	}

    private RoomInfo parseRoomInfo(Cursor cursor) {
        RoomInfo roomInfo = new RoomInfo();

        roomInfo.setUserId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_USERID)));
        roomInfo.setThreadId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_THREADID)));
        roomInfo.setUserType(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_USERTYPE)));
        roomInfo.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_USERNAME)));
        roomInfo.setMain_photo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_MAINPHOTO)));
        roomInfo.setIsChatting(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_ISCHATTING)));
        roomInfo.setLastOnline(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_LASTONLINE)));

		return roomInfo;
	}

    private ThreadsInfo parseThreadInfo(Cursor cursor) {
        ThreadsInfo threadsInfo = new ThreadsInfo();

        threadsInfo.setThreadId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_THREADID)));
        threadsInfo.setF(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_F)));
        threadsInfo.setLocalId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_LOCALID)));
        threadsInfo.setMessage(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_MESSAGE)));
        threadsInfo.setT(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_T)));
        threadsInfo.setIsSeen(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_BOOL_SEEN)));

        threadsInfo.setMessageType(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_TYPE)));
        threadsInfo.setFolderSticker(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_FOLDER)));
        threadsInfo.setFileSticker(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_FILE)));

        threadsInfo.setDate(cursor.getString(cursor.getColumnIndex(DataBaseWrapper._DATE)));

        return threadsInfo;
    }
}
