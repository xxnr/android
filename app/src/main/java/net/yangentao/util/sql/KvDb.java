package net.yangentao.util.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import net.yangentao.util.StrBuilder;
import net.yangentao.util.app.App;

public class KvDb {
	public static final int KEY_LIMIT = 512;
	private static final String DEFAULT_TABLE = "default_table";

	private InnerDB db;
	private Map<String, Table> tableMap = new ConcurrentHashMap<>();

	public KvDb(Context c, String dbName) {
		db = new InnerDB(c, dbName);
	}

	public KvDb(String dbName) {
		this(App.getApp(), dbName);
	}

	public Table tab(String name) {
		Table t = tableMap.get(name);
		if (t == null) {
			db.execSQL(createTableSql(name));
			t = new Table(db, name);
			tableMap.put(name, t);
		}
		return t;
	}

	public Table tab() {
		return tab(DEFAULT_TABLE);
	}

	private String createTableSql(String tableName) {
		StrBuilder sb = new StrBuilder(128);
		sb.append("create table if not exists ", tableName);
		sb.append(" (key varchar(", KEY_LIMIT, ") primary key, value text)");
		return sb.toString();
	}

	private static KvDb defDb;

	public static void setDefaultDb(KvDb mapdb) {
		defDb = mapdb;
	}

	public static KvDb defaultDb() {
		return defDb;
	}

	// public static void test() {
	// KvDb db = new KvDb("mydb");
	// Table t = db.tab("people");
	// t.put("100", "yang");
	// t.put("101", "zhangsan");
	// t.put("102", "lisi");
	// t.put("103", "wangwu");
	// Group g = t.group("leader");
	// g.put("100", "yang");
	// g.put("101", "zhangsan");
	// List<RowItem> leaders = db.tab("people").group("leader").query();
	// }

}
