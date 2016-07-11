package com.ksfc.newfarmer.http;


import com.google.gson.Gson;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;

import com.ksfc.newfarmer.App;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

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

    private static final int CONNECTION_TIMEOUT = 20 * 1000;

    private static OkHttpClient mOkHttpClient;


    public OkHttpClient getInstance() {

        if (mOkHttpClient == null) {
            synchronized (NetworkHelper.class) {
                if (mOkHttpClient == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
                    try {
                        builder.sslSocketFactory(setCertificates(App.getApp().getAssets().open("xxnr.cer")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mOkHttpClient = builder.build();
                }
            }
        }
        return mOkHttpClient;
    }


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
        RndLog.d(TAG, "put. url=" + url);

        if (urlParams != null && !urlParams.isEmpty()) {
            String value = urlParams.get("PUT");
            RndLog.d(TAG, "put. parameter[" + value + "]");
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


    public SSLSocketFactory setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {

                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
