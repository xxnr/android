package com.ksfc.newfarmer.common;


import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.RxApi.RxService;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.http.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.http.beans.RscOrderDetailResult;
import com.ksfc.newfarmer.utils.StringUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by CAI on 2016/5/13.
 */
public class OrderUtils {

    /**
     * 用户是否已经审核
     *
     * @param subscriber
     * @param orderId
     */
    public static void isChecked( Subscriber<Integer> subscriber, String orderId) {
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            RxService.createApi().GET_ORDER_DETAILS(userInfo.token, orderId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<MyOrderDetailResult, Integer>() {
                        @Override
                        public Integer call(MyOrderDetailResult myOrderDetailResult) {
                            return myOrderDetailResult.datas.rows.order.orderStatus.type;
                        }
                    })
                    .subscribe(subscriber);
        }
    }

    /**
     * RSC订单是否审核过
     *
     * @param subscriber
     * @param orderId
     */
    public static void CheckOffline( Subscriber<Integer> subscriber, String orderId) {

        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            RxService.createApi().GET_RSC_ORDER_Detail(userInfo.token, orderId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<RscOrderDetailResult, Integer>() {
                        @Override
                        public Integer call(RscOrderDetailResult rscOrderDetailResult) {
                            return rscOrderDetailResult.order.orderStatus.type;
                        }
                    })
                    .subscribe(subscriber);
        }
    }
}
