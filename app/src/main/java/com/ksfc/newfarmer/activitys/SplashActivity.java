package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.singulariti.deepshare.DeepShare;
import com.singulariti.deepshare.listeners.DSInappDataListener;
import com.trello.rxlifecycle.components.RxActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.yangentao.util.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 项目名称：QianXihe 类名称：SplashActivity 类描述： 启动页 创建人：Jimes 创建时间：2015-5-14 上午11:38:21
 * 修改备注：
 */
public class SplashActivity extends RxActivity implements DSInappDataListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 屏幕竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_welcome);
        final PreferenceUtil pu = new PreferenceUtil(this, "config");
        final boolean isFirst = pu.getBool("first", true); //判断用户是否是第一次打开app
        // 判断是否第一次启动
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindToLifecycle())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (isFirst) {
                            IntentUtil.activityForward(SplashActivity.this, GuideActivity.class, null, true);
                        } else {
                            IntentUtil.activityForward(SplashActivity.this, MainActivity.class, null, true);
                        }
                        pu.putBool("first", false);
                    }
                });
    }

    //DeepShare
    @Override
    protected void onStart() {
        super.onStart();
        DeepShare.init(this, "c8c3c0850ecb8d07", this);
    }

    @Override
    public void onInappDataReturned(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return;
            }
            String activity = jsonObject.getString("activity");
            String key = jsonObject.getString("key");
            String value = jsonObject.getString("value");
            Class aClass = Class.forName("com.ksfc.newfarmer.activitys." + activity);
            Intent intent = new Intent(this, aClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (StringUtil.checkStr(key) && StringUtil.checkStr(value)) {
                intent.putExtra(key, value);
            }
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String s) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        DeepShare.onStop();
    }
}
