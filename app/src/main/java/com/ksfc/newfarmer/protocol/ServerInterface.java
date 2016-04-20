package com.ksfc.newfarmer.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.utils.RndLog;

import android.text.TextUtils;

/**
 * 服务器接口
 *
 * @author wqz
 */
public final class ServerInterface {

    private static final String TAG = "ServerInterface";

    private static Gson mGson;

    private NetworkHelper mHelper;

    public ServerInterface() {
        mGson = new Gson();
        mHelper = new NetworkHelper();
    }

    public ResponseResult request(final ApiType api, final RequestParams params)
            throws NetworkException {

        HttpResponse response = getResponseByApi(api, params);

        if (response != null) {// response == null�?可能是无网络引起

            HttpEntity entity = response.getEntity();
            StatusLine status = response.getStatusLine();
            if (HttpStatus.SC_OK == status.getStatusCode()) {
                if (entity != null) {
                    try {
                        String json = "";

                        InputStream is = entity.getContent();
                        Header contentEncoding = response
                                .getFirstHeader("Content-Encoding");

                        if (contentEncoding != null
                                && contentEncoding.getValue().equalsIgnoreCase(
                                "gzip")) {
                            RndLog.d(TAG, "https response has be gziped!");
                            is = new GZIPInputStream(
                                    new BufferedInputStream(is));

                        }
                        json = toString(is, "UTF-8");
                        RndLog.d(TAG, "request. json.length = " + json);
                        return parseJson(json, getJsonClassByApi(api));
                    } catch (Exception e) {
                        // BzLog.e(TAG, e.getMessage(), e);
                        e.printStackTrace();
                        throw new NetworkException(e);
                    } finally {
                        try {
                            entity.consumeContent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            } else {
                int statusCode = status.getStatusCode();
                RndLog.e(TAG, "HTTP CODE :" + statusCode);
                throw new NetworkException(status.getStatusCode(),
                        status.getReasonPhrase());
            }
        } else {
            // 执行过程中产生异常
            return new ResponseResult(Request.HTTP_ERROR, "");
        }
    }

    /**
     * 修改为根据result类型返回结果
     *
     * @param json
     * @param clazz
     * @return
     * @throws JSONException
     */
    public static ResponseResult parseJson(String json,
                                           Class<? extends ResponseResult> clazz) throws JSONException {

        ResponseResult res;
        try {
            JSONObject obj = new JSONObject(json);
            json = obj.toString();
            res = mGson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            res = new ResponseResult(Request.PARSE_DATA_FAILED, "");
        }
        return res;
    }

    public static String toString(InputStream is, String charset) {

        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(is, charset));

            String line = reader.readLine();

            while (!TextUtils.isEmpty(line)) {

                buffer.append(line);
                line = reader.readLine();

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return buffer.toString();
    }

    /**
     * 通过参数返回http响应
     *
     * @param api
     * @param params
     * @return
     */
    private HttpResponse getResponseByApi(ApiType api, RequestParams params) {

        params.put("user-agent", "Android-v2.0");

        RndLog.i(TAG, buildDebugUrl(api, params));

        try {
            // TODO 判断api类型
            if (api.getRequestMethod() == RequestMethod.GET) {
                return mHelper.performGet(api.getOpt(), params);
            } else if (api.getRequestMethod() == RequestMethod.FILE) {
                return mHelper.postFile(api.getOpt(), params);
            } else if (api.getRequestMethod() == RequestMethod.POSTJSON) {
                try {
                    return mHelper.postBody(api.getOpt(), params);
                } catch (UnsupportedEncodingException e) {
                    RndLog.e(TAG, e.getMessage());
                    return null;
                }
            } else {
                return mHelper.performPost(api.getOpt(), params);
                // return mHelper.postFile(api.getOpt(), params);
            }
        } catch (NetworkException e) {
            RndLog.e(TAG, e.getMessage());
            return null;
        }

    }

    /**
     * 创建调试URL，便于在浏览器中调试
     *
     * @param api
     * @param params
     * @return
     */
    private String buildDebugUrl(ApiType api, RequestParams params) {
        StringBuilder sb = new StringBuilder();
        sb.append(api.getOpt());
        sb.append("?");
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            if (key.startsWith(NetworkHelper.Post_Entity_FILE_Data)) {
                continue;
            }
            sb.append(key);
            sb.append("=");
            sb.append(params.get(key));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static Class<? extends ResponseResult> getJsonClassByApi(ApiType api) {
        // TODO 返回api对应的CLASS
        return api.getClazz();
    }

}
