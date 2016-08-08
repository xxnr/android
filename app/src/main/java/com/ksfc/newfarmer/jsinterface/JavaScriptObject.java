package com.ksfc.newfarmer.jsinterface;

import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.ksfc.newfarmer.activitys.CampaignDetailActivity;
import com.ksfc.newfarmer.activitys.LoginActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.event.WebShareUrlEvent;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;


public class JavaScriptObject {
    CampaignDetailActivity mContxt;

    //sdk17版本以上加上注解
    public JavaScriptObject(CampaignDetailActivity mContxt) {
        this.mContxt = mContxt;
    }


    //启动一个页面的function
    @JavascriptInterface
    public void startActivity(String activity) {
        Class<?> aClass;
        try {
            RndLog.d("JavaScriptObject", "startActivity:" + activity);
            aClass = Class.forName("com.ksfc.newfarmer.activitys." + activity);
            Intent intent = new Intent(mContxt, aClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContxt.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //启动一个页面并且携带参数的function
    @JavascriptInterface
    public void startActivity(String activity, String params) {
        Class<?> aClass;
        try {
            if (StringUtil.checkStr(params)) {
                aClass = Class.forName("com.ksfc.newfarmer.activitys." + activity);
                Intent intent = new Intent(mContxt, aClass);
                JSONObject jsonObject = new JSONObject(params);
                JSONArray jsonArray = jsonObject.getJSONArray("params");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String key = jsonObject1.getString("key");
                    String value = jsonObject1.getString("value");
                    if (StringUtil.checkStr(key) && StringUtil.checkStr(value)) {
                        intent.putExtra(key, value);
                    }
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContxt.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //需要用户token的页面
    @JavascriptInterface
    public String getToken() {
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null && StringUtil.checkStr(userInfo.token)) {
            RndLog.d("JavaScriptObject", "token:" + userInfo.token);
            return userInfo.token;
        }
        return null;
    }

    @JavascriptInterface
    public void toLogin() {
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null && StringUtil.checkStr(userInfo.token)) {
            return;
        }
        IntentUtil.activityForward(mContxt, LoginActivity.class, null, false);
    }

    @JavascriptInterface
    public boolean isLogin() {
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        boolean isLogin = userInfo != null && StringUtil.checkStr(userInfo.token);
        RndLog.d("JavaScriptObject", "isLogin:" + isLogin);
        return isLogin;
    }

    @JavascriptInterface
    public void shareUrl(String shareUrl,boolean isNeedRefresh) {
        RndLog.d("JavaScriptObject", "isNeedRefresh:" + isNeedRefresh);
        RndLog.d("JavaScriptObject", "shareUrl:" + shareUrl);

        EventBus.getDefault().post(new WebShareUrlEvent(shareUrl,isNeedRefresh));
    }

    @JavascriptInterface
    public void shareUrl(String shareUrl) {
        shareUrl(shareUrl,false);
    }
}