package com.ksfc.newfarmer.protocol.RxApi;


import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.protocol.NetworkHelper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CAI on 2016/6/30.
 */
public class RxService {

    private static final String BASE_URL = MsgID.IP;
    private static OkHttpClient okHttpClient = NetworkHelper.getClient();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    private RxService() {
        //construct
    }

    public static ApiService createApi() {
        return retrofit.create(ApiService.class);
    }

}
