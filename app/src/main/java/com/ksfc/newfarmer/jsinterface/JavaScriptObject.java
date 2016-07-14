package com.ksfc.newfarmer.jsinterface;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.ksfc.newfarmer.activitys.LoginActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;


public class JavaScriptObject {
    Context mContxt;

    //sdk17版本以上加上注解
    public JavaScriptObject(Context mContxt) {
        this.mContxt = mContxt;
    }

    //启动一个页面的function
    @JavascriptInterface
    public void startActivity(String activity) {
        Class<?> aClass;
        try {
            aClass = Class.forName("com.ksfc.newfarmer.activitys." + activity);
            Intent intent = new Intent(mContxt, aClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContxt.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //启动一个页面并且携带一个参数的function
    @JavascriptInterface
    public void startActivity(String activity, String key, String value) {
        Class<?> aClass;
        try {
            if (StringUtil.checkStr(key)&&StringUtil.checkStr(value)){
                aClass = Class.forName("com.ksfc.newfarmer.activitys." + activity);
                Intent intent = new Intent(mContxt, aClass);
                intent.putExtra(key, value);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContxt.startActivity(intent);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //需要用户token的页面
    @JavascriptInterface
    public String getToken() {
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null && StringUtil.checkStr(userInfo.token)) {
            return userInfo.token;
        } else {
            IntentUtil.activityForward(mContxt, LoginActivity.class, null, false);
        }
        return null;
    }

    //调分享

}