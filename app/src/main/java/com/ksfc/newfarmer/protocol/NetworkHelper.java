package com.ksfc.newfarmer.protocol;



import com.google.gson.Gson;
import com.ksfc.newfarmer.utils.RndLog;

import com.ksfc.newfarmer.App;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    private static final String TAG = "NetworkHelper";


    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final int READ_TIMEOUT= 20 * 1000;

    private static final NetworkHelper mNetworkHelper = new NetworkHelper();
    private static final OkHttpClient mOkHttpClient;
    private static final Gson mGson;

    static {
        mGson = new Gson();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(READ_TIMEOUT,TimeUnit.MILLISECONDS);
        try {
            builder.sslSocketFactory(setCertificates(App.getApp().getAssets().open("xxnr.cer")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOkHttpClient = builder.build();
    }

    public static NetworkHelper getInstance() {
        return mNetworkHelper;
    }

    public static OkHttpClient getClient() {
        return mOkHttpClient;
    }

    /**
     * 通过http请求的基本信息，创建一个Request对象
     */
    public Response getResponse(ApiType api, String url, HashMap<String, String> urlParams) throws Exception {
        if (urlParams == null) {
            urlParams = new HashMap<>();
        }
        Request.Builder builder = new Request.Builder();
        if (ApiType.RequestMethod.GET == api.getRequestMethod()) {
            //GET
            RndLog.d(TAG, "performGet. url=" + url);
            builder.url(initGetRequest(url, urlParams)).get();
        } else if (ApiType.RequestMethod.POST == api.getRequestMethod()) {
            //POST
            RndLog.d(TAG, "performPost. url=" + url);
            builder.url(url).post(initRequestBody(ApiType.RequestMethod.POST, urlParams));
        } else if (ApiType.RequestMethod.PUT == api.getRequestMethod()) {
            //PUT
            RndLog.d(TAG, "performPut. url=" + url);
            builder.url(url).put(initRequestBody(ApiType.RequestMethod.PUT, urlParams));
        } else if (ApiType.RequestMethod.POSTJSON == api.getRequestMethod()) {
            //POSTJSON
            RndLog.d(TAG, "performPostJson. url=" + url);
            builder.url(url).post(initRequestBody(ApiType.RequestMethod.POSTJSON, urlParams));
        }
        return mOkHttpClient.newCall(builder.build()).execute();
    }


    /**
     * 初始化Get请求参数
     * init Get type params
     */
    private String initGetRequest(String url, HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder(url);
        //has params ?
        RndLog.d(TAG, "performGet. url=" + url);
        if (params != null && !params.isEmpty()) {
            sb.append('?');
            Set<Map.Entry<String, String>> entries = params.entrySet();
            int count = 0;
            for (Map.Entry entry : entries) {
                count++;
                sb.append(entry.getKey()).append('=').append(entry.getValue());
                RndLog.d(TAG, entry.getKey() + " = " + entry.getValue());
                if (count == params.size()) {
                    break;
                }
                sb.append('&');
            }
            url = new String(sb);
            RndLog.d(TAG, "performGet. Data=" + url);
        }
        return url;
    }

    /**
     * 初始化Body类型请求参数
     * init Body type params
     */
    private RequestBody initRequestBody(ApiType.RequestMethod method, HashMap<String, String> params) {
        if (params != null && !params.isEmpty()) {
            String json;
            if (method == ApiType.RequestMethod.PUT) {
                json = params.get("PUT");
            } else if (method == ApiType.RequestMethod.POSTJSON) {
                json = params.get("JSON");
            } else {
                json = mGson.toJson(params);
            }
            RndLog.d(TAG, "RequestBody" + json);
            return RequestBody.create(MEDIA_TYPE, json);
        }
        return null;
    }

    /**
     * https证书
     *
     * @param certificates
     * @return
     */
    private static SSLSocketFactory setCertificates(InputStream... certificates) {
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
                    e.printStackTrace();
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
