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
//	private String[] USERS_TABLE_COLUMNS = {  DataBaseWrapper._ID, DataBaseWrapper.USERINFO_USERID,
//                                                DataBaseWrapper.USERINFO_TYPE, DataBaseWrapper.USERINFO_USERNAME, DataBaseWrapper.USERINFO_EMAIL,
//                                                DataBaseWrapper.USERINFO_MAINPHOTO, DataBaseWrapper.USERINFO_SESSION, DataBaseWrapper.USERINFO_DOMAIN,
//                                                DataBaseWrapper.USERINFO_TOKEN, DataBaseWrapper.USERINFO_GENDER, DataBaseWrapper.USERINFO_ISLOGIN,
//                                                DataBaseWrapper._DATE};


    private String[] USERS_TABLE_COLUMNS = {
            DataBaseWrapper._ID,
            DataBaseWrapper.USERINFO_USERID,
            DataBaseWrapper.USERINFO_USERNAME,
            DataBaseWrapper.USERINFO_FIRSTNAME,
            DataBaseWrapper.USERINFO_GENDER,
            DataBaseWrapper.USERINFO_COUNTRY_ID,
            DataBaseWrapper.USERINFO_STATE_ID,
            DataBaseWrapper.USERINFO_CITY_ID,
            DataBaseWrapper.USERINFO_EMAIL,
            DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRATION,
            DataBaseWrapper.USERINFO_MEMBERSHIP_TYPE,
            DataBaseWrapper.USERINFO_MAINPHOTO,
            DataBaseWrapper.USERINFO_TYPE,
            DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRED,
            DataBaseWrapper.USERINFO_VALIDATE,
            DataBaseWrapper.USERINFO_DOMAIN_ID ,
            DataBaseWrapper.USERINFO_USER_PHONE,
            DataBaseWrapper.USERINFO_SESSION_ID,
            DataBaseWrapper.USERINFO_TOKEN,
            DataBaseWrapper.USERINFO_JSON_BASIC,
            DataBaseWrapper.USERINFO_JSON_APPEARANCE,
            DataBaseWrapper.USERINFO_JSON_LIFESTYLE,
            DataBaseWrapper.USERINFO_JSON_CULTURE_VALUES,
            DataBaseWrapper.USERINFO_JSON_PERSONAL,
            DataBaseWrapper.USERINFO_JSON_INTEREST,
            DataBaseWrapper.USERINFO_JSON_OTHERS,
            DataBaseWrapper.USERINFO_JSON_PREFERENCE,
            DataBaseWrapper.USERINFO_JSON_PHOTOS,
            DataBaseWrapper.USERINFO_ISLOGIN,
            DataBaseWrapper._DATE };

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
     *  @param username
     * @param gender
     * @param main_photo
     * @param basic
     *@param appearance
     * @param lifestyle
     * @param culture_values
     * @param personal
     * @param interest
     * @param others
     * @param preference
     * @param photos
     * @param isLogin  @return UserInfo Object
     */
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

    public UserInfo addUser(int user_id, String username, String firstname, String gender, int country, int state, int city,
                            String user_email, String membership_expiration, String membership_type, String main_photo, String user_type,
                            int membership_expired, int validate, int domain_id, String user_phone, String session_id, String user_token,
                            String basic, String appearance, String lifestyle, String culture_values, String personal, String interest,
                            String others, String preference, String photos, int isLogin) {
        emptyUser(); // Remove existing user information
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.USERINFO_USERID, user_id);
        values.put(DataBaseWrapper.USERINFO_USERNAME, username);
        values.put(DataBaseWrapper.USERINFO_FIRSTNAME, firstname);
        values.put(DataBaseWrapper.USERINFO_GENDER, gender);
        values.put(DataBaseWrapper.USERINFO_COUNTRY_ID, country);
        values.put(DataBaseWrapper.USERINFO_STATE_ID, state);
        values.put(DataBaseWrapper.USERINFO_CITY_ID, country);
        values.put(DataBaseWrapper.USERINFO_EMAIL, user_email);
        values.put(DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRATION, membership_expiration);
        values.put(DataBaseWrapper.USERINFO_MEMBERSHIP_TYPE, membership_type);
        values.put(DataBaseWrapper.USERINFO_MAINPHOTO, main_photo);
        values.put(DataBaseWrapper.USERINFO_TYPE, user_type);
        values.put(DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRED, membership_expired);
        values.put(DataBaseWrapper.USERINFO_VALIDATE, validate);
        values.put(DataBaseWrapper.USERINFO_DOMAIN_ID, domain_id);
        values.put(DataBaseWrapper.USERINFO_USER_PHONE, user_phone);
        values.put(DataBaseWrapper.USERINFO_SESSION_ID, session_id);
        values.put(DataBaseWrapper.USERINFO_TOKEN, user_token);

        values.put(DataBaseWrapper.USERINFO_JSON_BASIC, basic);
        values.put(DataBaseWrapper.USERINFO_JSON_APPEARANCE, appearance);
        values.put(DataBaseWrapper.USERINFO_JSON_LIFESTYLE, lifestyle);
        values.put(DataBaseWrapper.USERINFO_JSON_CULTURE_VALUES, culture_values);
        values.put(DataBaseWrapper.USERINFO_JSON_PERSONAL, personal);
        values.put(DataBaseWrapper.USERINFO_JSON_INTEREST, interest);
        values.put(DataBaseWrapper.USERINFO_JSON_OTHERS, others);
        values.put(DataBaseWrapper.USERINFO_JSON_PREFERENCE, preference);
        values.put(DataBaseWrapper.USERINFO_JSON_PHOTOS, photos);

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

    public void emptyAllUserData() {
        // User Informations
        databaseWrite.delete(DataBaseWrapper.USERINFO, null, null);
        databaseWrite.delete(DataBaseWrapper.PHOTOSINFO, null, null);
        // Preference
        databaseWrite.delete(DataBaseWrapper.PREFERENCEINFO, null, null);
        // My List
        databaseWrite.delete(DataBaseWrapper.MYLISTINFO, null, null);
        // Chat / Messaging
        databaseWrite.delete(DataBaseWrapper.MESSAGESTHREADINFO, null, null);
        databaseWrite.delete(DataBaseWrapper.ROOMINFO, null, null);

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

        user.setUser_id(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_USERID)));
        user.setUsername(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_USERNAME)));
        user.setFirstname(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_FIRSTNAME)));
        user.setGender(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_GENDER)));
        user.setCountry(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_COUNTRY_ID)));
        user.setState(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_STATE_ID)));
        user.setCity(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_CITY_ID)));
        user.setUser_email(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_EMAIL)));
        user.setMembership_expiration(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRATION)));
        user.setMembership_type(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_MEMBERSHIP_TYPE)));
        user.setMain_photo(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_MAINPHOTO)));
        user.setUser_type(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_TYPE)));
        user.setMembership_expired(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_MEMBERSHIP_EXPIRED)));
        user.setValidate(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_VALIDATE)));
        user.setDomain_id(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_DOMAIN_ID)));
        user.setUser_phone(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_USER_PHONE)));
        user.setSession_id(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_SESSION_ID)));
        user.setUser_token(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_TOKEN)));

        user.setBasic(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_BASIC)));
        user.setAppearance(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_APPEARANCE)));
        user.setLifestyle(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_LIFESTYLE)));
        user.setCulture_values(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_CULTURE_VALUES)));
        user.setPersonal(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_PERSONAL)));
        user.setInterest(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_INTEREST)));
        user.setOthers(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_OTHERS)));
        user.setPreference(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_PREFERENCE)));
        user.setPhotos(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.USERINFO_JSON_PHOTOS)));

        user.setIsLogin(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.USERINFO_ISLOGIN)));

        user.setDate(cursor.getString(cursor.getColumnIndex(DataBaseWrapper._DATE)));

		return user;
	}


}
