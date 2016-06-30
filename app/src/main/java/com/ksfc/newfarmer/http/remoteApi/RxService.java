package com.ksfc.newfarmer.http.remoteApi;

import com.ksfc.newfarmer.MsgID;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SkyEyesStion on 2016/2/26.
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

    public static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

}
