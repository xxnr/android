package com.ksfc.newfarmer.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksfc.newfarmer.db.BaseDao;
import com.ksfc.newfarmer.utils.RndLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ShoppingDao extends BaseDao {
	private static final String TAG = "ShoppingDao";

	public static String TABLE_NAME_SHOPPING = "shopping_" + uid;

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ADDITIONS = "additions";
	public static final String COLUMN_PID = "pid";
	public static final String COLUMN_SKUId = "SKUId";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_URL = "imageurl";
	public static final String COLUMN_NUMBERS = "numbers";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_STARS = "stars";
	public static final String COLUMN_DESC = "desc";
	public static final String COLUMN_PRICENOW = "pricenow";
	public static final String COLUMN_PRICEOLD = "priceold";
	public static final String COLUMN_TOTALSCORE = "totalscore";
	public static final String COLUMN_USESCORE = "usescore";
	public static final String COLUMN_PRAISES = "praises";

	public ShoppingDao(Context context) {
		super(context);
		System.out.println(TABLE_NAME_SHOPPING);
	}

	@Override
	protected void initTableName(String userId) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean isDBOpen() {
		// TODO Auto-generated method stub
		return true;
	}

	public synchronized boolean saveShopping(Map<String, String> map) {
		if (isDBOpen()) {
			ContentValues values = new ContentValues();

			values.put(COLUMN_ID, map.get("id"));
			values.put(COLUMN_PID, map.get("pid"));
			values.put(COLUMN_ADDITIONS, map.get("additions"));
			values.put(COLUMN_TITLE, map.get("title"));
			values.put(COLUMN_SKUId, map.get("SKUId"));
			values.put(COLUMN_URL, map.get("imageurl"));
			values.put(COLUMN_NUMBERS, map.get("numbers"));
			values.put(COLUMN_TYPE, map.get("type"));
			values.put(COLUMN_STARS, map.get("stars"));
			values.put(COLUMN_DESC, map.get("desc"));
			values.put(COLUMN_PRICENOW, map.get("pricenow"));
			values.put(COLUMN_PRICEOLD, map.get("priceold"));
			values.put(COLUMN_TOTALSCORE, map.get("totalscore"));
			values.put(COLUMN_USESCORE, map.get("usescore"));
			values.put(COLUMN_PRAISES, map.get("praises"));

			if (database.insert(TABLE_NAME_SHOPPING, null, values) > 0) {
				RndLog.i(
						TAG,
						"插入数据的值为：" + map.get("id") + " " +map.get("SKUId")+
								" " +map.get("additions")
								+ map.get("title")
								+ " " + map.get("imageurl") + " "
								+ map.get("numbers") + " " + map.get("type")
								+ " " + map.get("stars") + " "
								+ map.get("desc") + " " + map.get("pricenow")
								+ " " + map.get("priceold") + " "
								+ map.get("totalscore") + " "
								+ map.get("usescore") + " "
								+ map.get("praises"));
				return true;
			}
		}
		return false;
	}

	public synchronized Map<String, String> getShopping(String pid) {
		Map<String, String> map = new HashMap<String, String>();
		if (isDBOpen()) {
			String sql = "SELECT * FROM " + TABLE_NAME_SHOPPING + " WHERE "
					+ COLUMN_PID + " = ?";
			Cursor cursor = database.rawQuery(sql, new String[] { pid });

			if (cursor.moveToFirst()) {
				String id = cursor.getString(cursor.getColumnIndex(COLUMN_PID));
				String title = cursor.getString(cursor
						.getColumnIndex(COLUMN_TITLE));
				String SKUId = cursor.getString(cursor
						.getColumnIndex(COLUMN_SKUId));
				String additions = cursor.getString(cursor
						.getColumnIndex(COLUMN_ADDITIONS));
				String imageurl = cursor.getString(cursor
						.getColumnIndex(COLUMN_URL));
				String numbers = cursor.getString(cursor
						.getColumnIndex(COLUMN_NUMBERS));
				String type = cursor.getString(cursor
						.getColumnIndex(COLUMN_TYPE));
				String stars = cursor.getString(cursor
						.getColumnIndex(COLUMN_STARS));
				String desc = cursor.getString(cursor
						.getColumnIndex(COLUMN_DESC));
				String pricenow = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRICENOW));
				String priceold = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRICEOLD));
				String totalscore = cursor.getString(cursor
						.getColumnIndex(COLUMN_TOTALSCORE));
				String usescore = cursor.getString(cursor
						.getColumnIndex(COLUMN_USESCORE));
				String praises = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRAISES));
				map.put("pid", id);
				map.put("SKUId", SKUId);
				map.put("additions", additions);
				map.put("title", title);
				map.put("imageurl", imageurl);
				map.put("numbers", numbers);
				map.put("type", type);
				map.put("stars", stars);
				map.put("desc", desc);
				map.put("pricenow", pricenow);
				map.put("priceold", priceold);
				map.put("totalscore", totalscore);
				map.put("usescore", usescore);
				map.put("praises", praises);
			}
			cursor.close();
		}
		return map;
	}

	public synchronized List<Map> getAllShoppings(String ptype) {
		List<Map> list = new ArrayList<Map>();
		if (isDBOpen()) {
			// String sql = "SELECT * FROM " + TABLE_NAME_SHOPPING + " WHERE "
			// + COLUMN_TYPE + " = ?";
			// Cursor cursor = database.rawQuery(sql, new String[] { ptype });
			String sql = "SELECT * FROM " + TABLE_NAME_SHOPPING;
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String pid = cursor
						.getString(cursor.getColumnIndex(COLUMN_PID));
				String SKUId = cursor.getString(cursor
						.getColumnIndex(COLUMN_SKUId));
				String title = cursor.getString(cursor
						.getColumnIndex(COLUMN_TITLE));
				String additions = cursor.getString(cursor
						.getColumnIndex(COLUMN_ADDITIONS));
				String imageurl = cursor.getString(cursor
						.getColumnIndex(COLUMN_URL));
				String numbers = cursor.getString(cursor
						.getColumnIndex(COLUMN_NUMBERS));
				String type = cursor.getString(cursor
						.getColumnIndex(COLUMN_TYPE));
				String stars = cursor.getString(cursor
						.getColumnIndex(COLUMN_STARS));
				String desc = cursor.getString(cursor
						.getColumnIndex(COLUMN_DESC));
				String pricenow = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRICENOW));
				String priceold = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRICEOLD));
				String totalscore = cursor.getString(cursor
						.getColumnIndex(COLUMN_TOTALSCORE));
				String usescore = cursor.getString(cursor
						.getColumnIndex(COLUMN_USESCORE));
				String praises = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRAISES));
				Map<String, String> map = new HashMap<String, String>();
				map.put("pid", pid);
				map.put("SKUId", SKUId);
				map.put("additions", additions);
				map.put("title", title);
				map.put("imageurl", imageurl);
				map.put("numbers", numbers);
				map.put("type", type);
				map.put("stars", stars);
				map.put("desc", desc);
				map.put("pricenow", pricenow);
				map.put("priceold", priceold);
				map.put("totalscore", totalscore);
				map.put("usescore", usescore);
				map.put("praises", praises);
				list.add(map);
			}
			cursor.close();
		}
		return list;
	}

	public synchronized boolean updateShopping(String SkUId, String nums) {
		if (isDBOpen()) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_NUMBERS, nums);
			if (database.update(TABLE_NAME_SHOPPING, values, COLUMN_SKUId
					+ " = ?", new String[] { SkUId }) > 0) {
				RndLog.i(TAG, "呵呵，我将数量更新为：" + nums);
				return true;
			}
		}
		return false;
	}

	public synchronized boolean deleteShopping(String SkUId) {
		if (isDBOpen()) {
			if (database.delete(TABLE_NAME_SHOPPING, COLUMN_SKUId + " = ?",
					new String[] { SkUId }) > 0) {
				RndLog.i(TAG, "呵呵，我已经将 ID = " + SkUId + " 的数据删除");
				return true;
			}
			// String sql = "delete from "+TABLE_NAME_SHOPPING+
			// " where "+COLUMN_PID+" =?";
			// database.execSQL(sql, new String[] { pid });
		}
		return false;
	}

	public synchronized boolean deleteAllShopping() {
		if (isDBOpen()) {
			if (database.delete(TABLE_NAME_SHOPPING, null, null) > 0) {
				RndLog.i(TAG, "呵呵，我已经将表清空了 ");
				return true;
			}
		}
		return false;
	}

}
