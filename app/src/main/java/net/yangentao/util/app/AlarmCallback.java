package net.yangentao.util.app;

import org.json.JSONObject;

public interface AlarmCallback {
	public void onAlarm(long id, long atTime, JSONObject args);
}
