package com.relhs.asianfinder.operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.data.EmoticonsInfo;


public class EmoticonsOperations {
	// Database fields
	private DataBaseWrapper dbHelper;
	private String[] EMOTICONS_TABLE_COLUMNS = {  DataBaseWrapper._ID, DataBaseWrapper.EMOTICONSINFO_FILE,
                                                DataBaseWrapper.EMOTICONSINFO_TEXT,
                                                DataBaseWrapper._DATE};

	private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

	public EmoticonsOperations(Context context) {
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
     *
     * @param emoticon_file
     * @param emoticon_text
     * @return UserInfo Object
     */
	public EmoticonsInfo addEmoticons(String emoticon_file, String emoticon_text) {
	
		ContentValues values = new ContentValues();

        values.put(DataBaseWrapper.EMOTICONSINFO_FILE, emoticon_file);
		values.put(DataBaseWrapper.EMOTICONSINFO_TEXT, emoticon_text);

		long Id = databaseWrite.insert(DataBaseWrapper.EMOTICONSINFO, null, values);

        Log.d("NEW DATA ID", Id+"");
		// now that the user is created return it ...
		Cursor cursor = databaseRead.query(DataBaseWrapper.EMOTICONSINFO,
                EMOTICONS_TABLE_COLUMNS, DataBaseWrapper._ID + " = "
						+ Id, null, null, null, null);

		cursor.moveToFirst();
	
		EmoticonsInfo emoticonsInfo = parseEmoticonsInfo(cursor);

		cursor.close();
		return emoticonsInfo;
	}
    public EmoticonsInfo getEmoticons() {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.EMOTICONSINFO,
                EMOTICONS_TABLE_COLUMNS, DataBaseWrapper._ID, null, null, null, null);

        cursor.moveToFirst();

        EmoticonsInfo userInfo = parseEmoticonsInfo(cursor);

        return userInfo;
    }
    public EmoticonsInfo getEmoticon(String f) {
        // now that the user is created return it ...
        Cursor cursor = databaseRead.query(DataBaseWrapper.EMOTICONSINFO,
                EMOTICONS_TABLE_COLUMNS, DataBaseWrapper.EMOTICONSINFO_FILE + "='"+f+"'", null, null, null, null);

        cursor.moveToFirst();

        EmoticonsInfo userInfo = parseEmoticonsInfo(cursor);

        return userInfo;
    }
    public int getEmoticonsCount() {
        String countQuery = "SELECT  * FROM " + DataBaseWrapper.EMOTICONSINFO;
        Cursor cursor = databaseRead.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

	private EmoticonsInfo parseEmoticonsInfo(Cursor cursor) {
        EmoticonsInfo user = new EmoticonsInfo();

        user.setId(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper._ID)));
        user.setEmoticonFile(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.EMOTICONSINFO_FILE)));
        user.setEmoticonText(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.EMOTICONSINFO_TEXT)));
        user.setDate(cursor.getString(cursor.getColumnIndex(DataBaseWrapper._DATE)));

		return user;
	}
}
