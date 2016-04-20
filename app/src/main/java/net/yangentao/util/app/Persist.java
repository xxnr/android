package net.yangentao.util.app;

import java.util.Set;

import net.yangentao.util.PreferenceUtil;

/**
 * 将值保存到默认配置文件, 依赖App类
 * 
 * @author yangentao
 * 
 */
public class Persist {
	private static PreferenceUtil p = new PreferenceUtil(App.getApp(), "persist.global.preference");

	public static void putString(String key, String value) {
		p.putString(key, value);
	}

	public String getString(String key, String defValue) {
		return p.getString(key, defValue);
	}

	public void putBool(String key, boolean value) {
		p.putBool(key, value);
	}

	public boolean getBool(String key, boolean defValue) {
		return p.getBool(key, defValue);
	}

	public void putInt(String key, int value) {
		p.putInt(key, value);
	}

	public int getInt(String key, int defValue) {
		return p.getInt(key, defValue);
	}

	public void putLong(String key, long value) {
		p.putLong(key, value);
	}

	public long getLong(String key, long defValue) {
		return p.getLong(key, defValue);
	}

	public Set<String> getStringSet(String key, Set<String> defValue) {
		return p.getStringSet(key, defValue);
	}

	public void putStringSet(String key, Set<String> value) {
		p.putStringSet(key, value);
	}

	public float getFloat(String key, float defValue) {
		return p.getFloat(key, defValue);
	}

	public void putFloat(String key, float value) {
		p.putFloat(key, value);
	}

}
