package net.yangentao.util.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.ContentValues;
import android.database.Cursor;
import net.yangentao.util.StrBuilder;
import net.yangentao.util.Util;

/**
 * 键值对的存取<br/>
 * 可以做有限的筛选, 比如: keys(" 'key'>? ", "yang");<br/>
 * 
 * 
 * 
 * @author yangentao@gmail.com
 * 
 */
public class Table extends StrPutGet {
	private String TABLE;
	private InnerDB db;
	private int KEY_LIMIT = 512;

	protected Table(InnerDB db, String tableName, int keyLimit) {
		this.TABLE = tableName;
		this.db = db;
		this.KEY_LIMIT = keyLimit;
	}

	protected Table(InnerDB db, String tableName) {
		this(db, tableName, KvDb.KEY_LIMIT);
	}

	/**
	 * 返回所有的key集合
	 */
	public List<String> keys() {
		return keys(null);
	}

/**
	 * keys(" 'key'=?", "tom"); <br/>
	 * keys(" 'key'>'person/id/0/' and 'key'<'person/id/1/')
	 */
	public List<String> keys(String selection, String... args) {
		StrBuilder sb = new StrBuilder(128);
		sb.append("select key from ", TABLE);
		if (Util.notEmpty(selection)) {
			sb.append(" where ", selection);
		}
		String sql = sb.toString();
		Cursor c = db.query(sql, args);
		List<String> result = new ArrayList<String>(c.getCount() + 1);
		while (c.moveToNext()) {
			result.add(c.getString(0));
		}
		c.close();
		return result;
	}

	public boolean hasKey(String key) {
		long count = db.count(TABLE, "key=?", key);
		return count != 0;// !=0
	}

	private void keyCheck(String key) {
		if (Util.isEmpty(key)) {
			throw new IllegalArgumentException("key can not be empty.");
		}
		if (key.length() > KEY_LIMIT) {
			throw new IllegalArgumentException("key is limit to " + KEY_LIMIT);
		}
	}

	/**
	 * key非空<br/>
	 * 如果value是null, 会删除key对应的记录. 但是key可以是""
	 */
	@Override
	public void put(String key, String value) {
		keyCheck(key);
		if (value == null) {
			remove(key);
		} else {
			ContentValues values = new ContentValues(3);
			values.put("key", key);
			values.put("value", value);
			db.insertOrReplace(TABLE, values);
		}
	}

	public void remove(String key) {
		db.delete(TABLE, "key=?", key);
	}

	public void removeAll() {
		db.delete(TABLE);
	}

	/**
	 * removeAll(" 'key'='p'");<br/>
	 * removeAll(" 'key'< ?", "p");
	 */
	public void removeAll(String keySelection, String... args) {
		if (Util.isEmpty(keySelection)) {
			throw new IllegalArgumentException(
					"removeAll method, keySelection can not be empty.");
		}
		db.delete(TABLE, keySelection, args);
	}

	/**
	 * 没有会返回defValue
	 */
	@Override
	public String get(String key, String defValue) {
		keyCheck(key);
		Cursor c = db.queryTable(TABLE, "key=?", key);
		try {
			if (c.moveToFirst()) {
				String s = c.getString(c.getColumnIndex("value"));
				return s;
			}
		} finally {
			c.close();
		}
		return defValue;
	}

	/**
	 * 没有会返回null
	 */
	public String get(String key) {
		return get(key, (String) null);
	}

	public boolean transaction(Callable<Void> c) {
		return db.transaction(c);
	}

/**
	 * 在存的时候可以使用0和1(或'a','b')来界定区域<br/>
	 * query(" 'key'>'people/china/0/' AND 'key'<'people/china/1/'");<br/>
	 * =><br/>
	 * people/china/0/120 <br/>
	 * people/china/0/121 <br/>
	 * people/china/0/122 <br/>
	 * people/china/0/123 <br/>
	 */
	public List<RowItem> query(String selection, String... args) {
		StrBuilder sb = new StrBuilder(128);
		sb.append("select key, value from ", TABLE);
		if (Util.notEmpty(selection)) {
			sb.append(" where ", selection);
		}
		String sql = sb.toString();
		Cursor c = db.query(sql, args);
		List<RowItem> result = new ArrayList<RowItem>(c.getCount() + 1);
		while (c.moveToNext()) {
			result.add(new RowItem(c.getString(0), c.getString(1)));
		}
		c.close();
		return result;
	}

	public List<RowItem> queryAll() {
		StrBuilder sb = new StrBuilder(128);
		sb.append("select key, value from ", TABLE);
		String sql = sb.toString();
		Cursor c = db.query(sql);
		List<RowItem> result = new ArrayList<RowItem>(c.getCount() + 1);
		while (c.moveToNext()) {
			result.add(new RowItem(c.getString(0), c.getString(1)));
		}
		c.close();
		return result;
	}

	// public void test() {
	// this.group("people").put("100", "yangentao");
	// this.group("people").put("101", "doudou");
	//
	// List<RowItem> rows = this.group("people").query();
	// }

	public Group group(String groupName) {
		return new Group(this, groupName);
	}

	public static class Group {
		private Table table;
		private String gName;

		Group(Table table, String groupName) {
			this.table = table;
			this.gName = groupName;
			Util.failWhen(Util.empty(groupName), "groupName can not be null");
		}

		public void put(String key, String value) {
			table.put(gName + "/0/" + key, value);
		}

		public List<RowItem> query() {
			String s = StrBuilder.build("'key'>'", gName, "/0/'", " AND ",
					"'key'<'", gName, "/1/'");
			List<RowItem> items = table.query(s);
			int start = (gName + "/0/").length();
			for (RowItem item : items) {
				item.key = item.key.substring(start);
			}
			return items;
		}
	}

}
