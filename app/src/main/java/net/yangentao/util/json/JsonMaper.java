package net.yangentao.util.json;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.yangentao.util.TypeWrap;
import net.yangentao.util.Util;

/**
 * 目前不支持二维数组的映射, List<List<?>>
 * 
 * @author yet
 * 
 */
class JsonMaper {

	private static class FieldInfo {
		public Field field;
		public Class<?> cls;
		public String nameValue;
		public boolean require = false;

		public FieldInfo(String colName, Field f, boolean require) {
			this.nameValue = colName;
			this.field = f;
			this.require = require;
			cls = f.getType();
		}
	}

	private static Map<String, List<FieldInfo>> clsMap = new ConcurrentHashMap<>();

	/**
	 * List<Person> people = fromJsonArray(Person.class, jsonArray);
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList fromJSONArray(JSONArray jsonArray,
			Class<?> elementClass) throws Exception {
		if (jsonArray == null || jsonArray.length() == 0) {
			return new ArrayList();
		}
		ArrayList list = new ArrayList(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); ++i) {
			Object jsonValue = jsonArray.get(i);
			if (jsonValue instanceof JSONObject) {
				Object obj = fromJSONObjectByClass((JSONObject) jsonValue,
						elementClass);
				list.add(obj);
			} else if (jsonValue instanceof JSONArray) {// 多维数组
				ArrayList subArr = fromJSONArray((JSONArray) jsonValue,
						elementClass);
				list.add(subArr);
			} else {
				list.add(jsonValue);
			}
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ArrayList fromJSONArrayByFieldType(Type genericType,
			JSONArray jsonArray) throws Exception {
		if (jsonArray == null || jsonArray.length() == 0) {
			return new ArrayList();
		}
		TypeWrap tw = new TypeWrap(genericType);
		if (tw.isParameterizedList()) {// json 数组 ,必须映射成List或ArrayList
			ArrayList arrayList = new ArrayList(jsonArray.length());
			Object jObj = jsonArray.get(0);
			if (jObj instanceof JSONArray) {// json数组, 递归调用
				for (int i = 0; i < jsonArray.length(); ++i) {
					// tw.getActuralType0()如果不是参数化的List, 会在递归是报错:
					// if(tw.isParameterizedList()){...}
					ArrayList item = fromJSONArrayByFieldType(
							tw.getActuralType0(), jsonArray.getJSONArray(i));
					arrayList.add(item);
				}
			} else if (jObj instanceof JSONObject) {
				for (int i = 0; i < jsonArray.length(); ++i) {
					Object ob = fromJSONObjectByClass(
							jsonArray.getJSONObject(i),
							(Class) tw.getActuralType0());
					arrayList.add(ob);
				}
			} else {
				for (int i = 0; i < jsonArray.length(); ++i) {
					Object ob = jsonArray.get(i);
					arrayList.add(ob);
				}
			}
			return arrayList;
		}
		throw new Exception(
				"Json-array-field must be defined List<XXX> or ArrayList<xxx>");
	}

	/**
	 * Person p = new Person(); fromJson(jsonObject, p);
	 */
	@SuppressWarnings("rawtypes")
	public static void fromJSONObject(JSONObject json, Object dest)
			throws Exception {
		List<FieldInfo> fieldInfos = get(dest.getClass());
		for (FieldInfo fi : fieldInfos) {
			Object jVal = null;
			jVal = json.opt(fi.nameValue);
			if (jVal == null) {// 没有出现该字段,
								// ---如果出现了,但值是null,jVal的值不是null,而是一个Object对象.
								// 参考JSONObject.isNull(String)方法源代码
				if (fi.require) {// 要求有, 但是没有, 拋异常
					throw new JSONException("require field no found: "
							+ fi.nameValue);
				}
				// 可选字段, 没有出现, 不处理
				continue;
			}
			// jVal != null
			if (json.isNull(fi.nameValue)) {// 出现了该字段, 是值是null; { name:null,
											// age:32}
				fi.field.set(dest, null);
			} else if (jVal instanceof JSONObject) {
				Object objVal = fromJSONObjectByClass((JSONObject) jVal, fi.cls);
				fi.field.set(dest, objVal);
			} else if (jVal instanceof JSONArray) {
				JSONArray jArr = (JSONArray) jVal;
				Type type = fi.field.getGenericType();
				ArrayList arrList = fromJSONArrayByFieldType(type, jArr);
				fi.field.set(dest, arrList);
			} else {
				try {
					fi.field.set(dest, jVal);
				} catch (Exception e) {
					System.err.println(jVal);
					System.err.println(jVal.getClass().getName());
					System.err.println(fi.field.getName());
					throw e;
				}
			}
		}
	}

	public static Object fromJSONStringByClass(String json, Class<?> dataCls)
			throws Exception {
		JSONObject jo = new JSONObject(json);
		return fromJSONObjectByClass(jo, dataCls);
	}

	/**
	 * Person p = fromJson(jsonObject, Person.class);
	 */
	public static Object fromJSONObjectByClass(JSONObject json, Class<?> dataCls)
			throws Exception {
		Object data = dataCls.newInstance();
		fromJSONObject(json, data);
		return data;
	}

	public static void fromJSONString(String json, Object dest)
			throws Exception {
		JSONObject jo = new JSONObject(json);
		fromJSONObject(jo, dest);
	}

	synchronized private static List<FieldInfo> get(Class<?> cls)
			throws Exception {
		List<FieldInfo> fieldList = clsMap.get(cls.getName());
		if (fieldList == null) {
			Field[] fields = cls.getFields();
			fieldList = new ArrayList<>(fields.length);
			for (Field field : fields) {
				JsonColumn mf = field.getAnnotation(JsonColumn.class);
				if (mf != null) {
					String colName = field.getName();
					if (Util.notEmpty(mf.name())) {
						colName = mf.name();
					}
					fieldList.add(new FieldInfo(colName, field, mf.require()));
				}
			}
			clsMap.put(cls.getName(), fieldList);
		}
		return fieldList;
	}

	public static JSONArray toJSONArray(Collection<?> list) throws Exception {
		JSONArray array = new JSONArray();
		toJSONArray(list, array);
		return array;
	}

	public static void toJSONArray(Collection<?> list, JSONArray array)
			throws Exception {
		if (list == null || list.isEmpty()) {
			return;
		}
		for (Object t : list) {
			if (t == null || isBasicType(t.getClass())) {
				array.put(t);
			} else if (t instanceof List) {
				JSONArray arr = toJSONArray((List<?>) t);
				array.put(arr);
			} else {
				JSONObject obj = toJSONObject(t);
				array.put(obj);
			}
		}
	}

	/**
	 * 字符串/数字/布尔类型返回true, 其他类型返回false
	 * 
	 * @param cls
	 * @return
	 */
	private static boolean isBasicType(Class<?> cls) {
		if (cls.isPrimitive()) {
			return true;
		}
		if (cls == String.class) {
			return true;
		}
		if (cls == Boolean.class) {
			return true;
		}
		if (Number.class.isAssignableFrom(cls)) {
			return true;
		}
		return false;
	}

	/**
	 * <pre>
	 * 不能对基本类型调用此方法, 比如 toJSon("abc");
	 * 只能对成员被@JsonField修饰的对象调用此方法,
	 * class Person{
	 * //@JsonField
	 * public int id;
	 * //@JsonField
	 * public String name;
	 * }
	 * </pre>
	 */
	public static JSONObject toJSONObject(Object data) throws Exception {
		JSONObject json = new JSONObject();
		toJSONObject(data, json);
		return json;
	}

	/**
	 * <pre>
	 * 不能对基本类型调用此方法, 比如 toJSon("abc");
	 * 只能对成员被@JsonField修饰的对象调用此方法,
	 * class Person{
	 * //@JsonField
	 * public int id;
	 * //@JsonField
	 * public String name;
	 * }
	 * </pre>
	 */
	public static void toJSONObject(Object data, JSONObject json)
			throws Exception {
		List<FieldInfo> fieldInfos = get(data.getClass());
		for (FieldInfo fi : fieldInfos) {
			Object val = fi.field.get(data);
			if (val == null || isBasicType(val.getClass())) {
				json.put(fi.nameValue, val);
			} else if (val instanceof List) {
				JSONArray jArr = toJSONArray((List<?>) val);
				json.put(fi.nameValue, jArr);
			} else {
				json.put(fi.nameValue, toJSONObject(val));
			}
		}
	}

	public static String toJSONString(Object data) throws Exception {
		JSONObject jo = toJSONObject(data);
		return jo.toString();
	}

	public static String toJSONString(Object data, int indent) throws Exception {
		JSONObject jo = toJSONObject(data);
		return jo.toString(indent);
	}

}
