package com.pzf.liaotian.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHelper extends SQLiteOpenHelper{
	public static final String USER_DBNAME = "user.db";
	public UserDBHelper(Context context) {
		super(context, USER_DBNAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE table IF NOT EXISTS user"
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT, nick TEXT, img TEXT, channelId TEXT, _group TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE user ADD COLUMN other TEXT");
	}
}
