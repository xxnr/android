package com.ksfc.newfarmer.test;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.utils.StringUtil;

/**
 * Created by CAI on 2016/7/5.
 */
public class JavaScriptObject {
    Context mContxt;
    WebView webView;

    //sdk17版本以上加上注解
    public JavaScriptObject(Context mContxt, WebView webView) {
        this.mContxt = mContxt;
        this.webView = webView;
    }

    @JavascriptInterface
    public String fun1FromAndroid() {
        String token = Store.User.queryMe().token;
        return token;
    }


}