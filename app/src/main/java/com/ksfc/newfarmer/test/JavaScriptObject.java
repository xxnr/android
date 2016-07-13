package com.ksfc.newfarmer.test;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;


public class JavaScriptObject {
    Context mContxt;

    //sdk17版本以上加上注解
    public JavaScriptObject(Context mContxt) {
        this.mContxt = mContxt;
    }

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


}  