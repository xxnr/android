package net.yangentao.util.app;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import net.yangentao.util.Util;
import net.yangentao.util.XLog;
import net.yangentao.util.sql.KvDb;
import net.yangentao.util.sql.Table;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		XLog.d("AlarmReceiver.onReceive ");

		if (intent.getCategories().contains(AlarmUtil.CATEGORY)) {
			long id = Long.valueOf(intent.getAction());
			KvDb db = new KvDb(context, AlarmUtil.ALARM_DB);
			Table t = db.tab(AlarmUtil.ALARM_TABLE);
			String s = t.get("" + id);
			if (Util.notEmpty(s)) {
				try {
					JSONObject obj = new JSONObject(s);
					JSONObject args = obj.optJSONObject(AlarmUtil.ARGS);
					long atTime = obj.getLong(AlarmUtil.AT_TIME);
					String cls = obj.getString(AlarmUtil.CLS);
					AlarmCallback callback = (AlarmCallback) Class.forName(cls)
							.newInstance();
					callback.onAlarm(id, atTime, args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			t = null;
			db = null;
		}
	}
}
