package net.yangentao.util.app;

import java.util.List;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ksfc.newfarmer.App;

import net.yangentao.util.XLog;
import net.yangentao.util.sql.KvDb;
import net.yangentao.util.sql.RowItem;
import net.yangentao.util.sql.Table;

public class AlarmUtil {
	public static final String CATEGORY = "net.winchannel.wincrm.ALARM_RECEIVER";
	public static final String ID = "id";
	public static final String AT_TIME = "atTime";
	public static final String ARGS = "args";
	public static final String CLS = "class";

	public static final String ALARM_DB = "alarm_db";
	public static final String ALARM_TABLE = "alarm_table";

	static Context getApp() {
		return App.getApp();
	}

	private static KvDb db = null;
	private static Table tab = null;

	// 开机后, 重新设置
	public synchronized static void init(Context c) {
		db = new KvDb(c, ALARM_DB);
		tab = db.tab(ALARM_TABLE);

		try {
			List<RowItem> rows = tab.queryAll();
			for (RowItem item : rows) {
				JSONObject obj = new JSONObject(item.value);
				long id = obj.getLong(ID);
				long atTime = obj.getLong(AlarmUtil.AT_TIME);
				setAlarm(id, atTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 通知执行后, 从外存删除掉
	public synchronized static void remove(long id) {
		tab.remove("" + id);
		cancel(id);
	}

	public static boolean has(long id) {
		return tab.get("" + id) != null;
	}

	private static void scheduleRunnable(long id, long atTime,
			String clsCallback, JSONObject args) {
		try {
			JSONObject obj = new JSONObject();
			obj.put(ID, id);
			obj.put(AT_TIME, atTime);
			obj.put(CLS, clsCallback);
			obj.put(ARGS, args);

			tab.put("" + id, obj);// save info to db
			setAlarm(id, atTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void scheduleRunnable(long id, long atTime,
			Class<? extends AlarmCallback> clsCallback, JSONObject args) {
		scheduleRunnable(id, atTime, clsCallback.getName(), args);
	}

	private static void repeatAlarm(long id, long atTime, long interval) {
		AlarmManager am = (AlarmManager) getApp().getSystemService(
				Context.ALARM_SERVICE);
		Intent i = new Intent(getApp(), AlarmReceiver.class);
		i.addCategory(CATEGORY);
		i.setAction("" + id);
		PendingIntent pi = PendingIntent.getBroadcast(getApp(), 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, atTime, interval, pi);
	}

	private static void setAlarm(long id, long atTime) {
		AlarmManager am = (AlarmManager) getApp().getSystemService(
				Context.ALARM_SERVICE);
		Intent i = new Intent(getApp(), AlarmReceiver.class);
		i.addCategory(CATEGORY);
		i.setAction("" + id);
		PendingIntent pi = PendingIntent.getBroadcast(getApp(), 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		am.set(AlarmManager.RTC_WAKEUP, atTime, pi);
	}

	private static void cancel(long id) {
		XLog.d("cancel Notify: ", id);
		AlarmManager am = (AlarmManager) getApp().getSystemService(
				Context.ALARM_SERVICE);
		Intent i = new Intent(getApp(), AlarmReceiver.class);
		i.addCategory(CATEGORY);
		i.setAction("" + id);
		PendingIntent pi = PendingIntent.getBroadcast(getApp(), 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pi);
	}
}
