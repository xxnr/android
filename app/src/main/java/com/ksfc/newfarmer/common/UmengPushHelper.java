package com.ksfc.newfarmer.common;

import android.content.Context;
import android.content.Intent;

import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;


import java.util.Map;

/**
 * Created by HePeng on 2016/3/28.
 */
public class UmengPushHelper {

    public static String UM_XXNR_MESSAGE = "xxnr";

    //添加一个设备绑定移除设备绑定
    public static void removeAlias(final Context context, final String alias) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean removeAlias = PushAgent.getInstance(context).removeAlias(alias, UM_XXNR_MESSAGE);
                    RndLog.d("BaseActivity", "removeAlias:" + removeAlias);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //添加一个设备绑定token
    public static void addAlias(final Context context, final String alias) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean addAlias = PushAgent.getInstance(context).addExclusiveAlias(alias, UM_XXNR_MESSAGE);
                    RndLog.d("LoginActivity", "addAlias:" + addAlias);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK

    public static void lunchActivity(Context context) {

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage uMessage) {

                if (uMessage!=null){
                    String activity = uMessage.activity;
                    Map<String, String> map = uMessage.extra;
                    String orderId = null;
                    if (map != null && map.size() > 0) {
                        orderId = map.get("orderId");
                    }
                    if (StringUtil.checkStr(activity)) {
                        if (StringUtil.checkStr(orderId)) {
                            Class<?> aClass = null;
                            try {
                                aClass = Class.forName("com.ksfc.newfarmer.activitys." + activity);
                                Intent intent = new Intent(context, aClass);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("orderId", orderId);
                                context.startActivity(intent);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        };
        PushAgent.getInstance(context).setNotificationClickHandler(notificationClickHandler);
    }
}
