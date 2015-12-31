package net.yangentao.util.json;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import net.yangentao.util.Util;

public class Json {

	public static class Obj {
		public static void fromJ(JSONObject json, Object dest) throws Exception {
			JsonMaper.fromJSONObject(json, dest);
		}

		public static void fromJ(String json, Object dest) throws Exception {
			JsonMaper.fromJSONObject(new JSONObject(json), dest);
		}

		public static <T> T fromJ(String json, Class<T> dataClass)
				throws Exception {
			return fromJ(new JSONObject(json), dataClass);
		}

		@SuppressWarnings("unchecked")
		public static <T> T fromJ(JSONObject json, Class<T> dataCls)
				throws Exception {
			return (T) JsonMaper.fromJSONObjectByClass(json, dataCls);
		}

		public static void toJ(Object data, JSONObject json) throws Exception {
			JsonMaper.toJSONObject(data, json);
		}

		public static String toJ(Object data) throws Exception {
			return JsonMaper.toJSONString(data);
		}

		public static String toJ(Object data, int indent) throws Exception {
			return JsonMaper.toJSONString(data, indent);
		}
	}

	public static class Arr {
		@SuppressWarnings("rawtypes")
		public static ArrayList fromJ(JSONArray jsonArray, Class<?> elementClass)
				throws Exception {
			return JsonMaper.fromJSONArray(jsonArray, elementClass);
		}

		public static JSONArray toJ(Collection<?> list) throws Exception {
			return JsonMaper.toJSONArray(list);
		}
	}

	private static char firstChar(String json) {
		if (Util.isEmpty(json)) {
			return 0;
		}
		for (int i = 0; i < json.length(); ++i) {
			char ch = json.charAt(i);
			if (Character.isWhitespace(ch)) {
				continue;
			} else {
				return ch;
			}
		}
		return 0;
	}

	public static boolean isArray(String json) {
		return firstChar(json) == '[';
	}

	public static boolean isObject(String json) {
		return firstChar(json) == '{';
	}

}
