package com.ksfc.newfarmer.db;


import com.alibaba.fastjson.JSON;
import com.ksfc.newfarmer.http.beans.LoginResult.UserInfo;

import net.yangentao.util.SerialMap;
import com.ksfc.newfarmer.App;

public class Store {

	public static class User {
		private static SerialMap getUserMap() {
			return new SerialMap(App.getApp().getApplicationContext(), "user.db", "me");
		}

		public static void saveMe(UserInfo userInfo) {
			SerialMap map = getUserMap();
			map.put("userinfo", JSON.toJSONString(userInfo));
			map.close();
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



}
