package net.yangentao.util.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

class InnerDB extends SQLiteHelper {

	public InnerDB(Context context, String name) {
		super(context, name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}