package com.ksfc.newfarmer;

import android.content.Context;

import com.ksfc.newfarmer.db.DBManager;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.common.UmengPushHelper;

import greendao.DaoSession;

public class App {
    private static RndApplication app;

    public static void setApp(RndApplication application) {
        app = application;
    }

    public static RndApplication getApp() {
        return app;
    }

    /**
     * 退出登录
     */
    public static void loginOut(){

        Context context = app.getApplicationContext();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            //解除推送alias
            UmengPushHelper.removeAlias(context, userInfo.userid);
            App.getApp().setUid("");
        }
        Store.User.removeMe();
        SPUtils.clear(context);

        DaoSession writableDaoSession = DBManager.getInstance(context).getWritableDaoSession();
        writableDaoSession.getPotentialCustomersEntityDao().deleteAll();
        writableDaoSession.getInviteeEntityDao().deleteAll();
        writableDaoSession.getPotentialCustomersEntityDao().deleteAll();


    }

}
