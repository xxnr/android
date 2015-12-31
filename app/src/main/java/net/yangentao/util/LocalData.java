package net.yangentao.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有操作涉及到的 key 不能是null <br/>
 * put操作涉及到的 value 不能是null <br/>
 * get/remove操作返回null表示没有(之前没有)key对应的值<br/>
 * 
 * @author yangentao 内存临时数据
 */
public class LocalData {

	private static Map<String, Object> map = new ConcurrentHashMap<String, Object>(
			8);//

	/**
	 * @param key
	 *            not null
	 * @param value
	 *            不能是null
	 */
	public static void put(String key, Object value) {
		if (value == null) {
			throw new IllegalArgumentException(
					"TempData.pub(String key, String value), value MUST NOT be null! you can use remove method to clear some key.");
		}
		map.put(key, value);
	}

	public static boolean has(String key) {
		return map.containsKey(key);
	}

	public static Object remove(String key) {
		return map.remove(key);
	}

	public static Object getAndRemove(String key) {
		return map.remove(key);
	}

	public static Object get(String key) {
		return map.get(key);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String key, T defVal) {
		Object obj = map.get(key);
		return obj == null ? defVal : (T) obj;
	}

	public static Integer getInt(String key) {
		return (Integer) get(key);
	}

	public static int getInt(String key, int defVal) {
		return get(key, defVal);
	}

	public static String getString(String key) {
		return (String) get(key);
	}

	public static String getString(String key, String defVal) {
		return get(key, defVal);
	}

	public static Boolean getBool(String key) {
		return (Boolean) get(key);
	}

	public static boolean getBool(String key, boolean defValue) {
		return get(key, defValue);
	}
}
