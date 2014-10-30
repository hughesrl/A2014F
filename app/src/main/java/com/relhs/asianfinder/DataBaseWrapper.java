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
    public static final String USERINFO_TYPE = "user_type";
    public static final String USERINFO_EMAIL = "user_email";
    public static final String USERINFO_USERNAME = "username";
    public static final String USERINFO_GENDER = "gender";
    public static final String USERINFO_MAINPHOTO = "main_photo";
    public static final String USERINFO_SESSION = "session_id";
    public static final String USERINFO_DOMAIN = "domain_id";
    public static final String USERINFO_TOKEN = "user_token";
    public static final String USERINFO_MEMBERSHIP_TYPE = "membership_type";
    public static final String USERINFO_ISLOGIN = "_is_login";

    public static final String EMOTICONSINFO = "EmoticonsInfo";
    public static final String EMOTICONSINFO_FILE = "_f";
    public static final String EMOTICONSINFO_TEXT = "_s";

	
	// creation SQLite statement
	private static final String DATABASE_CREATE_USERINFO = "CREATE TABLE " + USERINFO
			+ "(" + _ID + " integer primary key autoincrement, "
            + USERINFO_USERID + " integer,"
            + USERINFO_TYPE + " text not null, "
            + USERINFO_USERNAME + " text not null, "
            + USERINFO_GENDER + " text not null, "
            + USERINFO_MAINPHOTO + " text not null, "
            + USERINFO_EMAIL + " text not null, "
            + USERINFO_SESSION + " text not null, "
            + USERINFO_DOMAIN + " text not null, "
            + USERINFO_TOKEN + " text not null, "
            + USERINFO_ISLOGIN + " int not null, "
			+ _DATE + " datetime default current_timestamp);";

    private static final String DATABASE_CREATE_EMOTICONSINFO = "CREATE TABLE " + EMOTICONSINFO
            + "(" + _ID + " integer primary key autoincrement, "
            + EMOTICONSINFO_FILE + " text not null, "
            + EMOTICONSINFO_TEXT + " text not null, "
            + _DATE + " datetime default current_timestamp);";


	public DataBaseWrapper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_USERINFO);
        db.execSQL(DATABASE_CREATE_EMOTICONSINFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// you should do some logging in here
		// ..
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_USERINFO);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_EMOTICONSINFO);
		onCreate(db);
	}

}
