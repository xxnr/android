package com.ksfc.newfarmer.db;


import com.google.gson.Gson;
import com.ksfc.newfarmer.beans.LoginResult.UserInfo;

import net.yangentao.util.SerialMap;

import com.ksfc.newfarmer.App;

public class Store {
    public static Gson mGson = new Gson();

    public static class User {
        private static SerialMap getUserMap() {
            return new SerialMap(App.getApp().getApplicationContext(), "user.db", "me");
        }

        public static void saveMe(UserInfo userInfo) {
            SerialMap map = getUserMap();
            map.put("userInfo", mGson.toJson(userInfo));
            map.close();
        }

        public static UserInfo queryMe() {
            SerialMap map = getUserMap();
            String s = map.get("userInfo");
            map.close();
            if (s != null) {
                return  mGson.fromJson(s,UserInfo.class);
            }
            return null;
        }

        public static void removeMe() {
            SerialMap map = getUserMap();
            map.remove("userInfo");
            map.close();
        }
    }


}
