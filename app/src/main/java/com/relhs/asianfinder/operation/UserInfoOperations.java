package com.relhs.asianfinder.operation;

import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.data.UserInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserInfoOperations {
	// Database fields
	private DataBaseWrapper dbHelper;
	private String[] USERS_TABLE_COLUMNS = {  DataBaseWrapper._ID, DataBaseWrapper.USERINFO_USERID,
                                                DataBaseWrapper.USERINFO_TYPE, DataBaseWrapper.USERINFO_USERNAME, DataBaseWrapper.USERINFO_EMAIL,
                                                DataBaseWrapper.USERINFO_MAINPHOTO, DataBaseWrapper.USERINFO_SESSION, DataBaseWrapper.USERINFO_DOMAIN,
                                                DataBaseWrapper.USERINFO_TOKEN, DataBaseWrapper.USERINFO_GENDER, DataBaseWrapper.USERINFO_ISLOGIN,
                                                DataBaseWrapper._DATE};

	private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;
	
	public UserInfoOperations(Context context) {
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
     *  @param sessionId
     * @param userToken
     * @param userDomainId
     * @param userId
     * @param userEmail
     * @param gender
     * @param username
     * @param main_photo
     * @param userType
     * @param isLogin
     * @return UserInfo Object
     */
	public UserInfo addUser(String sessionId, String userToken, String userDomainId, int userId, String gender, String userEmail, String username, String main_photo, String userType, int isLogin) {

        emptyUser(); // Remove existing user information

        ContentValues values = new ContentValues();

        values.put(DataBaseWrapper.USERINFO_SESSION, sessionId);
        values.put(DataBaseWrapper.USERINFO_TOKEN, userToken);
        values.put(DataBaseWrapper.USERINFO_DOMAIN, userDomainId);
        values.put(DataBaseWrapper.USERINFO_USERID, userId);
        values.put(DataBaseWrapper.USERINFO_GENDER, gender);
        values.put(DataBaseWrapper.USERINFO_EMAIL, userEmail);
        values.put(DataBaseWrapper.USERINFO_USERNAME, username);
        values.put(DataBaseWrapper.USERINFO_MAINPHOTO, main_photo);
        values.put(DataBaseWrapper.USERINFO_TYPE, userType);
        values.put(DataBaseWrapper.USERINFO_ISLOGIN, isLogin);

		long Id = databaseWrite.insert(DataBaseWrapper.USERINFO, null, values);

		Cursor cursor = databaseRead.query(DataBaseWrapper.USERINFO,
                USERS_TABLE_COLUMNS, DataBaseWrapper._ID + " = " + Id, null, null, null, null);

		cursor.moveToFirst();
		UserInfo userInfo = parseUserInfo(cursor);
		cursor.close();
		return userInfo;
	}
    public int isLogin() {
        int isLogin = 0;
        Cursor cursor = databaseRead.query(DataBaseWrapper.USERINFO,
                USERS_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            isLogin = cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_ISLOGIN));
        }
        cursor.close();
        return isLogin;
    }
    public UserInfo getUser() {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.USERINFO,
                USERS_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);
        cursor.moveToFirst();
        UserInfo userInfo = parseUserInfo(cursor);
        cursor.close();
        return userInfo;
    }
    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + DataBaseWrapper.USERINFO;
        Cursor cursor = databaseRead.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void emptyUser() {
        databaseWrite.delete(DataBaseWrapper.USERINFO, null, null);
    }
	
	public void deleteUser(UserInfo userInfo) {
		long id = userInfo.getId();
		System.out.println("Comment deleted with id: " + id);
        databaseWrite.delete(DataBaseWrapper.USERINFO, DataBaseWrapper._ID
				+ " = " + id, null);
	}
	
	private UserInfo parseUserInfo(Cursor cursor) {
		UserInfo user = new UserInfo();

        user.setId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper._ID)));
        user.setUserId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_USERID)));
        user.setUserType(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_TYPE)));
        user.setUserName(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_USERNAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_EMAIL)));
        user.setGender(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_GENDER)));
        user.setUserSessionId(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_SESSION)));
        user.setDomainId(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_DOMAIN)));
        user.setUserToken(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_TOKEN)));
        user.setIsLogin(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_ISLOGIN)));
        user.setMain_photo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_MAINPHOTO)));
        user.setDate(cursor.getString(cursor.getColumnIndex(DataBaseWrapper._DATE)));


		return user;
	}
}
