package com.ksfc.newfarmer.test;

import android.content.Intent;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.ksfc.newfarmer.activitys.IntegralTallActivity;
import com.ksfc.newfarmer.db.Store;

/**
 * Created by CAI on 2016/7/5.
 */
public class JavaScriptObject {
    WebToLoginActivity mActivity;
    Handler handler;

    //sdk17版本以上加上注解
    public JavaScriptObject(WebToLoginActivity mActivity, Handler handler) {
        this.mActivity = mActivity;
        this.handler = handler;
    }

    @JavascriptInterface  //向h5传token
    public String fun1FromAndroid() {
        String token = Store.User.queryMe().token;
        return token;
    }

    @JavascriptInterface  //启动活动
    public void fun2ToActivity() {
        mActivity.startActivity(new Intent(mActivity, IntegralTallActivity.class));
    }


}