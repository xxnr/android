package net.yangentao.util.sql;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.yangentao.util.StrSerializable;
import net.yangentao.util.Util;
import net.yangentao.util.json.JsonSerializable;

public abstract class StrPutGet {

	public abstract String get(String key, String defValue);

	public abstract void put(String key, String value);

	public void put(String key, JSONObject obj) {
		put(key, obj.toString());
	}

	public void put(String key, JSONArray jarr) {
		put(key, jarr.toString());
	}

	public void putBool(String key, boolean b) {
		put(key, Boolean.valueOf(b).toString());
	}

	public void putInt(String key, int n) {
		put(key, Integer.valueOf(n).toString());
	}

	public void putLong(String key, long value) {
		put(key, Long.valueOf(value).toString());
	}

	public void putFloat(String key, float value) {
		put(key, Float.valueOf(value).toString());
	}

	public void putDouble(String key, double value) {
		put(key, Double.valueOf(value).toString());
	}

	public void putJsonSerial(String key, JsonSerializable jsonModel) {
		put(key, jsonModel.toJson());
	}

	public void putStrSerial(String key, StrSerializable strSerial) {
		put(key, strSerial.toStr());
	}

	public JSONObject getJSONObject(String key) {
		String s = get(key, null);
		if (Util.notEmpty(s)) {
			try {
				return new JSONObject(s);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public JSONArray getJSONArray(String key) {
		String s = get(key, null);
		if (Util.notEmpty(s)) {
			try {
				return new JSONArray(s);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public StrSerializable getStrSerial(String key,
			Class<? extends StrSerializable> cls) {
		try {
			String s = get(key, null);
			if (s == null) {
				return null;
			}
			StrSerializable js = cls.newInstance();
			js.fromStr(s);
			return js;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JsonSerializable getJsonSerial(String key,
			Class<? extends JsonSerializable> cls) {
		try {
			String s = get(key, null);
			if (s == null) {
				return null;
			}
			JsonSerializable js = cls.newInstance();
			js.fromJson(s);
			return js;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getInt(String key, int defValue) {
		String s = get(key, null);
		if (Util.notEmpty(s)) {
			return Integer.valueOf(s).intValue();
		}
		return defValue;
	}

	public long getLong(String key, long defValue) {
		String s = get(key, null);
		if (Util.notEmpty(s)) {
			return Long.valueOf(s).longValue();
		}
		return defValue;
	}

	public boolean getBool(String key, boolean defValue) {
		String s = get(key, null);
		if (Util.notEmpty(s)) {
			return Boolean.valueOf(s).booleanValue();
		}
		return defValue;
	}

	public float getFloat(String key, float defValue) {
		String s = get(key, null);
		if (Util.notEmpty(s)) {
			return Float.valueOf(s).floatValue();
		}
		return defValue;
	}

	public double getDouble(String key, double defValue) {
		String s = get(key, null);
		if (Util.notEmpty(s)) {
			return Double.valueOf(s).doubleValue();
		}
		return defValue;
	}

}