package net.yangentao.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import net.yangentao.util.sql.SQLiteHelper;

public class SerialMap implements SortedMap<String, String> {
	protected static final String DEF_DB = "serial_map";
	protected static final String DEF_TAB = "default_table";

	protected SqlHelper dbHelper;
	protected Context context;
	protected String tableName;

	protected static class SqlHelper extends SQLiteHelper {
		public SqlHelper(Context context, String name) {
			super(context, name, null, 2);
		}

		@Override
		protected JSONObject mapOne(Cursor c) {
			JSONObject js = new JSONObject();
			String[] names = c.getColumnNames();
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
			return js;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("hehe","数据库更新了");
			super.onUpgrade(db, oldVersion, newVersion);
		}
	}

	public SerialMap() {
	}

	public SerialMap(Context context) {
		open(context, DEF_DB, DEF_TAB);
	}

	public SerialMap(Context context, String dbName) {
		open(context, dbName, DEF_TAB);
	}

	public SerialMap(Context context, String dbName, String tableName) {
		open(context, dbName, tableName);
	}

	public void close() {
		context = null;
		tableName = null;
		dbHelper.close();
	}

	private void open(Context context, String dbName, String tableName) {
		if (this.context != null) {
			throw new IllegalAccessError("database already open ! ");
		}
		if (dbName == null) {
			dbName = DEF_DB;
		}
		if (tableName == null) {
			tableName = DEF_TAB;
		}
		dbHelper = new SqlHelper(context, dbName);
		dbHelper.getWritableDatabase();
		useTable(tableName);
	}

	public void useTable(String tableName) {
		this.tableName = tableName;
		createTable();
	}

	private void createTable() {
		StrBuilder sb = new StrBuilder(128);
		sb.append("create table if not exists ", tableName);
		sb.append(" (key varchar(512) primary key, value text);");
		dbHelper.execSQL(sb.toString());
	}

	public Context getContext() {
		return context;
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public void clear() {
		dbHelper.delete(tableName);
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return dbHelper.count(tableName, "key=?", (String) key) > 0;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value instanceof String) {
			return dbHelper.count(tableName, "value=?", (String) value) > 0;
		}
		return false;
	}

	public String get(String key, String defValue) {
		JSONObject jo = dbHelper.queryTableOne(tableName, "key=?", key);
		return jo.optString("value", defValue);
	}

	@Override
	public String get(Object key) {
		if (key instanceof String) {
			JSONObject jo = dbHelper.queryTableOne(tableName, "key=?",
					(String) key);
			return jo.optString("value", null);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return dbHelper.count(tableName) == 0;
	}

	public void putBool(String key, boolean value) {
		putOnly(key, String.valueOf(value));
	}

	public void putInt(String key, int value) {
		putOnly(key, String.valueOf(value));
	}

	public void putLong(String key, long value) {
		putOnly(key, String.valueOf(value));
	}

	public boolean getBool(String key, boolean defValue) {
		String v = get(key, String.valueOf(defValue));
		return Boolean.parseBoolean(v);
	}

	public int getInt(String key, int defValue) {
		String v = get(key, String.valueOf(defValue));
		return Integer.parseInt(v);
	}

	public long getLong(String key, long defValue) {
		String v = get(key, String.valueOf(defValue));
		return Long.parseLong(v);
	}

	@Override
	public String put(String key, String value) {
		String oldValue = get(key);
		putOnly(key, value);
		return oldValue;
	}

	private void putOnly(String key, String value) {
		ContentValues values = new ContentValues(2);
		values.put("key", key);
		values.put("value", value);
		long id = dbHelper.insertOrReplace(tableName, values);
		if (id == -1) {
			System.err.println("SerialMap.class error insert! " + key + " : "
					+ value);
		}
	}

	@Override
	public void putAll(final Map<? extends String, ? extends String> otherMap) {

		dbHelper.transaction(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				for (Entry<? extends String, ? extends String> e : otherMap
						.entrySet()) {
					putOnly(e.getKey(), e.getValue());
				}
				return null;
			}
		});
	}

	@Override
	public String remove(Object key) {
		if (key instanceof String) {
			String oldValue = get(key);
			dbHelper.delete(tableName, "key=?", (String) key);
			return oldValue;
		}
		return null;
	}

	@Override
	public int size() {
		return (int) dbHelper.count(tableName);
	}

	private static Comparator<String> cmp = new Comparator<String>() {

		@Override
		public int compare(String arg0, String arg1) {
			if (arg0 == null) {
				if (arg1 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			return arg0.compareTo(arg1);
		}
	};

	@Override
	public Comparator<? super String> comparator() {
		return cmp;
	}

	private static class SerialMapEntry implements Map.Entry<String, String>,
			Comparable<SerialMapEntry> {
		private String key;
		private String value;

		public SerialMapEntry() {

		}

		public SerialMapEntry(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public String setValue(String value) {
			String oldV = this.value;
			this.value = value;
			return oldV;
		}

		@Override
		public int compareTo(SerialMapEntry o) {
			int n = cmp.compare(key, o.key);
			if (n != 0) {
				return n;
			}
			return cmp.compare(value, o.value);
		}

	}

	@NonNull
	@Override
	public Set<Entry<String, String>> entrySet() {
		TreeSet<Entry<String, String>> s = new TreeSet<Entry<String, String>>();
		try {
			JSONArray arr = dbHelper.queryTableMulti(tableName, null);
			for (int i = 0; i < arr.length(); ++i) {
				JSONObject jo = arr.getJSONObject(i);
				SerialMapEntry e = new SerialMapEntry(jo.getString("key"),
						jo.getString("value"));
				s.add(e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	@Override
	public String firstKey() {
		JSONObject jo = dbHelper.queryOne("select key from " + tableName
				+ " order by key asc limit 1;");
		return jo.optString("key", null);
	}

	@Override
	public String lastKey() {
		JSONObject jo = dbHelper.queryOne("select key from " + tableName
				+ " order by key desc limit 1;");
		return jo.optString("key", null);
	}

	@NonNull
	@Override
	public Collection<String> values() {
		JSONArray jarr = dbHelper.queryTableMulti(tableName, null);
		ArrayList<String> list = new ArrayList<String>(jarr.length());
		try {
			for (int i = 0; i < jarr.length(); ++i) {
				JSONObject jo = jarr.getJSONObject(i);
				list.add(jo.getString("value"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Set<String> keySet() {
		JSONArray jarr = dbHelper.queryTableMulti(tableName, null);
		Set<String> s = new TreeSet<String>();
		try {
			for (int i = 0; i < jarr.length(); ++i) {
				JSONObject jo = jarr.getJSONObject(i);
				s.add(jo.getString("key"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	private SortedMap<String, String> toSortedMap(JSONArray arr) {
		TreeMap<String, String> m = new TreeMap<String, String>();
		try {
			for (int i = 0; i < arr.length(); ++i) {
				JSONObject jo = arr.getJSONObject(i);
				m.put(jo.getString("key"), jo.getString("value"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	@NonNull
	@Override
	public SortedMap<String, String> headMap(String toKey) {
		JSONArray arr = dbHelper.queryTableMulti(tableName, "key<?", toKey);
		return toSortedMap(arr);
	}

	@NonNull
	@Override
	public SortedMap<String, String> subMap(String fromKey, String toKey) {
		JSONArray arr = dbHelper.queryTableMulti(tableName, "key>=? and key<?",
				fromKey, toKey);
		return toSortedMap(arr);
	}

	@NonNull
	@Override
	public SortedMap<String, String> tailMap(String fromKey) {
		JSONArray arr = dbHelper.queryTableMulti(tableName, "key>=?", fromKey);
		return toSortedMap(arr);
	}

	// extends

	private ArrayList<Map.Entry<String, String>> toArrayList(JSONArray arr) {
		ArrayList<Map.Entry<String, String>> m = new ArrayList<Map.Entry<String, String>>(
				arr.length());
		try {
			for (int i = 0; i < arr.length(); ++i) {
				JSONObject jo = arr.getJSONObject(i);
				m.add(new SerialMapEntry(jo.getString("key"), jo
						.getString("value")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	public Map.Entry<String, String> first() {
		JSONObject jo = dbHelper.queryTableOne(tableName, null);
		if (jo != null) {
			try {
				return new SerialMapEntry(jo.getString("key"),
						jo.getString("value"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<Map.Entry<String, String>> all() {
		return list();
	}

	public List<Map.Entry<String, String>> list() {
		JSONArray arr = dbHelper.queryTableMulti(tableName, null);
		return toArrayList(arr);
	}

	public List<Map.Entry<String, String>> headList(String toKey) {
		JSONArray arr = dbHelper.queryTableMulti(tableName, "key<?", toKey);
		return toArrayList(arr);
	}

	public List<Map.Entry<String, String>> subList(String fromKey, String toKey) {
		JSONArray arr = dbHelper.queryTableMulti(tableName, "key>=? and key<?",
				fromKey, toKey);
		return toArrayList(arr);
	}

	public List<Map.Entry<String, String>> tailList(String fromKey) {
		JSONArray arr = dbHelper.queryTableMulti(tableName, "key>=?", fromKey);
		return toArrayList(arr);
	}

	public Categroy categroy(String categroy) {
		return new Categroy(categroy);
	}

	public class Categroy {
		private String beginKey;
		private String endKey;

		public Categroy(String categroy) {
			beginKey = categroy + "/0/";
			endKey = categroy + "/1/";
		}

		public Categroy categroy(String categroy) {
			return new Categroy(beginKey + categroy);
		}

		public void removeAll() {
			dbHelper.delete(tableName, "key>=? and key<?", beginKey, endKey);
		}

		public void remove(String subKey) {
			SerialMap.this.remove(beginKey + subKey);
		}

		public List<String> values() {
			JSONArray jarr = dbHelper.queryTableMulti(tableName,
					"key>=? and key<?", beginKey, endKey);
			ArrayList<String> list = new ArrayList<String>(jarr.length());
			try {
				for (int i = 0; i < jarr.length(); ++i) {
					JSONObject jo = jarr.getJSONObject(i);
					list.add(jo.getString("value"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}

		// public List<Entry<String, String>> all(){
		// return subList(beginKey, endKey);
		// }

		public void put(String subKey, String value) {
			SerialMap.this.put(beginKey + subKey, value);
		}

	}

}
