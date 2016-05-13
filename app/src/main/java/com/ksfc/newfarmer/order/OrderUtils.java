package com.ksfc.newfarmer.order;


import com.google.gson.Gson;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.RscOrderDetailResult;
import com.ksfc.newfarmer.utils.StringUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CAI on 2016/5/13.
 */
public class OrderUtils {

    //订单是否已经审核
    public static boolean isChecked(final String orderId) {

        final boolean[] paid = {false};
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormBody.Builder builder1 = new FormBody.Builder();

        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            builder1.add("token", userInfo.token);
        }
        builder1.add("orderId", orderId);
        FormBody formBody = builder1.build();

        Request request = new Request.Builder()
                .url(ApiType.GET_ORDER_DETAILS.getOpt())
                .post(formBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                if (StringUtil.checkStr(json)) {
                    MyOrderDetailResult myOrderDetailResult = gson.fromJson(json, MyOrderDetailResult.class);
                    MyOrderDetailResult.Datas datas = myOrderDetailResult.datas;
                    if (datas != null
                            && datas.rows != null
                            && datas.rows.order != null
                            && datas.rows.order.orderStatus != null) {
                        switch (datas.rows.order.orderStatus.type) {
                            case 1:
                            case 2:
                            case 7:
                                paid[0] = false;
                                break;
                            default:
                                paid[0] = true;
                                break;
                        }
                    }
                }
            }
        });

        return paid[0];

    }



    //RSC订单是否审核过
    public static boolean CheckOffline( String orderId) {

        final boolean[] paid = {false};

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient mOkHttpClient = new OkHttpClient();

        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            Request request = new Request.Builder()
                    .url(ApiType.GET_RSC_ORDER_Detail.getOpt() + "?" + "orderId=" + orderId + "&token=" + userInfo.token)
                    .build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    if (StringUtil.checkStr(json)) {
                        String result = response.body().string();
                        if (StringUtil.checkStr(result)) {
                            try {
                                Gson gson = new Gson();
                                RscOrderDetailResult rscOrderDetailResult = gson.fromJson(result, RscOrderDetailResult.class);
                                paid[0] = rscOrderDetailResult.order.orderStatus.type != 2;
                            } catch (Exception e) {
                                e.printStackTrace();
                                paid[0] = false;
                            }
                        }
                    }
                }
            });
        }
        return paid[0];
    }


}
