package com.relhs.asianfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseWrapper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "af.db";
	
	public static final String _ID = "_id";
    public static final String _DATE = "_date";
	
	public static final String USERINFO = "UserInfo";
    public static final String USERINFO_USERID = "user_id";
    public static final String USERINFO_USERNAME = "username";
    public static final String USERINFO_FIRSTNAME = "firstname";
    public static final String USERINFO_GENDER = "gender";
    public static final String USERINFO_COUNTRY_ID = "country";
    public static final String USERINFO_STATE_ID = "state";
    public static final String USERINFO_CITY_ID = "city";
    public static final String USERINFO_EMAIL = "user_email";
    public static final String USERINFO_MEMBERSHIP_EXPIRATION = "membership_expiration";
    public static final String USERINFO_MEMBERSHIP_TYPE = "membership_type";
    public static final String USERINFO_MAINPHOTO = "main_photo";
    public static final String USERINFO_TYPE = "user_type";
    public static final String USERINFO_MEMBERSHIP_EXPIRED = "membership_expired";
    public static final String USERINFO_VALIDATE = "validate";
    public static final String USERINFO_DOMAIN_ID = "domain_id";
    public static final String USERINFO_USER_PHONE = "user_phone";
    public static final String USERINFO_SESSION_ID = "session_id";
    public static final String USERINFO_TOKEN = "user_token";

    public static final String USERINFO_JSON_BASIC = "basic";
    public static final String USERINFO_JSON_APPEARANCE = "appearance";
    public static final String USERINFO_JSON_LIFESTYLE = "lifestyle";
    public static final String USERINFO_JSON_CULTURE_VALUES = "culture_values";
    public static final String USERINFO_JSON_PERSONAL = "personal";
    public static final String USERINFO_JSON_INTEREST = "interest";
    public static final String USERINFO_JSON_OTHERS = "others";
    public static final String USERINFO_JSON_PREFERENCE = "preference";
    public static final String USERINFO_JSON_PHOTOS = "photos";

    public static final String USERINFO_ISLOGIN = "_is_login";




    public static final String EMOTICONSINFO = "EmoticonsInfo";
    public static final String EMOTICONSINFO_FILE = "_f";
    public static final String EMOTICONSINFO_TEXT = "_s";

    public static final String ROOMINFO = "MessagesRoomInfo";
    public static final String ROOMINFO_USERID = "user_id";
    public static final String ROOMINFO_USERTYPE = "user_type";
    public static final String ROOMINFO_USERNAME = "uname";
    public static final String ROOMINFO_ISCHATTING = "is_chatting";
    public static final String ROOMINFO_THREADID = "thread_id";
    public static final String ROOMINFO_LASTONLINE = "last_online";
    public static final String ROOMINFO_MAINPHOTO = "main_photo";

    public static final String MESSAGESTHREADINFO = "MessagesThreadInfo";
    public static final String MESSAGESTHREADINFO_THREADID = "thread_id";
    public static final String MESSAGESTHREADINFO_LOCALID = "local_id";
    public static final String MESSAGESTHREADINFO_MESSAGE = "message";
    public static final String MESSAGESTHREADINFO_F = "f";
    public static final String MESSAGESTHREADINFO_T = "t";
    public static final String MESSAGESTHREADINFO_BOOL_SEEN = "seen";
    public static final String MESSAGESTHREADINFO_TYPE = "type";
    public static final String MESSAGESTHREADINFO_FOLDER = "folder";
    public static final String MESSAGESTHREADINFO_FILE = "file";

    public static final String STICKERCATEGORYINFO = "StickerCategoryInfo";
    public static final String STICKERCATEGORYINFO_NAME = "name";
    public static final String STICKERCATEGORYINFO_URL = "url";

    public static final String STICKERSINFO = "StickerInfo";
    public static final String STICKERSINFO_CATEGORY_ID = "category";
    public static final String STICKERSINFO_FILE = "filename";




	// creation SQLite statement
    private static final String DATABASE_CREATE_USERINFO = "CREATE TABLE " + USERINFO
            + "(" + _ID + " integer primary key autoincrement, "
            + USERINFO_USERID + " integer,"
            + USERINFO_USERNAME + " text not null, "
            + USERINFO_FIRSTNAME + " text, "
            + USERINFO_GENDER + " text not null, "
            + USERINFO_COUNTRY_ID + " integer not null, "
            + USERINFO_STATE_ID + " integer not null, "
            + USERINFO_CITY_ID + " integer not null, "
            + USERINFO_EMAIL + " text not null, "
            + USERINFO_MEMBERSHIP_EXPIRATION + " text, "
            + USERINFO_MEMBERSHIP_TYPE + " text, "
            + USERINFO_MAINPHOTO + " text not null, "
            + USERINFO_TYPE + " text not null, "
            + USERINFO_MEMBERSHIP_EXPIRED + " integer not null, "
            + USERINFO_VALIDATE + " integer not null, "
            + USERINFO_DOMAIN_ID + " integer not null, "
            + USERINFO_USER_PHONE + " text, "
            + USERINFO_SESSION_ID + " text not null, "
            + USERINFO_TOKEN + " text not null, "
            + USERINFO_JSON_BASIC + " text, "
            + USERINFO_JSON_APPEARANCE + " text, "
            + USERINFO_JSON_LIFESTYLE + " text, "
            + USERINFO_JSON_CULTURE_VALUES + " text, "
            + USERINFO_JSON_PERSONAL + " text, "
            + USERINFO_JSON_INTEREST + " text, "
            + USERINFO_JSON_OTHERS + " text, "
            + USERINFO_JSON_PREFERENCE + " text, "
            + USERINFO_JSON_PHOTOS + " text, "
            + USERINFO_ISLOGIN + " integer not null, "
            + _DATE + " datetime default current_timestamp);";

    private static final String DATABASE_CREATE_EMOTICONSINFO = "CREATE TABLE " + EMOTICONSINFO
            + "(" + _ID + " integer primary key autoincrement, "
            + EMOTICONSINFO_FILE + " text not null, "
            + EMOTICONSINFO_TEXT + " text not null, "
            + _DATE + " datetime default current_timestamp);";

    private static final String DATABASE_CREATE_ROOMINFO = "CREATE TABLE " + ROOMINFO
            + "(" + _ID + " integer primary key autoincrement, "
            + ROOMINFO_USERID + " integer not null,"
            + ROOMINFO_USERTYPE + " text not null, "
            + ROOMINFO_USERNAME + " text not null, "
            + ROOMINFO_ISCHATTING + " text not null, "
            + ROOMINFO_THREADID + " integer not null,"
            + ROOMINFO_LASTONLINE + " text not null, "
            + ROOMINFO_MAINPHOTO + " text not null, "
            + _DATE + " datetime default current_timestamp);";

    private static final String DATABASE_CREATE_MESSAGESTHREADINFO = "CREATE TABLE " + MESSAGESTHREADINFO
            + "(" + _ID + " integer primary key autoincrement, "
            + MESSAGESTHREADINFO_THREADID + " integer not null,"
            + MESSAGESTHREADINFO_LOCALID + " integer not null,"
            + MESSAGESTHREADINFO_MESSAGE + " text not null, "
            + MESSAGESTHREADINFO_F + " text not null, "
            + MESSAGESTHREADINFO_T + " datetime not null, "
            + MESSAGESTHREADINFO_BOOL_SEEN + " text not null, "
            + MESSAGESTHREADINFO_TYPE + " text not null, "
            + MESSAGESTHREADINFO_FOLDER + " text, "
            + MESSAGESTHREADINFO_FILE + " text, "
            + _DATE + " datetime default current_timestamp);";

    private static final String DATABASE_CREATE_STICKERCATEGORYINFO = "CREATE TABLE " + STICKERCATEGORYINFO
            + "(" + _ID + " integer primary key autoincrement, "
            + STICKERCATEGORYINFO_NAME + " text not null, "
            + STICKERCATEGORYINFO_URL + " text not null, "
            + _DATE + " datetime default current_timestamp);";

    private static final String DATABASE_CREATE_STICKERSINFO = "CREATE TABLE " + STICKERSINFO
            + "(" + _ID + " integer primary key autoincrement, "
            + STICKERSINFO_CATEGORY_ID + " integer not null, "
            + STICKERSINFO_FILE + " text not null, "
            + _DATE + " datetime default current_timestamp);";


	public DataBaseWrapper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_USERINFO);
        db.execSQL(DATABASE_CREATE_EMOTICONSINFO);

        //Messaging
        db.execSQL(DATABASE_CREATE_ROOMINFO);
        db.execSQL(DATABASE_CREATE_MESSAGESTHREADINFO);

        //Sticker
        db.execSQL(DATABASE_CREATE_STICKERCATEGORYINFO);
        db.execSQL(DATABASE_CREATE_STICKERSINFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// you should do some logging in here
		// ..
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_USERINFO);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_EMOTICONSINFO);

        //Messaging
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_ROOMINFO);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_MESSAGESTHREADINFO);
		onCreate(db);
	}

}
