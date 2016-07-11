package com.ksfc.newfarmer.http.RxApi;

import android.util.Log;

import com.ksfc.newfarmer.MsgID;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CAI on 2016/6/30.
 */
public class RxService {

    private static final String BASE_URL = MsgID.IP;


    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
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
