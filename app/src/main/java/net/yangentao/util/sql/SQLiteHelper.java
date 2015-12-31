package net.yangentao.util.sql;

import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public void beginTransaction() {
		getWritableDatabase().beginTransaction();
	}

	public void endTransaction() {
		getWritableDatabase().endTransaction();
	}

	public void setTransactionSuccessful() {
		getWritableDatabase().setTransactionSuccessful();
	}

	/**
	 * Callable.call返回true, 则提交; 返回false, 则回滚<br/>
	 * 成功提交返回true, 失败返回false
	 */
	public boolean transaction(Callable<Void> c) {
		try {
			beginTransaction();
			c.call();
			setTransactionSuccessful();
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}
		return false;
	}

	/**
	 * 错误返回-1, 否则返回行号
	 */
	public long insertOrReplace(String table, ContentValues values) {
		return insertWithOnConflict(table, values,
				SQLiteDatabase.CONFLICT_REPLACE);
	}

	/**
	 * 错误返回-1, 否则返回行号
	 */
	public long insertOrIgnore(String table, ContentValues values) {
		return insertWithOnConflict(table, values,
				SQLiteDatabase.CONFLICT_IGNORE);
	}

	/**
	 * 错误返回-1, 否则返回行号
	 */
	public long insertOrAbort(String table, ContentValues values) {
		return insertWithOnConflict(table, values,
				SQLiteDatabase.CONFLICT_ABORT);
	}

	/**
	 * 错误返回-1, 否则返回行号
	 */
	public long insertOrRollback(String table, ContentValues values) {
		return insertWithOnConflict(table, values,
				SQLiteDatabase.CONFLICT_ROLLBACK);
	}

	/**
	 * 错误返回-1, 否则返回行号
	 */
	public long insertOrFail(String table, ContentValues values) {
		return insertWithOnConflict(table, values, SQLiteDatabase.CONFLICT_FAIL);
	}

	// 2.2以上系统有这个方法, 可以直接调用
	private long insertWithOnConflict(String table,
			ContentValues initialValues, int conflictAlgorithm) {
		return getWritableDatabase().insertWithOnConflict(table, null,
				initialValues, conflictAlgorithm);
	}

	public long queryForLong(String sql, String... args) {
		Cursor c = query(sql, args);
		if (c.moveToFirst()) {
			return c.getLong(0);
		}
		return 0L;
	}

	public long queryTableForLong(String table, String where, String... args) {
		Cursor c = queryTable(table, where, args);
		if (c.moveToFirst()) {
			return c.getLong(0);
		}
		return 0L;
	}

	public Cursor query(String sql, String... args) {
		SQLiteDatabase db = getWritableDatabase();
		return db.rawQuery(sql, args);
	}

	public synchronized void execSQL(String sql, Object... bindArgs) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql, bindArgs);
	}

	public synchronized long insert(String table, ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		return db.insert(table, null, values);
	}

	public synchronized int update(String table, ContentValues values,
			String whereClause, String... whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		return db.update(table, values, whereClause, whereArgs);
	}

	public synchronized int deleteByRowID(String tableName, long rowid) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(tableName, "_ROWID_=?",
				new String[] { String.valueOf(rowid) });
	}

	public int delete(String table) {
		return delete(table, null);
	}

	public synchronized int delete(String tableName, String where,
			String... args) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(tableName, where, args);
	}

	public long count(String table) {
		SQLiteDatabase db = getReadableDatabase();
		return DatabaseUtils.queryNumEntries(db, table);

	}

	public long count(String table, String where, String... whereArgs) {
		SQLiteDatabase db = getReadableDatabase();
		String s = (!TextUtils.isEmpty(where)) ? " where " + where : "";
		return DatabaseUtils.longForQuery(db, "select count(*) from " + table
				+ s, whereArgs);
	}

	public Cursor queryTable(String table) {
		SQLiteDatabase db = getReadableDatabase();
		return db.query(table, null, null, null, null, null, null);
	}

	public Cursor queryTable(String table, String where, String... args) {
		return queryColumnsWhereN(table, null, where, args);
	}

	public Cursor queryColumnsWhereN(String table, String[] columns,
			String selection, String... selectionArgs) {
		return queryColumnsWhere(table, columns, selection, selectionArgs);
	}

	public Cursor queryColumnsWhere(String table, String[] columns,
			String selection, String[] selectionArgs) {
		SQLiteDatabase db = getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, null, null,
				null);
	}

	@SuppressLint("NewApi")
	protected JSONObject mapOne(Cursor c) {
		JSONObject js = new JSONObject();
		String[] names = c.getColumnNames();
		if (Build.VERSION.SDK_INT < 11) { // 11 is 3.0
			Log.w("SQLiteHelper", "mapOne only support String-type under 3.0!!");
			try {
				for (String name : names) {
					int index = c.getColumnIndex(name);
					if (index >= 0) {
						js.put(name, c.getString(index));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			for (String name : names) {
				int index = c.getColumnIndex(name);
				if (index >= 0) {
					int type = c.getType(index);// TODO 只能在3.0以上
					try {
						switch (type) {
						case Cursor.FIELD_TYPE_BLOB:
							throw new IllegalArgumentException(
									"blob field can not fill to JSONObject!");
							// break;
						case Cursor.FIELD_TYPE_FLOAT:
							js.put(name, c.getDouble(index));
							break;
						case Cursor.FIELD_TYPE_INTEGER:
							js.put(name, c.getLong(index));
							break;
						case Cursor.FIELD_TYPE_NULL:
							js.put(name, null);
							break;
						case Cursor.FIELD_TYPE_STRING:
							js.put(name, c.getString(index));
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return js;
	}

	/**
	 * 只支持double/long/String
	 */
	public JSONObject queryTableOne(String table, String where, String... args) {
		Cursor c = queryTable(table, where, args);
		JSONObject js = null;
		if (c.moveToFirst()) {
			js = mapOne(c);
		} else {
			js = new JSONObject();
		}
		c.close();
		return js;
	}

	/**
	 * 只支持double/long/String
	 */
	public JSONArray queryTableMulti(String table, String where, String... args) {
		Cursor c = queryTable(table, where, args);
		// c.getCount();
		JSONArray arr = new JSONArray();// TODO capcity
		while (c.moveToNext()) {
			JSONObject js = mapOne(c);
			arr.put(js);
		}
		c.close();
		return arr;
	}

	/**
	 * 只支持double/long/String
	 */
	public JSONObject queryOne(String sql, String... args) {
		Cursor c = query(sql, args);
		JSONObject js = null;
		if (c.moveToFirst()) {
			js = mapOne(c);
		} else {
			js = new JSONObject();
		}
		c.close();
		return js;
	}

	/**
	 * 只支持double/long/String
	 */
	public JSONArray queryMulti(String sql, String... args) {
		Cursor c = query(sql, args);
		// c.getCount();
		JSONArray arr = new JSONArray();// TODO capcity
		while (c.moveToNext()) {
			JSONObject js = mapOne(c);
			arr.put(js);
		}
		c.close();
		return arr;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
