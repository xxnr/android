package com.ksfc.newfarmer.protocol;


import com.google.gson.Gson;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.*;
import okhttp3.Request;


/**
 * 连接网络的帮助类
 *
 * @author wqz
 */
public class NetworkHelper {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String TAG = "NetworkHelper";

    private static final int CONNECTION_TIMEOUT = 10 * 1000;

    private static OkHttpClient mOkHttpClient;

    /**
     * GET
     */
    public Response performGet(String url, HashMap<String, String> urlParams)
            throws IOException {
        RndLog.d(TAG, "performGet. url=" + url);
        if (urlParams != null && !urlParams.isEmpty()) {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(url).append("?");
            for (String key : urlParams.keySet()) {
                urlBuilder.append(key).append("=").append(urlParams.get(key)).append("&");
                RndLog.d(TAG, key + " = " + urlParams.get(key));
            }
            String subUrl = urlBuilder.substring(0, urlBuilder.length() - 1);
            RndLog.d(TAG, "performGet. Data=" + subUrl);

            //创建一个Request
            final Request request = new Request.Builder()
                    .url(subUrl)
                    .build();
            return getInstance().newCall(request).execute();

        }
        return null;
    }


    private OkHttpClient getInstance() {
        if (mOkHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (mOkHttpClient == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
                    mOkHttpClient = new OkHttpClient();
                }
            }
        }
        return mOkHttpClient;
    }


    //post请求

    public Response performPost(String url,
                                HashMap<String, String> urlParams) throws IOException {
        RndLog.d(TAG, "performPost. url=" + url);
        if (urlParams != null && !urlParams.isEmpty()) {
            Gson gson = new Gson();
            String json = gson.toJson(urlParams);
            RequestBody requestBody = RequestBody.create(JSON, json);
            RndLog.d(TAG, "performBody" + json);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            return getInstance().newCall(request).execute();
        }
        return null;
    }


    /**
     * postJson
     */
    public Response postBody(String url, HashMap<String, String> urlParams) throws IOException {
        RndLog.d(TAG, "postBody. url=" + url);

        if (urlParams != null && !urlParams.isEmpty()) {
            String value = urlParams.get("JSON");
            RndLog.d(TAG, "postBody. parameter[" + value + "]");
            //如果value 为空 new StringEntity的时候会报错
            if (StringUtil.checkStr(value)) {
                RequestBody requestBody = RequestBody.create(JSON, value);
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                return getInstance().newCall(request).execute();
            }
        }
        return null;
    }

    /**
     * put
     */
    public Response putBody(String url, HashMap<String, String> urlParams) throws IOException {
        RndLog.d(TAG, "postBody. url=" + url);

        if (urlParams != null && !urlParams.isEmpty()) {
            String value = urlParams.get("PUT");
            RndLog.d(TAG, "postBody. parameter[" + value + "]");
            //如果value 为空 new StringEntity的时候会报错
            if (StringUtil.checkStr(value)) {
                RequestBody requestBody = RequestBody.create(JSON, value);
                Request request = new Request.Builder()
                        .url(url)
                        .put(requestBody)
                        .build();
                return getInstance().newCall(request).execute();
            }
        }
        return null;
    }
}
