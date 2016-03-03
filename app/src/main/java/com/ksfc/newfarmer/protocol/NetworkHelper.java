package com.ksfc.newfarmer.protocol;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;


import com.ksfc.newfarmer.utils.RndLog;

/**
 * 连接网络的帮助类
 *
 * @author wqz
 */
public class NetworkHelper {

    public static final String Post_Entity_Json_Data = "post-json-data";
    public static final String Post_Entity_FILE_Data = "post-file-data";

    // ===========================================================
    // Constants
    // ===========================================================
    private static final String TAG = "NetworkHelper";

    private static final int SO_TIMEOUT = 10*1000;

    private static final int CONNECTION_TIMEOUT = 10*1000;

    // private HttpClient httpClient;

    public HttpResponse performGet(String url, HashMap<String, String> urlParams)
            throws NetworkException {

        RndLog.d(TAG, "performGet. url=" + url);

        final HttpClient client = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        // httpGet.setHeader("Accept-Encoding", "gzip");
        if (urlParams != null && !urlParams.isEmpty()) {
            for (String key : urlParams.keySet()) {
                httpGet.addHeader(key, urlParams.get(key));

                RndLog.d(TAG, key + " = " + urlParams.get(key));
            }

        }

        return execute(client, httpGet);
    }

    //post请求

    public HttpResponse performPost(String url,
                                    HashMap<String, String> urlParams) throws NetworkException {
        RndLog.d(TAG, "performPost. url=" + url);
        // HttpParams httpParams = new BasicHttpParams();
        // HttpConnectionParams.setConnectionTimeout(httpParams,
        // CONNECTION_TIMEOUT);
        // HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
        // final DefaultHttpClient client = new DefaultHttpClient();
        // String api = url.split("/")[url.split("/").length - 1];
        final HttpClient client = getHttpClient();
        HttpPost post = new HttpPost(url);
        // post.setHeader("Accept-Encoding", "gzip");
        // RndLog.d(TAG, "https has be gziped!");
        ArrayList<NameValuePair> nvps = null;

        if (urlParams != null && !urlParams.isEmpty()) {
            nvps = new ArrayList<NameValuePair>();
            for (String key : urlParams.keySet()) {
                String value = urlParams.get(key);
                RndLog.d(TAG, "performPost. parameter[" + key + "=" + value
                        + "]");
                nvps.add(new BasicNameValuePair(key, value));
            }
        }

        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (Exception e) {
            RndLog.e(TAG, "performPost. ", e);
        }
        return execute(client, post);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param urlParams
     * @return
     * @throws NetworkException
     */
    public HttpResponse postFile(String url, HashMap<String, String> urlParams)
            throws NetworkException {
        RndLog.d(TAG, "postFile url=" + url);
        final HttpClient client = getHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept-Encoding", "gzip");
        RndLog.d(TAG, "https has be gziped!");

        // 利用httpmime上传
        MultipartEntity reqEntity = new MultipartEntity();

        if (urlParams != null && !urlParams.isEmpty()) {
            for (String key : urlParams.keySet()) {
                String value = urlParams.get(key);
                RndLog.d(TAG, "performPost. parameter[" + key + "=" + value
                        + "]");
                if (key.startsWith(Post_Entity_FILE_Data)) {
                    // 文件数据
                    String fileKey = key.substring(Post_Entity_FILE_Data
                            .length());
                    FileBody fileBody = new FileBody(new File(value));
                    reqEntity.addPart(fileKey, fileBody);
                } else {
                    // 文本数据
                    try {

                        StringBody sb = new StringBody(value,
                                Charset.forName("UTF-8"));
                        reqEntity.addPart(key, sb);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        post.setEntity(reqEntity);
        return execute(client, post);
    }

    /**
     * postJson
     *
     * @param url
     * @param urlParams
     * @return
     * @throws NetworkException
     * @throws UnsupportedEncodingException
     */
    public HttpResponse postBody(String url, HashMap<String, String> urlParams)
            throws NetworkException, UnsupportedEncodingException {
        RndLog.d(TAG, "postBody. url=" + url);
        final HttpClient client = getHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept-Encoding", "gzip");
        RndLog.d(TAG, "https has be gziped!");

        if (urlParams != null && !urlParams.isEmpty()) {
            String value = urlParams.get("JSON");
            RndLog.d(TAG, "postBody. parameter[" + value + "]");
            StringEntity entity = new StringEntity(value, "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
        }
        return execute(client, post);
    }

    private HttpResponse execute(HttpClient client, HttpRequestBase method)
            throws NetworkException {
        try {
            RndLog.e(TAG, "execute, ");
            return client.execute(method);
        } catch (Exception e) {
            // RndLog.e(TAG, "execute. " + e.getMessage());
            e.printStackTrace();
            throw new NetworkException(e);
        }
    }

    private HttpClient getHttpClient() {

        HttpClient httpClient = null;

        if (httpClient == null) {

            // 初始化工�?
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore
                        .getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                // sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                // // 允许�?��主机的验�?
                sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER); // 允许�?��主机的验�?

                HttpParams params = new BasicHttpParams();
                params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params,
                        HTTP.DEFAULT_CONTENT_CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, true);

                // 设置连接管理器的超时
                ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);
                // 设置连接超时
                HttpConnectionParams.setConnectionTimeout(params,
                        CONNECTION_TIMEOUT);
                // 设置socket超时
                HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);

                // 设置http https支持
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), 80));
                // schReg.register(new Scheme("https", sf, 443));
                ClientConnectionManager conManager = new ThreadSafeClientConnManager(
                        params, schReg);

                httpClient = new DefaultHttpClient(conManager, params);

            } catch (Exception e) {
                e.printStackTrace();
                return new DefaultHttpClient();
            }

        }
        return httpClient;
    }

    class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {

                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


}
