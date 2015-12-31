package com.ksfc.newfarmer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 项目名称：QianXihe 类名称：DBManager 类描述： 创建人：范东 创建时间：2015年6月16日 下午5:04:30 修改备注：
 */
public class DBManager {

	private KsfcDBOpenHelper dbOpenHelper = null;
	private SQLiteDatabase database = null;
	// private Context appContext ;

	private static DBManager instance;

	/** 保证单例类 */
	private DBManager() {

	}

	public static DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	public void Init(Context context) {
		dbOpenHelper = KsfcDBOpenHelper.getInstance(context);
		database = dbOpenHelper.getWritableDatabase();
		dbOpenHelper.createTables(database);
	}

	public void closeDB(Context context) {
		dbOpenHelper = KsfcDBOpenHelper.getInstance(context);
		database = dbOpenHelper.getWritableDatabase();
		dbOpenHelper.closeDB();
	}
}
