package com.ksfc.newfarmer.db;

import com.ksfc.newfarmer.RndApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * 项目名称：QianXihe 类名称：QXHDBOpenHelper 类描述： 创建人：范东 创建时间：2015年6月16日 下午5:04:43 修改备注：
 */
public class KsfcDBOpenHelper extends SQLiteOpenHelper {

	// private static final String DATABASENAME = "qxhdb.db";
	private static final String DATABASENAME = RndApplication.DB_NAME;

	private static final int DATABASE_VERSION = 1;
	private Context appContext;

	SQLiteDatabase database = null;
	private static KsfcDBOpenHelper instance;

	private KsfcDBOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		appContext = context;
	}

	public static KsfcDBOpenHelper getInstance(Context context) {

		if (instance == null) {
			instance = new KsfcDBOpenHelper(context, DATABASENAME, null,
					DATABASE_VERSION);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	public void createTables(SQLiteDatabase db) {
		// ********************** 取到用户ID *************************
		synchronized (instance) {
			// 执行创建表语句
			for (String str : DBConstact.CREATE_TABLES) {
				db.execSQL(str);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/**
	 * 清空DBdao
	 */
	public void clearDBdao() {

	}

	/**
	 * 当Activity退出时调用
	 */
	public void closeDB() {
		if (instance != null) {
			try {
				SQLiteDatabase db = instance.getWritableDatabase();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance = null;
		}
	}
}
