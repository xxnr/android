package com.ksfc.newfarmer.db;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.ksfc.newfarmer.protocol.beans.LoginResult.UserInfo;

import net.yangentao.util.SerialMap;
import net.yangentao.util.app.App;

public class Store {

	public static class User {
		private static SerialMap getUserMap() {
			return new SerialMap(App.getApp(), "user.db", "me");
		}

		public static void saveMe(UserInfo userInfo) {
			SerialMap map = getUserMap();
			map.put("userinfo", JSON.toJSONString(userInfo));
			map.close();
			// MsgCenter.fireNull(MsgID.USERINFO_UPDATED);
		}

		public static UserInfo queryMe() {
			SerialMap map = getUserMap();
			String s = map.get("userinfo");
			map.close();
			if (s != null) {
				return JSON.parseObject(s, UserInfo.class);
			}
			return null;
		}

		public static void removeMe() {
			SerialMap map = getUserMap();
			map.remove("userinfo");
			map.close();
		}
	}

	public static class Push {
		private static SerialMap getMap() {
			return new SerialMap(App.getApp(), "push.db", "push");
		}

		public static boolean isBind() {
			SerialMap map = getMap();
			boolean b = map.getBool("push.bind", false);
			map.close();
			return b;
		}

		public static void setBind(boolean bind, String appid, String userid,
				String channelid) {
			SerialMap map = getMap();
			map.putBool("push.bind", bind);
			map.put("push.appid", appid);
			map.put("push.userid", userid);
			map.put("push.channelid", channelid);
			map.close();
		}

		public static String getAppId() {
			SerialMap map = getMap();
			String appid = map.get("push.appid", null);
			map.close();
			return appid;
		}

		public static String getUserId() {
			SerialMap map = getMap();
			String userid = map.get("push.userid", null);
			map.close();
			return userid;
		}

		public static String getChannelId() {
			SerialMap map = getMap();
			String channelid = map.get("push.channelid", null);
			map.close();
			return channelid;
		}
	}

	public static class PushMsg {
		private static SerialMap getMap() {
			return new SerialMap(App.getApp(), "push.db", "msg");
		}

		public static void put(int code, long timestamp, String msg) {
			SerialMap m = getMap();
			m.categroy("" + code).put("" + timestamp, msg);
			m.close();
		}

		public static List<String> getAll(int code) {
			SerialMap m = getMap();
			List<String> values = m.categroy("" + code).values();
			m.close();
			return values;
		}

		public static void removeAll(int code) {
			SerialMap m = getMap();
			m.categroy("" + code).removeAll();
			m.close();
		}
	}

}
